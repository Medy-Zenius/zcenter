(ns ssc.models.reports
  (:require [clj-pdf.core :refer [pdf template]]
            [ring.util.response :as response]
            [compojure.core :refer :all]
            [noir.session :as session]
            ;[reporting.reports :as reports]
            [ssc.views.layout :as layout]
            ;[zen.routes.regcust :as regcust]
            [ssc.models.db :as db]

  )
)

(defn pdf-table [column-widths rows]
  (into [:pdf-table column-widths]
    (map (fn [element] [:pdf-cell element]) rows)))

(defn content-report [out no nm ci fe sisa usr dt]
  (pdf
    [{}
     [:spacer 9]
     [:image {:xscale 0.1 :yscale 0.1}
      "resources/public/img/logo ZC.jpg"]
     [:paragraph {:align :center :style :bold :size 14} "TANDA TERIMA"]
     ;[:line]
     [:spacer]
     [:paragraph {:align :left :style :bold :size 12} (str "Telah diterima pembayaran ke - " ci) ]
     [:spacer]
     [:pdf-table {:border :false :cell-border :false :size 12}
       [25 40]
       ["Atas nama" nm]
       ["Nomer Zenius" no]
       ["Senilai " (str "Rp." fe ",-")]
       ["Pada (tahun-bulan-hari)" (subs (str dt) 0 10)]
       ["Sisa Cicilan" (str "Rp." sisa ",-")]
     ]
     [:spacer]
     [:paragraph {:style :bold :size 12} "Yang menerima,"]
     [:spacer 3]
     [:paragraph {:style :bold :size 12} (str "(" usr ")")]
     [:spacer 2]
     [:line]
     [:spacer 2]

     [:image {:xscale 0.1 :yscale 0.1}
      "resources/public/img/logo ZC.jpg"]
     [:paragraph {:align :center :style :bold :size 14} "TANDA TERIMA"]
     [:spacer]
     [:paragraph {:align :left :style :bold :size 12} (str "Telah diterima pembayaran ke - " ci) ]
     [:spacer]
     [:pdf-table {:border false :cell-border false :size 12}
       [25 40]
       ["Atas nama" nm]
       ["Nomer Zenius" no]
       ["Senilai " (str "Rp." fe ",-")]
       ["Pada (tahun-bulan-hari)" (subs (str dt) 0 10)]
       ["Sisa Cicilan" (str "Rp." sisa ",-")]
     ]
     [:spacer]
     [:paragraph {:style :bold :size 12} "Yang menerima,"]
     [:spacer 3]
     [:paragraph {:style :bold :size 12} (str "(" usr ")")]

    ]
   out
  )
)

(defn write-response [report-bytes fname]
(with-open [in (java.io.ByteArrayInputStream. report-bytes)]
    (-> (response/response in)
    (response/header "Content-Disposition" fname)
    (response/header "Content-Length" (count report-bytes))
    (response/content-type "application/pdf"))))

(defn fname[no ci]
  (str  "filename=" no "-" ci ".pdf")
  )

(defn tanda-terima [no nm ci fe sisa usr dt]
   (try
    (let [out (new java.io.ByteArrayOutputStream)]
    (content-report out no nm ci fe sisa usr dt)
    (write-response (.toByteArray out) (fname no ci)))
   )
)

