(ns ssc.models.presence
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
      [ssc.models.register :as register]
  ))

(defn two-char-p [s]
  (if (< (count s) 2)(apply str ["0" s])(apply str [s])))

(defn string-date-p[y mo d h mn]
  (apply str [y "-" (two-char-p mo) "-" (two-char-p d) " " (two-char-p h) ":" (two-char-p mn) ":00"]))

(defn str-date-Timestamp-p [y mo d h mn]
  (java.sql.Timestamp/valueOf (string-date-p y mo d h mn)))

(defn strDTimestamp-p [y mo d h mn]
  (->> (string-date-p y mo d h mn)
     (timef/parse (timef/formatter "yyyy-MM-dd kk:mm:ss"))
     timec/to-timestamp))

(defn presence-std [zen nm page ofs lim act]
    (layout/render "presence/std-pre-info.html"
       {
        :notes zen
        :name nm
        :presences (db/get-data (str "SELECT date,tutor,activities from kodepertemuan INNER JOIN datapertemuan ON
                                  kodepertemuan.kode = datapertemuan.kodeper where datapertemuan.notes = '" zen "'
                                  order by date DESC OFFSET " (* page ofs) " LIMIT " lim) 2)
        :rows (:count (db/get-data (str "SELECT count (*) from kodepertemuan INNER JOIN datapertemuan ON
                                  kodepertemuan.kode = datapertemuan.kodeper where datapertemuan.notes = '" zen "'") 1))
        :page page
        :action act
        }))

(defn create-class-page []
    (layout/render "presence/create-class.html"
        {:tahun (db/get-data "select * from tahun" 2)
         :data (db/get-data (str "select kode,hari,keterangan from kelas where kocab='" (session/get :scode) "'
                                 order by hari asc" ) 2)}))

(defn handle-create-class [co da mo ye ho mn tu ac usr br]
  (db/insert-data "kodepertemuan"
                   {:kodekelas (Integer/parseInt co)
                    :tutor tu
                    :activities ac
                    :id usr
                    :kocab br
                    :date (str-date-Timestamp-p ye mo da ho mn)
                    })
     (layout/render "presence/res-create-class.html"
     {
      :tutor tu
      :date (str-date-Timestamp-p ye mo da ho mn)
      :kelas (db/get-data (str "select kode,hari,keterangan from kelas where kode='" (Integer/parseInt co) "'") 1)
      :codes (db/get-data (str "select kode from kodepertemuan where date ='" (str-date-Timestamp-p ye mo da ho mn) "'
                          and tutor ='" tu "' and id ='" usr "'") 1)
     }))

(defn handle-input-presence[kl cd no tu dt]
   (db/insert-data "datapertemuan"
                      {
                       :kodeper (Integer/parseInt cd)
                       :notes (Integer/parseInt no)
                      })
   (layout/render "presence/res-input-presence.html"
     {:kelas kl
      :tutor tu
      :date dt
      :code cd
      }))

(defn input-presence-page []
  (layout/render "presence/get-class-info.html"
        {:tahun (db/get-data "select * from tahun" 2)
         :data (db/get-data (str "select kode,hari,keterangan from kelas where kocab='" (session/get :scode) "'
                                 order by hari asc" ) 2)}))

(defn handle-presence-date [kl d m y br]
    (layout/render "presence/res-presence-date.html"
     {:tahun (db/get-data "select * from tahun" 2)
      :kelas (db/get-data (str "select kode,hari,keterangan from kelas where kode='" kl "'") 1)
      :data (db/get-data (str "select kode,hari,keterangan from kelas where kocab='" (session/get :scode) "'
                                 order by hari asc" ) 2)
      :kodepers (db/get-data (str "select * from kodepertemuan where cast (date as date) = '"
                                  (subs (register/string-date y m d) 0 10) "' and kocab = '" br "' and kodekelas='" kl "'
                                  order by kode asc") 2)
     }))
(defn handle-input-student-presence [kl ko]
  (layout/render "presence/input-student-presence.html"
     {:kelas kl
      :dataper (db/get-data (str "select date,tutor,activities from kodepertemuan where kode='" ko "'") 1)
      :code ko
     }))

(defn handle1-input-student-presence[kl da tu ac cd no]
   (db/insert-data "datapertemuan"
                      {
                       :kodeper (Integer/parseInt cd)
                       :notes (Integer/parseInt no)
                      })
   (layout/render "presence/input-student-presence.html"
     {:code cd
      :kelas kl
      :dataper {:date da :tutor tu :activities ac}
      }))

(defn handle-list-presensi-siswa [no br]
  (layout/render "presence/list-presensi-siswa.html"
   {:notes no
    :nama (db/get-data (str "select name from students where notes='" no "' and kocab='" br "'") 2)
    :presencies (db/get-data (str "SELECT date,tutor,activities,nomer,notes from kodepertemuan INNER JOIN datapertemuan ON
                                      kodepertemuan.kode = datapertemuan.kodeper where datapertemuan.notes =
                                    '" no "' and kocab='" br "' order by date DESC") 2)}))

(defn handle-delete-presence [nomer notes]
  (try
    (db/delete-data "datapertemuan" (str "nomer='" nomer "'"))
    (handle-list-presensi-siswa notes (session/get :scode))
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Hapus Presensi Gagal! error: " ex)}))))

(defn handle-class-presence-info [kkel kbul]
   (layout/render "presence/list-presence-class.html"
      {:kelas (db/get-data (str "select kode,hari,keterangan from kelas where kode='" kkel "'") 1)
       :data (db/get-data (str "select kode,date,tutor,activities from kodepertemuan where kodekelas='" kkel "'
                               and extract(month from date)='" kbul "' order by date desc") 2)}))
(defn handle-list-pre-class-student [kodepertemuan kelas]
  (layout/render "presence/list-pre-class-student.html"
          {:kelas kelas
           :dataper (db/get-data (str "select date,tutor,activities from kodepertemuan where kode='"
                                      kodepertemuan "'") 1)
           :siswa (db/get-data (str "select datapertemuan.notes,name from datapertemuan inner join students on
                                    (datapertemuan.notes=students.notes) where kodeper='" kodepertemuan "' order by name asc") 2)}))

(defn handle-scp-choose-month [notes]
   (layout/render "presence/scp-choose-month.html"
       {:notes notes
        :bulan (db/get-data "select * from kodebulan" 2)}))
  (defn handle-scp-info-student [notes kbul]
  (let [kodekelas (:kodekelas (db/get-data (str "select kodekelas from datakelas where notes='" notes "'") 1))
        kelas (db/get-data (str "select kode,hari,keterangan from kelas where kode='" kodekelas "'") 1)]
   (layout/render "presence/list-scp.html"
        {:kelas kelas
         :bulan (:bulan (db/get-data (str "select bulan from kodebulan where kode='" kbul "'") 1))
         :data (db/get-data (str "select kode,tutor,date,activities,notes from kodepertemuan left join
                                 (select * from datapertemuan where notes='" notes "') as foo
                                 on kodepertemuan.kode=foo.kodeper where kodepertemuan.kodekelas='" kodekelas "'
                                 and extract(month from date)='" kbul "' order by date desc" ) 2)})))
