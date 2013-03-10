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

(ns net.grinder.console.web.livedata
  "Long polling support.

   Data streams are partitioned by key. Each key has a current value (or nil),
   with an associated token. New data values are provided with the `push`
   function.

   Clients `poll`, supplying a list of key/token pairs, and a callback
   function. If one of the provided tokens is not current, the callback
   is invoked immediately with the out of date values.

   Otherwise, the callback is retained and invoked asynchronously when one
   of the keys has a new value."

  (:use
    [net.grinder.console.web.ringutil :only [json-response]])
  (:require
    [clojure.set]
    [clojure.tools [logging :as log]]))


(let [default 0
      values (atom {})]

  (defn get-token
    "Get the current token for key `k`."
    [k]
    (->
      k
      (@values default)
      str))

  (defn- next-token
    "Generate a new token for key `k`."
    [k]
    (->
      k
      ((swap! values
         (fn [vs] (assoc vs k (inc (vs k default))))))
      str)))


(let [ ; Holds {k #{callback}}
      callbacks (ref {})]

  (defn- register-callback
    "Register callback for key `k`."
    [k callback]
    (dosync
      (commute callbacks
        #(merge-with clojure.set/union % {k #{callback}})))
    (log/debugf "register-callback: %s %s -> %s"
                 k
                 callback
                 @callbacks))

  (defn- remove-callbacks
    "Remove and return the registered callbacks for key `k`.

     A callback can be regsitered for many keys. Currently, we do not remove
     returned callbacks from the lists belonging to other keys. Hence, some
     of the callbacks in the result may already have been used."
    [k]
    (dosync (let [cbs (@callbacks k)]
              (commute callbacks dissoc k)
              cbs))))

(defn- make-response [values]
  (json-response
    (for [[k v s] values] {:key k :value v :next s})))

(let [last-data (atom {})]

  (defn poll
    "Register a single use callback for a list of `[key token]` pairs.

     If there are tokens that are not current, the callback will be invoked
     synchronously with a Ring response containing the current values for the
     stale keys.

     Otherwise, the callback will be not be invoked immediately. When a
     value arrives for one of the keys, the callback will be invoked
     asynchronously with a Ring response containing the single value.

     The current implementation does not discard used callbacks, so relies
     on the callback implementation to be a no-op if called more than once."
    [callback kts]

    (log/debugf "(poll %s)" kts)

    (let [kwts (for [[k t] kts] [(keyword k) t])
          stale (for [[k t] kwts
                      :let [t' (get-token k)
                            v (@last-data k)]
                      :when (and v (not= t t'))]
                  [k v t'])]
      (if (not-empty stale)
        ; Client has one or more stale values => give them the current values.
        (do
          (log/debugf "sync response %s -> %s" kts stale)
          (callback (make-response stale))
        )

        ; Client has current value for each key => register callbacks.
        (doseq [[k _t] kwts] (register-callback k callback))
      )))

  (defn push
    "Send `data` to all clients listening to key `k`."
    [k data]
    (let [kkw (keyword k)]
      (log/debugf "(push %s) %s" k (get-token kkw))

      (swap! last-data assoc kkw data)

      (let [r (make-response [[kkw data (next-token kkw)]])]
        (doseq [cb (remove-callbacks kkw)]
          (log/debugf "async response to %s with %s" cb r)
          (cb r))))))

