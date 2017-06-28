(ns ssc.models.preference
  (:require
      ;[hiccup.form :refer :all]
      [compojure.core :refer :all]
      ;[zen.routes.home :refer :all]
      [ssc.views.layout :as layout]
      [noir.session :as session]
      ;[noir.validation :as vali]
      ;[noir.response :as resp]
      ;[noir.util.crypt :as crypt]
      ;[clj-time [format :as timef] [coerce :as timec]]
      [ssc.models.db :as db]
      ;[ssc.models.presence :as presence]
      ;[ssc.models.register :as register]
  )
)

(defn handle-add-ref [lbl act]
  (layout/render "info/add-ref.html"
      {:label lbl :action act}))

(defn add-ref [nama tbl]
  (try
     (db/insert-data tbl {:name nama})
      (layout/render "pesan.html" {:pesan (str "Berhasil Add Data dengan Nama " nama)})
    (catch Exception ex
                  (layout/render "pesan.html" {:pesan (str "Gagal Add Data error: " ex)}))))

(defn handle-list-namasek [cab]
  (layout/render "info/list-namasek.html"
     {:data (db/get-data (str "select nama from namasek where kocab='" cab "' order by nama asc") 2)}))

(defn input-namasek []
  (layout/render "info/input-namasek.html"))

(defn handle-add-namasek [nama cab]
  (try
    (db/insert-data "namasek" {:nama nama :kocab cab})
      (layout/render "pesan.html" {:pesan (str "Berhasil Add Data dengan Nama " nama)})
    (catch Exception ex
                  (layout/render "pesan.html" {:pesan (str "Gagal Add Data error: " ex)}))))
