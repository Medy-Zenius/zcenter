(ns ssc.routes.owner
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

(defroutes owner-routes
  (GET "/info-jumlah-siswa" []
     (share/handle-jumlah-siswa (session/get :scode)))
  (GET "/info-komposisi-siswa" []
     (share/handle-komposisi-siswa (session/get :scode)))
  (GET "/info-asal-sekolah" []
     (share/handle-asal-sekolah (session/get :scode)))

  (GET "/info-biaya-total-siswa" []
      (let [kocab (session/get :scode)]
        (finance/handle-rekap kocab "Biaya Bimbel"
            (str "select sum (fee) from sfinance where kocab='" kocab "'"))))
  (GET "/info-angsuran-siswa" []
     (let [kocab (session/get :scode)]
      (finance/handle-rekap kocab "Transaksi"
          (str "select sum (biaya) from transactions where kocab='" kocab "'"))))
  (GET "/info-piutang-siswa" []
     (let [kocab (session/get :scode)]
      (finance/handle-rekap kocab "Piutang"
          (str "select sum (sisa) from sfinance where kocab='" kocab "'"))))

  (GET "/info-inflow" []
     (share/handle-info-inflow (session/get :scode) "Pemasukan (Inflow)"))
  (GET "/info-outflow" []
     (share/handle-info-outflow (session/get :scode) "Pengeluaran (Outflow)"))
  )
