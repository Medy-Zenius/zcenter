(ns ssc.routes.mentor
  (:require [compojure.core :refer :all]
            [ssc.views.layout :as layout]
            ;[ssc.models.db :as db]
            [noir.io :as io]
            [noir.session :as session]
            [noir.response :as response]
            [ring.util.response :refer [file-response]]
            ;[ssc.models.member :as member]
            ;[ssc.models.presence :as presence]
            ;[ssc.models.finance :as finance]
            ;[ssc.models.test :as tes]
            ;[ssc.models.lessonplan :as lp]
            [ssc.models.share :as share]
            ;[ssc.models.kelas :as kelas]
            [ssc.models.message :as message]
            ))

(defroutes mentor-routes
  (GET "/wali-daftar-siswa" []
       (share/daftar-siswa (session/get :id) "/detail-student" "Info Siswa !"))
  (GET "/wali-test" []
       (share/daftar-siswa (session/get :id) "/tinfo-student" "Test Siswa !"))
  (GET "/wali-presensi" []
       (share/daftar-siswa (session/get :id) "/pinfo-student" "Test Siswa !"))

  (GET "/wali-pesan-buat" []
       (share/daftar-siswa (session/get :id) "/wali-pesan-buat" "Test Siswa !"))
  (POST "/wali-pesan-buat" [notes]
        (message/handle-wali-pesan-buat (Integer/parseInt notes) (session/get :id)))
  (POST "/wali-kirim-pesan" [notes isi]
        (message/wali-kirim-pesan (Integer/parseInt notes) isi (session/get :id)))

  (GET "/wali-pesan-lihat" []
       (share/daftar-siswa (session/get :id) "/wali-pesan-lihat" "Melihat Pesan !"))
  (POST "/wali-pesan-lihat" [notes]
        (message/handle-wali-pesan-lihat (Integer/parseInt notes) (session/get :id)))


)
