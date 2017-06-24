(ns zcenter.handler
  (:require [compojure.core :refer [defroutes]]
            ;[hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [noir.util.middleware :as noir-middleware]
            [zcenter.routes.home :refer [home-routes]]
            ))

(defn init []
  (println "zen is starting"))

(defn destroy []
  (println "zen is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (noir-middleware/app-handler
       [
        home-routes
        app-routes
        ]))
