(ns ssc.routes.home
  (:require [compojure.core :refer :all]
            [ssc.views.layout :as layout]
            ;[noir.response :as resp]
            ;[ssc.models.db :as db]
            ;[noir.io :as io]
            ;[noir.session :as session]
            ;[noir.response :as response]
            [ring.util.anti-forgery :refer :all]
            [ring.util.response :refer [file-response]]
            [ssc.models.member :as member]
            [ssc.models.message :as message]
            ))

(defroutes home-routes
  (GET "/" [] (layout/render "home/index.html"))
  (GET "/home-login-siswa" [] (layout/render "home/loginsis.html"))
  (POST "/login-siswa" [zen pw]
        (member/loginsis zen pw))
  (GET "/home-contact" [] (layout/render "home/contact.html"))
  (POST "/home-komentar" [nama email telpon komen]
        (message/komentar nama email telpon komen))
  (GET "/home-zenedu" []
       (layout/render "home/zenedu.html"))

  (GET "/zeniusmax" [] (layout/render "home/zeniusmax.html"))
  (GET "/filosofi" [] (layout/render "home/filosofi.html"))
  (GET "/tujuan" [] (layout/render "home/tujuan.html"))
  (GET "/tutor" [] (layout/render "home/tutor.html"))
  (GET "/fasilitas" [] (layout/render "home/fasilitas.html"))
  (GET "/kontak" [] (layout/render "home/kontak.html"))
  (GET "/home-sd1-5" [] (layout/render "home/program-sd1-5.html"))
  (GET "/home-sd6" [] (layout/render "home/program-sd6.html"))
  (GET "/home-smp7-8" [] (layout/render "home/program-smp7-8.html"))
  (GET "/home-smp9" [] (layout/render "home/program-smp9.html"))
  (GET "/home-sma10-11" [] (layout/render "home/program-sma10-11.html"))
  (GET "/home-sma12" [] (layout/render "home/program-sma12.html"))
  (GET "/home-alumni" [] (layout/render "home/program-sma13.html"))
  (GET "/lokasi-cijantung" [] (layout/render "home/lokasi-cijantung.html"))
  (GET "/lokasi-cinere" [] (layout/render "home/lokasi-cinere.html"))
  (GET "/lokasi-depok" [] (layout/render "home/lokasi-depok.html"))
  (GET "/lokasi-dukuh-zamrud" [] (layout/render "home/lokasi-dukuh-zamrud.html"))
  (GET "/lokasi-fatmawati" [] (layout/render "home/lokasi-fatmawati.html"))
  ;(GET "/intsma" [] (layout/render "home/program-intsma.html"))

  (GET "/login-nonsiswa" []
       (member/login "/login"))
  (POST "/login" [id pass]
       (member/handle-login id pass))

  (GET "/logout" [] (member/logout))

)
