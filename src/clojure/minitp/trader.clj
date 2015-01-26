(ns minitp.trader
  (:use [com.ohua.compile]
        [com.ohua.logging]
        [minitp.core])
  (:require [com.ohua.resource :as res])
  (:import [java.util HashMap ArrayList]
           [java.util.concurrent.atomic AtomicReference]
           [org.zeromq ZMQ])
  )

(ohua :import [com.minitp.operators
               com.minitp.messaging
               com.minitp.logging])

(def trading (new AtomicReference true))
(def provs (new java.util.HashMap))

; logs
(def log-it false)
(def requests (new java.util.ArrayList))
(def prices (new java.util.ArrayList))
(def orders (new java.util.ArrayList))
(def reports (new java.util.ArrayList))
(def configs (new java.util.ArrayList))

(defn trading-node
  "Runs the trading node."
  [zmq-trad-addr zmq-trad-port zmq-req-addr zmq-req-port zmq-prov-addr zmq-prov-port]
  ; Ohua debug and runtime configuration
  (set-debug-rt-config "trading")
  
  ; create a ZeroMQ resource for receiving
  (def zmq-resource-rcv (res/resource "0mqT"
                                      com.ohua.distributed.connection.resource.ZeroMQResource
                                      {"protocol" "tcp", "address" zmq-trad-addr, "port" (str zmq-trad-port)}))
  
  ; create a ZeroMQ resource for sending to request-gateway
  (def zmq-resource-send-req (res/resource "0mqR"
                                           com.ohua.distributed.connection.resource.ZeroMQResource
                                           {"protocol" "tcp", "address" zmq-req-addr, "port" (str zmq-req-port)}))
  
  ; create a ZeroMQ resource for sending to provider-gateway
  (def zmq-resource-send-prov (res/resource "0mqP"
                                            com.ohua.distributed.connection.resource.ZeroMQResource
                                            {"protocol" "tcp", "address" zmq-prov-addr, "port" (str zmq-prov-port)}))
  
  (let [zmq-trad (res/cnn zmq-resource-rcv (into-array Object [org.zeromq.ZMQ/PULL]))
        zmq-req1 (res/cnn zmq-resource-send-req (into-array Object [org.zeromq.ZMQ/PUSH]))
        zmq-req2 (res/cnn zmq-resource-send-req (into-array Object [org.zeromq.ZMQ/PUSH]))
        zmq-prov1 (res/cnn zmq-resource-send-prov (into-array Object [org.zeromq.ZMQ/PUSH]))
        zmq-prov2 (res/cnn zmq-resource-send-prov (into-array Object [org.zeromq.ZMQ/PUSH]))]
    
    ;  Ohua code starts here
    (com.ohua.logging/enable-compilation-logging )
    (ohua
      (let
        ; receive requests
        [req (deserialize-minitp (accept-zeromq zmq-trad))
         [type content]  (parse-request-type req)]
        (cond
          ; en- /disable trading, requested by the admin
          (= "admin" type) (log-entry 
                             (build-admin-log-entry 
                               (set-trading 
                                 (parse-admin-request content) 
                                 trading)) 
                             configs log-it)
          
          ; forward the received rfs to the registered provider
          (= "rfs" type) (let [[id time product requester provider] (parse-rfs-request content)]
                           (log-entry (build-rfs-log-entry id time product requester provider) requests log-it)
                           (send-zeromq
                             (serialize-minitp (build-rfs-msg id time product requester provider)) 
                             zmq-prov1)
                           )
          ; send the received offer to the requester
          (= "price" type) (let [price (parse-price-info content)
                                 ]
                             (log-entry (build-price-log-entry price) prices log-it)
                             (send-zeromq
                               (serialize-minitp (build-price-msg price))
                               zmq-req1)
                             )
          ; forward the received order to the provider
          (= "order" type) (let [[requester provider order] (parse-order-request content)]
                             (log-entry (build-order-log-entry order) orders log-it)
                             (send-zeromq
                               (serialize-minitp (build-order-msg order requester provider))
                               zmq-prov2)
                             )
          ; send the report to the requester
          (= "report" type) (let [report (pipe content)]
                              (log-entry (build-report-log-entry report) reports log-it)
                              (send-zeromq
                                (serialize-minitp (add-type-to-list report "report"))
                                zmq-req2)
                              )
          ))
      
      ; Ohua runtime configuration
      (:run-with-config (configure))
      )
    )
  )