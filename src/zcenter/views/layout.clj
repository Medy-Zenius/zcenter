(ns zcenter.views.layout
  (:require ;[hiccup.page :refer [html5 include-css include-js]]
            ;[hiccup.element :refer [link-to]]
            ;[hiccup.form :refer :all]
            [noir.session :as session]
            [selmer.parser :as parser]
            [ring.util.response :refer [content-type response]]
            [compojure.response :refer [Renderable]]))


(def template-folder "zcenter/views/templates/")

(defn utf-8-response [html]
    (content-type (response html) "text/html; charset=utf-8"))


(deftype RenderablePage [template params]
  Renderable
  (render [this request]
    (->> (assoc params
          :context (:context request)
          :user (session/get :id)
          :status (session/get :status)
          :pass (session/get :pass)
          :scode (session/get :branch))
        (parser/render-file (str template-folder template))
        utf-8-response)))

(defn render [template & [params]]
    (RenderablePage. template params))
