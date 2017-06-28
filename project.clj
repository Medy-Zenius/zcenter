(defproject zcenter "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-anti-forgery "1.1.0"]
                 [postgresql/postgresql "9.1-901.jdbc4"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [lib-noir "0.9.9"]
                 ;[lib-noir "0.7.6"]
                 [selmer "1.10.7"]
                 [ring-server "0.4.0"]
                 [org.clojure/data.json "0.2.6"]
                 [date-clj "1.0.1"]
                 [clj-pdf "2.2.18"]
                 ]
  :repl-options {:init-ns ssc.repl
                 :timeout 120000}
  :jvm-opts ["-Xmx512M"]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler ssc.handler/app
         :init ssc.handler/init
         :destroy ssc.handler/destroy
         :port 3000}
  :aot :all
  :profile
  {:production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}}
  :dev {:dependencies [[ring/ring-devel "1.6.1"]
                        [ring-mock "0.3.0"]]}})
