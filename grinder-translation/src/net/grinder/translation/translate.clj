; Copyright (C) 2013 Philip Aston
; All rights reserved.
;
; This file is part of The Grinder software distribution. Refer to
; the file LICENSE which is part of The Grinder distribution for
; licensing details. The Grinder distribution is available on the
; Internet at http://grinder.sourceforge.net/
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

(ns net.grinder.translation.translate
  "Internationalisation."
  (:use [taoensso.tower.ring :only [make-wrap-i18n-middleware]])
  (:require [taoensso.tower :as tower])
  (:import
    [java.util MissingResourceException ResourceBundle]
    net.grinder.translation.Translatable))

(def ^:dynamic *resource-bundle-name* nil)

(tower/load-dictionary-from-map-resource! "translations.clj")

(defn- to-resource-bundle-keys
  "Translate tower format keys to a list of potential resource bundle keys"
  [k]
  (let [kw-name (name k)]
    [
     (str kw-name ".label")
     (str kw-name ".text")
     ]))

(defn- resource-bundle-result
  "Translate the given key using `to-resource-bundle-keys` and find its
   value using the current resource bundle and locale.
   Return `nil` if not found, or `*resource-bundle-name*` is nil."
  [k]
  (when-let [bundle-name *resource-bundle-name*]
    (let [resources (ResourceBundle/getBundle
                      *resource-bundle-name*
                      tower/*Locale*)
          g (fn [x]
              (try (.getString resources x)
                (catch MissingResourceException e)))]
      (some g (to-resource-bundle-keys k)))))


(defmacro with-resource-bundle
  "Bind the resource bundle name."
  [resource-bundle-name & body]
  `(binding [*resource-bundle-name* ~resource-bundle-name]
     ~@body))

(defmulti tkeys
  "Convert an object into a keyword to use for translation."
  class)

(defmethod tkeys clojure.lang.Keyword [k] k)

(defmethod tkeys java.lang.String [^String s] (keyword s))

(defmethod tkeys Translatable [^Translatable t]
  (keyword (.getTranslationKey t)))

(defn t
  "Extends `tower/t` so that if the key is missing, the last part of the
   key is looked up using the Java resource bundle and the current tower
   locale.

   This method is more relaxed than `tower/t` in its interpretation of
   the supplied keys. As well as keywords, it accepts Strings, and
   implementations of `net.grinder.common.Translatable`. See `tkeys`."
  ([k-or-ks & interpolation-args]
    (when-let [pattern (t k-or-ks)]
       (apply tower/format-msg pattern interpolation-args)))

  ([k-or-ks]
    (let [kchoices* (if (vector? k-or-ks) k-or-ks [k-or-ks])
          kchoices  (apply vector (map tkeys kchoices*))]
      (or
        (tower/t (conj kchoices nil))
        (some resource-bundle-result kchoices)
        (tower/t kchoices)
        ))))

(defn make-wrap-with-translation
  "Returns Ring middleware that binds the translation context.
   The optional parameters control the tower scope, and the name of
   the Java resource bundle to be used if the translation is not
   found in the tower dictionary."
  [& [tower-scope resource-bundle-name]]

  (comp
    (make-wrap-i18n-middleware)
    (fn [handler]
      (fn [request]
        (with-resource-bundle resource-bundle-name
          (tower/with-scope tower-scope
            (handler request)))))))
