(ns ssc.models.kelas
  (:require
      [compojure.core :refer :all]
      [ssc.views.layout :as layout]
      [noir.session :as session]
      [noir.util.crypt :as crypt]
      [clj-time [format :as timef] [coerce :as timec]]
      [ssc.models.db :as db]
      ;[ssc.models.presence :as presence]
      [ssc.models.register :as register]
  )
)

(defn handle-create-new-group [le ha de br]
  (try
    (db/insert-data "kelas" {:level le :hari ha :keterangan de :kocab br})
    (layout/render "pesan.html" {:pesan "Kelas baru berhasil dibuat !"})
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Gagal Bikin Kelas Baru error: " ex)}))))

(defn list-group [action ket]
    (layout/render "kelas/list-group.html"
       {:action action
        :keterangan ket
        :classes (db/get-data (str "select * from kelas where kocab='" (session/get :scode) "' order by level ASC" ) 2)}))

(defn handle-list-student [ko]
    (layout/render "kelas/list-student-group.html"
       {
        :classes (db/get-data (str "select * from kelas where kode='" ko "'") 2)
        :students (db/get-data (str "SELECT students.notes, name from students INNER JOIN datakelas ON
                                    datakelas.notes = students.notes where datakelas.kodekelas = '" ko "'
                                    order by students.notes DESC") 2)
        }))

(defn edit-group [ko]
  (layout/render "kelas/edit-group.html"
       {
        :kode ko
        :groups (db/get-data (str "SELECT * from kelas where kode='" ko "'") 2)
        }))

(defn update-group [ko le ha ket wa]
  (try
    (db/update-data "kelas" (str "kode='" ko "'")
       {
        :level le
        :hari ha
        :keterangan ket
        :wali wa
        })
    (layout/render "pesan.html" {:pesan "Update Kelas Berhasil !"})
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Update Kelas Gagal! error: " ex)}))))

(defn list-hapus-siswa [ko]
    (layout/render "kelas/list-hapus-siswa.html"
       {:kode ko
        :classes (db/get-data (str "select * from kelas where kode='" ko "'") 2)
        :students (db/get-data (str "SELECT students.notes,name,nomer from students INNER JOIN datakelas ON
                                    datakelas.notes = students.notes where datakelas.kodekelas = '" ko "'
                                    order by students.notes DESC") 2)}))
(defn hapus-siswa [no ko]
 (try
   (db/delete-data "datakelas" (str "nomer='" no "'"))
   (list-hapus-siswa ko)
 (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Gagal Hapus Siswa error: " ex)}))))

(defn hapus-group [ko]
  (db/delete-data "kelas" (str "kode='" ko "'"))
  (db/delete-data "datakelas" (str "kodekelas='" ko "'"))
  (list-group "/hapus-group" "Hapus Data Kelas"))

(defn handle-input-student-group [ko]
    (layout/render "kelas/input-student-group.html"
       {:kode ko
        :classes (db/get-data (str "select * from kelas where kode='" ko "'") 2)}))

(defn handle-input-student [ko no]
  (let [vnotes (db/get-data (str "select notes from students where kocab='" (session/get :scode) "' and notes='" no "'") 2)]
     (if vnotes
        (try (db/insert-data "datakelas" {:kodekelas ko :notes no})
          (layout/render "kelas/input-student-group.html"
             {:kode ko
              :classes (db/get-data (str "select * from kelas where kode='" ko "'") 2)})
        (catch Exception ex
          (layout/render "pesan.html" {:pesan (str "Gagal Input Siswa error: " ex)})))
        (layout/render "pesan.html" {:pesan "Tidak ada siswa dengan nomer ZEN tersebut !"})
       )))

(defn handle-find-student-group [no cab]
  (let [data (db/get-data (str "select notes,kodekelas,hari,keterangan from datakelas INNER JOIN kelas
                                ON (datakelas.kodekelas=kelas.kode) where notes='" no "' and kelas.kocab='" cab "'") 1)]
       (if data
         (layout/render "pesan.html"
             {:pesan (str "Zen " no " terdaftar pada kelas " (:kodekelas data) "-" (:hari data) "-" (:keterangan data))})
         (layout/render "pesan.html" {:pesan "Tidak ada data kelas siswa !"}))))

(defn searching-pertemuan [usr actdate ket]
  (layout/render "kelas/search-pertemuan.html"
  {:tahun (db/get-data "select * from tahun" 2)
   :act_date actdate
   :keterangan ket}))

(defn list-pertemuan [d m y br]
    (layout/render "kelas/list-pertemuan.html"
     {:tahun (db/get-data "select * from tahun" 2)
      :kodepers (db/get-data (str "select * from kodepertemuan where cast (date as date) = '"
                                  (subs (register/string-date y m d) 0 10) "'
                                  and kocab = '" br "' order by kode asc") 2)}))

(defn edit-pertemuan-go [kd]
  (layout/render "kelas/form-edit-pertemuan.html"
       {:pert (db/get-data (str "select * from kodepertemuan where kode='" kd "'") 1)}))

(defn update-pertemuan [kd tu tgl ac]
  (try
  (db/update-data "kodepertemuan" (str "kode='" kd "'")
    {:tutor tu :activities ac :date (java.sql.Timestamp/valueOf tgl)})
    (layout/render "pesan.html" {:pesan "Berhasil Edit Pertemuan !"})
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Gagal Update Pertemuan error: " ex)}))))

(defn delete-pertemuan [kd]
  (try
  (db/delete-data "kodepertemuan" (str "kode='" kd "'"))
    (layout/render "pesan.html" {:pesan "Berhasil Hapus Pertemuan !"})
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Gagal Hapus Pertemuan error: " ex)}))))

(defn list-test [d m y br act]
    (layout/render "kelas/list-test.html"
     {:tahun (db/get-data "select * from tahun" 2)
      :kodetest (db/get-data (str "select * from kodetest where cast (date as date) = '"
                                  (subs (register/string-date y m d) 0 10) "'
                                  and kocab = '" br "' order by kode asc") 2)
      :action act}))

(defn edit-test-go [kd]
  (layout/render "kelas/form-edit-test.html"
       {:test (db/get-data (str "select * from kodetest where kode='" kd "'") 2)}))

(defn update-test-master [kd tgl mp de dok]
  (try
     (db/update-data "kodetest" (str "kode='" kd "'")
         {:matpel mp :description de :dokumen dok :date (java.sql.Timestamp/valueOf tgl)})
         (layout/render "pesan.html" {:pesan "Berhasil Edit Test !"})
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Gagal Update Test error: " ex)}))))

(defn hapus-kdtest [kd]
  (try
    (db/delete-data "kodetest" (str "kode='" kd "'"))
    (layout/render "pesan.html" {:pesan "Berhasil menghapus Data Test !"})
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Gagal Menghapus Data Test error: " ex)}))))

(defn upload-page []
    (layout/render "share/upload.html"))

(defn handle-new-wali [na al tl bi tel pen kocab id em]
  (try
      (db/insert-data "users"
                      {:name na :alamat al :tempatlahir tl
                       :telpon tel :pendidikan pen :id id :email em
                       :scode kocab
                       :pass (crypt/encrypt "abcde")
                       :status 5
                       :tanggallahir (register/str-date-Timestamp bi)
                       })
      (layout/render "pesan.html"
           {:pesan (str "Input data Wali dengan nama " na " berhasil !")})
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Gagal Menambah Wali Baru error: " ex)}))))

(defn searching-wali [usr actname actid ket]
  (layout/render "kelas/search-wali.html"
  {:act_name actname
   :act_id actid
   :keterangan ket}))

(defn searching-wali-name [usr nama act ket]
  (layout/render "kelas/search-wali-nama.html"
      {:walis (db/get-data (str "select nomer,name,id,pass from users where name LIKE '%" nama "%'
                                and scode='" (session/get :scode) "' and status=5 order by name") 2)
       :action act
       :keterangan ket}))

(defn searching-wali-id [usr id act ket]
  (layout/render "kelas/search-wali-nama.html"
      {:walis (db/get-data (str "select nomer,name,id,pass from users where id LIKE '%" id "%'
                                and scode='" (session/get :scode) "' and status=5 order by id") 2)
       :action act
       :keterangan ket }))

(defn handle-detail-wali [no]
  (layout/render "kelas/detail-wali.html"
     {:wali (db/get-data (str "select * from users where nomer='" (Integer/parseInt no) "'") 1)
      }))

(defn handle-update-wali [no na al tl tel pen kocab id pass em]
  (try
    (db/update-data "users" (str "nomer='" no "'")
      {:name na :alamat al :tempatlahir tl
       :telpon tel :pendidikan pen :scode (Integer/parseInt kocab) :id id :email em
       })
    (layout/render "pesan.html" {:pesan "Berhasil Update Wali !"})
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Update Wali Gagal! error: " ex)}))))

(defn handle-delete-wali [no]
  (try
     (db/delete-data "users" (str "nomer='" no "'"))
     (layout/render "pesan.html" {:pesan "Berhasil menghapus record Wali!"})
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Hapus Wali Gagal! error: " ex)}))))

(defn handle-input-siswa-wali [nom]
  (let [dwali (db/get-data (str "select id from users where nomer ='" (Integer/parseInt nom) "'") 1)]
  (layout/render "kelas/input-siswa-wali.html"
      {:nomer nom
       :idwali (:id dwali)
       }
    )))

(defn handle-input-siswa-wali-data [nom id no]
  (let [vnotes (db/get-data (str "select notes from students where kocab='" (session/get :scode) "' and notes='" no "'") 2)]
     (if vnotes
        (try
         (db/insert-data "datawali" {:notes no :idwali id})
         (layout/render "kelas/input-siswa-wali.html" {:nomer nom :idwali id})
        (catch Exception ex
          (layout/render "pesan.html" {:pesan (str "Gagal Input Siswa! error: " ex)})))
        (layout/render "pesan.html" {:pesan "Tidak ada siswa dengan nomer ZEN tersebut !"})
       )))


(defn handle-list-siswa-wali-data [nom]
  (let [dwali (db/get-data (str "select id from users where nomer ='" (Integer/parseInt nom) "'") 1)]
  (layout/render "kelas/list-siswa-wali.html"
      {
       :idwali (:id dwali)
       :siswas (db/get-data (str "select nomer,datawali.notes as zen,idwali,name from datawali INNER JOIN students ON
                                 datawali.notes=students.notes where idwali='" (:id dwali) "'") 2)
       }
    )))

(defn handle-hapus-siswa-wali-data [no id]
  (db/delete-data "datawali" (str "nomer='" no "'"))
  (layout/render "kelas/list-siswa-wali.html"
      {
       :idwali id
       :siswas (db/get-data (str "select nomer,datawali.notes as zen,idwali,name from datawali INNER JOIN students ON
                                 datawali.notes=students.notes where idwali='" id "'") 2)
       }
    ))

(defn handle-cari-siswa-wali [no cab]
  (let [data (db/get-data (str "select notes,idwali,name from datawali INNER JOIN users
                                ON (datawali.idwali=users.id) where notes='" no "' and users.scode='" cab "'") 1)]
       (if data
         (layout/render "pesan.html"
             {:pesan (str "Mentor Zen " no " adalah " (:name data))})
         (layout/render "pesan.html" {:pesan "Tidak ada data mentor siswa !"}))))

(defn handle-list-siswa-mentor [no cab]
  (let [idwali (:id (db/get-data (str "select id from users where nomer='" no "'") 1))]
       (layout/render "info/list-siswa-mentor.html"
            {:data (db/get-data (str "select datawali.notes, name from datawali INNER JOIN students
                                     ON (datawali.notes=students.notes) where datawali.idwali='" idwali "'") 2)})))
