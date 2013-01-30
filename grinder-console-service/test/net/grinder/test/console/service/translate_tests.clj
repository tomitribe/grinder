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

(ns net.grinder.test.console.service.translate-tests
  "Unit tests for net.grinder.console.service.translate."
  (:use [clojure.test])
  (:require [net.grinder.console.service.translate :as translate])
  (:require [taoensso.tower :as tower]))

(def tb "net.grinder.test.console.service.TestBundle")

(deftest to-resource-bundle-keys
  (are [x k] (= x (#'translate/to-resource-bundle-keys k))
    ["foo.label"] :foo
    ["foo.label"] :blah/foo
    ["x.label"] :x
    ))

(deftest resource-bundle-result
  (are [x k] (= x (#'translate/resource-bundle-result k))
    nil :hello)

  (binding
    [translate/*resource-bundle-name* tb]
    (are [x k] (= x (#'translate/resource-bundle-result k))
      "Hello World" :hello
      "Goodbye" :goodbye
      nil :blah
      ))

  (binding
    [translate/*resource-bundle-name* tb
     tower/*Locale* (java.util.Locale. "tr")]
    (are [x k] (= x (#'translate/resource-bundle-result k))
      "Merhaba Dünya" :hello
      "Goodbye" :goodbye
      nil :blah
      )))

(deftest with-resource-bundle
  (let [x {:k "v"}]
    (translate/with-resource-bundle x
      (is (= x translate/*resource-bundle-name*))))

  (is (nil? translate/*resource-bundle-name*))

  (translate/with-resource-bundle tb
    (are [x k] (= x (#'translate/resource-bundle-result k))
      "Hello World" :hello)))

(defn- load-test-tower-config
  []
  (tower/set-config! [:dictionary] {}) ; Discard any configuration.
  (tower/set-config! [:log-missing-translation!-fn] (fn [x])) ; Don't log.
  (tower/load-dictionary-from-map-resource!
    "net/grinder/test/console/service/testtranslations.clj"))

(deftest test-t
  (load-test-tower-config)
  (translate/with-resource-bundle tb
    (tower/with-locale :en
      (tower/with-scope :test
        (are [x k] (= x (translate/t k))
          "blah" :foo
          "blah" [:bah :foo]
          "Hello World" :hello
          "Hello World" [:hello]
          "Hello World" [:x :hello :y]
          "missing for en" :not-there
          )
        (is "Hi World" (translate/t :hi "World"))))))

(deftest test-make-wrap-with-translation
  (load-test-tower-config)
  (let [mw (translate/make-wrap-with-translation
             :test
             tb)
        request {}
        tr-request {:params {:locale "tr"}}]
    ((mw (fn [r]
          (is (= r request))
          (are [x k] (= x (translate/t k))
          "blah" :foo
          "Hello World" :hello)
          ))
      request)

    ((mw (fn [r]
          (is (= r tr-request))
          (are [x k] (= x (translate/t k))
          "blah" :foo
          "Merhaba Dünya" :hello)
          ))
      tr-request)
    ))
