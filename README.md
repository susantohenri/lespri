# Lespri — Product Requirements Document (PRD)

> **Status:** Draft v1.0  
> **Tanggal:** 15 November 2026  
> **Platform:** Android  
> **Bahasa aplikasi:** Indonesia dan English  
> **Target MVP:** Pengelola bimbel rumahan/skala kecil dengan sekitar 5–30 siswa  
> **Model MVP:** Gratis dengan AdMob; tidak wajib registrasi; data utama disimpan lokal di perangkat.

---

## 1. Ringkasan Produk

Lespri adalah aplikasi Android ringan untuk membantu pemilik/pengelola bimbel rumahan mengelola administrasi operasional harian: siswa, tutor, kelas, jadwal, kehadiran, tagihan, pembayaran, dan pengingat.

Lespri ditujukan untuk bimbel kecil yang sudah berjalan dan kemungkinan memiliki data siswa di buku, Excel, atau CSV. Produk harus membuat pengelola bisa mulai menggunakan aplikasi tanpa harus membuat akun, memindahkan seluruh data secara manual, atau mempelajari sistem administrasi yang kompleks.

Lespri bukan LMS, marketplace tutor, atau software ERP sekolah. Fokus utamanya adalah administrasi bimbel yang cepat, mudah dipindai, dan tetap berjalan tanpa internet.

### 1.1 Positioning

> Administrasi bimbel rumahan yang rapi dan cepat, tanpa harus bergantung pada Excel, buku catatan, atau pesan WhatsApp yang tersebar.

### 1.2 Nilai utama

- Langsung ke masalah utama: pengelola dapat melihat jadwal hari ini, kehadiran, dan pembayaran yang perlu ditindaklanjuti.
- Mudah dipahami sejak pertama dibuka: navigasi jelas, istilah sederhana, dan alur kerja pendek.
- Tidak memaksa migrasi besar: data awal dapat diimpor dari CSV; file Excel dapat dikonversi ke CSV.
- Offline-first: data dan fungsi operasional inti tetap tersedia tanpa koneksi internet.
- Kontrol tetap di tangan pengguna: pengingat muncul sebagai notifikasi lokal; pengguna sendiri yang memutuskan apakah akan membagikan pesan ke orang tua/siswa.
- Data portabel: pengguna dapat mengekspor data dan membuat backup penuh kapan saja.

---

## 2. Tujuan dan Ukuran Keberhasilan

### 2.1 Tujuan MVP

- Mengurangi pekerjaan administratif berulang untuk bimbel berisi 5–30 siswa.
- Memudahkan pengelola mengetahui siapa yang hadir, jadwal yang berlangsung, dan pembayaran yang belum lunas.
- Mengakomodasi kelas privat maupun kelas kelompok.
- Memungkinkan satu pemilik mengelola beberapa tutor.
- Mendukung skema pembayaran bulanan, paket sesi, dan per sesi.
- Memungkinkan pemakaian tanpa akun dan tanpa koneksi internet.
- Menghasilkan peluang pendapatan melalui AdMob tanpa mengganggu pekerjaan inti.

### 2.2 Indikator keberhasilan produk

| Metrik | Definisi | Target validasi awal |
|---|---|---|
| Time to first student | Waktu dari aplikasi dibuka pertama kali sampai satu siswa tersimpan | ≤ 3 menit |
| Setup completion | Pengguna berhasil menyelesaikan setup awal dan membuat/mengimpor data | ≥ 70% pengguna uji |
| First-week activation | Pengguna membuat minimal satu kelas/jadwal dan mencatat kehadiran dalam 7 hari | ≥ 60% pengguna uji |
| Weekly active usage | Pengguna membuka aplikasi untuk aktivitas operasional minimal sekali per minggu | Dipantau selama pilot |
| Import success | Baris valid berhasil masuk tanpa kehilangan data | ≥ 95% baris valid pada file uji |
| Backup restore success | Backup valid berhasil dipulihkan pada instalasi/perangkat uji | 100% skenario QA |
| Core task completion | Pengguna dapat mencatat kehadiran dan pembayaran tanpa bantuan | ≥ 90% pengguna uji |
| Ad impact | Ad tidak menghalangi alur inti atau meningkatkan accidental tap | Tidak ada gangguan pada tugas inti |

### 2.3 Bukan tujuan MVP

- Menggantikan proses belajar-mengajar atau menyediakan materi belajar.
- Menyediakan kelas online/video conference.
- Menjadi sistem pembayaran digital atau memverifikasi transfer bank otomatis.
- Mengirim WhatsApp/DM secara otomatis.
- Mengelola payroll tutor secara kompleks.
- Menyediakan portal/login terpisah untuk tutor, siswa, atau orang tua.
- Menyediakan sinkronisasi cloud atau multi-device.
- Menjadi aplikasi untuk tutor privat independen yang tidak mengelola bimbel.

---

## 3. Target Pengguna dan Batasan Penggunaan

### 3.1 Persona utama: Pemilik/pengelola bimbel rumahan

- Mengelola sekitar 5–30 siswa.
- Memiliki satu atau beberapa tutor.
- Mengelola kelas privat satu siswa atau kelas kelompok.
- Menggunakan WhatsApp untuk komunikasi dengan orang tua/siswa.
- Sebagian data mungkin sudah tersimpan di buku, Excel, atau Google Sheets.
- Membutuhkan ringkasan operasional, bukan laporan akuntansi yang rumit.
- Dapat menjalankan usaha dari rumah atau lokasi bimbel kecil.

### 3.2 Peran pengguna MVP

Hanya satu peran aplikasi: Owner/Admin.

Owner dapat membuat dan mengelola beberapa data tutor, tetapi tutor belum memiliki akun atau akses login sendiri. Seluruh perubahan data dilakukan dari satu instalasi/perangkat yang digunakan pemilik.

### 3.3 Batasan skala MVP

- Fokus pengalaman dan pengujian pada 5–30 siswa.
- Tidak membatasi jumlah siswa secara hard limit pada versi gratis MVP, kecuali kelak ada keputusan monetisasi yang jelas.
- Satu instalasi mengelola satu entitas bimbel.
- Beberapa tutor dan banyak kelas diperbolehkan.
- Multi-cabang tidak termasuk MVP.

---

## 4. Keputusan Produk yang Sudah Ditetapkan

| Area | Keputusan |
|---|---|
| Platform | Android app |
| Target | Pengelola bimbel saja |
| Skala utama | 5–30 siswa |
| Koneksi | Offline-first; internet dibutuhkan untuk memuat iklan AdMob dan fitur online yang mungkin ditambahkan nanti |
| Jenis kelas | Privat dan kelompok |
| Pembayaran | Bulanan, paket sesi, dan per sesi |
| Tutor | Satu pemilik dapat mengelola beberapa tutor |
| Akses tutor | Tidak ada login/role tutor di MVP |
| Migrasi | Import CSV; XLSX dikonversi menjadi CSV sebelum import |
| Ekspor | CSV untuk data terpilih; JSON untuk backup/restore penuh |
| Pengingat | Notifikasi lokal; pengguna memutuskan sendiri apakah akan share pesan |
| Akun | Tidak wajib registrasi untuk penggunaan lokal |
| Bahasa | Bahasa Indonesia dan English |
| Monetisasi MVP | Gratis dengan AdMob |
| Format/penempatan iklan | Dibuat fleksibel; kombinasi unit dan placement ditentukan melalui pengujian setelah UX inti stabil |
| UI direction | Informasi cukup padat, lebar ruang dimanfaatkan, tetapi hierarki visual tetap bersih dan mudah dipindai; TutorDesk sebagai referensi UX, bukan untuk ditiru mentah-mentah |

---

## 5. Prinsip UX dan Arah Visual

### 5.1 Arah desain

Gunakan prinsip wide but clean yang disukai dari TutorDesk:

- Maksimalkan lebar konten yang tersedia; hindari card besar yang hanya menampilkan sedikit informasi.
- Informasi penting dapat dipindai dengan cepat: nama, kelas, jadwal, status kehadiran, dan status pembayaran.
- Gunakan hierarki tipografi yang tegas, jarak konsisten, dan status chip yang mudah dibedakan.
- Tampilkan informasi terkait dalam satu area; minimalkan perpindahan halaman.
- Gunakan tabel/list padat pada layar lebar, tetapi tetap gunakan row/card yang nyaman disentuh pada ponsel kecil.
- Hindari dashboard dekoratif yang besar tetapi tidak memberi tindakan yang jelas.

### 5.2 Prinsip interaksi

- Tugas rutin sebisa mungkin selesai dalam 1–3 langkah setelah pengguna berada di halaman yang relevan.
- Tombol aksi utama harus terlihat tanpa harus membuka menu overflow.
- Pencarian dan filter mudah ditemukan pada halaman daftar.
- Status pembayaran dan kehadiran tidak hanya dibedakan melalui warna; sertakan label teks/icon.
- Jangan menghapus data permanen secara tidak sengaja. Gunakan konfirmasi untuk tindakan destruktif.
- Simpan otomatis setelah validasi berhasil; tampilkan umpan balik yang jelas.
- Empty state harus memberi tindakan berikutnya, misalnya “Tambah siswa” atau “Impor CSV”.

### 5.3 Navigasi utama

Rekomendasi navigasi utama:

- Dashboard — ringkasan dan tindakan hari ini.
- Siswa — daftar siswa, profil, dan enrollment.
- Jadwal — kalender/list jadwal, kelas, dan kehadiran.
- Keuangan — tagihan, pembayaran, dan tunggakan.
- Lainnya — tutor, laporan, impor/ekspor, pengaturan.

Pada layar lebar/tablet, navigasi dapat berubah menjadi navigation rail atau side navigation. Pada ponsel, gunakan bottom navigation atau pola navigasi Android yang sesuai.

### 5.4 Komponen visual utama

- Summary cards berukuran ringkas.
- Compact list/table rows.
- Status chips: Lunas, Belum lunas, Jatuh tempo, Hadir, Izin, Sakit, Tidak hadir.
- Filter chips untuk tutor, kelas, status, dan rentang tanggal.
- Bottom sheet untuk aksi cepat yang kontekstual.
- Form bertahap hanya bila benar-benar perlu; jangan membuat onboarding panjang.

---

## 6. Ruang Lingkup Fitur

Prioritas:

- P0 — Wajib MVP: tidak boleh dihilangkan dari rilis awal.
- P1 — Penting: masuk MVP bila kapasitas implementasi memungkinkan; jika tidak, rilis minor berikutnya.
- Later — Setelah MVP: bukan bagian dari acceptance criteria rilis pertama.

### 6.1 Ringkasan prioritas

| Fitur | Prioritas | Catatan |
|---|---|---|
| Setup bimbel tanpa registrasi | P0 | Local profile |
| Siswa dan kontak orang tua | P0 | Data inti |
| Data tutor | P0 | Beberapa tutor per bimbel |
| Kelas privat dan kelompok | P0 | Satu siswa atau banyak siswa |
| Jadwal rutin dan sesi khusus | P0 | Bisa diubah/dibatalkan |
| Kehadiran per sesi | P0 | Riwayat harus tersimpan |
| Pembayaran bulanan | P0 | Status tagihan dan pembayaran |
| Pembayaran paket sesi | P0 | Saldo sesi otomatis berkurang sesuai aturan |
| Pembayaran per sesi | P0 | Berdasarkan sesi yang ditagihkan |
| Dashboard operasional/keuangan | P0 | Fokus ke tindakan dan tunggakan |
| Notifikasi lokal | P0 | Tidak mengirim pesan ke pihak lain |
| Share pesan melalui Android Sharesheet | P0 | Manual share oleh pengguna |
| Import CSV | P0 | Mapping kolom dan preview |
| Export CSV | P0 | Dataset terpilih |
| Backup/restore JSON | P0 | Full local data |
| Bahasa Indonesia dan English | P0 | String terlokalisasi |
| AdMob integration layer | P0 | Placement/format dapat diatur |
| Story/share visual template | P1 | Tidak menampilkan data pribadi siswa secara default |
| Laporan ringkas yang dapat di-share | P1 | Ringkasan teks lebih dulu |
| Export invoice sebagai PDF/image | Later | Mulai dari share teks; evaluasi kebutuhan |
| Cloud sync/multi-device | Later | Perlu akun dan backend |
| Login dan permission tutor | Later | Role-based access |
| Portal orang tua/siswa | Later | Di luar MVP |
| Tutor payroll | Later | Di luar MVP |
| Payment gateway/auto bank verification | Later | Di luar MVP |
| LMS, tryout, bank soal, video learning | Later | Di luar positioning |

---

## 7. Functional Requirements

### FR-01 — Setup awal dan penggunaan tanpa akun

**Prioritas:** P0

Pengguna dapat memakai Lespri tanpa membuat akun, memasukkan email, atau login.

Kebutuhan:

- Pada penggunaan pertama, pengguna dapat memasukkan nama bimbel dan informasi dasar opsional.
- Pengguna dapat memilih mulai dari data kosong atau import CSV.
- Pengguna dapat melewati informasi yang tidak wajib.
- Dashboard ditampilkan segera setelah setup minimal selesai.
- Aplikasi menjelaskan bahwa data utama disimpan di perangkat dan menyarankan backup berkala.
- Bahasa awal mengikuti bahasa perangkat jika tersedia; pengguna dapat mengubahnya di Pengaturan.

Acceptance criteria:

- Tidak ada login/register wajib untuk membuat siswa, kelas, jadwal, kehadiran, atau transaksi.
- Setelah force close dan membuka ulang aplikasi, data lokal tetap tersedia.
- Aplikasi tetap dapat membuka dan mengelola data inti saat mode pesawat aktif.

### FR-02 — Manajemen siswa

**Prioritas:** P0

Data siswa minimal:

- Nama siswa (wajib).
- Nama orang tua/wali (opsional).
- Nomor WhatsApp/telepon orang tua/wali (opsional).
- Jenjang/kelas sekolah (opsional).
- Catatan internal (opsional).
- Tanggal mulai bergabung (default tanggal saat dibuat).
- Status aktif/nonaktif.

Kebutuhan:

- Tambah, lihat, edit, arsipkan, dan cari siswa.
- Cari berdasarkan nama siswa, nama wali, atau nomor telepon.
- Filter siswa aktif/nonaktif dan berdasarkan kelas/kelompok.
- Satu siswa dapat mengikuti lebih dari satu kelas/enrollment.
- Profil siswa menampilkan ringkasan kelas, jadwal, kehadiran, tagihan, pembayaran, dan paket sesi yang aktif.
- Mengarsipkan siswa tidak menghapus riwayat lama.

Acceptance criteria:

- Nama wajib divalidasi.
- Nomor telepon disimpan sebagai teks, bukan angka, agar angka nol di depan tidak hilang.
- Mengarsipkan siswa tidak menghapus transaksi dan sesi yang sudah tercatat.

### FR-03 — Manajemen tutor

**Prioritas:** P0

Data tutor minimal:

- Nama tutor (wajib).
- Nomor telepon (opsional).
- Mata pelajaran/keahlian (opsional).
- Catatan internal (opsional).
- Status aktif/nonaktif.

Kebutuhan:

- Satu bimbel dapat memiliki beberapa tutor.
- Tutor dapat ditugaskan ke beberapa kelas.
- Daftar tutor memperlihatkan kelas yang diampu.
- Tutor nonaktif tidak dapat dipilih untuk kelas baru, tetapi riwayat penugasan tetap disimpan.
- Tidak ada akun/login tutor pada MVP.

Acceptance criteria:

- Satu kelas dapat memiliki satu tutor utama pada MVP.
- Mengarsipkan tutor tidak menghapus sesi atau riwayat kelas sebelumnya.

### FR-04 — Kelas privat dan kelompok

**Prioritas:** P0

Jenis kelas:

- Privat: satu siswa per enrollment/kelas.
- Kelompok: beberapa siswa dalam satu kelas.

Data kelas minimal:

- Nama kelas (wajib).
- Jenis kelas: privat/kelompok.
- Mata pelajaran (opsional).
- Tutor utama (opsional pada saat pembuatan, tetapi harus ditentukan sebelum sesi dijadwalkan jika jadwal membutuhkan tutor).
- Lokasi/catatan tempat (opsional).
- Status aktif/nonaktif.

Kebutuhan:

- Tambah/edit/arsipkan kelas.
- Tambah atau keluarkan siswa dari kelas.
- Lihat daftar siswa dan tutor pada kelas.
- Kelas privat secara default memiliki satu siswa aktif.
- Kelas kelompok dapat memiliki banyak siswa.
- Satu siswa dapat terdaftar di lebih dari satu kelas.

Acceptance criteria:

- Sistem mencegah enrollment siswa yang sama terduplikasi pada kelas yang sama dan periode aktif yang sama.
- Mengarsipkan kelas tidak menghapus jadwal, attendance, atau transaksi historis.

### FR-05 — Jadwal dan sesi belajar

**Prioritas:** P0

Kebutuhan:

- Membuat jadwal berulang berdasarkan hari, jam mulai, dan durasi.
- Membuat sesi satu kali di luar jadwal rutin.
- Mengubah, membatalkan, atau menjadwalkan ulang sesi.
- Menampilkan jadwal dalam bentuk agenda hari ini/mendatang dan kalender sederhana.
- Filter berdasarkan tutor dan kelas.
- Setiap sesi terhubung ke kelas, tutor, tanggal, waktu mulai, durasi, dan status sesi.
- Status sesi minimal: Terjadwal, Selesai, Dibatalkan.
- Jadwal yang dibatalkan tidak membuat tagihan per sesi atau mengurangi saldo paket.

Aturan jadwal berulang:

- Perubahan jadwal rutin berlaku untuk sesi ke depan, bukan mengubah sesi historis.
- Pengguna harus dapat mengubah satu sesi saja tanpa mengubah seluruh jadwal rutin.
- Sesi yang sudah memiliki attendance/transaksi tidak boleh dihapus permanen melalui edit jadwal biasa.

Acceptance criteria:

- Pengguna dapat membuka daftar sesi hari ini dan mengetahui tutor serta kelas yang terkait.
- Pengguna dapat membatalkan satu sesi tanpa menghapus pola jadwal mingguan.
- Sesi yang telah selesai tetap dapat dilihat di riwayat.

### FR-06 — Kehadiran

**Prioritas:** P0

Status attendance minimal:

- Hadir
- Izin
- Sakit
- Tidak hadir
- Belum dicatat

Kebutuhan:

- Pengguna dapat membuka sesi dan mencatat kehadiran seluruh siswa kelas dalam satu tampilan.
- Default status adalah Belum dicatat; jangan mengasumsikan semua siswa hadir.
- Pengguna dapat mengubah status sebelum atau setelah sesi selesai.
- Catatan singkat per siswa/sesi bersifat opsional.
- Riwayat kehadiran tersedia pada profil siswa dan kelas.
- Sesi tanpa attendance setelah waktu sesi dapat ditampilkan sebagai tindakan yang perlu diselesaikan di dashboard.

Aturan awal:

- Status Hadir menjadi dasar pengurangan saldo paket sesi.
- Untuk skema per sesi, status Hadir menjadi dasar pembuatan biaya default.
- Status selain Hadir tidak otomatis mengurangi saldo paket atau membuat biaya per sesi. Pengguna dapat melakukan penyesuaian manual pada transaksi bila perjanjian dengan orang tua mengharuskannya.

Acceptance criteria:

- Mencatat attendance untuk kelas kelompok tidak memerlukan membuka profil setiap siswa satu per satu.
- Mengubah attendance memperbarui perhitungan paket/per sesi sesuai aturan dan menampilkan perubahan sebelum disimpan jika berdampak pada tagihan.
- Attendance historis tidak terhapus ketika siswa atau tutor diarsipkan.

### FR-07 — Skema pembayaran dan tagihan

**Prioritas:** P0

Setiap enrollment dapat menggunakan salah satu skema pembayaran berikut:

- Bulanan: biaya tetap per periode/bulan.
- Paket sesi: siswa membeli sejumlah sesi di muka.
- Per sesi: biaya dikenakan berdasarkan sesi yang memenuhi aturan penagihan.

Skema dapat berbeda antar siswa, bahkan dalam kelas yang sama bila memang ada kesepakatan berbeda.

#### A. Bulanan

- Simpan nominal biaya dan tanggal jatuh tempo (atau tanggal tagihan) per enrollment.
- Pengguna dapat membuat tagihan untuk satu siswa atau beberapa siswa sekaligus.
- Sistem dapat menyiapkan daftar tagihan bulan berjalan berdasarkan enrollment aktif.
- Pengguna harus dapat meninjau dan mengonfirmasi sebelum tagihan dibuat.
- Tagihan yang telah dibayar sebagian tetap menunjukkan sisa tagihan.

#### B. Paket sesi

- Pengguna membuat paket dengan jumlah sesi dan harga total.
- Simpan tanggal pembelian dan masa berlaku opsional.
- Saldo sesi berkurang satu ketika sesi berstatus Hadir dan enrollment terkait menggunakan paket tersebut.
- Jika attendance diubah dari Hadir ke status lain, saldo paket dikembalikan.
- Pengguna dapat menambah paket baru ketika saldo habis.
- Sisa sesi terlihat pada profil siswa dan dashboard bila perlu.

#### C. Per sesi

- Biaya dihitung berdasarkan sesi yang dihadiri.
- Nominal dapat ditetapkan per enrollment; jangan memaksa satu harga yang sama untuk semua siswa.
- Tagihan dapat dibuat secara otomatis sebagai draft setelah sesi selesai atau melalui aksi “Buat tagihan”.
- Pengguna mengonfirmasi tagihan sebelum ditetapkan.

#### D. Pembayaran dan status tagihan

Data transaksi minimal:

- Nominal tagihan.
- Tanggal dibuat.
- Tanggal jatuh tempo (opsional sesuai skema).
- Status: Draft, Belum lunas, Dibayar sebagian, Lunas, Dibatalkan.

Riwayat pembayaran:

- nominal
- tanggal
- metode
- catatan/referensi opsional

Metode pembayaran MVP:

- Tunai
- Transfer
- Lainnya

Kebutuhan:

- Mendukung pembayaran penuh dan sebagian.
- Satu tagihan dapat memiliki beberapa pembayaran.
- Sisa tagihan dihitung otomatis.
- Pengguna dapat mencatat pembayaran secara manual.
- Tidak ada verifikasi transfer otomatis atau payment gateway.
- Tagihan yang lunas tidak berubah menjadi belum lunas tanpa penyesuaian yang tercatat.

Acceptance criteria:

- Saldo tagihan = total tagihan dikurangi total pembayaran yang valid.
- Status Lunas hanya jika saldo tagihan nol atau kurang.
- Pembayaran sebagian menampilkan saldo tersisa secara jelas.
- Mengubah atau menghapus transaksi harus menjaga konsistensi total tagihan dan saldo paket.
- Perhitungan nominal memakai integer satuan mata uang (misalnya rupiah), bukan floating point.

### FR-08 — Dashboard “pain payer”

**Prioritas:** P0

Dashboard harus memprioritaskan apa yang perlu dilakukan pemilik, bukan hanya menampilkan statistik dekoratif.

Komponen utama:

- Tunggakan saat ini: total nilai tagihan belum lunas.
- Siswa belum lunas: jumlah siswa dengan saldo tagihan.
- Tagihan jatuh tempo/terlambat: jumlah dan nominal.
- Jadwal hari ini: sesi, jam, kelas, dan tutor.
- Kehadiran belum dicatat: sesi selesai/terlewat yang attendance-nya belum lengkap.
- Paket hampir habis: jumlah siswa dengan saldo sesi rendah/habis.
- Pemasukan bulan ini: total pembayaran yang diterima pada bulan berjalan.

Aksi cepat:

- Tambah siswa.
- Tambah kelas/jadwal.
- Catat kehadiran.
- Catat pembayaran.
- Buat tagihan.
- Import data.

Aturan tampilan:

- Nilai uang dan jumlah harus dapat dibaca sekilas.
- Tap pada summary membawa pengguna ke daftar terfilter yang relevan.
- Bila belum ada data, tampilkan empty state dengan CTA; jangan menampilkan angka nol dalam card besar tanpa konteks.
- Dashboard harus tetap berguna tanpa internet.

### FR-09 — Notifikasi lokal dan pengingat

**Prioritas:** P0

Jenis notifikasi kandidat MVP:

- Pengingat jadwal kelas kepada pemilik.
- Pengingat tagihan mendekati jatuh tempo.
- Pengingat tagihan terlambat.
- Pengingat untuk melengkapi attendance sesi.
- Peringatan saldo paket sesi hampir habis.

Kebutuhan:

- Pemilik dapat mengaktifkan/nonaktifkan tiap jenis pengingat.
- Pengguna dapat mengatur lead time yang sederhana, misalnya pada hari yang sama atau satu hari sebelumnya.
- Tap notifikasi membuka halaman detail terkait.
- Notifikasi tidak mengirim pesan kepada orang tua/siswa/tutor.
- Notifikasi tidak otomatis membuka WhatsApp atau aplikasi sosial.
- Aplikasi meminta izin notifikasi pada konteks yang tepat, bukan langsung pada first launch tanpa penjelasan.
- Jadwal notifikasi harus diselaraskan kembali setelah perangkat reboot, perubahan waktu, atau perubahan jadwal yang relevan.

### FR-10 — Share melalui Android Sharesheet

**Prioritas:** P0 untuk teks; P1 untuk visual Story

Kebutuhan:

- Aksi Share membuka Android Sharesheet agar pengguna memilih aplikasi tujuan.
- Lespri menyediakan teks siap-edit untuk pengingat pembayaran, detail tagihan, perubahan jadwal, pembatalan kelas, dan ringkasan laporan.
- Pengguna dapat mengedit pesan sebelum mengirim.
- Pengguna dapat memilih aplikasi seperti WhatsApp atau aplikasi lain yang mendukung share.
- Aplikasi tidak boleh mengklaim pesan sudah terkirim hanya karena Sharesheet terbuka.
- Jangan otomatis mengirim pesan atau memilih penerima.

Privasi:

- Pesan untuk orang tua hanya memuat informasi yang relevan bagi penerima tersebut.
- Jangan menyertakan data siswa lain dalam pesan individual.
- Ringkasan grup tidak menampilkan nomor telepon atau data pribadi siswa.

Template minimal:

- Pengingat tagihan dengan nama siswa, periode, nominal, dan tanggal jatuh tempo.
- Konfirmasi pembayaran diterima.
- Pengingat jadwal kelas.
- Pemberitahuan jadwal berubah/dibatalkan.
- Ringkasan kehadiran/perkembangan yang aman untuk dibagikan.

Story visual (P1):

- Template vertikal 9:16 untuk promosi bimbel atau pengumuman umum.
- Data pribadi siswa tidak dimasukkan secara default.
- Pengguna dapat mengedit teks utama sebelum menghasilkan gambar.
- Sharing dilakukan melalui Android Sharesheet; tidak memposting otomatis ke Story.

### FR-11 — Import CSV

**Prioritas:** P0

Import ditujukan untuk bimbel yang sudah memiliki data siswa di Excel/Google Sheets/CSV.

Keputusan implementasi hemat ukuran aplikasi: MVP tidak membaca file `.xlsx` secara langsung. Pengguna membuka file Excel lalu memilih Save As / Download as CSV UTF-8, kemudian mengimpor file CSV tersebut ke Lespri.

Alasan:

- Menghindari dependency parser/writer XLSX yang tidak diperlukan untuk fungsi inti.
- Mengurangi kompleksitas parsing workbook, worksheet, formula, format, dan variasi file Excel.
- CSV dapat dibuka/diedit oleh Excel dan Google Sheets.
- Import tetap dapat dilakukan secara offline melalui Android Storage Access Framework.

Jenis data yang diprioritaskan untuk import:

- Siswa dan kontak wali.
- Tutor.
- Kelas/kelompok.
- Enrollment siswa ke kelas dan skema pembayaran dasar.

MVP dapat menggunakan satu template CSV gabungan dengan satu baris per enrollment. Field inti yang disarankan:

`student_name` (wajib)  
`guardian_name`  
`guardian_phone`  
`school_grade`  
`class_name`  
`class_type` (`private` / `group`)  
`subject`  
`tutor_name`  
`schedule_day`  
`schedule_start_time`  
`schedule_duration_minutes`  
`billing_type` (`monthly` / `package` / `per_session`)  
`fee_amount`  
`package_session_count`  
`due_day`

Ketentuan import:

- Pengguna memilih file CSV dari perangkat.
- Sistem membaca header dan menawarkan mapping kolom.
- Sistem menyediakan preview beberapa baris sebelum import.
- Sistem memvalidasi field wajib, tipe data, nomor telepon, tanggal/jam, dan nominal.
- Sistem mendeteksi kemungkinan duplikat berdasarkan kombinasi data yang masuk akal; jangan menggabungkan otomatis tanpa konfirmasi.
- Pengguna melihat jumlah baris valid, baris bermasalah, dan kandidat duplikat.
- Pengguna dapat memilih lanjut hanya dengan baris valid atau membatalkan seluruh import.
- Hasil import menampilkan jumlah record berhasil, dilewati, dan gagal.
- Import tidak boleh menghapus data yang sudah ada.

Format CSV:

- UTF-8; toleransi UTF-8 BOM.
- Deteksi delimiter umum: koma, titik koma, atau tab.
- Mendukung field yang dikutip dan karakter newline di dalam field.
- Nomor telepon diperlakukan sebagai teks.
- Pesan error harus menyebut nomor baris dan kolom.

Acceptance criteria:

- Pengguna dapat mengimpor CSV dari file manager tanpa koneksi internet.
- Pengguna dapat memetakan header CSV yang berbeda ke field Lespri.
- Baris invalid tidak menyebabkan seluruh aplikasi crash atau data parsial yang tidak terlaporkan.
- Import ulang file yang sama tidak diam-diam membuat duplikat tanpa peringatan.

### FR-12 — Export CSV dan backup/restore

**Prioritas:** P0

#### Export CSV

- Pengguna dapat mengekspor data inti dalam format CSV.
- Minimal tersedia export siswa, tutor, kelas/enrollment, attendance, dan transaksi.
- File dapat dibuka di Excel/Google Sheets.
- Export menggunakan UTF-8 dan mempertahankan nomor telepon sebagai teks sejauh format CSV memungkinkan.
- Pengguna memilih lokasi/tujuan penyimpanan melalui Android Storage Access Framework atau Sharesheet.

#### Backup penuh

- Gunakan JSON sebagai format backup kanonis.
- File mencakup seluruh data lokal, relasi, konfigurasi penting, dan versi schema.
- Ekstensi yang disarankan: `.lespri.json`.
- Kompresi opsional: `.lespri.json.gz`, memakai GZIP bawaan Android bila dibutuhkan; tidak perlu dependency kompresi tambahan.
- Backup tidak perlu menyimpan file media karena MVP tidak menyimpan lampiran pengguna.
- File backup dapat dipindahkan melalui file manager, Drive, atau media penyimpanan lain oleh pengguna.

#### Restore

- Pengguna memilih file backup.
- Sistem memvalidasi format, versi, dan integritas file sebelum mengubah database.
- Tampilkan ringkasan isi backup sebelum konfirmasi.
- MVP menggunakan pola replace local data agar relasi konsisten dan implementasi sederhana; tampilkan peringatan tegas bahwa data lokal saat ini akan diganti.
- Sebelum restore, buat snapshot lokal sementara sebagai mekanisme pemulihan dari kegagalan proses.
- Restore harus bersifat transaksional: jika gagal, database lama tetap utuh.
- Setelah restore berhasil, jadwalkan ulang notifikasi lokal.

Catatan batasan: CSV digunakan untuk pertukaran dataset; JSON digunakan untuk backup penuh yang mempertahankan relasi. CSV bukan pengganti backup.

Acceptance criteria:

- Backup dan restore dapat dilakukan tanpa internet dan tanpa akun.
- Backup dari versi aplikasi yang sama dapat dipulihkan 100% pada instalasi uji.
- File backup invalid tidak boleh mengosongkan atau merusak data saat ini.
- Restore menampilkan konfirmasi sebelum mengganti data lokal.

### FR-13 — Bahasa aplikasi

**Prioritas:** P0

Bahasa yang didukung: Bahasa Indonesia (`id`) dan English (`en`).

- Default mengikuti bahasa perangkat jika didukung; fallback Bahasa Indonesia.
- Pengguna dapat mengganti bahasa di Pengaturan tanpa membuat ulang data.
- Semua label, tombol, validasi, empty state, notifikasi lokal, dan pesan error harus dilokalisasi.
- Konten yang ditulis pengguna (nama, catatan, nama kelas) tidak diterjemahkan.
- Format tanggal, waktu, dan angka mengikuti locale yang dipilih.
- Mata uang MVP berfokus pada Rupiah (IDR), tetapi simbol/format jangan disebar sebagai string hardcoded di seluruh UI.

### FR-14 — AdMob

**Prioritas:** P0 untuk integrasi; format dan placement final diputuskan setelah evaluasi UX.

Kebutuhan:

- Lespri menggunakan AdMob sebagai monetisasi MVP.
- Iklan hanya dimuat ketika koneksi internet tersedia.
- Seluruh fungsi inti harus tetap berjalan ketika offline, ketika iklan gagal dimuat, atau ketika AdMob tidak mengisi inventory.
- Ad tidak boleh menghalangi input, penyimpanan, pencatatan attendance, pencatatan pembayaran, import, export, backup, atau restore.
- Unit ID tidak boleh ditanam langsung di banyak screen; gunakan satu lapisan/config ad placement.
- Setiap placement/format dapat diaktifkan atau dinonaktifkan secara independen.
- Gunakan test ad unit selama development/QA.
- Jangan mengirim nama siswa, nomor telepon, catatan, jadwal individual, atau nominal pembayaran ke sistem iklan.
- Lengkapi privacy policy, disclosure data, dan pengaturan consent yang diwajibkan untuk distribusi.

Format yang disiapkan sebagai opsi:

| Format | Potensi placement | Catatan UX |
|---|---|---|
| Adaptive banner | Dashboard atau area non-kritis | Kandidat paling sederhana; jangan menutup CTA atau konten penting |
| Native | Di antara konten informasional non-kritis | Bisa menyatu dengan visual, tetapi harus tetap jelas sebagai iklan |
| Interstitial | Transisi yang benar-benar natural | Risiko mengganggu tinggi untuk aplikasi utilitas; jangan tampil setelah Save atau saat tugas inti |
| Rewarded | Aksi opsional yang memang memberi benefit | Belum ada reward MVP yang ditetapkan; jangan dibuat hanya demi menampilkan iklan |
| App open | Saat membuka aplikasi | Tidak direkomendasikan sebagai default; dapat mengganggu pengguna yang hanya ingin mencatat transaksi cepat |
| Rewarded interstitial | Transisi dengan reward yang diumumkan | Bukan prioritas MVP; memerlukan desain consent/opt-out yang jelas |

Strategi:

- Bangun `AdManager`/adapter terpusat dan konfigurasi placement agar format dapat ditambahkan atau diuji tanpa mengubah logika bisnis setiap screen.
- MVP tidak harus mengaktifkan seluruh format sekaligus.
- Format dan frekuensi final ditentukan melalui pengujian setelah alur utama stabil.
- Jangan menampilkan iklan full-screen pada setiap pembukaan halaman atau setiap penyimpanan.
- Hindari iklan pada layar form, attendance, transaksi, dan proses data portability.

---

## 8. User Journey Utama

### Journey A — Pengguna baru mulai dari nol

1. Buka Lespri.
2. Masukkan nama bimbel atau lewati jika opsional.
3. Pilih “Mulai dari awal”.
4. Tambahkan siswa pertama.
5. Tambahkan tutor (dapat dilewati).
6. Buat kelas privat/kelompok.
7. Tambahkan jadwal.
8. Dashboard menampilkan ringkasan yang relevan.

Prinsip: jangan memaksa pengguna mengisi seluruh konfigurasi sebelum aplikasi dapat dipakai.

### Journey B — Pengguna pindah dari Excel

1. Pengguna membuka spreadsheet lama di Excel/Google Sheets.
2. Pengguna menyimpan sheet terkait sebagai CSV UTF-8.
3. Pengguna membuka Lespri → Lainnya → Import data.
4. Pilih file CSV.
5. Lespri mendeteksi header dan menampilkan mapping kolom.
6. Pengguna melihat preview dan hasil validasi.
7. Pengguna mengonfirmasi import.
8. Lespri menampilkan ringkasan hasil import dan tautan ke daftar siswa/kelas.

### Journey C — Mencatat sesi kelas kelompok

1. Buka Dashboard atau Jadwal.
2. Pilih sesi hari ini.
3. Lihat daftar siswa pada kelas.
4. Tandai status kehadiran masing-masing siswa.
5. Tambahkan catatan singkat bila perlu.
6. Simpan attendance.
7. Lespri memperbarui saldo paket atau menyiapkan biaya per sesi sesuai skema enrollment.

### Journey D — Menindaklanjuti pembayaran

1. Buka Dashboard.
2. Tap card tunggakan/jatuh tempo.
3. Pilih siswa dan tagihan.
4. Pilih “Catat pembayaran” atau “Share pengingat”.
5. Jika mencatat pembayaran, masukkan nominal, tanggal, metode, dan catatan opsional.
6. Jika share, Lespri membuat pesan siap-edit dan membuka Android Sharesheet.
7. Pengguna memilih aplikasi/penerima dan mengirim sendiri.

### Journey E — Backup sebelum ganti perangkat

1. Buka Lainnya → Data & Backup.
2. Pilih “Buat backup”.
3. Lespri membuat `.lespri.json` atau `.lespri.json.gz`.
4. Pengguna menyimpan/memindahkan file ke lokasi aman.
5. Di perangkat baru, instal Lespri dan pilih Restore.
6. Pilih backup, periksa ringkasan, lalu konfirmasi.
7. Lespri memulihkan data dan menjadwalkan ulang notifikasi.

---

## 9. Struktur Layar

### 9.1 Dashboard

- Ringkasan tunggakan, siswa belum lunas, pemasukan bulan ini.
- Jadwal hari ini.
- Kehadiran yang belum dicatat.
- Paket sesi yang hampir habis.
- Quick actions.
- Slot iklan non-kritis (opsional, sesuai konfigurasi).

### 9.2 Siswa

- Search bar.
- Filter aktif/nonaktif, kelas, status pembayaran.
- Compact list: nama siswa, kelas ringkas, status tagihan, saldo paket jika relevan.
- Detail siswa dengan tab/section: Overview, Kelas, Kehadiran, Keuangan, Catatan.

### 9.3 Tutor

- Daftar tutor dan jumlah kelas aktif.
- Detail tutor berisi kontak dan kelas yang diampu.
- Tidak ada halaman login/portal tutor.

### 9.4 Kelas

- Daftar kelas dengan jenis, mata pelajaran, tutor, jumlah siswa, jadwal berikutnya.
- Detail kelas menampilkan siswa, tutor, jadwal, dan riwayat sesi.
- Aksi cepat: tambah siswa, ubah jadwal, mulai attendance.

### 9.5 Jadwal

- Agenda hari ini sebagai tampilan default.
- Pilihan kalender/list untuk rentang tanggal.
- Filter tutor/kelas.
- Status sesi dan CTA untuk attendance.

### 9.6 Kehadiran

- Header sesi: kelas, tutor, tanggal, jam.
- Daftar siswa ringkas dengan pilihan status.
- Aksi “Tandai semua hadir” boleh tersedia, tetapi harus eksplisit dan dapat dikoreksi.
- Catatan per siswa opsional.

### 9.7 Keuangan

- Summary: belum lunas, jatuh tempo, pemasukan bulan berjalan.
- Tab/list: Tagihan, Pembayaran, Paket sesi.
- Filter status, tanggal, siswa, skema pembayaran.
- Detail tagihan dengan riwayat pembayaran.

### 9.8 Data & Backup

- Import CSV.
- Export CSV.
- Backup penuh.
- Restore backup.
- Informasi kapan backup terakhir dibuat (jika dapat disimpan lokal).

### 9.9 Pengaturan

- Nama dan profil bimbel.
- Bahasa.
- Preferensi notifikasi.
- Preferensi tampilan tanggal/waktu bila diperlukan.
- Informasi penyimpanan lokal dan backup.
- Kebijakan privasi dan versi aplikasi.

---

## 10. Model Data Konseptual

Implementasi database dapat disesuaikan dengan stack teknis, tetapi relasi domain berikut perlu dipertahankan.

### 10.1 Entitas utama

| Entitas | Fungsi | Field inti |
|---|---|---|
| `OwnerProfile` | Profil lokal bimbel | `id`, `business_name`, `created_at`, `updated_at` |
| `Student` | Data siswa | `id`, `name`, `guardian_name`, `guardian_phone`, `school_grade`, `notes`, `status` |
| `Tutor` | Data tutor | `id`, `name`, `phone`, `expertise`, `notes`, `status` |
| `ClassGroup` | Kelas privat/kelompok | `id`, `name`, `class_type`, `subject`, `tutor_id`, `location_note`, `status` |
| `Enrollment` | Relasi siswa-kelas dan skema tagihan | `id`, `student_id`, `class_id`, `billing_type`, `fee_amount`, `due_day`, `package_session_count`, `status` |
| `ScheduleRule` | Pola jadwal berulang | `id`, `class_id`, `tutor_id`, `weekday`, `start_time`, `duration_minutes`, `effective_from`, `effective_until`, `status` |
| `LessonSession` | Sesi spesifik pada tanggal/jam tertentu | `id`, `class_id`, `tutor_id`, `session_date`, `start_time`, `duration_minutes`, `status`, `source_rule_id` |
| `AttendanceRecord` | Kehadiran siswa pada sesi | `id`, `session_id`, `enrollment_id`, `attendance_status`, `note`, `updated_at` |
| `Charge` | Tagihan/biaya | `id`, `enrollment_id`, `charge_type`, `period_start`, `period_end`, `amount`, `due_date`, `status` |
| `Payment` | Pembayaran terhadap tagihan | `id`, `charge_id`, `amount`, `paid_at`, `method`, `reference_note` |
| `PackageLedger` | Perubahan saldo sesi paket | `id`, `enrollment_id`, `session_id`, `delta_sessions`, `reason`, `created_at` |
| `NotificationPreference` | Pengaturan notifikasi lokal | `id`, `notification_type`, `enabled`, `lead_time` |
| `AppSettings` | Preferensi aplikasi | `language`, `date_format`, `schema_version`, `other_settings` |

### 10.2 Aturan model data

- Gunakan ID stabil (UUID atau strategi setara) agar relasi tidak bergantung pada nama.
- Nama siswa/tutor/kelas tidak boleh menjadi foreign key.
- Gunakan arsip/soft delete untuk entitas yang memiliki riwayat.
- Simpan waktu/tanggal dalam format konsisten; tampilkan berdasarkan locale/timezone perangkat.
- Simpan nominal uang sebagai integer satuan mata uang terkecil yang dipakai (untuk IDR: rupiah).
- Setiap perubahan yang mempengaruhi saldo paket harus menghasilkan ledger entry yang dapat ditelusuri.
- `Charge` dan `Payment` harus memiliki relasi yang eksplisit agar pembayaran parsial dapat dihitung dengan benar.
- Backup harus menyimpan versi schema untuk mendukung migrasi format di versi mendatang.

---

## 11. Business Rules dan Edge Cases

- Satu siswa di beberapa kelas: diperbolehkan; skema pembayaran dapat berbeda per enrollment.
- Satu kelas dengan beberapa siswa: hanya untuk kelas kelompok.
- Kelas tanpa tutor: dapat disimpan sebagai draft, tetapi sistem memberi peringatan sebelum jadwal dibuat.
- Tutor nonaktif: tetap terlihat dalam histori; tidak dapat dipilih untuk penugasan baru.
- Siswa nonaktif: tidak muncul sebagai default pilihan enrollment baru; histori tetap tersedia.
- Jadwal rutin diubah: hanya sesi mendatang yang belum diselesaikan yang mengikuti aturan perubahan; sesi historis tetap.
- Sesi dibatalkan: tidak menghasilkan biaya per sesi dan tidak mengurangi saldo paket secara otomatis.
- Attendance berubah dari Hadir: sistem membalikkan dampak paket/per sesi yang sebelumnya berasal dari attendance tersebut.
- Pembayaran sebagian: sisa tagihan harus tetap terlihat dan dapat ditagih.
- Pembayaran melebihi tagihan: MVP sebaiknya menolak nominal berlebih atau meminta pengguna mengonfirmasi sebagai saldo/kredit terpisah. Default yang direkomendasikan: tolak nominal yang melebihi saldo tagihan untuk menghindari saldo kredit yang kompleks.
- Tagihan bulanan terbuat dua kali: sistem harus mendeteksi enrollment dan periode yang sama agar tidak membuat duplikat.
- Paket sesi habis: tampilkan saldo nol dan CTA untuk mencatat pembelian paket baru.
- Restore saat data sudah ada: wajib menampilkan peringatan bahwa data lokal akan diganti.
- CSV dengan nomor telepon berawalan nol: harus tetap disimpan sebagai teks.
- CSV memakai titik koma: importer harus mendeteksi delimiter atau memberi pilihan kepada pengguna.
- Nama siswa sama: jangan otomatis menganggap duplikat; gunakan kombinasi nama, wali, nomor telepon, dan/atau pilihan pengguna.
- Tidak ada internet: semua CRUD, jadwal, attendance, transaksi, export, backup, dan restore tetap berjalan.
- Iklan gagal dimuat: tidak boleh menampilkan error blocking atau menghalangi layar.
- Izin notifikasi ditolak: fitur lokal tetap dapat digunakan; pengaturan menunjukkan status izin sistem.
- Perangkat ganti zona waktu: jadwal dan notifikasi harus dihitung ulang tanpa mengubah tanggal historis secara keliru.

---

## 12. Non-Functional Requirements

### 12.1 Offline-first dan ketahanan data

- Database lokal adalah source of truth MVP.
- Operasi baca/tulis inti tidak memerlukan API/backend.
- Tidak ada loading screen yang menunggu koneksi internet untuk membuka data lokal.
- Inisialisasi AdMob berlangsung terpisah dari kesiapan database/UI inti.
- Penulisan data penting harus aman dari app termination atau proses yang terhenti.
- Gunakan transaksi database untuk operasi multi-record seperti import, restore, attendance batch, dan pencatatan paket.

### 12.2 Performa

- Daftar siswa 30 record harus terbuka dan dapat dicari dengan lancar pada perangkat Android kelas menengah ke bawah.
- Dashboard tidak boleh menghitung ulang seluruh histori secara tidak efisien pada setiap interaksi.
- Gunakan pagination/lazy rendering bila jumlah data membesar.
- Tugas inti tidak boleh tertahan karena request iklan.

### 12.3 Penyimpanan dan ukuran aplikasi

- Pilih database lokal SQLite atau solusi setara yang ringan dan matang.
- Hindari dependency spreadsheet/XLSX bila CSV memenuhi kebutuhan migrasi MVP.
- Gunakan parser CSV ringan atau implementasi parser yang sudah tersedia di stack yang dipilih.
- Gunakan JSON untuk backup; GZIP bawaan Android dapat digunakan untuk kompresi tanpa library tambahan.
- Hindari memasukkan font, ikon, atau asset besar yang tidak dibutuhkan.
- Tetapkan target ukuran APK/AAB setelah stack dan SDK dipilih; lakukan pemeriksaan ukuran build pada setiap release candidate.

### 12.4 Privasi dan keamanan

- Data siswa, wali, nomor telepon, attendance, dan transaksi disimpan lokal pada MVP.
- Tidak ada pengiriman data operasional ke server Lespri karena backend tidak diperlukan.
- Data personal tidak boleh dimasukkan ke ad request sebagai custom targeting.
- Jelaskan kepada pengguna bahwa menghapus data aplikasi/uninstall dapat menghapus data lokal jika belum dibackup.
- Backup adalah file yang dikelola pengguna; ingatkan agar disimpan di tempat aman karena mengandung data pribadi.
- Jangan tampilkan data siswa lain dalam pesan individual.
- Sediakan kebijakan privasi yang menjelaskan penggunaan AdMob dan praktik pengolahan data SDK pihak ketiga.

### 12.5 Aksesibilitas dan usability

- Ukuran teks dan target sentuh sesuai praktik Android.
- Status tidak hanya dibedakan dengan warna.
- Form memiliki label dan pesan validasi yang jelas.
- Layout tetap usable pada layar sempit dan mendukung layar lebih lebar.
- Kontras teks memadai dalam light/dark mode bila keduanya diimplementasikan.

### 12.6 Kompatibilitas

- Tetapkan `minSdk` dan target SDK berdasarkan kebutuhan distribusi Google Play saat implementasi.
- Uji pada setidaknya satu perangkat Android low/mid-range dan satu layar berukuran lebih besar.
- Perhatikan permission notifikasi Android versi baru.
- Jangan mengasumsikan WhatsApp terpasang; Sharesheet tetap menyediakan alternatif aplikasi lain.

---

## 13. Monetisasi dan Ad Placement Strategy

### 13.1 Prinsip

Lespri adalah aplikasi administrasi yang dipakai untuk tugas singkat dan berulang. Pendapatan iklan tidak boleh merusak kecepatan dan kepercayaan pengguna.

### 13.2 Inventory yang mungkin diuji

- Banner/adaptive banner
- Native ads
- Interstitial
- Rewarded
- App open
- Rewarded interstitial

MVP memerlukan integrasi AdMob dan struktur konfigurasi yang memungkinkan lebih dari satu ad unit/placement, tetapi tidak mewajibkan semua format aktif pada rilis pertama.

### 13.3 Area yang dilarang untuk placement iklan

- Di atas tombol Simpan/Submit.
- Di dalam form ketika pengguna sedang mengetik.
- Di alur catat attendance.
- Di alur catat pembayaran atau membuat tagihan.
- Di tengah import/export/backup/restore.
- Tepat setelah pengguna menekan Simpan atau menyelesaikan tindakan penting.
- Pada dialog konfirmasi atau error.

### 13.4 Evaluasi format

- Mulai dari placement yang tidak mengganggu, misalnya adaptive banner pada area dashboard non-kritis.
- Native dapat diuji pada konten informasional yang memiliki ruang dan pemisahan visual jelas.
- Interstitial hanya boleh dipertimbangkan pada transisi natural dan tidak boleh muncul dengan frekuensi tinggi. Google menyarankan interstitial untuk titik jeda/transisi yang wajar dan memperingatkan pengalaman buruk bila iklan mengejutkan atau menghalangi pengguna.
- Rewarded hanya masuk akal bila ada benefit opsional yang nyata; jangan membuat benefit artifisial.
- App open bukan default karena banyak pengguna membuka aplikasi hanya untuk satu tugas singkat.

### 13.5 Konfigurasi teknis

Ad placement sebaiknya didefinisikan terpusat, misalnya:

- `placement_key`
- `format`
- `ad_unit_id`
- `enabled`
- `screen_context`
- `frequency_cap` atau aturan tampil bila relevan

Nama field final menyesuaikan stack. Jangan menyimpan ID iklan di banyak screen. Sediakan build configuration terpisah untuk test dan production ad unit.

---

## 14. Out of Scope MVP

- XLSX parser/import/export native.
- Login/registrasi wajib.
- Cloud database, cloud sync, dan multi-device.
- Multi-owner atau multi-cabang.
- Akun/akses tutor.
- Akun/portal orang tua dan siswa.
- Auto WhatsApp/Instagram sending.
- Payment gateway, QRIS integration, dan verifikasi transfer bank.
- Payroll/honor tutor.
- LMS, lesson content, bank soal, tryout, grading, dan video conference.
- Marketplace atau pencarian tutor.
- AI assistant atau AI-generated lesson plan.
- Advanced accounting, tax, cash flow, dan inventory.
- Notifikasi push dari server.

---

## 15. Rencana Rilis

### Milestone 1 — Core local data

- Setup lokal tanpa akun.
- Bahasa Indonesia/English.
- CRUD siswa, tutor, kelas.
- Import CSV dasar.
- Database lokal dan backup JSON awal.

### Milestone 2 — Jadwal dan attendance

- Jadwal rutin dan sesi khusus.
- Agenda hari ini.
- Attendance kelas privat dan kelompok.
- Riwayat sesi dan attendance.

### Milestone 3 — Keuangan

- Skema bulanan, paket sesi, per sesi.
- Tagihan dan pembayaran sebagian/penuh.
- Dashboard tunggakan dan pemasukan.
- Aturan pembalikan saldo paket saat attendance dikoreksi.

### Milestone 4 — Pengingat dan sharing

- Notifikasi lokal.
- Template pesan siap-edit.
- Android Sharesheet.
- Laporan ringkas yang dapat di-share.

### Milestone 5 — Data portability, ads, dan release hardening

- Export CSV seluruh dataset prioritas.
- Backup/restore JSON dan opsi GZIP.
- AdMob integration dan placement configuration.
- QA offline, data integrity, restore, localization, dan ad failure.
- Privacy policy serta Play Store release readiness.

---

## 16. Test Scenarios Minimum

### Data dan relasi

- Tambah/edit/arsipkan siswa.
- Tambah beberapa tutor.
- Buat kelas privat dan kelompok.
- Satu siswa mengikuti dua kelas dengan skema pembayaran berbeda.
- Tutor nonaktif dengan jadwal historis.

### Jadwal dan attendance

- Buat jadwal rutin mingguan.
- Ubah satu sesi tanpa mengubah jadwal rutin.
- Batalkan satu sesi.
- Catat attendance kelas kelompok.
- Ubah status Hadir menjadi Izin dan verifikasi saldo paket/tagihan terkoreksi.
- Selesaikan sesi tanpa attendance dan verifikasi dashboard mengingatkan pemilik.

### Keuangan

- Tagihan bulanan dibuat satu kali untuk periode yang sama.
- Pembayaran lunas.
- Pembayaran sebagian.
- Paket sesi dibeli, digunakan, habis, dan ditambah kembali.
- Per sesi menghasilkan biaya berdasarkan attendance Hadir.
- Pembatalan sesi tidak membuat biaya.
- Nominal nol/negatif dan pembayaran melebihi tagihan ditangani sesuai validasi.

### Import/export/backup

- Import CSV dengan koma.
- Import CSV dengan titik koma.
- Import UTF-8 BOM.
- Import nomor telepon dengan angka nol di depan.
- Import header tidak dikenal dan mapping manual.
- Import baris valid bercampur invalid.
- Import file yang sama dua kali.
- Export CSV dibuka di Excel/Google Sheets.
- Backup dan restore pada perangkat yang sama.
- Backup dan restore pada instalasi/perangkat berbeda.
- Restore file corrupt tidak merusak database aktif.
- Restore backup versi schema lama (ketika migrasi versi sudah diimplementasikan).

### Offline dan iklan

- Mode pesawat: seluruh operasi inti berjalan.
- Iklan tidak tersedia: UI tetap normal.
- Internet putus saat iklan sedang dimuat: tidak ada crash/blocking.
- Iklan tidak menutupi tombol atau data.
- Test ad unit digunakan pada build QA.

### Bahasa dan notifikasi

- Ganti bahasa Indonesia ↔ English tanpa kehilangan data.
- Validasi/form/notifikasi menggunakan bahasa aktif.
- Permission notifikasi ditolak.
- Notifikasi dijadwalkan ulang setelah reboot/perubahan jadwal.
- Tap notifikasi menuju detail yang benar.

---

## 17. Risiko dan Mitigasi

| Risiko | Dampak | Mitigasi |
|---|---|---|
| Pengguna memiliki format Excel berbeda-beda | Import sulit dan rawan gagal | Template CSV, column mapping, preview, error per baris |
| Pengguna mengira backup sama dengan CSV export | Data relasi tidak ikut tersimpan | Jelaskan fungsi berbeda; sediakan “Backup penuh” terpisah |
| Attendance mengubah tagihan/saldo paket | Kesalahan finansial | Ledger, preview dampak, transaksi database, audit perubahan penting |
| Jadwal rutin menghasilkan sesi duplikat | Attendance/tagihan ganda | ID rule, uniqueness check berdasarkan kelas+tanggal+jam |
| AdMob mengganggu tugas harian | Retensi turun | Placement terpusat, feature flag, larangan placement pada alur inti |
| Pengguna kehilangan ponsel/data lokal | Kehilangan data operasional | Backup reminder yang dapat diatur; export/backup selalu mudah ditemukan |
| Tutor menginginkan akses sendiri | Keterbatasan produk | Masukkan role tutor/cloud sync sebagai fase berikutnya |
| Ukuran aplikasi membesar | Instalasi lebih berat | Hindari XLSX dependency, asset besar, SDK yang tidak wajib |

---

## 18. Open Decisions untuk Setelah MVP

Hal-hal berikut belum perlu menghambat implementasi core MVP:

- AdMob: format yang benar-benar diaktifkan, lokasi placement, dan frekuensi.
- Apakah Story template masuk MVP pertama atau rilis minor berikutnya.
- Apakah perlu PDF/image invoice setelah share teks divalidasi.
- Apakah perlu opsi merge saat restore; MVP menggunakan replace untuk menyederhanakan konsistensi data.
- Apakah perlu sinkronisasi cloud dan multi-user setelah kebutuhan pasar terbukti.
- Apakah perlu skema honor tutor; jangan dibangun sebelum aturan pembayaran tutor tervalidasi.
- Apakah perlu batas gratis atau paket Pro/IAP; tidak termasuk MVP.

---

## 19. Referensi Produk dan Dokumentasi

- TutorDesk – Coaching Manager di Google Play — referensi pembanding untuk pendekatan offline-first, daftar siswa/kelas, kehadiran, pembayaran, dan backup. Gunakan sebagai referensi UX/density, bukan untuk menyalin UI atau seluruh feature set.
- Google AdMob — Ad units, ad formats, and ad types — daftar format banner, interstitial, rewarded, native, app open, dan jenis iklan.
- Google AdMob — Android banner ads — panduan banner/adaptive banner Android.
- Google AdMob — Interstitial ad guidance — panduan penempatan interstitial pada titik jeda/transisi yang sesuai dan risiko gangguan UX.

---

## 20. Definition of Done — MVP

MVP dapat dianggap siap diuji oleh pengguna bimbel ketika:

- [ ] Pengguna dapat menggunakan aplikasi tanpa registrasi.
- [ ] Bahasa Indonesia dan English tersedia.
- [ ] Siswa, tutor, kelas privat, dan kelas kelompok dapat dikelola.
- [ ] Satu pemilik dapat mengelola beberapa tutor dari satu perangkat.
- [ ] Jadwal rutin, sesi khusus, pembatalan, dan reschedule berfungsi.
- [ ] Attendance dapat dicatat per sesi dan per siswa.
- [ ] Skema bulanan, paket sesi, dan per sesi berfungsi sesuai aturan.
- [ ] Dashboard menampilkan tunggakan, pemasukan, jadwal, dan tindakan yang tertunda.
- [ ] Notifikasi lokal bekerja dan tidak mengirim pesan otomatis.
- [ ] Pesan dapat dibagikan melalui Android Sharesheet setelah pengguna memilih aksi.
- [ ] CSV import memiliki mapping, preview, validasi, dan laporan hasil.
- [ ] CSV export tersedia untuk dataset utama.
- [ ] Backup JSON dan restore berjalan tanpa internet.
- [ ] Operasi inti berjalan pada mode pesawat.
- [ ] AdMob gagal/tidak tersedia tanpa memblokir fungsi aplikasi.
- [ ] Tidak ada placement iklan pada form atau alur attendance/pembayaran/data portability.
- [ ] Tidak ada data pribadi siswa yang dikirim sebagai custom targeting ke sistem iklan.
- [ ] Test scenarios minimum pada bagian 16 lulus.
