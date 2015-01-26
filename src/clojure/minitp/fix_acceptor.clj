(ns minitp.fix-acceptor
  (:import [com.minitp.fixacceptor FIXAcceptor]))

;;;
;;; An application to run the FIX acceptor. 
;;;

(defn start
  "Runs the FIX acceptor. Quit qith 'q'!"
  [fix-config]
  (println "Enter 'q' to quit:")
  (def acc (new FIXAcceptor "./resources/settings-acceptor"))
  (.start acc)
  (loop [in ""]
    (if (= in "q")
      (.stop acc)
      (recur (read-line))))
  )