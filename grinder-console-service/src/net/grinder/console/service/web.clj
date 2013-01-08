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


(defn render-properties-form [p]
  (page
    (form-to
      {:id :properties}
      [:post "./properties" ]
      [:hgroup
       [:h2
        "Console Properties"
       ]
       ]

      [:fieldset
       [:legend "General Settings"]

       (for [[k v ] (properties/get-properties p)]
         [:div
           (label k k)
           (text-field {:placeholder "default"} k v)])
       ]
      (submit-button {:id "submit"} "Save"))))

(defn handle-properties-form [params]
  (println params)
  {:body (str "stored" (params :task))})

(defn create-app
  "Create the Ring routes, given a map of the various console components."
  [{:keys [process-control
           sample-model
           sample-model-views
           properties
           file-distribution]}]
  (->
    (routes
      (resources "/resources/" {:root "static"})
      (resources "/core/" {:root "net/grinder/console/common/resources"})
      (GET "/properties" [] (render-properties-form properties))
      (POST "/properties" {params :params} (handle-properties-form params))
      (not-found "Whoop!!!")
      )
    compojure.handler/api))
