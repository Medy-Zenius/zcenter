(ns ssc.models.message
  (:require
      [compojure.core :refer :all]
      [ssc.views.layout :as layout]
      [noir.session :as session]
      [clj-time [format :as timef] [coerce :as timec]]
      [ssc.models.db :as db]
  ))

(defn read-message [zen act]
  (layout/render "message/pesan-wali.html"
      {
       :pesans (db/get-data (str "select tanggal,isi,status from pesan where notes='" zen "'
                                 order by tanggal DESC LIMIT 20") 2)
       :idwali (:idwali (db/get-data (str "select idwali from datawali where notes='" zen "'") 1))
       :action act
       }))

(defn send-message [zen isi id]
  (try (db/insert-data "pesan" {:idtutor id :notes zen :isi isi :status "Z"
                          :tanggal (java.sql.Timestamp. (.getTime (java.util.Date.)))})
       (layout/render "pesan.html" {:pesan "Pesan kepada mentor sudah dikirim"})
  (catch Exception ex
       (layout/render "pesan.html" {:pesan (str "Gagal Kirim Pesan ! error: " ex)}))))

(defn handle-wali-pesan-buat [no id]
  (layout/render "message/pesan-buat.html"
          {:notes no
           }))

(defn wali-kirim-pesan [no isi id]
  (try
      (db/insert-data "pesan" {:idtutor id :notes no :isi isi :status "M"
                          :tanggal (java.sql.Timestamp. (.getTime (java.util.Date.)))})
      (layout/render "pesan.html"
              {:pesan (str "Pesan kepada siswa dengan notes " no " sudah dikirim")})
  (catch Exception ex
       (layout/render "pesan.html" {:pesan (str "Gagal Kirim Pesan ! error: " ex)}))))

(defn handle-wali-pesan-lihat [no id]
  (layout/render "message/list-pesan-notes.html"
          {:notes no
           :datasis (db/get-data (str "select name,level,kocab from students where notes='" no "'") 2)
           :pesans (db/get-data (str "select tanggal,isi,status from pesan where notes='" no "'
                                     order by tanggal DESC LIMIT 20") 2)
           }))
