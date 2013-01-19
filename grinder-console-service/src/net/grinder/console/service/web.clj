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

(ns net.grinder.console.service.web
  "Compojure application that provides the console web UI."
  (:use [compojure [core :only [GET POST PUT context routes]]
                   [route :only [not-found resources]]]
        hiccup.core
        hiccup.def
        hiccup.element
        hiccup.form
        hiccup.page)
  (:require
    [compojure.handler]
    [net.grinder.console.model [files :as files]
                               [processes :as processes]
                               [properties :as properties]
                               [recording :as recording]])
  )

(defelem page [body]
  (html5
    (include-css "resources/main.css")
    [:div {:id :wrapper}
      [:div {:id :header}
       [:div {:id :title} [:h1 "The Grinder"]]
       [:div {:id :logo} (image "core/logo.png" "Logo")]]
      [:div {:id :sidebar}
       (link-to "./properties" "Console Properties")]
      [:div {:id :content}
       (html body)]]
  ))

(defn- property->description [r s]
  (if-let [label (.getString r (str (name s) ".label") false)]
    label
    (name s))
  )

(defn- render-property-group [legend res properties defaults]
  [:fieldset
   [:legend legend]
   (for [[d k v]
         (sort
           (map
             (fn [[k v]] [(property->description res k) k v])
             properties))]
     (let [coerced (properties/coerce-value v)]
       [:div
        (label k d)
        (text-field {:placeholder (str (defaults k))} k coerced)]))
   ])

(defn- render-properties-form [p res]
  (page
    (form-to
      {:id :properties}
      [:post "./properties" ]
      [:hgroup
       [:h2
        "Console Properties"
       ]]

      (let [properties (properties/get-properties p)
            defaults (properties/default-properties res)
            groups [["Communication"
                     #{:consoleHost
                       :consolePort
                       :httpHost
                       :httpPort}]
                    ["Sampling"
                     #{:significantFigures
                       :collectSampleCount
                       :sampleInterval
                       :ignoreSampleCount}]
                    ["File Distribution"
                     #{:scanDistributionFilesPeriod
                       :distributionDirectory
                       :propertiesFile
                       :distributionFileFilterExpression}]]
            all (conj groups
                  ["Other Settings"
                   (apply clojure.set/difference
                     (set (keys properties))
                     (map second groups))])
            ]
        (for [[l ks] all]
          (render-property-group l res (select-keys properties ks) defaults)))

      (submit-button {:id "submit"} "Save"))))

(defn handle-properties-form [params]
  (println params)
  {:body (str "stored" (params :task))})


(defn- wrap-spy [handler spyname]
  (fn [request]
    (println "-------------------------------")
    (println spyname "request:")
    (clojure.pprint/pprint request)
    (let [response (handler request)]
      (println spyname "response:")
      (clojure.pprint/pprint response)
      (println "-------------------------------")
      response)))

(defn create-app
  "Create the Ring routes, given a map of the various console components."
  [{:keys [process-control
           sample-model
           sample-model-views
           properties
           file-distribution
           console-resources]}]
  (->
    (routes
      (resources "/resources/" {:root "static"})
      ;(wrap-spy
        (resources "/core/" {:root "net/grinder/console/common/resources"})
      ;  "core")
      ;(wrap-spy
        (GET "/properties" [] (render-properties-form properties console-resources))
      ;  "props")
      (POST "/properties" {params :params} (handle-properties-form params))
      (not-found "Whoop!!!")
      )
    compojure.handler/api))


