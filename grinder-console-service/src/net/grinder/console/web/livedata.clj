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
    [net.grinder.console.web.ringutil])
  (:require
    [clojure.set]
    [clojure.tools [logging :as log]]))


(let [default 0
      values (atom {})]

  (defn get-sequence
    "Get the current value for `channel`."
    [channel]
    (->
      channel
      (@values default)
      str))

  (defn- next-sequence
    "Return a new value for `channel`."
    [channel]
    (->
      channel
      ((swap! values
         (fn [vs] (assoc vs channel (inc (vs channel default))))))
      str)))


(let [ ; Holds {channel #{client}}
      clients (ref {})]

  (defn- register-client
    "Register http-kit callback `client` for `channel`."
    [channel client]
    (dosync
      (commute clients
        #(merge-with clojure.set/union % {channel #{client}})))
    (log/debugf "register-client: %s %s -> %s"
                 channel
                 client
                 @clients))

  (defn- remove-clients
    "Remove and return the registered clients for `channel`."
    [channel]
    (dosync (let [cs (@clients channel)]
              (commute clients dissoc channel)
              cs))))

(let [last-data (atom {})]
  (defn poll
    "Respond to a client poll for `channel` and `sequence`
     with a synchronous or asynchronous Ring response."
    [channel sequence]

    (log/debugf "(poll %s %s)" channel sequence)

    (let [ch (keyword channel)
          v (@last-data ch)]

      (let [s (get-sequence ch)]
        (if (and v (not= sequence s))
          ; Client has stale value => give them the current value.
          (do
            (log/debugf "sync response %s %s/%s %s" ch sequence s v)
            (json-response {:data v
                            :next s}))

          ; Client has current value, or there is none => long poll.
          (async-response client
            (register-client ch client))))))

  (defn push
    "Send `data` to all clients listening to `channel`."
    [channel data]
    (let [ch (keyword channel)]
      (log/debugf "(push %s) %s" channel (get-sequence ch))

      (if (not= data (@last-data ch)) ; Is this check worth it?

        (let [r (json-response {:data data
                                :next (next-sequence ch)})]
          (swap! last-data assoc ch data)
          (doseq [c (remove-clients ch)]
            (log/debugf "async response to %s with %s" c r)
            (c r)))

        (log/debugf "ignoring push of same value %s" data)))))
