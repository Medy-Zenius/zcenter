(ns ssc.models.share
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
      ;[ssc.models.presence :as presence]
      [ssc.models.register :as register]
  )
)

(defn handle-search-name [nm br actname actnotes actdetail ket]
  (let [upnm (clojure.string/upper-case nm)]
   (layout/render "share/res-search-name.html"
     {:kocab br
      :actname actname :actnotes actnotes :actdetail actdetail :keterangan ket
      :students (db/get-data (str "select name,notes from students where upper(name) LIKE '%" upnm "%' and kocab='" br "' order by name") 2)}
   )))

(defn handle-detail-student [no]
   (layout/render "share/res-detail-student.html"
     {:notes no
      :students (db/get-data (str "select * from students where notes ='" (Integer/parseInt no) "'") 2)}
   ))

(defn handle-search-notes [no br actname actnotes actdetail ket]
   (layout/render "share/res-search-name.html"
     {:kocab br
      :students (db/get-data (str "select notes,name from students where notes ='" (Integer/parseInt no) "' and kocab ='" br "'") 2)
      :actname actname :actnotes actnotes :actdetail actdetail :keterangan ket}
   ))

(defn list-class-page []
    (layout/render "share/list-class.html"
       {:classes (db/get-data (str "select * from kelas where kocab='" (session/get :scode) "' order by level ASC" ) 2)}))

(defn handle-list-student [ko]
    (let [vko (Integer/parseInt ko)
          data (db/get-data (str "select level,hari,keterangan from kelas where kode='" vko "'") 1)]
    (layout/render "share/list-student-class.html"
       {
        :kode ko
        :level (data :level)
        :hari (data :hari)
        :keterangan (data :keterangan)
        :students (db/get-data (str "SELECT students.notes, name from students INNER JOIN datakelas ON
                                    datakelas.notes = students.notes where datakelas.kodekelas = '" vko "'
                                    order by students.notes DESC") 2)})))

(defn handle-delete-student [zen]
  (try
    (db/delete-data "students" (str "notes='" zen "'"))
    (layout/render "pesan.html" {:pesan (str "Berhasil Delete Siswa dengan ZEN = " zen)})
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Delete Siswa Gagal! error: " ex)}))))


(defn class-info-page []
    (layout/render "share/class-info.html"
        {:tahun (db/get-data "select * from tahun" 2)}))

(defn handle-presence-info-date [d m y br]
    (layout/render "share/res-presence-info-date.html"
     {:tahun (db/get-data "select * from tahun" 2)
      :kodepers (db/get-data (str "select * from kodepertemuan where cast (date as date) = '" (subs (register/string-date y m d) 0 10) "'
                                  and kocab = '" br "' order by kode asc") 2)}))

(defn handle-presence-detail [kode]
    (layout/render "share/res-presence-detail.html"
     {
      :kodeper (db/get-data (str "select * from kodepertemuan where kode ='" (Integer/parseInt kode) "'") 2)
      :details (db/get-data (str "select students.notes as std, name from students inner join datapertemuan ON
                                  students.notes = datapertemuan.notes where datapertemuan.kodeper = '"
                                  (Integer/parseInt kode) "'" ) 2)}))

(defn change-pw [usr]
  (layout/render "share/ganti-pw.html"
     {:id usr
      }))
(defn handle-change-pw [usr opw npw npw1]
  (let [duser (db/get-data (str "select pass from users where id ='" usr "'") 1)]
   (if (and duser (= opw (:pass duser)))
    (if (= npw npw1)
      (try (db/update-data "users" (str "id='" usr "'") {:pass npw})
         (layout/render "pesan.html" {:pesan "Berhasil Ganti Password !"}))
      (layout/render "pesan.html" {:pesan "Konfirmasi password baru gagal !"})
    )
    (layout/render "pesan.html" {:pesan "Password lama salah !"}) )))

(defn handle-edit-student [no]
  (layout/render "share/edit-student.html"
          {:student (db/get-data (str "select * from students where notes='" no "'") 1)
           }))
(defn handle-update-student [no na pb db ad ph em le ma sc pna pad pph pem pjo pass]
  (try
    (db/update-data "students" (str "notes='" no "'")
      {:name na :pbirth pb :address ad :phone ph :email em :school sc
       :level (Integer/parseInt le) :major (Integer/parseInt ma)
       :pname pna :paddress pad :pphone pph :pemail pem :pjob pjo :pass pass
       })
    (layout/render "pesan.html" {:pesan "Berhasil Update Data Siswa!"})
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Update Data Siswa Gagal! error: " ex)}))))

(defn daftar-siswa [id act ket]
    (layout/render "siswa/daftar-siswa.html"
         {:action act
          :keterangan ket
          :siswas (db/get-data (str "select datawali.notes as zen,name,kocab from datawali INNER JOIN students ON
                                 datawali.notes=students.notes where idwali='" id "'") 2)
          }))

(defn handle-resetpw [no]
  (try
    (db/update-data "users" (str "nomer='" no "'") {:pass "abcde"})
    (layout/render "pesan.html" {:pesan "Berhasil Reset Password  Menjadi abcde !"})
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Reset Password Gagal! error: " ex)}))))

(defn handle-resetpwid [id]
  (try
    (db/update-data "users" (str "id='" id "'") {:pass "abcde"})
    (layout/render "pesan.html" {:pesan "Berhasil Reset Password  Menjadi abcde !"})
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Reset Password Gagal! error: " ex)}))))

(defn handle-jumlah-siswa [cab]
  (let [jsiswa (:cnotes (db/get-data (str "select count (notes) as cnotes from students where kocab='" cab "'") 1))]
      (layout/render "pesan.html" {:pesan (str "Jumlah Siswa = " jsiswa)})))

(defn handle-komposisi-siswa [cab]
  (layout/render "share/komposisi-siswa.html"
     {:data (db/get-data (str "select level, count(level) as clevel from students where kocab='" cab "'
                              group by level order by level") 2)}))

(defn handle-asal-sekolah [cab]
  (layout/render "share/asal-sekolah.html"
     {:data (db/get-data (str "select school, count(school) as cschool from students where kocab='" cab "'
                              group by school order by school asc") 2)}))

(defn handle-info-inflow [cab ket]
  (layout/render "info/list-flow.html"
     {:keterangan ket
      :total (:sjumlah (db/get-data (str "select sum(jumlah) as sjumlah from inflow where kocab='" cab "'") 1))
      :data (db/get-data (str "select name, sum (jumlah) as sjumlah from kodeinflow
                              INNER JOIN inflow ON (inflow.kode=kodeinflow.code) where kocab='" cab "' group by name") 2)}))

(defn handle-info-outflow [cab ket]
  (layout/render "info/list-flow.html"
     {:keterangan ket
      :total (:sjumlah (db/get-data (str "select sum(jumlah) as sjumlah from outflow where kocab='" cab "'") 1))
      :data (db/get-data (str "select name, sum (jumlah) as sjumlah from kodeoutflow
                              INNER JOIN outflow ON (outflow.kode=kodeoutflow.code) where kocab='" cab "' group by name") 2)}))
