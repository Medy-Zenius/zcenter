(ns ssc.models.finance
  (:require
      ;[hiccup.form :refer :all]
      [compojure.core :refer :all]
      ;[zen.routes.home :refer :all]
      [ssc.views.layout :as layout]
      [noir.session :as session]
      ;[noir.validation :as vali]
      ;[noir.response :as resp]
      ;[noir.util.crypt :as crypt]
      [clj-time [format :as timef] [coerce :as timec]]
      [ssc.models.db :as db]
      [ssc.models.register :as register]
      [ssc.models.reports :as reports]
  )
)

(defn TStamp []
  (java.sql.Timestamp. (.getTime (java.util.Date.))))

(defn student-finance [no nm]
   (layout/render "finance/std-payment.html"
     {
      :notes no
      :name nm
      :transactions (db/get-data (str "select * from transactions where notes='" no "' order by notrans asc") 2)
      :jumlah (db/get-data (str "select sum (biaya) from transactions where notes ='" no "'") 2)
     }))

(defn bayar-page [act_name act_notes ket]
    (layout/render "finance/bayar-search.html" {
                                           :actname act_name
                                           :actnotes act_notes
                                           :keterangan ket
                                           }))

(defn handle-bayar-search-name [act_name act_notes act_next ket nm br]
   (layout/render "finance/res-bayar-search-name.html"
     {
      :students (db/get-data (str "select name,notes from students where name LIKE '%" nm "%'
                                  and kocab='" br "' order by name ASC" ) 2)
      :actname act_name
      :actnotes act_notes
      :actnext act_next
      :keterangan ket}
   ))

(defn handle-bayar-search-notes [act_name act_notes act_next ket no br]
   (layout/render "finance/res-bayar-search-name.html"
     {
      :students (db/get-data (str "select notes,name from students where notes ='" (Integer/parseInt no) "' and kocab ='" br "'") 2)
      :actname act_name
      :actnotes act_notes
      :actnext act_next
      :keterangan ket}
   ))

(defn handle-payment [no]
  (let [nno (Integer/parseInt no)
        nm (:name (db/get-data (str "select name from students where notes='" nno "'") 1))
        vid (db/get-data (str "select fee,sisa from sfinance where notes='" nno "'") 1)
        cci (+ 1 (:count (db/get-data (str "select count(cicilan) from transactions where notes='" nno "'") 1)))]
  (layout/render "finance/student-payment.html"
   {
    :notes no
    :name nm
    :total (vid :fee)
    :sisanya (vid :sisa)
    :ncicilan cci
    })
  ))

(defn handle-payment-prev [no]
  (let [nno (Integer/parseInt no)
        nm (:name (db/get-data (str "select name from students where notes='" nno "'") 1))
        vid (db/get-data (str "select fee,sisa from sfinance where notes='" nno "'") 1)
       cci (+ 1 (:count (db/get-data (str "select count(cicilan) from transactions where notes='" nno "'") 1)))]
  (layout/render "finance/student-payment-prev.html"
   {:ncicilan cci
    :notes no
    :name nm
    :total (vid :fee)
    :sisanya(vid :sisa)
    :tahun (db/get-data "select * from tahun" 2)
    })
  ))

(defn create-payment [no nm ci fe sisa usr]
   (let [namap (:name (db/get-data (str "select name from users where id='" usr "'") 1))
         task1 (db/insert-data "transactions"
                       {:notes (Integer/parseInt no)
                        :cicilan (Integer/parseInt ci)
                        :biaya (Integer/parseInt fe)
                        :id usr
                        :kocab (session/get :scode)
                        :dtrans  (TStamp)})
         task2 (db/update-data "sfinance" (str "notes='" no "'") {:sisa (Integer/parseInt sisa)})
         task3 (db/insert-data "inflow"
                       {:kode 1
                        :keterangan (str "Pembayaran ke " ci " zen " no)
                        :jumlah (Integer/parseInt fe)
                        :id usr
                        :kocab (session/get :scode)
                        :date (TStamp)})]
        (reports/tanda-terima no nm ci fe sisa namap (TStamp))))

(defn create-payment-prev [no nm ci fe sisa tanggal usr]
   (let [namap (:name (db/get-data (str "select name from users where id='" usr "'") 1))
         task1 (db/insert-data "transactions"
                       {:notes (Integer/parseInt no)
                        :cicilan (Integer/parseInt ci)
                        :biaya (Integer/parseInt fe)
                        :id usr
                        :kocab (session/get :scode)
                        :dtrans  (register/str-date-Timestamp tanggal)})
         task2 (db/update-data "sfinance" (str "notes='" no "'") {:sisa (Integer/parseInt sisa)})
         task3 (db/insert-data "inflow"
                       {:kode 1
                        :keterangan (str "Pembayaran ke " ci " zen " no)
                        :jumlah (Integer/parseInt fe)
                        :id usr
                        :kocab (session/get :scode)
                        :date (register/str-date-Timestamp tanggal)})]
    (reports/tanda-terima no nm ci fe sisa namap (register/str-date-Timestamp tanggal))))

(defn laporan-page []
    (layout/render "finance/laporan-search.html"
       {:tahun (db/get-data "select * from tahun" 2)}))

(defn handle-laporan-date [d m y usr]
  (layout/render "finance/laporan-harian.html"
    {
     :date (subs (register/string-date  y m d) 0 10)
     :transactions (db/get-data (str "select * from transactions where cast (dtrans as date) = '"
                                     (subs (register/string-date y m d) 0 10) "' and id = '" usr "'") 2)
     :jumlah (db/get-data (str "select sum (biaya) from transactions where cast (dtrans as date) = '"
                               (subs (register/string-date y m d) 0 10) "' and id = '" usr "'") 2)
    }))

(defn keu-edit-biaya-siswa [no br]
  (layout/render "finance/edit-biaya.html"
        {:datasis (db/get-data (str "select sfinance.notes as zen,sfinance.program as prg,fee,sfinance.id as idf,
                                    sfinance.dtreg as date,sfinance.start as bulan,sisa,name
                                    from sfinance INNER JOIN students ON sfinance.notes=students.notes
                                    where sfinance.notes='" no "' and sfinance.kocab='" br "'") 2)
         }))

(defn keu-update-biaya [no,pr,st,fe,si]
  (try
    (db/update-data "sfinance" (str "notes='" no "'")
       {:program (Integer/parseInt pr)
        :start (Integer/parseInt st)
        :fee (Integer/parseInt fe)
        :sisa (Integer/parseInt si)
        })
    (layout/render "pesan.html" {:pesan (str "Berhasil Update Data Biaya Siswa dengan ZEN " no)})
    (catch Exception ex
                  (layout/render "pesan.html" {:pesan (str "Gagal Update Data error: " ex)}))))

(defn keu-handle-list-keu-siswa [no br]
  (layout/render "finance/list-keu-siswa.html"
   {:notes no
    :nama (db/get-data (str "select name from students where notes='" no "' and kocab='" br "'") 2)
    :total (db/get-data (str "select fee from sfinance where notes='" no "' and kocab='" br "'") 2)
    :jumlah (db/get-data (str "select sum (biaya) from transactions where notes ='" no "' and kocab='" br "'") 2)
    :transactions (db/get-data (str "select * from transactions where notes ='" no "' and kocab='" br "' order by notrans asc") 2)
    }))

(defn keu-delete-trans [notrans notes]
  (try
    (db/delete-data "transactions" (str "notrans='" notrans "'"))
    (layout/render "pesan.html" {:pesan "Berhasil Hapus Data Transaksi Siswa !"})
    (catch Exception ex
                  (layout/render "pesan.html" {:pesan (str "Gagal Hapus Data Transaksi error: " ex)}))))

(defn keu-delete-list-keu-siswa [no br]
  (layout/render "finance/delete-list-keu-siswa.html"
   {:notes no
    :nama (db/get-data (str "select name from students where notes='" no "' and kocab='" br "'") 2)
    :total (db/get-data (str "select fee from sfinance where notes='" no "' and kocab='" br "'") 2)
    :jumlah (db/get-data (str "select sum (biaya) from transactions where notes ='" no "' and kocab='" br "'") 2)
    :transactions (db/get-data (str "select * from transactions where notes ='" no "' and kocab='" br "' order by notrans asc") 2)
    }))

(defn keu-handle-edit-trans [notrans]
  (layout/render "finance/edit-trans.html"
   {:notrans notrans
    :transactions (db/get-data (str "select * from transactions where notrans ='" notrans "'") 2)
    }))

(defn keu-handle-update-transactions [notrans cicilan biaya dtrans]
  (try
    (db/update-data "transactions" (str "notrans='" notrans "'")
      {:cicilan cicilan
       :biaya biaya
       :dtrans (java.sql.Timestamp/valueOf dtrans)
       })
   (layout/render "pesan.html" {:pesan (str "Berhasil Update Transaksi Siswa")})
    (catch Exception ex
                  (layout/render "pesan.html" {:pesan (str "Gagal Update Data error: " ex)}))))

(defn keu-list-transaksi [no br]
  (layout/render "finance/list-transaksi.html"
      {:sisa (:sisa (db/get-data (str "select sisa from sfinance where notes='" no "' and kocab='" br "'") 1))
       :transactions (db/get-data (str "select notrans,dtrans,cicilan,biaya,id from transactions
                                       where notes='" no "' and kocab='" br "'") 2)
       :notes no
       }))

(defn keu-cetak-kuitansi [notrans sisa]
  (let [dkui (db/get-data (str "select transactions.notes as no,name,cicilan,biaya,transactions.id as id,dtrans from transactions
                               INNER JOIN students ON transactions.notes=students.notes where notrans ='" notrans "'") 1)]
    (reports/tanda-terima (str (:no dkui)) (:name dkui) (:cicilan dkui) (:biaya dkui) sisa (:id dkui) (:dtrans dkui))
    ))

(defn rekap [act ket]
    (layout/render "share/list-cabang.html"
        {:action act :keterangan ket
         :cabangs (db/get-data "select * from kodecabang" 2)
         }))

(defn handle-rekap [br ket query]
  (layout/render "finance/rekap.html"
      {:jumlah (db/get-data query 2)
       :keterangan ket
       :kocab br
       }))

(defn handle-save-flow [ko ket jum tbl]
  (try
    (db/insert-data tbl
                    {:kode ko
                     :keterangan ket
                     :jumlah jum
                     :id (session/get :id)
                     :kocab (session/get :scode)
                     :date (TStamp)})
    (layout/render "pesan.html" {:pesan "Berhasil Simpan Data  !"})
  (catch Exception ex
                  (layout/render "pesan.html" {:pesan (str "Gagal Simpan Data error: " ex)}))))

(defn handle-save-flow-pdate [tanggal ko ket jum tbl]
  (try
    (db/insert-data tbl
                    {:kode ko
                     :keterangan ket
                     :jumlah jum
                     :id (session/get :id)
                     :kocab (session/get :scode)
                     :date (register/str-date-Timestamp tanggal)})
    (layout/render "pesan.html" {:pesan "Berhasil Simpan Data  !"})
  (catch Exception ex
                  (layout/render "pesan.html" {:pesan (str "Gagal Simpan Data error: " ex)}))))

(defn handle-trans-per-day [tgl cab]
  (let [sumin (:sumin (db/get-data (str "select sum (jumlah) as sumin from inflow where cast (date as date) = '"
                                     tgl "' and kocab = '" cab "'") 1))
        sumout (:sumout (db/get-data (str "select sum (jumlah) as sumout from outflow where cast (date as date) = '"
                                     tgl "' and kocab = '" cab "'") 1))]
   (layout/render "finance/list-trans-day.html"
        {:keterangan (str "Tanggal " tgl)
         :datain (db/get-data (str "select jumlah,keterangan,id from inflow where cast (date as date) = '"
                                     tgl "' and kocab = '" cab "'") 2)
         :dataout (db/get-data (str "select jumlah,keterangan,id from outflow where cast (date as date) = '"
                                     tgl "' and kocab = '" cab "'") 2)
         :totalin (if sumin sumin 0)
         :totalout (if sumout sumout 0)
         :saldo (- (if sumin sumin 0) (if sumout sumout 0))
         })))

(defn handle-trans-per-month [kbul cab]
  (let [sumin (:sumin (db/get-data (str "select sum (jumlah) as sumin from inflow where extract(month from date) = '"
                                     kbul "' and kocab = '" cab "'") 1))
        sumout (:sumout (db/get-data (str "select sum (jumlah) as sumout from outflow where extract(month from date) = '"
                                     kbul "' and kocab = '" cab "'") 1))]
   (layout/render "finance/list-trans-day.html"
        {:keterangan (str "Bulan " (:bulan (db/get-data (str "select bulan from kodebulan where kode='" kbul "'") 1)))
         :datain (db/get-data (str "select jumlah,keterangan,id from inflow where extract(month from date) = '"
                                     kbul "' and kocab = '" cab "'") 2)
         :dataout (db/get-data (str "select jumlah,keterangan,id from outflow where extract(month from date) = '"
                                     kbul "' and kocab = '" cab "'") 2)
         :totalin (if sumin sumin 0)
         :totalout (if sumout sumout 0)
         :saldo (- (if sumin sumin 0) (if sumout sumout 0))
         })))

(defn handle-transactions-info [cab]
  (let [bdata (db/get-data (str "select transactions.notes,notrans,date(dtrans) as tanggal, cicilan as ke, biaya, name, phone, pphone,
                                fee, sisa from students INNER JOIN sfinance ON (students.notes=sfinance.notes)
                               INNER JOIN transactions ON (sfinance.notes=transactions.notes)
                               where transactions.kocab ='" cab "' order by transactions.notes, tanggal") 2)]

  (layout/render "finance/total-trans.html"
       {:data bdata})
    ))

(defn handle-mini-transactions-info [cab]
  ())

;; Edit Pendapatan
(defn keu-list-inflow [katakunci br act ket]
  (layout/render "finance/list-flow.html"
                 {:data (db/get-data (str "select * from inflow where keterangan LIKE '%" katakunci "%' and kocab='" br "'
                                          order by keterangan") 2)
                  :action act
                  :keterangan ket}
    ))

(defn keu-edit-hapus-inflow [no act1 act2 ket]
  (layout/render "finance/edit-hapus-flow.html"
                 {:data (db/get-data (str "select * from inflow where nomer='" no "'") 1)
                  :action1 act1
                  :action2 act2
                  :keterangan ket}))

(defn keu-delete-inflow [no]
  (try
    (db/delete-data "inflow" (str "nomer='" no "'"))
    (layout/render "pesan.html" {:pesan "Berhasil Hapus Data Pendapatan!"})
    (catch Exception ex
                  (layout/render "pesan.html" {:pesan (str "Gagal Hapus Data Pendapatan error: " ex)}))))

(defn keu-update-inflow [no ket jum tgl]
  (try
  (db/update-data "inflow" (str "nomer='" no "'")
    {:keterangan ket :jumlah jum :date (java.sql.Timestamp/valueOf tgl)})
    (layout/render "pesan.html" {:pesan "Berhasil Update Data Pendapatan !"})
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Gagal Update Data Pendapatan error: " ex)}))))

;; Edit Pengeluaran
(defn keu-list-outflow [katakunci br act ket]
  (layout/render "finance/list-flow.html"
                 {:data (db/get-data (str "select * from outflow where keterangan LIKE '%" katakunci "%' and kocab='" br "'
                                          order by keterangan") 2)
                  :action act
                  :keterangan ket}
    ))

(defn keu-edit-hapus-outflow [no act1 act2 ket]
  (layout/render "finance/edit-hapus-flow.html"
                 {:data (db/get-data (str "select * from outflow where nomer='" no "'") 1)
                  :action1 act1
                  :action2 act2
                  :keterangan ket}))

(defn keu-delete-outflow [no]
  (try
    (db/delete-data "outflow" (str "nomer='" no "'"))
    (layout/render "pesan.html" {:pesan "Berhasil Hapus Data Pengeluaran!"})
    (catch Exception ex
                  (layout/render "pesan.html" {:pesan (str "Gagal Hapus Data Pengeluaran error: " ex)}))))

(defn keu-update-outflow [no ket jum tgl]
  (try
  (db/update-data "outflow" (str "nomer='" no "'")
    {:keterangan ket :jumlah jum :date (java.sql.Timestamp/valueOf tgl)})
    (layout/render "pesan.html" {:pesan "Berhasil Update Data Pengeluaran !"})
  (catch Exception ex
    (layout/render "pesan.html" {:pesan (str "Gagal Update Data Pengeluaran error: " ex)}))))
