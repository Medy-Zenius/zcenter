(ns ssc.models.test
  (:require
      ;[hiccup.form :refer :all]
      [compojure.core :refer :all]
      ;[zen.routes.home :refer :all]
      [ssc.views.layout :as layout]
      [noir.session :as session]
      ;[noir.validation :as vali]
      ;[noir.response :as resp]
      ;[noir.util.crypt :as crypt]
      [clj-time [format :as timef] [coerce :as timec]]
      [ssc.models.db :as db]
      [ssc.models.presence :as presence]
      [ssc.models.register :as register]
  )
)

(defn student-test [zen nm page ofs lim act]
    (layout/render "test/student-test-info.html"
       {
        :notes zen
        :name nm
        :tests (db/get-data (str "SELECT date,matpel,description,dokumen,score from kodetest INNER JOIN datatest ON
                                  kodetest.kode = datatest.kodetest where datatest.notes = '" zen "'
                                 order by date DESC OFFSET " (* page ofs) " LIMIT " lim) 2)
        :rows (:count (db/get-data (str "SELECT count (*) from kodetest INNER JOIN datatest ON
                                  kodetest.kode = datatest.kodetest where datatest.notes = '" zen "'") 1))
        :page page
        :action act
        }))

(defn create-test-page []
    (layout/render "test/create-test.html"
      {:tahun (db/get-data "select * from tahun" 2)
       :kodepels (db/get-data (str "select * from kodepel order by nama asc") 2)
       }))

(defn handle-create-test [da mo ye ho mn mp de usr br]
  (db/insert-data "kodetest"
                   {:matpel mp
                    :description de
                    :id usr
                    :kocab br
                    :date (presence/str-date-Timestamp-p ye mo da ho mn)
                    })

   (layout/render "test/res-create-test.html"
     {
      :matpel mp
      :date (presence/str-date-Timestamp-p ye mo da ho mn)
      :codes (db/get-data (str "select kode from kodetest where date ='"
                               (presence/str-date-Timestamp-p ye mo da ho mn) "' and matpel ='" mp "' and id ='" usr "'") 2)
     }))

(defn handle-input-test[cd no mp sc dt]
   (db/insert-data "datatest"
                      {
                       :kodetest (Integer/parseInt cd)
                       :notes (Integer/parseInt no)
                       :score (read-string sc)
                      })
   (layout/render "test/res-input-test.html"
     {
      :matpel mp
      :date dt
      :code cd
     }))

(defn input-test-page []
  (layout/render "test/get-test-info.html"
     {:tahun (db/get-data "select * from tahun" 2)}))

(defn handle-test-date [d m y br]
    (layout/render "test/res-test-date.html"
     {:tahun (db/get-data "select * from tahun" 2)
      :kodetests (db/get-data (str "select * from kodetest where cast (date as date) = '"
                                   (subs (register/string-date y m d) 0 10) "' and kocab = '" br "' order by kode asc") 2)
     }))

(defn handle-input-student-test [ko]
  (layout/render "test/input-student-test.html"
     {:code ko}))

(defn handle1-input-student-test[cd no sc]
   (db/insert-data "datatest"
                      {
                       :kodetest (Integer/parseInt cd)
                       :notes (Integer/parseInt no)
                       :score (read-string sc)
                      })
   (layout/render "test/input-student-test.html"
     {:code cd}))

(defn handle-list-test-siswa [no br]
  (layout/render "test/list-test-siswa.html"
   {:notes no
    :nama (db/get-data (str "select name from students where notes='" no "' and kocab='" br "'") 2)
    :tests (db/get-data (str "SELECT date,matpel,description,score,nomer from kodetest INNER JOIN datatest ON
                                  kodetest.kode = datatest.kodetest where datatest.notes = '" no "'
                             and kocab='" br "' order by matpel ASC") 2)
    }))

(defn handle-edit-test [nomer]
  (layout/render "test/edit-test.html"
   {:nomer nomer
    :datatests (db/get-data (str "select * from datatest where nomer ='" nomer "'") 2)
    }))

(defn handle-update-test [nomer score]
  (try
    (db/update-data "datatest" (str "nomer='" nomer "'") {:score score})
    (layout/render "pesan.html" {:pesan "Berhasil Update Test!"})
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Update Test Gagal! error: " ex)}))))

(defn hapus-test-std [nom notes br]
  (try
    (db/delete-data "datatest" (str "nomer='" nom "'"))
    (handle-list-test-siswa notes br)
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Gagal Hapus Test! error: " ex)}))))
