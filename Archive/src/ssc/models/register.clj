(ns ssc.models.register
  (:require [compojure.core :refer :all]
            [ssc.views.layout :as layout]
            [noir.validation :as vali]
            [clj-time [format :as timef] [coerce :as timec]]
            [ssc.models.db :as db]
            [noir.io :as io]
            [noir.session :as session]
            [noir.response :as response]
            [noir.util.crypt :as crypt]
            ))

(defn two-char [s]
  (if (< (count s) 2)(apply str ["0" s])(apply str [s])))

(defn string-date[y m d]
  (apply str [y "-" (two-char m) "-" (two-char d) " 10:10:10"]))

(defn str-date-Timestamp [waktu]
  (java.sql.Timestamp/valueOf (str waktu " 10:10:10")))

(defn strDTimestamp [y m d]
  (->> (string-date y m d)
     (timef/parse (timef/formatter "yyyy-MM-dd hh:mm:ss"))
     timec/to-timestamp))

(defn str-date [d m y]
 (apply str [d "/" m "/" y]))

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))
;validation
(defn valid? [nm ad ph]
  (vali/rule (vali/has-value? nm)
    [:name "Nama harus diisi!"])
  (vali/rule (vali/has-value? ad)
    [:address "Alamat harus diisi!"])
  (vali/rule (vali/has-value? ph)
    [:phone "Telepon harus diisi!"])
  ;(vali/rule (= pass pass1)
  ;  [:pass "entered passwords do not match"])
  (not (vali/errors? :name :address :phone)))

(defn regcust [& [nm ad ph]]
  (layout/render "register/regcust.html"
    {:name nm
     :address ad
     :phone ph
     :name-error (first (vali/get-errors :name))
     :address-error (first (vali/get-errors :address))
     :phone-error (first (vali/get-errors :phone))
     :namasek (db/get-data (str "select nama from namasek where kocab='" (session/get :scode) "' order by nama asc") 2)
     :programs (db/get-data (str "select * from kodeprogram order by code asc") 2)
     :kojur (db/get-data (str "select * from kodejurusan order by kode asc") 2)}
  ))

(defn handle-regcust [nm pb bi ad ph em le ma sc pnm pad pph pem pjo pro sta fee]
(if (valid? nm ad ph)
 (try (db/insert-data "students"
                     {:name nm
                      :pbirth pb
                      :dbirth (str-date-Timestamp bi)
                      :address ad
                      :phone ph
                      :email em
                      :level (Integer/parseInt le)
                      :major (Integer/parseInt ma)
                      :school sc
                      :pass "abcd"
                      :pname pnm
                      :paddress pad
                      :pphone pph
                      :pemail pem
                      :pjob pjo
                      :id (session/get :id)
                      :kocab (session/get :scode)
                      :dtreg  (java.sql.Timestamp. (.getTime (java.util.Date.)))
                      })

       (let [vid (db/get-data (str "select notes,name from students where name ='" nm "' and address='" ad "' and
                                  phone ='" ph "' and id ='" (session/get :id) "'") 1)]

         (db/insert-data "sfinance" {:notes (vid :notes)
                                     :kocab (session/get :scode)
                                     :dtreg (java.sql.Timestamp. (.getTime (java.util.Date.)))
                                     :id (session/get :id)
                                     :fee (Integer/parseInt fee)
                                     :sisa (Integer/parseInt fee)
                                     :program (Integer/parseInt pro)
                                     :start (Integer/parseInt sta)
                                     })
         (layout/render "pesan.html" {:pesan (str "Berhasil mendaftarkan " nm " dengan Nomer Zenius " (vid :notes))})))
  (regcust nm ad ph)
  ))
