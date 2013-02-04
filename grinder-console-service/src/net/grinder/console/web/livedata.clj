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
  "Long polling support."
  (:use
    [org.httpkit.server :only [async-response]]
    [ring.util [response :only [response
                                content-type]]]
    )
  (:require
    [cheshire.core :as json]
    [clojure.tools [logging :as log]]))


(let [default 0
      values (atom {})]

  (defn- get-value
    "Get the value for key `k`."
    [k]
    (->
      k
      (@values default)
      str))

  (defn- next-value
    "Return a new value for key `k`."
    [k]
    (->
      k
      ((swap! values
         (fn [vs] (assoc vs k (inc (vs k default))))))
      str)))

(defn- json-response
  "Format a clojure structure as a Ring JSON response."
  [c]
  (-> c
    json/generate-string
    response
    (content-type "application/json")))

(let [ ; {data-key #{client}}
      clients (ref {})]

  (defn- register-client
    "Register http-kit callback `client` for `data-key`."
    [client data-key]
    (dosync
      (commute clients
        #(merge-with clojure.set/union % {data-key #{client}})))
    (log/debugf "register-client: %s %s -> %s"
                 data-key
                 client
                 @clients))

  (defn- remove-clients
    "Remove and return the registered clients for `data-key`."
    [data-key]
    (dosync (let [cs (@clients data-key)]
              (commute clients dissoc data-key)
              cs))))

(let [last-value (atom {})]
  (defn poll
    "Respond to a client poll for `data-key` and `sequence`
     with a synchronous or asynchronous Ring response."
    [data-key sequence]

    (let [k (keyword data-key)
          v (@last-value k)]

      (let [s (get-value k)]
        (if (and v (not= sequence s))
          ; Client has stale value => give them the current value.
          (do
            (log/debugf "sync response %s/%s %s" sequence (get-value k) v)
            (json-response {:html v
                            :sequence s}))

          ; Client has current value, or there is none => long poll.
          (async-response client
            (register-client client k))))))

  (defn push
    "Send `html-data` to all clients listening to `data-key`."
    [data-key html-data]
    (let [k (keyword data-key)]
      (if (not= html-data (@last-value k))

        (let [r (json-response {:html html-data
                                :sequence (next-value k)})]
          (swap! last-value assoc k html-data)
          (doseq [c (remove-clients k)]
            (log/debugf "async response to %s with %s" c r)
            (c r)))

        (log/debugf "ignoring push of same value %s" html-data)))))

