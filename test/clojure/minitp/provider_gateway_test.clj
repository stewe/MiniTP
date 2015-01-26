(ns minitp.provider-gateway-test
  (:use [com.ohua.compile])
  (:require [clojure.test :refer :all]
            [minitp.provider-gateway :as prov]))

(deftest single-provider-test
  (println "Starting the provider.")
    (Thread/sleep 5000)
    (prov/provide "./resources/settings-prov"
                  "127.0.0.1" 9095
                  "127.0.0.1" 9080))
      
