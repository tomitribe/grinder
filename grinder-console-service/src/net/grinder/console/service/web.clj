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
        hiccup.page
        [ring.util [response :only [redirect-after-post]]])
  (:require
    [compojure.handler]
    [net.grinder.console.model [files :as files]
                               [processes :as processes]
                               [properties :as properties]
                               [recording :as recording]])
  (:import java.awt.Rectangle)
  )

(defelem page [body]
  (html5
    (include-css "resources/main.css")
    (include-js "resources/grinder-console.js")
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

(defn- render-text-field
  [k v d & [attributes]]
  (text-field
    (merge {:placeholder (properties/coerce-value d)
            :class "changeable"} attributes)
    k
    (properties/coerce-value v)))

(defn- render-number-field
  [k v d & [attributes]]
  (render-text-field k v d
    (merge {:type "number"} attributes)))

(defmulti render-property
  (fn [k v d & attributes] (type v)))

(defmethod render-property Boolean
  [k v d & attributes]
  [:div {:class "property"}
   (check-box (merge {:class "changeable"} attributes) k v)])

(defmethod render-property Rectangle
  [k ^Rectangle v ^Rectangle d & [attributes]]

  [:div {:class "property rectangle"}
   (for [[s vv dd] [["x" (.x v) (and d (.x d))]
                    ["y" (.y v) (and d (.y d))]
                    ["w" (.width v) (and d (.width d))]
                    ["h" (.height v) (and d (.height d))]]]
     (render-number-field (str k s) vv dd {:name k}))
    ])

(defmethod render-property Number
  [k v d & [attributes]]
  [:div {:class "property"}
   (render-number-field k v d (merge {} attributes))])

(defmethod render-property :default
  [k v d & [attributes]]
  [:div {:class "property"}
   (render-text-field k v d (merge {} attributes))])

(defn- render-property-group [legend res properties defaults]
  [:fieldset
   [:legend legend]
   (for [[d k v]
         (sort
           (map
             (fn [[k v]] [(property->description res k) k v])
             properties))]
     [:div {:class "property-line"}
      [:div {:class "label"}
       (label k d)]
      (render-property k v (defaults k))])
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
            groups [["File Distribution"
                     #{;:scanDistributionFilesPeriod
                       :distributionDirectory
                       :propertiesFile
                       ; :distributionFileFilterExpression
                       }]
                    ["Sampling"
                     #{:significantFigures
                       :collectSampleCount
                       :sampleInterval
                       :ignoreSampleCount}]
                    ["Communication"
                     #{:consoleHost
                       :consolePort
                       :httpHost
                       :httpPort}]
                    ["Swing Console"
                     #{:externalEditorCommand
                       :externalEditorArguments
                       :saveTotalsWithResults
                       :lookAndFeel
                       :frameBounds
                       :resetConsoleWithProcesses}]]
            ]
        (for [[l ks] groups]
          (render-property-group l res (select-keys properties ks) defaults)))

      (submit-button {:id "submit"} "Set"))))

(defn handle-properties-form [p params]
  (let [expanded (properties/add-missing-boolean-properties params)]
    (properties/set-properties p expanded)
    ;(clojure.pprint/pprint expanded)
    (redirect-after-post "./properties")))

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

      (GET "/properties" []
        (render-properties-form properties console-resources))

      (wrap-spy
        (POST "/properties" {params :form-params}
          (handle-properties-form properties params))
        "post")

      (not-found "Whoop!!!")
      )
    compojure.handler/api))


