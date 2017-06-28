(ns ssc.routes.student
  (:require [compojure.core :refer :all]
            [ssc.views.layout :as layout]
            ;[noir.response :as resp]
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
            [ssc.models.message :as message]
            ))

(defroutes student-routes
  (GET "/ganti-pw-std" []
       (member/ganti-pw-std))
      (POST "/ganti-pws" [opw npw npw1]
      (member/handle-ganti-pws (session/get :id) opw npw npw1))

  (GET "/student-presence" []
      (presence/presence-std (session/get :id) (session/get :nama) 0 10 10 "/student-presence-next"))
  (POST "/student-presence-next" [notes nama hal]
      (presence/presence-std (Integer/parseInt notes) nama (Integer/parseInt hal) 10 10 "/student-presence-next"))
  (GET "/student-presence-class" []
       (presence/handle-scp-choose-month (session/get :id)))

  (GET "/student-finance" []
      (finance/student-finance (session/get :id) (session/get :nama)))

  (GET "/student-test" []  ;;test-std
      (tes/student-test (session/get :id) (session/get :nama) 0 10 10 "student-test-next"))
  (POST "/student-test-next" [notes nama hal]
        (tes/student-test (Integer/parseInt notes) nama (Integer/parseInt hal) 10 10 "student-test-next"))

  (GET "/student-lp" [];;home-lp
       (lp/search-lp "/student-view-bab-lp" "Pilih Lesson Plan sesuai Kelas-Kurikulum-Bidang Studi"))
  (POST "/student-view-bab-lp" [kelas kurikulum bidangstudi]
       (lp/search-bab-lp "/student-view-butir" "Bab Pelajaran" kelas kurikulum bidangstudi))
  (POST "/student-view-butir" [kode]
        (lp/view-butir kode))

    (GET "/student-message" []
       (message/read-message (session/get :id) "/student-send-message"))
  (POST "/student-send-message" [isi idwali]
        (message/send-message (session/get :id) isi idwali))

  (POST "/view-document" [file]
      (response/redirect
         (str "/files/"  file)))
  (GET "/files/:filename" [filename]
       (file-response (str "dokumen/" filename)))

  (POST "/view-proset" [file]
      (response/redirect
         (str "/filespro/"  file)))
  (GET "/filespro/:filename" [filename]
       (file-response (str "proset/" filename)))
)
