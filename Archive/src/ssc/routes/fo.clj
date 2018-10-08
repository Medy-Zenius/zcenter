(ns ssc.routes.fo
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
            [ssc.models.preference :as preference]
            [ssc.models.register :as reg]
            [ssc.models.share :as share]
            ))

(defroutes fo-routes
   (GET "/daftar" []
        (reg/regcust))
   (POST "/daftar" [name pbirth birth address phone email level major school
                     pname paddress pphone pemail pjob program start fee]
        (reg/handle-regcust name pbirth birth address phone email level major school
                    pname paddress pphone pemail pjob program start fee))

   ;;payment
  (GET "/current-date" []
  ;(finance/bayar-page "/bayar-search-name" "/bayar-search-notes" "Pembayaran Hari Ini"))
   (layout/render "share/search.html"
                   {:actname "/bayar-search-name" :actnotes "/bayar-search-notes" :keterangan "Informasi Siswa"}))

  (POST "/bayar-search-name" [nama]
        (share/handle-search-name nama (session/get :scode) "/bayar-search-name" "/bayar-search-notes" "/bayar-student"
                                  "Pembayaran Hari Ini" ))
  (POST "/bayar-search-notes" [notes]
        (share/handle-search-notes notes (session/get :scode) "/bayar-search-name" "/bayar-search-notes" "/bayar-student"
                                  "Pembayaran Hari Ini"))

  (POST "/bayar-student" [notes]
        (finance/handle-payment notes))

  (POST "/payment" [notes name cicilan fee sisa]
        (finance/create-payment notes name cicilan fee sisa (session/get :id)))

  (GET "/prev-date" []
  (layout/render "share/search.html"
                 {:actname "/bayar-search-name-prev" :actnotes "/bayar-search-notes-prev" :keterangan "Pembayaran Hari Sebelumnya"}))
  (POST "/bayar-search-name-prev" [nama]
        (share/handle-search-name nama (session/get :scode) "/bayar-search-name-prev" "/bayar-search-notes-prev" "/bayar-student-prev"
                                  "Pembayaran Hari Sebelumnya"))
  (POST "/bayar-search-notes-prev" [notes]
        (share/handle-search-notes notes (session/get :scode) "/bayar-search-name-prev" "/bayar-search-notes-prev" "/bayar-student-prev"
                                  "Pembayaran Hari Sebelumnya"))
  (POST "/bayar-student-prev" [notes]
        (finance/handle-payment-prev notes))
  (POST "/payment-prev" [notes name cicilan fee sisa tanggal]
        (finance/create-payment-prev notes name cicilan fee sisa tanggal (session/get :id)))

  (GET "/laporan" []
    (finance/laporan-page))
  (POST "/laporan-search-date" [date month year]
       (finance/handle-laporan-date date month year (session/get :id)))

  (GET "/create-class" []
    (presence/create-class-page))
  (POST "/create-class" [kodekelas date month year hour minute tutor activities]
    (presence/handle-create-class kodekelas date month year hour minute tutor activities (session/get :id) (session/get :scode)))
  (POST "/input-presence" [kelas code notes tutor date]
      (presence/handle-input-presence kelas code notes tutor date))

  (GET "/input-presence" []
    (presence/input-presence-page))
  (POST "/presence-date" [kodekelas date month year]
    (presence/handle-presence-date (Integer/parseInt kodekelas) date month year (session/get :scode)))
  (POST "/input-student-presence" [kelas code]
        (presence/handle-input-student-presence kelas code))
  (POST "/input1-student-presence" [kelas date tutor activities code notes]
        (presence/handle1-input-student-presence kelas date tutor activities code notes))

  (GET "/create-test" []
    (tes/create-test-page))
  (POST "/create-test" [date month year hour minute matpel description]
    (tes/handle-create-test date month year hour minute matpel description (session/get :id) (session/get :scode)))
  (POST "/input-test" [code notes matpel score date]
      (tes/handle-input-test code notes matpel score date))

  (GET "/input-test" []
    (tes/input-test-page))
  (POST "/test-date" [date month year]
        (tes/handle-test-date date month year (session/get :scode)))
  (POST "/input-student-test" [code]
       (tes/handle-input-student-test code))
  (POST "/input1-student-test" [code notes score]
        (tes/handle1-input-student-test code notes score))

  (GET "/student-info" []
    (layout/render "share/search.html"
                   {:actname "/search-name" :actnotes "/search-notes" :keterangan "Informasi Siswa"}))
  (POST "/search-name" [nama]
        (share/handle-search-name nama (session/get :scode) "/search-name" "/search-notes"
                            "/detail-student" "Informasi Siswa Detail"))
  (POST "/detail-student" [notes]
        (share/handle-detail-student notes))
  (POST "/search-notes" [notes]
        (share/handle-search-notes notes (session/get :scode) "/search-name" "/search-notes"
                            "/detail-student" "Informasi Siswa Detail"))

  (GET "/list-class" []
    (share/list-class-page))
  (POST "/list-student" [kode]
        (share/handle-list-student kode))

  (GET "/class-info" []
    (share/class-info-page))
  (POST "/presence-info-date" [date month year]
      (share/handle-presence-info-date date month year (session/get :scode)))
  (POST "/presence-detail" [kode]
        (share/handle-presence-detail kode))

  (GET "/student-presence-info" []
       (layout/render "share/search.html"
                   {:actname "/pinfo-search-name" :actnotes "/pinfo-search-notes" :keterangan "Informasi Kehadiran Siswa"}))
  (POST "/pinfo-search-name" [nama]
        (share/handle-search-name nama (session/get :scode) "/pinfo-search-name" "/pinfo-search-notes" "/pinfo-student"
                                  "Informasi Kehadiran Siswa" ))
  (POST "/pinfo-search-notes" [notes]
        (share/handle-search-notes notes (session/get :scode) "/pinfo-search-name" "/pinfo-search-notes" "/pinfo-student"
                                  "Informasi Kehadiran siswa"))
  (POST "/pinfo-student" [notes]
      (let [vno (Integer/parseInt notes)
            data (db/get-data (str "select name from students where notes='" vno "'") 1)]
           (presence/presence-std vno (data :name) 0 10 10 "/student-presence-next")))

  (GET "/class-presence-info" []
       (layout/render "presence/choose-class-month.html"
          {:kelas (db/get-data (str "select kode,hari,keterangan from kelas where kocab='" (session/get :scode) "'") 2)
           :bulan (db/get-data "select * from kodebulan" 2)}))
  (POST "/class-presence-info" [kodekelas kodebulan]
       (presence/handle-class-presence-info (Integer/parseInt kodekelas) (Integer/parseInt kodebulan)))
  (POST "/list-pre-class-student" [kodepertemuan kelas]
       (presence/handle-list-pre-class-student (Integer/parseInt kodepertemuan) kelas))

  (GET "/student-class-presence-info" []
       (layout/render "share/search.html"
                   {:actname "/scp-search-name" :actnotes "/scp-search-notes" :keterangan "Info Presensi Siswa-Kelas"}))
  (POST "/scp-search-name" [nama]
        (share/handle-search-name nama (session/get :scode) "/scp-search-name" "/scp-search-notes" "/scp-choose-month"
                                  "Informasi Kehadiran Siswa" ))
  (POST "/scp-search-notes" [notes]
        (share/handle-search-notes notes (session/get :scode) "/scp-search-name" "/scp-search-notes" "/scp-choose-month"
                                  "Informasi Kehadiran siswa"))
  (POST "/scp-choose-month" [notes]
        (presence/handle-scp-choose-month (Integer/parseInt notes)))
  (POST "/scp-info-student" [notes kodebulan]
      (presence/handle-scp-info-student (Integer/parseInt notes) (Integer/parseInt kodebulan)))

  (GET "/student-test-info" []
       (layout/render "share/search.html"
                   {:actname "/tinfo-search-name" :actnotes "/tinfo-search-notes" :keterangan "Informasi Test Siswa"}))
  (POST "/tinfo-search-name" [nama]
        (share/handle-search-name nama (session/get :scode) "/tinfo-search-name" "/tinfo-search-notes" "/tinfo-student"
                                  "Informasi Test Siswa" ))
  (POST "/tinfo-search-notes" [notes]
        (share/handle-search-notes notes (session/get :scode) "/tinfo-search-name" "/tinfo-search-notes" "/tinfo-student"
                                  "Informasi Test Siswa"))
  (POST "/tinfo-student" [notes]
      (let [vno (Integer/parseInt notes)
            data (db/get-data (str "select name from students where notes='" vno "'") 1)]
           (tes/student-test vno (data :name) 0 10 10 "/student-test-next")))

  (GET "/payment-info" []
       (layout/render "share/search.html"
                   {:actname "/payinfo-search-name" :actnotes "/payinfo-search-notes" :keterangan "Informasi Pembayaran Siswa"}))
  (POST "/payinfo-search-name" [nama]
        (share/handle-search-name nama (session/get :scode) "/payinfo-search-name" "/payinfo-search-notes" "/payinfo-student"
                                  "Informasi Pembayaran Siswa" ))
  (POST "/payinfo-search-notes" [notes]
        (share/handle-search-notes notes (session/get :scode) "/payinfo-search-name" "/payinfo-search-notes" "/payinfo-student"
                                  "Informasi Pembayaran Siswa"))
  (POST "/payinfo-student" [notes]
      (let [vno (Integer/parseInt notes)
            data (db/get-data (str "select name from students where notes='" vno "'") 1)]
           (finance/student-finance vno (data :name))))

  (GET "/transactions-info" []
       (finance/handle-transactions-info (session/get :scode)))

  (GET "/mini-transactions-info" []
       (finance/handle-mini-transactions-info (session/get :scode)))

  (GET "/list-namasek" []
    (preference/handle-list-namasek (session/get :scode)))
  (GET "/add-namasek" []
    (preference/input-namasek))
  (POST "/add-namasek" [nama]
    (preference/handle-add-namasek nama (session/get :scode)))

  (GET "/user-profile" []
      (member/user-profile (session/get :id)))


  (GET "/user-change-password" []
       (share/change-pw (session/get :id)))
  (POST "/user-change-password" [opw npw npw1]
       (share/handle-change-pw (session/get :id) opw npw npw1))


)
