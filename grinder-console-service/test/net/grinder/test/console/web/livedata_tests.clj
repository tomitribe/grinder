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

(deftest next-value
  (with-no-logging
    (let [m 10
          n 1000
          current-values
            #(doall
               (pmap (fn [i] (Integer/parseInt (#'ld/get-value i))) (range m)))
          before-values (current-values)
          vs (partition n ; Partition the results by key.

               ; Generate n values for each of m keys, in parallel.
               (pmap
                 (fn [i] (Integer/parseInt (#'ld/next-value (int (/ i n)))))
                 (range (* m n))))

          ; For each key, we expect a continuous sequence of values.
          expected (for [v vs]
                     (let [s (sort v)
                           f (first s)]
                       (= s (range f (+ f n)))))]
      (is (every? identity expected))
      (is (= (map #(+ n %) before-values) (current-values))))))


(deftest register-client
  (with-no-logging
    (let [data [{:k (gensym) :vs (range 10)}
                {:k (gensym) :vs (range 3)}
                {:k (gensym) :vs nil }]]

      (doseq [{:keys [k vs]} data v vs] (#'ld/register-client k v))

      (is (=
        (for [{:keys [vs]} data] (and vs (into #{} vs)))
        (for [{:keys [k]} data] (#'ld/remove-clients k))))

      (is (=
        (repeat (count data) nil)
        (for [{:keys [k]} data] (#'ld/remove-clients k)))))))

(deftest poll-no-value
  (with-no-logging
    (let [k (gensym)
          r1  (ld/poll k "-1")
          msg "Hello world"]
      (is (= 200 (:status r1)))
      (is (nil? (.get (:body r1))))

      (ld/push k msg)

      (let [r2 (.get (:body r1))]
        (is (= 200 (:status r2)))
        (is (= "application/json" ((:headers r2) "Content-Type")))
        (is (= {"html" msg "sequence" "1"} (json/decode (:body r2))))
        )
      )))

(deftest poll-value
  (with-no-logging
    (let [k (gensym)
          msg "Someday"
          msg2 "this will all be yours"]

      (ld/push k msg)

      (let [r  (ld/poll k "-1")]
        (is (= 200 (:status r)))
        (is (= "application/json" ((:headers r) "Content-Type")))
        (is (= {"html" msg "sequence" "1"} (json/decode (:body r)))))

      ; push of the same value is a no-op.
      (ld/push k msg)

      ; new client gets existing value.
      (let [r  (ld/poll k "-1")]
        (is (= 200 (:status r)))
        (is (= "application/json" ((:headers r) "Content-Type")))
        (is (= {"html" msg "sequence" "1"} (json/decode (:body r)))))

      ; existing client gets long poll
      (let [r  (ld/poll k "1")]
        (is (= 200 (:status r)))
        (is (nil? (.get (:body r)))))

      ; push a new value.
      (ld/push k msg2)

      ; existing client gets new value
      (let [r  (ld/poll k "1")]
        (is (= 200 (:status r)))
        (is (= "application/json" ((:headers r) "Content-Type")))
        (is (= {"html" msg2 "sequence" "2"} (json/decode (:body r)))))
      )))
