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


; {data-key #{client}}
(def ^:private clients (atom {}))

(let [sequence (atom 0)]
  (defn- next-sequence []
    (swap! sequence inc)))

(defn- json-response
  "Format a clojure structure as a Ring JSON response."
  [c]
  (-> c
    json/generate-string
    response
    (content-type "application/json")))

(defn- register-client
  "Register http-kit callback `client` for `data-key`."
  [client data-key]
  (dosync
    (swap! clients
      #(merge-with clojure.set/union % {data-key #{client}})))
  (log/debugf "poll: %s %s -> %s"
               data-key
               client
               @clients))

(defn poll
  "Respond to a client poll for `data-key` and `sequence`
   and appropriate synchrnous or asynchronous Ring response."
  [data-key sequence]
  (async-response client
    (register-client client (keyword data-key))))

(defn push
  "Send `html-data` to all clients listening to `data-key`."
  [data-key html-data]
  (let [cs (dosync (let [cs (@clients data-key)]
                     (swap! clients dissoc "process-state")
                     cs))
        r (json-response {:html html-data
                          :sequence (next-sequence)})]
    (doseq [c cs]
      (log/debugf "Responding to %s with %s" c r)
      (c r))))

