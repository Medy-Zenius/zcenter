(ns ssc.routes.super
  (:require [compojure.core :refer :all]
            [ssc.views.layout :as layout]
            ;[noir.response :as resp]
            [ssc.models.db :as db]
            [noir.io :as io]
            [noir.session :as session]
            [noir.response :as response]
            [ring.util.response :refer [file-response]]
            ;[ssc.models.member :as member]
            ;[ssc.models.presence :as presence]
            ;[ssc.models.finance :as finance]
            ;[ssc.models.test :as tes]
            [ssc.models.lessonplan :as lp]
            ;[ssc.models.message :as message]
            ;[ssc.models.register :as reg]
            [ssc.models.preference :as preference]
            ))

(defroutes super-routes
  (GET "/new-lp" []
        (lp/search-lp "/new-lp" "New LP"))
  (POST "/new-lp" [kelas kurikulum bidangstudi]
        (lp/handle-new-lp kelas kurikulum bidangstudi))
  (POST "/input-lp" [kelas kurikulum bidangstudi kodesubjek deskripsi kodekonten]
        (lp/handle-input-lp kelas kurikulum bidangstudi kodesubjek deskripsi kodekonten))

  (GET "/view-edit-lp" []
        (lp/search-lp "/view-edit-bab-lp" "View/Edit LP"))
  (POST "/view-edit-bab-lp" [kelas kurikulum bidangstudi]
        (lp/search-bab-lp "/view-edit-lp" "Lihat/edit Butir Pelajaran" kelas kurikulum bidangstudi))
  (POST "/view-edit-lp" [kode]
        (lp/handle-view-edit-lp kode "/edit-lp" "Edit LP"))
  (POST "/edit-lp" [nomer]
        (lp/handle-edit-lp nomer))
  (POST "/update-lp" [nomer kode keterangan kodekonten proset dp]
        (lp/handle-update-lp nomer kode keterangan kodekonten proset dp))

  (GET "/delete-lp" []
        (lp/search-lp "/bab-delete-lp" "Delete LP"))
  (POST "/bab-delete-lp" [kelas kurikulum bidangstudi]
        (lp/search-bab-lp "/view-delete-lp" "Delete LP" kelas kurikulum bidangstudi))
  (POST "/view-delete-lp" [kode]
        (lp/handle-view-edit-lp kode "/delete-lp" "Delete LP"))
  (POST "/delete-lp" [nomer kode]
        (lp/handle-delete-lp nomer kode))


  (GET "/upload-proset" []
       (layout/render "lessonplan/upload.html"))

  (POST "/upload-proset" [file]
       (io/upload-file "proset/" file)
       (response/redirect
         (str "/files/" (:filename file))))

  (GET "/files/:filename" [filename]
       (file-response (str "proset/" filename)))
)
