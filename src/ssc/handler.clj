(ns ssc.handler
  (:require [compojure.core :refer [defroutes]]
            ;[hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [noir.util.middleware :as noir-middleware]
            ;[ring.middleware.anti-forgery :refer :all]
            ;[ring.middleware.session :refer :all]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ssc.routes.home :refer [home-routes]]
            [ssc.routes.student :refer [student-routes]]
            [ssc.routes.fo :refer [fo-routes]]
            [ssc.routes.keu :refer [keu-routes]]
            [ssc.routes.admin :refer [admin-routes]]
            [ssc.routes.akademik :refer [akademik-routes]]
            [ssc.routes.mentor :refer [mentor-routes]]
            [ssc.routes.super :refer [super-routes]]
            [ssc.routes.owner :refer [owner-routes]]
            ))

(defn init []
  (println "zen is starting"))

(defn destroy []
  (println "zen is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (noir-middleware/app-handler
       [owner-routes
        super-routes
        mentor-routes
        akademik-routes
        admin-routes
        keu-routes
        fo-routes
        home-routes
        student-routes
        app-routes
        ]
        :ring-defaults (assoc-in site-defaults [:security :anti-forgery] false)
        ))


