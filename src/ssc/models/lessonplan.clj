(ns ssc.models.lessonplan
  (:require
      [compojure.core :refer :all]
      [ssc.views.layout :as layout]
      [noir.session :as session]
      [clj-time [format :as timef] [coerce :as timec]]
      [ssc.models.db :as db]
  ))

(defn search-lp [act ket]
  (layout/render "lessonplan/search-lp.html"
  {
   :kodekelas (db/get-data "select * from kodekelaslp order by kode asc" 2)
   :kodekurikulum (db/get-data "select * from kodekuri order by kode asc" 2)
   :bidangstudi (db/get-data "select * from bidangstudi order by kode asc" 2)
   :action act
   :keterangan ket}))

(defn search-bab-lp [act ket ke ku bi]
  (layout/render "lessonplan/search-bab-lp.html"
  {:action act :keterangan ket
   :babs (db/get-data (str "select kode,keterangan from lessonplan where kode like '" ke ku bi "_' order by kode ASC") 2)
   }))

(defn view-butir [ko]
  (layout/render "lessonplan/view-butir-lp.html"
      {
       :butirs (db/get-data (str "select keterangan,kodekonten,proset from lessonplan where kode LIKE '" ko "%' order by kode ASC") 2)
       }))

(defn handle-new-lp [ke ku bi]
  (layout/render "lessonplan/input-lp.html"
      {
       :kodekelas ke :kodekuri ku :kodebidang bi
       :kelas (:keterangan (db/get-data (str "select keterangan from kodekelaslp where kode='" ke "'") 1))
       :kurikulum (:keterangan (db/get-data (str "select keterangan from kodekuri where kode='" ku"'") 1))
       :bidang (:keterangan (db/get-data (str "select keterangan from bidangstudi where kode='" bi "'") 1))
       }))

(defn handle-input-lp [ke ku bi ko des kk]
  (try
     (db/insert-data "lessonplan" {:kode (str ke ku bi ko) :keterangan des :kodekonten kk})
     (handle-new-lp ke ku bi)
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Gagal Input Lessonplan error: " ex)}))))

(defn handle-view-edit-lp [ko act ket]
  (layout/render "lessonplan/view-edit-lp.html"
      {:action act
       :keterangan ket
       :butirs (db/get-data (str "select * from lessonplan where kode LIKE '" ko "%' order by kode ASC") 2)
       :kode ko
       }))

(defn handle-edit-lp [nomer]
  (layout/render "lessonplan/edit-lp.html"
       {
        :butir (db/get-data (str "select * from lessonplan where nomer='" nomer "'") 2)
        }))

(defn handle-update-lp [no ko ke kk ps dp]
  (try
    (db/update-data "lessonplan" (str "nomer='" no "'")
      {:kode ko :keterangan ke :kodekonten kk :proset ps :dp dp
       })
    (handle-view-edit-lp (subs ko 0 6) "/edit-lp" "Edit LP")
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Gagal Update Lessonplan error: " ex)}))))

(defn handle-delete-lp [nomer ko]
   (try
     (db/delete-data "lessonplan" (str "nomer='" nomer "'" ))
     (handle-view-edit-lp (subs ko 0 6) "/delete-lp" "Delete LP")
   (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Gagal Delete Lessonplan error: " ex)}))))


