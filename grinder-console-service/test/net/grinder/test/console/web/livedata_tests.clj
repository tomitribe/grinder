; Copyright (C) 2013 Philip Aston
; All rights reserved.
;
; This file is part of The Grinder software distribution. Refer to
; the file LICENSE which is part of The Grinder distribution for
; licensing details. The Grinder distribution is available on the
; Internet at http:;grinder.sourceforge.net/
;
; THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
; "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
; LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
; FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
; COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
; INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
; (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
; SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
; HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
; STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
; ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
; OF THE POSSIBILITY OF SUCH DAMAGE.

(ns net.grinder.test.console.web.livedata-tests
  "Unit tests for net.grinder.console.service.livedata."
  (:use
    [clojure.test]
    [net.grinder.test])
  (:require
    [net.grinder.console.web.livedata :as ld]
    [cheshire.core :as json]))

(deftest next-sequence
  (with-no-logging
    (let [m 10
          n 1000
          current-values
            #(doall
               (pmap (fn [i]
                       (Integer/parseInt (#'ld/get-sequence i))) (range m)))
          before-values (current-values)
          vs (partition n ; Partition the results by channel.

               ; Generate n values for each of m channels, in parallel.
               (pmap
                 (fn [i] (Integer/parseInt (#'ld/next-sequence (int (/ i n)))))
                 (range (* m n))))

          ; For each channel, we expect a continuous sequence of values.
          expected (for [v vs]
                     (let [s (sort v)
                           f (first s)]
                       (= s (range f (+ f n)))))]
      (is (every? identity expected))
      (is (= (map #(+ n %) before-values) (current-values))))))


(deftest register-client
  (with-no-logging
    (let [data [{:ch (gensym) :vs (range 10)}
                {:ch (gensym) :vs (range 3)}
                {:ch (gensym) :vs nil }]]

      (doseq [{:keys [ch vs]} data v vs] (#'ld/register-client ch v))

      (is (=
        (for [{:keys [vs]} data] (and vs (into #{} vs)))
        (for [{:keys [ch]} data] (#'ld/remove-clients ch))))

      (is (=
        (repeat (count data) nil)
        (for [{:keys [ch]} data] (#'ld/remove-clients ch)))))))

(deftest poll-no-value
  (with-no-logging
    (let [ch (gensym)
          r1  (ld/poll ch "-1")
          msg "Hello world"]
      (is (= 200 (:status r1)))
      (is (nil? (.get (:body r1))))

      (ld/push ch msg)

      (let [r2 (.get (:body r1))]
        (is (= 200 (:status r2)))
        (is (= "application/json" ((:headers r2) "Content-Type")))
        (is (= {"data" msg "next" "1"} (json/decode (:body r2))))
        )
      )))

(deftest poll-value
  (with-no-logging
    (let [ch (gensym)
          msg "Someday"
          msg2 "this will all be yours"]

      (ld/push ch msg)

      (let [r  (ld/poll ch "-1")]
        (is (= 200 (:status r)))
        (is (= "application/json" ((:headers r) "Content-Type")))
        (is (= {"data" msg "next" "1"} (json/decode (:body r)))))

      ; push of the same value is a no-op.
      (ld/push ch msg)

      ; new client gets existing value.
      (let [r  (ld/poll ch "-1")]
        (is (= 200 (:status r)))
        (is (= "application/json" ((:headers r) "Content-Type")))
        (is (= {"data" msg "next" "1"} (json/decode (:body r)))))

      ; existing client gets long poll
      (let [r  (ld/poll ch "1")]
        (is (= 200 (:status r)))
        (is (nil? (.get (:body r)))))

      ; push a new value.
      (ld/push ch msg2)

      ; existing client gets new value
      (let [r  (ld/poll ch "1")]
        (is (= 200 (:status r)))
        (is (= "application/json" ((:headers r) "Content-Type")))
        (is (= {"data" msg2 "next" "2"} (json/decode (:body r)))))
      )))
