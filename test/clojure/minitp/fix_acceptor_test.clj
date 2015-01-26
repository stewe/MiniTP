(ns minitp.fix-acceptor-test
  (:require [clojure.test :refer :all]
            [minitp.fix-acceptor :as acc]))

(deftest single-trade-test []
  (println "Starting the FIX acceptor.")
  (acc/start "./resources/settings-acceptor"))
  