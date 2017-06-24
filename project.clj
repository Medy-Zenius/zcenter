(defproject zcenter "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [lib-noir "0.9.9"]
                 [selmer "1.10.7"]
                 [ring-server "0.4.0"]
                 ]
  :repl-options {:init-ns zcenter.repl
                 :timeout 120000}
  :jvm-opts ["-Xmx512M"]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler zcenter.handler/app
         :init zcenter.handler/init
         :destroy zcenter.handler/destroy
         :port 3000}
  :aot :all
  :profile
  {:production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}}
  :dev {:dependencies [[ring/ring-devel "1.6.1"]
                        [ring-mock "0.3.0"]]}})
