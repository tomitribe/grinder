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

   Data streams are partitioned into 'channels'. Each channel has a current
   value (or nil), with an associated sequence number. New data values are
   provided with the `push` function.

   Clients `poll`, supplying a channel, sequence number, and callback function.
   If the provided sequence does not match the current sequence, the callback
   is invoked immediately with the current value andsequence for the channel.

   Otherwise, the callback is retained and invoked asynchronously when the
   channel has new data."

  (:use
    [net.grinder.console.web.ringutil :only [json-response]])
  (:require
    [clojure.set]
    [clojure.tools [logging :as log]]))


(let [default 0
      values (atom {})]

  (defn get-sequence
    "Get the current sequence number for `channel`."
    [channel]
    (->
      channel
      (@values default)
      str))

  (defn- next-sequence
    "Generate a new sequence number for `channel`."
    [channel]
    (->
      channel
      ((swap! values
         (fn [vs] (assoc vs channel (inc (vs channel default))))))
      str)))


(let [ ; Holds {channel #{callback}}
      callbacks (ref {})]

  (defn- register-callback
    "Register callback for `channel`."
    [channel callback]
    (dosync
      (commute callbacks
        #(merge-with clojure.set/union % {channel #{callback}})))
    (log/debugf "register-callback: %s %s -> %s"
                 channel
                 callback
                 @callbacks))

  (defn- remove-callbacks
    "Remove and return the registered callbacks for `channel`."
    [channel]
    (dosync (let [cs (@callbacks channel)]
              (commute callbacks dissoc channel)
              cs))))

(defn- make-response [data s] (json-response {:data data :next s}))

(let [last-data (ref {})]

  (defn poll
    "Register a callback for `channel` and `sequence`. The callback will
     be invoked with a Ring response, synchronously or asynchronously
     depending on whether the provided sequence number is current."
    [callback channel sequence]

    (log/debugf "(poll %s %s)" channel sequence)

    (let [ch (keyword channel)
          v (@last-data ch)]

      (let [s (get-sequence ch)]
        (if (and v (not= sequence s))
          ; Client has stale value => give them the current value.
          (do
            (log/debugf "sync response %s %s/%s %s" ch sequence s v)
            (callback (make-response {ch v} s)))

          ; Client has current value, or there is none => long poll.
          (register-callback ch callback)))))

  (defn- pushf
    "Sets data for `channel` to the result of calling `f` with the old value.
     Sends the new data to all clients listening to `channel`."
    [channel f]
    (let [ch (keyword channel)]
      (log/debugf "(push %s) %s" channel (get-sequence ch))

      (let [data
        (dosync
          (let [new-data (f (@last-data ch))]
            (commute last-data assoc ch new-data)
            new-data))]

        (let [r (make-response {ch data} (next-sequence ch))]
          (doseq [cb (remove-callbacks ch)]
            (log/debugf "async response to %s with %s" cb r)
            (cb r))))))

  (defn push
    "Send `data` to all clients listening to `channel`."
    [channel data]
    (pushf channel (constantly data)))

  (defn push-assoc
    "Assoc new values to the `data` for `channel` and send it to all listening
     clients. Assumes the current data value is a map, or nil."
    [channel key val & kvs]
    (pushf channel #(apply assoc %1 key val kvs))))

