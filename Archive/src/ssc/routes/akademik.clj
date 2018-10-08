(ns ssc.routes.akademik
  (:require [compojure.core :refer :all]
            [ssc.views.layout :as layout]
            [ssc.models.db :as db]
            [noir.io :as io]
            [noir.session :as session]
            [noir.response :as response]
            [ring.util.response :refer [file-response]]
            [ssc.models.member :as member]
            [ssc.models.presence :as presence]
            [ssc.models.finance :as finance]
            [ssc.models.test :as tes]
            [ssc.models.lessonplan :as lp]
            [ssc.models.share :as share]
            [ssc.models.kelas :as kelas]
            ))

(defroutes akademik-routes
  (GET "/new-group" []
      (layout/render "kelas/new-group.html"))
  (POST "/create-new-group" [level hari desc]
        (kelas/handle-create-new-group (Integer/parseInt level) hari desc (session/get :scode)))

  (GET "/list-group" []
       (kelas/list-group "/list-student" "Lihat Data Kelas"))
  (POST "/list-student" [kode]
        (kelas/handle-list-student kode))

  (GET "/edit-group" []
       (kelas/list-group "/edit-group" "Edit Kelas"))
  (POST "/edit-group" [kode]
        (kelas/edit-group (Integer/parseInt kode)))
  (POST "/update-group" [kode level hari keterangan wali]
        (kelas/update-group (Integer/parseInt kode) (Integer/parseInt level) hari keterangan wali))

  (GET "/hapus-siswa" []
       (kelas/list-group "/list-hapus-siswa" "Hapus Siswa dalam Kelas"))
  (POST "/list-hapus-siswa" [kode]
        (kelas/list-hapus-siswa (Integer/parseInt kode)))
  (POST "/hapus-siswa" [nomer kode]
        (kelas/hapus-siswa (Integer/parseInt nomer) (Integer/parseInt kode)))

  (GET "/hapus-group" []
       (kelas/list-group "/hapus-group" "Hapus Kelas"))
  (POST "/hapus-group" [kode]
        (kelas/hapus-group (Integer/parseInt kode)))

  (GET "/input-group" []
       (kelas/list-group "/input-student-group" "Input Siswa - Klik kode yang benar !"))
  (POST "/input-student-group" [kode]
        (kelas/handle-input-student-group (Integer/parseInt kode)))
  (POST "/input-student" [kode notes]
        (kelas/handle-input-student (Integer/parseInt kode) (Integer/parseInt notes)))

  (GET "/find-student-group" []
       (layout/render "share/search.html"
                   {:actname "/fsg-search-name" :actnotes "/fsg-search-notes" :keterangan "Cari Kelas Siswa !"}))
  (POST "/fsg-search-name" [nama]
        (share/handle-search-name nama (session/get :scode) "/fsg-search-name" "/fsg-search-notes"
                            "/find-student-group" "Kelas Siswa"))
  (POST "/fsg-search-notes" [notes]
        (share/handle-search-notes notes (session/get :scode) "/fsg-search-name" "/fsg-search-notes"
                            "/find-student-group" "Kelas Siswa"))
  (POST "/find-student-group" [notes]
        (kelas/handle-find-student-group (Integer/parseInt notes) (session/get :scode)))

  (GET "/edit-pertemuan" []
       (kelas/searching-pertemuan (session/get :id)  "/edit-date" "Edit Pertemuan"))
  (POST "/edit-date" [date month year]
        (kelas/list-pertemuan date month year (session/get :scode)))
  (POST "/edit-pertemuan-go" [kode]
        (kelas/edit-pertemuan-go (Integer/parseInt kode)))
  (POST "/update-pertemuan" [kode tutor tanggal activities]
        (kelas/update-pertemuan (Integer/parseInt kode) tutor tanggal activities))
  (POST "/delete-pertemuan" [kode]
        (kelas/delete-pertemuan (Integer/parseInt kode)))

  (GET "/edit-test" []
       (kelas/searching-pertemuan (session/get :id)  "/edit-test-date" "Edit Test"))
  (POST "/edit-test-date" [date month year]
        (kelas/list-test date month year (session/get :scode) "/edit-test-go"))
  (POST "/edit-test-go" [kode]
        (kelas/edit-test-go (Integer/parseInt kode)))
  (POST "/update-test-master" [kode tanggal matpel description dokumen]
        (kelas/update-test-master (Integer/parseInt kode) tanggal matpel description dokumen))
  (POST "/hapus-kdtest" [kode]
        (kelas/hapus-kdtest (Integer/parseInt kode)))

   ;;;;;;;; Upload Dokumen Test
  (GET "/upload-dokumen" []
       (kelas/upload-page))
  (POST "/upload" [file]
       (io/upload-file "dokumen/" file)
       (response/redirect
         (str "/files/" (:filename file))))

  (GET "/files/:filename" [filename]
       (file-response (str "dokumen/" filename)))

  (GET "/list-wali" []
       (layout/render "info/list-mentor.html"
          {:data (db/get-data (str "select nomer,id,name from users where status = 5") 2)}))
  (POST "/list-siswa-mentor" [nomer]
       (kelas/handle-list-siswa-mentor (Integer/parseInt nomer) (session/get :scode)))

  (GET "/new-wali" []
           (layout/render "kelas/new-wali.html"
             {:kodecabang (session/get :scode)}))
  (POST "/new-wali" [nama alamat tempatlahir birth telpon pendidikan id email]
        (kelas/handle-new-wali nama alamat tempatlahir birth telpon pendidikan (session/get :scode) id email))

  (GET "/view-edit-wali" []
        (kelas/searching-wali (session/get :id)  "/search-wali-name" "/search-wali-id" "Lihat Wali berdasarkan Nama/ID"))
  (POST "/search-wali-name" [nama]
        (kelas/searching-wali-name (session/get :id) nama "/detail-wali" "Lihat/Edit Wali"))
  (POST "/search-wali-id" [id]
        (kelas/searching-wali-id (session/get :id) id "/detail-wali" "Lihat/Edit Wali"))
  (POST "/detail-wali" [nomer]
        (kelas/handle-detail-wali nomer))
  (POST "/update-wali" [nomer nama alamat tempatlahir telpon pendidikan kocab id pass email]
        (kelas/handle-update-wali nomer nama alamat tempatlahir telpon pendidikan kocab id pass email))
  (POST "/resetpw-wali" [nomer]
        (share/handle-resetpw nomer))

  (GET "/delete-wali" []
        (kelas/searching-wali (session/get :id)  "/del-wali-name" "/del-wali-id" "Hapus Wali berdasarkan Nama/ID"))
  (POST "/del-wali-name" [nama]
        (kelas/searching-wali-name (session/get :id) nama "/delete-wali" "Menghapus Wali"))
  (POST "/del-wali-id" [id]
        (kelas/searching-wali-id (session/get :id) id "/delete-wali" "Menghapus Wali"))
  (POST "/delete-wali" [nomer]
        (kelas/handle-delete-wali (Integer/parseInt nomer)))

  (GET "/siswa-wali" []
       (kelas/searching-wali (session/get :id)  "/siswa-wali-name" "/siswa-wali-id" "Lihat Wali berdasarkan Nama/ID"))
  (POST "/siswa-wali-name" [nama]
        (kelas/searching-wali-name (session/get :id) nama "/input-siswa-wali" "Menambah Siswa Asuhan !"))
  (POST "/siswa-wali-id" [id]
        (kelas/searching-wali-id (session/get :id) id "/input-siswa-wali" "Menambah Siswa Asuhan"))
  (POST "/input-siswa-wali" [nomer]
        (kelas/handle-input-siswa-wali nomer))
  (POST "/input-siswa-wali-data" [nomer idwali notes]
        (kelas/handle-input-siswa-wali-data nomer idwali (Integer/parseInt notes)))

  (GET "/hapus-siswa-wali" []
        (kelas/searching-wali (session/get :id)  "/hapus-siswa-wali-name" "/hapus-siswa-wali-id"
                           "Lihat Wali berdasarkan Nama/ID"))
  (POST "/hapus-siswa-wali-name" [nama]
        (kelas/searching-wali-name (session/get :id) nama "/list-siswa-wali-data" "Melihat Siswa Asuhan"))
  (POST "/hapus-siswa-wali-id" [id]
        (kelas/searching-wali-id (session/get :id) id "/list-siswa-wali-data" "Melihat Siswa Asuhan"))
  (POST "/list-siswa-wali-data" [nomer]
        (kelas/handle-list-siswa-wali-data nomer))
  (POST "/hapus-siswa-wali-data" [nomer id]
        (kelas/handle-hapus-siswa-wali-data nomer id))

  (GET "/cari-siswa-wali" []
       (layout/render "share/search.html"
                   {:actname "/csw-search-name" :actnotes "/csw-search-notes" :keterangan "Cari Mentor Zenian !"}))
  (POST "/csw-search-name" [nama]
        (share/handle-search-name nama (session/get :scode) "/csw-search-name" "/csw-search-notes"
                            "/cari-siswa-wali" "Cari Mentor Zenian !"))
  (POST "/csw-search-notes" [notes]
        (share/handle-search-notes notes (session/get :scode) "/csw-search-name" "/csw-search-notes"
                            "/cari-siswa-wali" "Cari Mentor Zenian !"))
  (POST "/cari-siswa-wali" [notes]
        (kelas/handle-cari-siswa-wali (Integer/parseInt notes) (session/get :scode)))

   ;; Data Pribadi Siswa

  (GET "/edit-data-pribadi" []
       (layout/render "share/search.html"
                   {:actname "/edp-search-name" :actnotes "/edp-search-notes" :keterangan "Edit Data Pribadi"}))
  (POST "/edp-search-name" [nama]
        (share/handle-search-name nama (session/get :scode) "/edp-search-name" "/edp-search-notes"
                            "/edit-data-pribadi" "Data Pribadi"))
  (POST "/edp-search-notes" [notes]
        (share/handle-search-notes notes (session/get :scode) "/edp-search-name" "/edp-search-notes"
                            "/edit-data-pribadi" "Data Pribadi"))
  (POST "/edit-data-pribadi" [notes]
        (share/handle-edit-student (Integer/parseInt notes)))

  (POST "/update-student" [notes nama pbirth dbirth address phone email level major school
                           pname paddress pphone pemail pjob pass]
        (share/handle-update-student (Integer/parseInt notes) nama pbirth dbirth address phone email level major school
                               pname paddress pphone pemail pjob pass))
  ;;;; Test Siswa
  (GET "/edit-test-siswa" []
       (layout/render "share/search.html"
                   {:actname "/test-search-name" :actnotes "/test-search-notes" :keterangan "Edit Data Test"}))
  (POST "/test-search-name" [nama]
        (share/handle-search-name nama (session/get :scode) "/test-search-name" "/test-search-notes"
                            "/list-test-siswa" "Data Pribadi"))
  (POST "/test-search-notes" [notes]
        (share/handle-search-notes notes (session/get :scode) "/test-search-name" "/test-search-notes"
                            "list-test-siswa" "Data Pribadi"))

  (POST "/list-test-siswa" [notes]
        (tes/handle-list-test-siswa (Integer/parseInt notes) (session/get :scode)))
  (POST "/edit-test-siswa" [nomer]
        (tes/handle-edit-test (Integer/parseInt nomer)))
  (POST "/update-test" [nomer score]
        (tes/handle-update-test (Integer/parseInt nomer) (read-string score)))
  (POST "/hapus-test-std" [nomer notes]
        (tes/hapus-test-std (Integer/parseInt nomer) (Integer/parseInt notes) (session/get :scode)))

   ;;; Presensi Siswa

  (GET "/edit-presensi-siswa" []
       (layout/render "share/search.html"
                   {:actname "/pre-search-name" :actnotes "/pre-search-notes" :keterangan "Edit Presensi Siswa"}))
  (POST "/pre-search-name" [nama]
        (share/handle-search-name nama (session/get :scode) "/pre-search-name" "/pre-search-notes"
                            "/edit-presensi-siswa" "Edit Presensi Siswa"))
  (POST "/pre-search-notes" [notes]
        (share/handle-search-notes notes (session/get :scode) "/pre-search-name" "/pre-search-notes"
                            "edit-presensi-siswa" "Edit Presensi Siswa"))
  (POST "/edit-presensi-siswa" [notes]
        (presence/handle-list-presensi-siswa (Integer/parseInt notes) (session/get :scode)))
  (POST "/delete-presence" [nomer notes]
        (presence/handle-delete-presence (Integer/parseInt nomer) (Integer/parseInt notes)))
  )
