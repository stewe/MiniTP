(ns minitp.trader-test
  (:use [com.ohua.compile])
  (:require [clojure.test :refer :all]
            [minitp.trader :as tra]))

(deftest single-trade-test []
  (println "Starting the trading node.")
  (tra/trading-node "127.0.0.1" 9080
                    "127.0.0.1" 9090
                    "127.0.0.1" 9095))
  