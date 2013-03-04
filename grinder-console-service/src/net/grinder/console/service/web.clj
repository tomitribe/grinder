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
  (:use
    [compojure [core :only [GET POST PUT context routes]]
     [route :only [not-found resources]]]
    [hiccup core def element form page
     [middleware :only [wrap-base-url]]
     [util :only [to-str to-uri]]]
    [net.grinder.console.service
     [translate :only [t make-wrap-with-translation]]]
    [ring.util [response :only [redirect
                                redirect-after-post
                                response]]])
  (:require
    [compojure.handler]
    [clojure.tools [logging :as log]]
    [net.grinder.console.model [files :as files]
                               [processes :as processes]
                               [properties :as properties]
                               [recording :as recording]]
    [net.grinder.console.web [livedata :as livedata]
                             [ringutil :as ringutil]])
  (:import
    java.awt.Rectangle
    net.grinder.console.ConsoleFoundation
    [net.grinder.statistics ExpressionView]))

(defn- state [type p]
  (let [s (:state p)]
    [s
     (t [(keyword (str (name s) "-" (name type))) s])]))

(defmulti render-process-state #(first %&))

(defmethod render-process-state :agent [type p]
  (let [[s d] (state type p)]
    (html
      [:div {:class s} d])))

(defmethod render-process-state :worker [type p]
  (let [[s d] (state type p)]
    (html
      [:div {:class s}
       (if (= :running s)
         (str d " " (t :worker-threads
                      (:running-threads p)
                      (:maximum-threads p)))
         d)])))

(defn- live-data-subscription
  "Add live data subscription details to a hiccup attribute map."
  ([ld-key m]
    (-> m
      (assoc :data-ld-key ld-key
             :data-ld-seq (livedata/get-value ld-key))
      (update-in [:class] (partial str "ld-subscribe ")))))

(defn- render-process-table [process-control]
  (let [processes (processes/status process-control)]
    (html
      [:table (live-data-subscription :process-state
                {:class "grinder-table process-table ld-animate"})
       [:thead
        [:tr
         [:th (t [:agent-name])]
         [:th (t [:worker-name])]
         [:th (t [:process-status :status])]]]
       (if (empty? processes)
         [:tr [:td (t :no-processes)]]
         (for [agent processes]
           [:tr
            [:td (:name agent)]
            [:td]
            [:td (render-process-state :agent agent)]
            (for [worker (:workers agent)]
              [:tr
               [:td]
               [:td (:name worker)]
               [:td (render-process-state :worker worker)]
               ])
            ]))
       ]
      )))

(defn- make-button
  [id]
  (html
    [:button {:class "grinder-button grinder-button-icon" :id id } (t id)]))

(defn- render-processes [{:keys [process-control]}]
  (let [buttons [(make-button :start-processes)
                 (make-button :reset-processes)
                 (make-button :stop-processes)]]
    (html
      [:div {:class "process-controls"} (for [b buttons] b) ]
      (render-process-table process-control))))

(defn- render-data-table [sample-model sample-model-views]
  (let [{:keys [status columns tests totals] :as data}
        (recording/data sample-model sample-model-views :as-text true)]

    (html
      [:div (live-data-subscription :data
              {:class "grinder-table data-table ld-display"})
       [:table
        [:thead
         [:tr
          [:th (t :test-number)]
          [:th (t :test-description)]
          (for [c columns] [:th c])]]
        (for [{:keys [test description statistics]} tests]
          [:tr
           [:th test]
           [:th description]
           (for [s statistics] [:td s])
           ])
        [:tr {:class "total-row"}
         [:th (t :totals)]
         [:th]
         (for [c totals] [:td c])]]
       ])))


(defn- render-data [{:keys [sample-model
                            sample-model-views]}]
  (let [buttons [(make-button :start-recording)
                 (make-button :stop-recording)
                 (make-button :reset-recording)]]
    (html
      [:div {:class "recording-controls"} (for [b buttons] b)]
      (render-data-table sample-model sample-model-views)

      [:div {:id :charts}
       ; position:relative div with no margins so rule position is correct.
       [:div {:id :cubism}]
       [:fieldset
        [:legend (t :chart-statistic)]
        (map-indexed
          (fn [i ^ExpressionView v]
            [:input {:type :radio
                     :name :chart-statistic
                     :value i}
             (.getDisplayName v)])
          (.getExpressionViews (.getIntervalStatisticsView sample-model-views)))
        ]])))

(defn- render-files [{:keys [process-control]}]
  "files")


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

(defn- render-property-group [legend properties defaults]
  [:fieldset
   [:legend legend]
   (for [[d k v]
         (sort
           (map
             (fn [[k v]] [(t k) k v])
             properties))]
     [:div {:class "property-line"}
      [:div {:class "label"}
       (label k d)]
      (render-property k v (defaults k))])
   ])

(defn- render-properties-form [{:keys [properties console-resources]}]
  (form-to
    {:id :properties}
    [:post "./properties" ]

    (let [properties (properties/get-properties properties)
          defaults (properties/default-properties console-resources)
          groups [[(t :file-distribution)
                   #{;:scanDistributionFilesPeriod
                     :distributionDirectory
                     :propertiesFile
                     ; :distributionFileFilterExpression
                     }]
                  [(t :sampling)
                   #{:significantFigures
                     :collectSampleCount
                     :sampleInterval
                     :ignoreSampleCount}]
                  [(t :communication)
                   #{:consoleHost
                     :consolePort
                     :httpHost
                     :httpPort}]
                  [(t :swing-console)
                   #{:externalEditorCommand
                     :externalEditorArguments
                     :saveTotalsWithResults
                     :lookAndFeel
                     :frameBounds
                     :resetConsoleWithProcesses}]]
          ]
      (for [[l ks] groups]
        (render-property-group l (select-keys properties ks) defaults)))

    (submit-button {:id :submit} (t :set-button))))

(defn handle-properties-form [p params]
  (let [expanded (properties/add-missing-boolean-properties params)]
    (properties/set-properties p expanded)
    (redirect-after-post "./properties")))


(defn render-data-summary []
  (html
        [:div {:id :data-summary}
         [:span {:id :description}]
         ]))

(def ^{:const true} sections [
  [:processes {:render-fn #'render-processes}]
   ;            :summary-fn #'render-process-summary}]
  [:data {:render-fn #'render-data
          :summary-fn #'render-data-summary}]
  [:file-distribution {:render-fn #'render-files}]
  [:console-properties {:render-fn #'render-properties-form}]])

(defn- section-url [section]
  (str "/" (name section)))

(defelem content [section body]
  (html
     [:h2 (t section)]
     body))

(defelem page [body]
  (html5
    ;(include-js "lib/jquery-1.9.0.min.js")
    (include-js "lib/jquery-1.9.0.js")
    (include-js "lib/jquery-ui-1.10.0.custom.js")
    (include-js "lib/d3.v3.js")
    (include-js "lib/cubism.v1.js")
    (include-css "lib/jquery-ui-1.10.0.custom.css")

    (include-css "resources/main.css")
    (include-js "resources/grinder-console.js")

    [:div {:id :wrapper}
      [:div {:id :header}
       [:div {:id :title} [:h1 "The Grinder"]]
       [:div {:id :logo} (image "core/logo.png" "Logo")]]

      [:div {:id :sidebar}
       (for [[k {:keys [summary-fn] :as v}] sections]
         [:button {:class "grinder-button replace-content"
                   :id k} (t k)
          (when summary-fn
            [:div {:class "summary"} (summary-fn)])
         ])]

      [:div {:id :content} body]

      [:script (live-data-subscription :sample
                 {:id "sample" :type "text/json"})]
      ]))


(defn- context-url [p]
  "Force hiccup to add its base-url to the given path"
  (to-str (to-uri p)))

(defn create-app
  "Create the Ring routes, given a map of the various console components."
  [{:keys [process-control
           sample-model
           sample-model-views
           properties
           file-distribution
           console-resources]
    :as state}]

  (processes/add-listener :key
    (fn [k _]
      (livedata/push :process-state
        (render-process-table process-control))))

  (recording/add-listener :key
    (fn [k]
      (livedata/push :data
        (render-data-table sample-model sample-model-views))
      (livedata/push :sample
        (assoc
          (recording/data sample-model sample-model-views :sample true)
          :timestamp (System/currentTimeMillis)))))

  (let [translate (make-wrap-with-translation
                    nil
                    ConsoleFoundation/RESOURCE_BUNDLE)]

    (->
      (routes
        (resources "/resources/" {:root "web"})
        (resources "/lib/" {:root "web/lib"})
        (resources "/core/" {:root "net/grinder/console/common/resources"})

        (GET "/poll" [k s]
          (livedata/poll k s))

        (->
          (apply routes
            (for [[section {:keys [render-fn]}] sections :when render-fn]
              (GET (section-url section) []
                (page (content section (apply render-fn [state]))))))
          ; (spy "get")
          translate)

        (context "/content" []
          (->
            (apply routes
              (for [[section {:keys [render-fn]}] sections :when render-fn]
                (GET (section-url section) []
                  (content section (apply render-fn [state])))))
            ringutil/wrap-no-cache
            translate))

        (POST "/properties" {params :form-params}
          (handle-properties-form properties params))

        (context "/action" []
          (POST "/start-processes" []
            (ringutil/json-response
              (processes/workers-start process-control properties {})))

          (POST "/reset-processes" []
            (ringutil/json-response
              (processes/workers-stop process-control)))

          (POST "/stop-processes" []
            (ringutil/json-response
              (processes/agents-stop process-control)))

          (POST "/start-recording" []
            (ringutil/json-response
              (recording/start sample-model)))

          (POST "/stop-recording" []
            (ringutil/json-response
              (recording/stop sample-model)))

          (POST "/reset-recording" []
            (ringutil/json-response
              (recording/zero sample-model))))

        (GET "/" []
          (page (content (t :about)
                  (.getStringFromFile console-resources "about.text" true))))

        (not-found "Whoop!!!"))

      wrap-base-url
      compojure.handler/api)))
