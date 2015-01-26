(ns minitp.request-gateway
  (:use [com.ohua.compile]
        [minitp.core])
  (:require [com.ohua.resource :as res])
  (:import [com.minitp.adapters FIXResource]))

(ohua :import [com.minitp.operators
               com.minitp.messaging
               com.minitp.adapters
               com.minitp.logging])

;;;
;;; The gateway algorithm for all requests:
;;; This gateway receives FIX messages and speaks with the trading platform.
;;;

(defn request
  "Sends a request for a certain product, handles the according offer, orders and stores the report."
  [fix-config zmq-gw-addr zmq-gw-port zmq-trad-addr zmq-trad-port]
  ; Ohua debug configuration
  (set-debug-rt-config "request-gateway")
  
  ; create a FIX resource 
  (def fix-resource (res/resource "fix" com.minitp.adapters.FIXResource
                                  {"settings" fix-config}))
  ; create a ZeroMQ resource for receiving
  (def zmq-resource-recv (res/resource "0mqR"
                                       com.ohua.distributed.connection.resource.ZeroMQResource
                                       {"protocol" "tcp", "address" zmq-gw-addr, "port" (str zmq-gw-port)}))
  ; create a ZeroMQ resource for sending to trader
  (def zmq-resource-send (res/resource "0mqT"
                                       com.ohua.distributed.connection.resource.ZeroMQResource
                                       {"protocol" "tcp", "address" zmq-trad-addr, "port" (str zmq-trad-port)}))
  
  ; get a connection to receive / send messages from
  (let [fix-cnn-recv (res/cnn fix-resource nil)
        fix-cnn-send (res/cnn fix-resource nil)
        zmq-req (res/cnn zmq-resource-recv (into-array Object [org.zeromq.ZMQ/PULL]))
        zmq-trad (res/cnn zmq-resource-send (into-array Object [org.zeromq.ZMQ/PUSH]))]
    (ohua
      ;      Ohua algorithm
      (let []
        ( ; receive requests from the requester via FIX and forward them via ZeroMQ
          send-zeromq (serialize-minitp (parse-fix (accept-fix fix-cnn-recv))) zmq-trad)
      (let
          ; receive responses from the trader via ZeroMQ and forward them via FIX
          [req (deserialize-minitp (accept-zeromq zmq-req))
           [type content]  (parse-request-type req)]
          (send-fix
            (cond
              (= "price" type) (let [[id time rfsID buyPrice sellPrice provider requester] (parse-price-info content)]
                                 (build-fix-price id time rfsID buyPrice sellPrice provider requester "MINITP-ACCEPTOR")
                                 )
              (= "report" type) (let [report (pipe content)]
                                  (build-fix-report report "MINITP-ACCEPTOR")
                                  ))
            fix-cnn-send))
      )
      
      ; Ohua runtime configuration
      (:run-with-config (configure))
      )
    )
  
  )
