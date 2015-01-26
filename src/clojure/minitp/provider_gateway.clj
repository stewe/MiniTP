(ns minitp.provider-gateway
  (:use [com.ohua.compile]
        [minitp.core])
  (:require [com.ohua.resource :as res])
  (:import [com.minitp.adapters FIXResource]))

(ohua :import [com.minitp.operators
               com.minitp.messaging
               com.minitp.adapters
               com.minitp.logging])


;;;
;;; The gateway for the communication between the trading platform and the providers. 
;;; As opposed to the requester gateway, this is a bi-directional communication:
;;; The providers register via FIX at the provider gateway while it receives requests from the trading platform.
;;; The communication with the provider is all FIX.  
;;;

(defn provide
  "Runs the provider gateway."
  [fix-config zmq-gw-addr zmq-gw-port zmq-trad-addr zmq-trad-port]
  ; Ohua debug configuration
  (set-debug-rt-config "provider-gateway")
  
  ; create a FIX resource 
  (def fix-resource (res/resource "fix" com.minitp.adapters.FIXResource
                                  {"settings" fix-config}))
  ; create a ZeroMQ resource for receiving
  (def zmq-resource-recv (res/resource "0mqP"
                                       com.ohua.distributed.connection.resource.ZeroMQResource
                                       {"protocol" "tcp", "address" zmq-gw-addr, "port" (str zmq-gw-port)}))
  ; create a ZeroMQ resource for sending to trader
  (def zmq-resource-send (res/resource "0mqT"
                                       com.ohua.distributed.connection.resource.ZeroMQResource
                                       {"protocol" "tcp", "address" zmq-trad-addr, "port" (str zmq-trad-port)}))
  
  ; get a connection to receive messages from
  (let [fix-cnn-recv (res/cnn fix-resource nil)
        fix-cnn-send (res/cnn fix-resource nil)
        zmq-prov (res/cnn zmq-resource-recv (into-array Object [org.zeromq.ZMQ/PULL]))
        zmq-trad (res/cnn zmq-resource-send (into-array Object [org.zeromq.ZMQ/PUSH]))] 
    (ohua
      ; Ohua algorithm
      (let
        []
        ( ;receive responses from the provider via FIX and forward them via ZeroMQ
          send-zeromq (serialize-minitp (parse-fix (accept-fix fix-cnn-recv))) zmq-trad)
        (let
          ; receive requests from the trader via ZeroMQ and forward them via FIX
          [req (deserialize-minitp (accept-zeromq zmq-prov))
           [type content]  (parse-request-type req)]
          (send-fix
            (cond
              (= "rfs" type) (let [[id time product requester provider] (parse-rfs-request content)]
                               (build-fix-rfs id time product requester provider "MINITP-ACCEPTOR"))
              (= "order" type) (let [[requester provider order] (parse-order-request content)] ;;; TODO use the same approach for all types, replace map -> destructure for clarity!
                                 (build-fix-order order "MINITP-ACCEPTOR"))
              )
            fix-cnn-send)
          )
        )
      
      
      
      ; Ohua runtime configuration
      (:run-with-config (configure))
      )
    )
  )
  