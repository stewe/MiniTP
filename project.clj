(defproject minitp "0.1.0-SNAPSHOT"
  :description "This is an example project that shows how one can create a simple trading platform with Ohua."
  :url "https://bitbucket.org/stefan_we/minitp"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ohua/ohua "0.2.1"]
                 [org.zeromq/jzmq "3.1.0"]
                 [quickfixj/quickfixj-core "1.5.3"]
                 [quickfixj/quickfixj-fixt11 "1.5.3"]
                 [quickfixj/quickfixj-msg-fix50 "1.5.3"]
                 [org.slf4j/slf4j-api "1.6.3"]
                 [org.slf4j/slf4j-jdk14 "1.6.3"]
                 [org.apache.mina/mina-core "1.1.7"]
                 ]
  ; quickfixj is not in the standard maven repo.
  :repositories [["marketcetera" "http://repo.marketcetera.org/maven/"]]
  
  :main minitp.core
  :source-paths ["src" "src/clojure"]
  :java-source-paths ["src/java"]
  :test-paths ["test" "test/clojure"]
  
  ; this is important in order to produce enough information for our analysis to work properly.
  ; the -g option is important because it includes the local variable table into the byte code
  ; which we use to assign names to parameters.
  :javac-options ["-target" "1.8" "-source" "1.8" "-Xlint:-options" "-g"]
  :jvm-opts ["-Djava.library.path=/usr/local/lib"]
)
