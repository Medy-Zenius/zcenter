(ns ssc.models.member
  (:require [compojure.core :refer :all]
            [ssc.views.layout :as layout]
            [noir.validation :as vali]
            ;[noir.util.crypt :as crypt]
            [ssc.models.db :as db]
            [noir.io :as io]
            [noir.session :as session]
            [noir.response :as response]
            ;[noir.util.crypt :as crypt]
            ))

(defn loginsis [zen pw]
  (let [vuser (db/get-data (str "select * from students where notes ='" (read-string zen) "'") 1)]
       (if (and vuser (= pw (vuser :pass)))
           (do (session/put! :id (vuser :notes))
               (session/put! :status 1)
               (session/put! :branch (vuser :kocab))
               (session/put! :nama (vuser :name))
               (session/put! :pass pw)
               (layout/render "home/student.html"))
           (response/redirect "/"))))

(defn login [act]
  (layout/render "home/login.html" {:action act}))
(defn handle-login [id pass]
  (let [user (db/get-data (str "select * from users where id='" id "'") 1)]
    (if (and user (= pass (:pass user)))
      (do (session/put! :id id)
          (session/put! :status (:status user))
          (session/put! :scode (:scode user))
          (session/put! :nomer (:nomer user))
          (layout/render "pesan.html" {:pesan "MIS Zeniuscenter"}))
      (response/redirect "/"))))

(defn logout []
  (session/clear!)
  (response/redirect "/"))

(defn ganti-pw-std []
  (layout/render "siswa/ganti-pw.html"))

(defn handle-ganti-pws [zen opw npw npw1]
  (let [duser (db/get-data (str "select pass from students where notes ='" zen "'") 1)]
   (if (and duser (= opw (:pass duser)))
    (if (= npw npw1)
      (try (db/update-data-1 "students" ["notes=?" zen] {:pass npw})(logout))
      (layout/render "pesan.html" {:pesan "Konfirmasi password baru gagal !"}))
    (layout/render "pesan.html" {:pesan "Password lama salah !"}) )))

(defn user-profile [id]
  (layout/render "profile.html"
          {:data (db/get-data (str "select id,email,users.name as nama,kodecabang.name as nacab from users
                                   LEFT OUTER JOIN kodecabang ON users.scode=kodecabang.code WHERE id='" id "'") 1)
           }))

(defn search-user [actid actcabang ket]
  (layout/render "share/search-user.html"
          {:actid actid :actcabang actcabang :keterangan ket
           :cabang (db/get-data (str "select * from kodecabang order by code") 2)}))

(defn search-user-id [id actid actcabang actedit ket]
  (layout/render "share/res-search-user.html"
        {:actid actid :actcabang actcabang :actedit actedit :keterangan ket
         :cabang (db/get-data (str "select * from kodecabang order by code") 2)
         :data (db/get-data (str "select nomer,id,users.name as nama,kodecabang.name as nacab from users
                                 LEFT OUTER JOIN kodecabang ON users.scode=kodecabang.code WHERE
                                 id LIKE '%" id "%' order by id") 2)}))
(defn search-user-cabang [code actid actcabang actedit ket]
  (layout/render "share/res-search-user.html"
        {:actid actid :actcabang actcabang :actedit actedit :keterangan ket
         :cabang (db/get-data (str "select * from kodecabang order by code") 2)
         :data (db/get-data (str "select nomer,id,users.name as nama,kodecabang.name as nacab from users
                                 LEFT OUTER JOIN kodecabang ON users.scode=kodecabang.code WHERE
                                 users.scode = '" code "' order by id") 2)}))

(defn handle-user-edit [nomer]
  (layout/render "share/user-edit.html"
      {:data (db/get-data (str "select id,name,status,email,scode from users where nomer='" nomer "'") 1)}))

(defn user-update-data [id nama status email kocab]
  (try (db/update-data "users" (str "id='" id "'")
       {:name nama
        :status status
        :email email
        :scode kocab
        })
    (layout/render "pesan.html" {:pesan (str "Berhasil Update ID " id)})
    (catch Exception ex
                  (layout/render "pesan.html" {:pesan (str "Gagal Update Data error: " ex)}))))

(defn valid? [id]
  (vali/rule (vali/has-value? id)
    [:id "user id is required"])
  (not (vali/errors? :id)))

(defn format-error [id ex]
  (cond
    (and (instance? org.postgresql.util.PSQLException ex)
      (= 0 (.getErrorCode ex)))
    (str "The user with id " id " already exists!")
    :else
    "An error has occured while processing the request"))

(defn user-add [& [kd id nm em st]]
(layout/render "share/user-add.html"
{ :kode kd
  :id id
  :name nm
  :email em
  :stat st
  :id-error (first (vali/get-errors :id))}))

(defn handle-user-add [kd nm em id st]
  (if (valid? id)
    (try
      (db/insert-data "users" {:scode kd :name nm :email em :id id :pass "abcde" :status st})
      (layout/render "pesan.html"
         {:pesan (str "Berhasil registrasi User dengan ID = " id)})
      (catch Exception ex
        (vali/rule false [:id (format-error id ex)])
        (user-add)))
      (user-add kd id nm em st)))

(defn handle-user-delete [nomer]
  (try
    (db/delete-data "users" (str "nomer='" nomer "'"))
    (layout/render "pesan.html" {:pesan "Berhasil Delete Data !"})
    (catch Exception ex
                  (layout/render "pesan.html" {:pesan (str "Gagal Update Data error: " ex)}))))


