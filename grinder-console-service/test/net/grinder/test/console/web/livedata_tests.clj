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


(deftest register-callback
  (with-no-logging
    (let [data [{:ch (gensym) :vs (range 10)}
                {:ch (gensym) :vs (range 3)}
                {:ch (gensym) :vs nil }]]

      (doseq [{:keys [ch vs]} data v vs] (#'ld/register-callback ch v))

      (is (=
        (for [{:keys [vs]} data] (and vs (into #{} vs)))
        (for [{:keys [ch]} data] (#'ld/remove-callbacks ch))))

      (is (=
        (repeat (count data) nil)
        (for [{:keys [ch]} data] (#'ld/remove-callbacks ch)))))))

; what's a better idiom for this?
(defprotocol ResultHolder
  (none [this])
  (one [this])
  (adder [this]))

(defn result-holder []
  (let [results (atom [])
        get (fn []
             (let [r @results]
               (reset! results [])
               r))]
    (reify ResultHolder
      (none [_this] (is (= 0 (count @results))))
      (one [_this] (let [r (get)] (is (= 1 (count r))) (first r)))
      (adder [_this] (fn [r] (swap! results conj r))))))

(deftest poll-no-value
  (with-no-logging
    (let [ch (gensym)
          msg "Hello world"
          rh (result-holder)]
      (ld/poll (adder rh) ch "-1")

      (none rh)

      (ld/push ch msg)

      (let [r (one rh)]
        (is (= 200 (:status r)))
        (is (= "application/json" ((:headers r) "Content-Type")))
        (is (= {"data" {(str ch) msg} "next" "1"} (json/decode (:body r))))))))

(deftest poll-value
  (with-no-logging
    (let [ch (gensym)
          msg "Someday"
          msg2 "this will all be yours"
          rh (result-holder)]

      (ld/push ch msg)

      (ld/poll (adder rh) ch "-1")

      (let [r (one rh)]
        (is (= 200 (:status r)))
        (is (= "application/json" ((:headers r) "Content-Type")))
        (is (= {"data" {(str ch) msg} "next" "1"} (json/decode (:body r)))))

      ; new client gets existing value.
      (ld/poll (adder rh) ch "-1")

      (let [r (one rh)]
        (is (= 200 (:status r)))
        (is (= "application/json" ((:headers r) "Content-Type")))
        (is (= {"data" {(str ch) msg} "next" "1"} (json/decode (:body r)))))

      ; existing client gets long poll
      (ld/poll (adder rh) ch "1")

      (none rh)

      ; push a new value.
      (ld/push ch msg2)

      ; client called back
      (let [r (one rh)]
        (is (= 200 (:status r)))
        (is (= "application/json" ((:headers r) "Content-Type")))
        (is (= {"data" {(str ch) msg2} "next" "2"} (json/decode (:body r))))))))

(deftest push-assoc
  (with-no-logging
    (let [ch (gensym)
          rh (result-holder)]

      (ld/push-assoc ch :a 1)

      (ld/poll (adder rh) ch "-1")

      (let [r (one rh)]
        (is (= 200 (:status r)))
        (is (= "application/json" ((:headers r) "Content-Type")))
        (is (= {"data" {(str ch) {"a" 1}} "next" "1"} (json/decode (:body r)))))

      (ld/push-assoc ch :b 2 :c 3)

      (ld/poll (adder rh) ch "1")

      (let [r (one rh)]
        (is (= 200 (:status r)))
        (is (= "application/json" ((:headers r) "Content-Type")))
        (is (= {"data" {(str ch) {"a" 1 "b" 2 "c" 3}} "next" "2"}
              (json/decode (:body r)))))
      )))

