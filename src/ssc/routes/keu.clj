(ns ssc.routes.keu
  (:require [compojure.core :refer :all]
            [ssc.views.layout :as layout]
            ;[noir.response :as resp]
            [ssc.models.db :as db]
            [noir.io :as io]
            [noir.session :as session]
            [noir.response :as response]
            ;[ring.util.response :refer [file-response]]
            [ssc.models.member :as member]
            [ssc.models.presence :as presence]
            [ssc.models.finance :as finance]
            [ssc.models.test :as tes]
            [ssc.models.lessonplan :as lp]
            ;[ssc.models.message :as message]
            [ssc.models.register :as reg]
            [ssc.models.share :as share]
            ))

(defroutes keu-routes

  (GET "/keu-biaya-siswa" []
    (layout/render "share/search.html"
                   {:actname "/keu-search-name" :actnotes "/keu-search-notes" :keterangan "Edit Biaya Siswa"}))
  (POST "/keu-search-name" [nama]
        (share/handle-search-name nama (session/get :scode) "/keu-search-name" "/keu-search-notes"
                            "/keu-biaya-siswa" "Edit Biaya Siswa"))
  (POST "/keu-search-notes" [notes]
        (share/handle-search-notes notes (session/get :scode) "/keu-search-name" "/keu-search-notes"
                            "/keu-biaya-siswa" "Edit Biaya Siswa"))
  (POST "/keu-biaya-siswa" [notes]
        (finance/keu-edit-biaya-siswa (Integer/parseInt notes) (session/get :scode)))
  (POST "/keu-update-biaya" [notes,program,start,fee,sisa]
        (finance/keu-update-biaya notes program,start,fee,sisa))

  (GET "/keu-transaksi-siswa" []
    (layout/render "share/search.html"
                   {:actname "/trans-search-name" :actnotes "/trans-search-notes" :keterangan "Edit Transaksi Siswa"}))
  (POST "/trans-search-name" [nama]
        (share/handle-search-name nama (session/get :scode) "/trans-search-name" "/trans-search-notes"
                            "/keu-transaksi-siswa" "Edit Transaksi Siswa"))
  (POST "/trans-search-notes" [notes]
        (share/handle-search-notes notes (session/get :scode) "/trans-search-name" "/trans-search-notes"
                            "/keu-transaksi-siswa" "Edit Transaksi Siswa"))
  (POST "/keu-transaksi-siswa" [notes]
        (finance/keu-handle-list-keu-siswa (Integer/parseInt notes) (session/get :scode)))
  (POST "/keu-edit-trans" [notrans]
        (finance/keu-handle-edit-trans (Integer/parseInt notrans)))
  (POST "/keu-update-transactions" [notrans cicilan biaya dtrans]
        (finance/keu-handle-update-transactions (Integer/parseInt notrans) (Integer/parseInt cicilan)
                                                (Integer/parseInt biaya) dtrans))

  (GET "/keu-cetak-kuitansi" []
       (layout/render "share/search.html"
                   {:actname "/ck-search-name" :actnotes "/ck-search-notes" :keterangan "Cetak Kuitansi Siswa"}))
  (POST "/ck-search-name" [nama]
        (share/handle-search-name nama (session/get :scode) "/ck-search-name" "/ck-search-notes"
                            "/keu-list-transaksi-siswa" "Cetak Kuitansi Siswa"))
  (POST "/ck-search-notes" [notes]
        (share/handle-search-notes notes (session/get :scode) "/ck-search-name" "/ck-search-notes"
                            "/keu-list-transaksi-siswa" "Cetak Kuitansi Siswa"))
  (POST "/keu-list-transaksi-siswa" [notes]
        (finance/keu-list-transaksi (Integer/parseInt notes) (session/get :scode)))
  (POST "/keu-cetak-kuitansi" [notrans sisa]
        (finance/keu-cetak-kuitansi (Integer/parseInt notrans) (Integer/parseInt sisa)))

  (GET "/outflow-cdate" []
       (layout/render "finance/input-flow.html"
            {:action "/outflow-cdate"
             :data (db/get-data "select * from kodeoutflow " 2)}))
  (POST "/outflow-cdate" [kode keterangan jumlah]
       (finance/handle-save-flow (Integer/parseInt kode) keterangan (Integer/parseInt jumlah) "outflow"))

  (GET "/outflow-pdate" []
       (layout/render "finance/input-flow-pdate.html"
            {:tahun (db/get-data "select * from tahun" 2)
             :action "/outflow-pdate"
             :data (db/get-data "select * from kodeoutflow " 2)}))
  (POST "/outflow-pdate" [tanggal kode keterangan jumlah]
       (finance/handle-save-flow-pdate tanggal (Integer/parseInt kode) keterangan (Integer/parseInt jumlah) "outflow"))

  (GET "/inflow-cdate" []
       (layout/render "finance/input-flow.html"
            {:action "/inflow-cdate"
             :data (drop 1 (db/get-data "select * from kodeinflow " 2))}))
  (POST "/inflow-cdate" [kode keterangan jumlah]
       (finance/handle-save-flow (Integer/parseInt kode) keterangan (Integer/parseInt jumlah) "inflow"))

  (GET "/inflow-pdate" []
       (layout/render "finance/input-flow-pdate.html"
            {:tahun (db/get-data "select * from tahun" 2)
             :action "/inflow-pdate"
             :data (drop 1 (db/get-data "select * from kodeinflow " 2))}))
  (POST "/inflow-pdate" [tanggal kode keterangan jumlah]
       (finance/handle-save-flow-pdate tanggal (Integer/parseInt kode) keterangan (Integer/parseInt jumlah) "inflow"))

  (GET "/trans-per-day" []
      (layout/render "share/choose-date.html"
          {:tahun (db/get-data "select * from tahun" 2)
           :action "/trans-per-day"}))
  (POST "/trans-per-day" [tanggal]
      (finance/handle-trans-per-day tanggal (session/get :scode)))

  (GET "/trans-per-month" []
      (layout/render "share/choose-month.html" {:bulan (db/get-data "select * from kodebulan" 2)
                                                :action "/trans-per-month"}))
  (POST "/trans-per-month" [kodebulan]
      (finance/handle-trans-per-month (Integer/parseInt kodebulan) (session/get :scode)))

  (GET "/keu-total-transaksi" []
       (finance/handle-transactions-info (session/get :scode)))
  (GET "/keu-delete-transaksi-siswa" []
       (layout/render "share/search.html"
                   {:actname "/delete-search-name" :actnotes "/delete-search-notes" :keterangan "Delete Transaksi Siswa"}))
  (POST "/delete-search-name" [nama]
        (share/handle-search-name nama (session/get :scode) "/trans-search-name" "/trans-search-notes"
                            "/keu-delete-transaksi-siswa" "Edit Transaksi Siswa"))
  (POST "/delete-search-notes" [notes]
        (share/handle-search-notes notes (session/get :scode) "/trans-search-name" "/trans-search-notes"
                            "/keu-delete-transaksi-siswa" "Edit Transaksi Siswa"))
  (POST "/keu-delete-transaksi-siswa" [notes]
        (finance/keu-delete-list-keu-siswa (Integer/parseInt notes) (session/get :scode)))
  (POST "/keu-delete-trans" [notrans notes]
        (finance/keu-delete-trans (read-string notrans) (read-string notes)))

  (GET "/keu-edit-inflow" []
      (layout/render "finance/kata-kunci.html" {:action "/keu-list-inflow"}))
  (POST "/keu-list-inflow" [katakunci]
      (finance/keu-list-inflow katakunci (session/get :scode) "/keu-edit-hapus-inflow" "Edit Pendapatan"))
  (POST "/keu-edit-hapus-inflow" [nomer]
      (finance/keu-edit-hapus-inflow (read-string nomer) "/keu-update-inflow" "/keu-delete-inflow" "Edit/Hapus Pendapatan"))
  (POST "/keu-update-inflow" [nomer keterangan jumlah tanggal]
      (finance/keu-update-inflow (read-string nomer) keterangan (read-string jumlah) tanggal))
  (POST "/keu-delete-inflow" [nomer]
      (finance/keu-delete-inflow (read-string nomer)))

  (GET "/keu-edit-outflow" []
      (layout/render "finance/kata-kunci.html" {:action "/keu-list-outflow"}))
  (POST "/keu-list-outflow" [katakunci]
      (finance/keu-list-outflow katakunci (session/get :scode) "/keu-edit-hapus-outflow" "Edit Pengeluaran"))
  (POST "/keu-edit-hapus-outflow" [nomer]
      (finance/keu-edit-hapus-outflow (read-string nomer) "/keu-update-outflow" "/keu-delete-outflow" "Edit/Hapus Pengeluaran"))
  (POST "/keu-update-outflow" [nomer keterangan jumlah tanggal]
      (finance/keu-update-outflow (read-string nomer) keterangan (read-string jumlah) tanggal))
  (POST "/keu-delete-outflow" [nomer]
      (finance/keu-delete-outflow (read-string nomer)))

)
