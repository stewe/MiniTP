(ns minitp.core)

(defmacro set-debug-rt-config
  "Sets configurations for debugging and the runtime execution."
  [name]
  (set! (. com.ohua.engine.utils.GraphVisualizer PRINT_FLOW_GRAPH) (str "test/" name "-flow"))
  (set! (. com.ohua.engine.utils.GraphVisualizer PRINT_SECTION_GRAPH) (str "test/" name "-flow-sections")))

(defn configure 
  "Configuration of the Ohua runtime system."
  []
  (doto (new com.ohua.engine.RuntimeProcessConfiguration)
    (.setProperties (doto (new java.util.Properties)
                      ; uncomment to run deterministically in single-threaded mode (for debugging purposes)
                      (.setProperty "execution-mode" (.name (com.ohua.engine.RuntimeProcessConfiguration$Parallelism/MULTI_THREADED)))
                      
                      ; resource constraints on the application
                      (.setProperty "inter-section-arc-boundary" "100")
                      (.setProperty "inner-section-arc-boundary" "0")
                      
                      ; parallelism degree
                      (.setProperty "core-thread-pool-size" "20")
                      (.setProperty "max-thread-pool-size" "20")
                      
                      ; quantas to guarantee fairness and processing requirements
                      (.setProperty "operator-quanta" "100") ; this quanta refers to the actions of a single op
                      (.setProperty "scheduling-quanta" "200000") ; this quanta refers to the scheduling operations of the operator scheduler
                      (.setProperty "arc-activation" "1") ; prefer latency 
                      
                      (.setProperty "data-format" "com.ohua.data.model.map.MapDataFormat")
                      )
      ))
  )