(ns ssc.routes.admin
  (:require [compojure.core :refer :all]
            [ssc.views.layout :as layout]
            ;[noir.response :as resp]
            [ssc.models.db :as db]
            [noir.io :as io]
            [noir.session :as session]
            [noir.response :as response]
            ;[ring.util.response :refer [file-response]]
            [ssc.models.member :as member]
            [ssc.models.share :as share]
            [ssc.models.finance :as finance]
            ;[ssc.models.test :as tes]
            ;[ssc.models.lessonplan :as lp]
            ;[ssc.models.message :as message]
            ;[ssc.models.register :as reg]
            [ssc.models.preference :as preference]
            ))

(defroutes admin-routes

  (GET "/admin-student-info" []
     (layout/render "info/get-scode.html" {:action "/admin-student-info"}))
  (POST "/admin-student-info" [kocab]
     (layout/render "share/search.html"
                   {:actname "/admin-search-name" :actnotes "/admin-search-notes" :keterangan "Informasi Siswa" :kocab kocab}))
  (POST "/admin-search-name" [nama kocab]
        (share/handle-search-name nama (Integer/parseInt kocab) "/admin-search-name" "/admin-search-notes"
                            "/detail-student" "Informasi Siswa Detail"))
  (POST "/admin-search-notes" [notes kocab]
        (share/handle-search-notes notes (Integer/parseInt kocab) "/admin-search-name" "/admin-search-notes"
                            "/detail-student" "Informasi Siswa Detail"))

  (GET "/admin-student-delete" []
      (layout/render "info/get-scode.html" {:action "/admin-student-delete"}))
  (POST "/admin-student-delete" [kocab]
      (layout/render "share/search.html"
          {:actname "/admin-search-name-del" :actnotes "/admin-search-notes-del" :keterangan "Delete Siswa" :kocab kocab}))
  (POST "/admin-search-name-del" [nama kocab]
        (share/handle-search-name nama (Integer/parseInt kocab) "/admin-search-name-del" "/admin-search-notes-del"
                            "/admin-do-delete-student" "Delete Siswa"))
  (POST "/admin-search-notes-del" [notes kocab]
        (share/handle-search-notes notes (Integer/parseInt kocab) "/admin-search-name-del" "/admin-search-notes-del"
                            "/admin-do-delete-student" "Delete Siswa"))
  (POST "/admin-do-delete-student" [notes]
        (share/handle-delete-student (Integer/parseInt notes)))

  (GET "/users-list" []
     (layout/render "info/list-users.html"
         {:data (db/get-data (str "select id,status,email,users.name as nama,kodecabang.name as nacab from users
                                  LEFT OUTER JOIN kodecabang ON users.scode=kodecabang.code order by nacab") 2)
          }))
  (GET "/user-edit" []
     (member/search-user "/user-search-id" "/user-search-cabang" "Edit User"))
  (POST "/user-search-id" [id]
     (member/search-user-id id "/user-search-id" "/user-search-cabang" "/user-edit" "Edit User"))
  (POST "/user-search-cabang" [code]
     (member/search-user-cabang (Integer/parseInt code) "/user-search-id" "/user-search-cabang" "/user-edit" "Edit User"))
  (POST "/user-edit" [nomer]
     (member/handle-user-edit (Integer/parseInt nomer)))
  (POST "/user-update-data" [id nama status email kocab]
     (member/user-update-data id nama (Integer/parseInt status) email (Integer/parseInt kocab)))
  (POST "/user-resetpw" [id]
     (share/handle-resetpwid id))

  (GET "/user-add" []
     (member/user-add))
  (POST "/user-add" [kode nama email id stat]
    (member/handle-user-add (Integer/parseInt kode) nama email id (Integer/parseInt stat)))

  (GET "/user-delete" []
     (member/search-user "/duser-search-id" "/duser-search-cabang" "Edit User"))
  (POST "/duser-search-id" [id]
     (member/search-user-id id "/duser-search-id" "/duser-search-cabang" "/user-delete" "Delete User"))
  (POST "/duser-search-cabang" [code]
     (member/search-user-cabang (Integer/parseInt code) "/duser-search-id" "/duser-search-cabang" "/user-delete" "Delete User"))
  (POST "/user-delete" [nomer]
     (member/handle-user-delete (Integer/parseInt nomer)))

  (GET "/list-kodecabang" []
     (layout/render "info/kode.html"
          {:keterangan "Kode Cabang"
           :data (db/get-data (str "select * from kodecabang order by code") 2)}))
  (GET "/add-cabang" []
     (preference/handle-add-ref "Nama Cabang" "/add-cabang"))
  (POST "/add-cabang" [nama]
     (preference/add-ref nama "kodecabang"))

  (GET "/list-kodestatus" []
     (layout/render "info/kode.html"
          {:keterangan "Kode Status"
           :data (db/get-data (str "select * from kodestatus order by code") 2)}))

  (GET "/list-acc-outflow" []
     (layout/render "info/kode.html"
          {:keterangan "Kode Outflow"
           :data (db/get-data (str "select * from kodeoutflow order by code") 2)}))
  (GET "/add-acc-outflow" []
     (preference/handle-add-ref "Nama Account" "/add-acc-outflow"))
  (POST "/add-acc-outflow" [nama]
     (preference/add-ref nama "kodeoutflow"))

  (GET "/list-acc-inflow" []
     (layout/render "info/kode.html"
          {:keterangan "Kode Inflow"
           :data (db/get-data (str "select * from kodeinflow order by code") 2)}))
  (GET "/add-acc-inflow" []
     (preference/handle-add-ref "Nama Account" "/add-acc-inflow"))
  (POST "/add-acc-inflow" [nama]
     (preference/add-ref nama "kodeinflow"))

  (GET "/rekap-biaya-bimbel" []
       (finance/rekap "/rekap-fee" "Rekapitulasi Biaya Bimbel"))
  (POST "/rekap-fee" [kocab]
        (finance/handle-rekap kocab "Biaya Bimbel"
            (str "select sum (fee) from sfinance where kocab='" (Integer/parseInt kocab) "'")))
  (GET "/rekap-pembayaran" []
       (finance/rekap "/rekap-trans" "Rekapitulasi Transaksi"))
  (POST "/rekap-trans" [kocab]
        (finance/handle-rekap kocab "Transaksi"
          (str "select sum (biaya) from transactions where kocab='" (Integer/parseInt kocab) "'")))


  )

