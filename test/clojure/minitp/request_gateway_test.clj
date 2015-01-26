(ns minitp.request-gateway-test
  (:use [com.ohua.compile])
  (:require [clojure.test :refer :all]
            [minitp.request-gateway :as req-gw]))

(deftest run-requester-gateway-test [] 
  (println "Starting the request gateway.")
  (req-gw/request "./resources/settings-req"
                  "127.0.0.1" 9090
                  "127.0.0.1" 9080))