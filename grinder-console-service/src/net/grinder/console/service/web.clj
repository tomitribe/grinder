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
        [hiccup core def element form page
         [middleware :only [wrap-base-url]]
         [util :only [to-str to-uri]]]
        [net.grinder.console.service
         [translate :only [t make-wrap-with-translation]]]
        [org.httpkit.server :only [async-response]]
        [ring.middleware.format-response :only [wrap-json-response]]
        [ring.util [response :only [redirect redirect-after-post response]]])
  (:require
    [cheshire.core :as json]
    [compojure.handler]
    [clojure.tools [logging :as log]]
    [net.grinder.console.model [files :as files]
                               [processes :as processes]
                               [properties :as properties]
                               [recording :as recording]]
    [taoensso.tower :as tower])
  (:import
    java.awt.Rectangle
    net.grinder.console.ConsoleFoundation))

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

(defn- render-process-table [process-control & [test]]
  (let [processes (processes/status process-control)]
    (html
      [:table {:id :process-state :class "process-table live-data"}
       [:caption (or test (t :running-processes))]
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


(defn- render-processes [{:keys [process-control]}]
  (let [buttons [
          (image {} "core/start-processes.png" (t :start-processes))
          (image {} "core/reset-processes.png" (t :reset-processes))
          (image {} "core/stop-processes.png" (t :stop-processes))
         ]]
    (html
      [:div {:class "process-controls"}
       (for [b buttons] b)
       ]
      (render-process-table process-control))))


(defn- render-data [{:keys [process-control]}]
  "data")

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


(def ^{:const true} sections [
  [:processes {:url "/processes"
               :render-fn #'render-processes}]
  [:data {:url "/data"
          :render-fn #'render-data}]
  [:file-distribution {:url "/files"
                       :render-fn #'render-files}]
  [:console-properties {:url "/properties"
                        :render-fn #'render-properties-form}]
  ])

(defelem page [section body]
  (html5
    (include-css "resources/main.css")
    ;(include-js "resources/jquery-1.9.0.min.js")
    (include-js "resources/jquery-1.9.0.js")
    (include-js "resources/grinder-console.js")
    [:div {:id :wrapper}
      [:div {:id :header}
       [:div {:id :title} [:h1 "The Grinder"]]
       [:div {:id :logo} (image "core/logo.png" "Logo")]]

      [:div {:id :sidebar}
       (for [[k v] sections]
         (link-to (:url v) (t k)))
       ]
      [:div {:id :content}
       [:h2 (t section)]
       (html body)]]))

(defn- spy [handler spyname]
  (fn [request]
    (let [response (handler request)]
      (log/debugf
        (str "--------------> %s >----------------%n"
          "request: %s\nresponse:%s%n"
          "--------------< %s <-----------------%n")
        spyname request response spyname)
      response)))

(defn- context-url [p]
  "Force hiccup to add its base-url to the given path"
  (to-str (to-uri p)))

; Live-data-id -> set of {:client, :seq}
(def clients (atom {}))

(defn- register-client
  [client data-key sequence]
  (dosync
    (swap! clients
      #(merge-with clojure.set/union %
         {data-key #{{:seq sequence :client client}}})))
  (log/debugf "poll: %s %s %s -> %s"
               data-key
               sequence
               client
               @clients))

(defn- push-data
  [data-key html-data]
  (let [cs (dosync (let [cs (@clients data-key)]  ; TODO use keyword as key to map
                     (swap! clients dissoc "process-state")
                     cs))]
    (doseq [c cs]
      (let [r (
                ; Ring middleware is incompatible with httpkit callback API.
                json/generate-string

                ; TODO Use some global seq instead
                {:html html-data :seq 99})]
        (println "Responding to " c " with" r)
        ((:client c) (response r))))))


(defn create-app
  "Create the Ring routes, given a map of the various console components."
  [{:keys [process-control
           sample-model
           sample-model-views
           properties
           file-distribution
           console-resources]
    :as state}]

  (tower/load-dictionary-from-map-resource! "translations.clj")

  (let [translate (make-wrap-with-translation
                    nil
                    ConsoleFoundation/RESOURCE_BUNDLE)]

    (->
      (routes
        (resources "/resources/" {:root "static"})
        (resources "/core/" {:root "net/grinder/console/common/resources"})

        (GET "/poll" [k s]
          (async-response client (register-client client k s)))

        (GET "/test" [m]
          (push-data "process-state" (render-process-table process-control m))
          (str "Sent data"))

        (->
          (apply routes
            (for [[section {:keys [url render-fn]}] sections :when render-fn]
              (GET url [] (page section (apply render-fn [state])))))
          ; (spy "get")
          translate)

        (POST "/properties" {params :form-params}
          (handle-properties-form properties params))

        (GET "/" [] (redirect (context-url (:url (second (first sections))))))

        (not-found "Whoop!!!"))

      wrap-base-url
      compojure.handler/api)))
