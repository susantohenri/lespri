# Lespri — Product Requirements Document (PRD)

> **Status:** Final MVP Spec v1.1  
> **Tanggal:** 15 November 2026  
> **Platform:** Android (compileSdk 36, targetSdk 36, minSdk 24)  
> **Application ID (Package Name):** `edu.lespri.app`  
> **Tech Stack:** Kotlin, Jetpack Compose, Material 3, Room DB, Firebase, AdMob  
> **Bahasa aplikasi:** Indonesia dan English (Auto-detect OS + in-app switch)  
> **Target MVP:** Pengelola bimbel rumahan/skala kecil dengan sekitar 5–30 siswa  
> **Model MVP:** Gratis dengan AdMob; tidak wajib registrasi; data utama disimpan lokal di perangkat (Room SQLite).  
> **Privacy Policy URL:** `https://susantohenri.github.io/privacy-policy`  

---

## 1. Ringkasan Produk

**Lespri** adalah aplikasi Android ringan untuk membantu pemilik/pengelola bimbel rumahan mengelola administrasi operasional harian: siswa, tutor, kelas, jadwal, kehadiran, tagihan, pembayaran, dan pelaporan ringkas.

Lespri ditujukan untuk bimbel kecil yang sudah berjalan dan kemungkinan memiliki data siswa di buku, Excel, atau CSV. Produk harus membuat pengelola bisa mulai menggunakan aplikasi tanpa harus memindahkan semua proses ke sistem baru atau membangun infrastruktur backend.

Lespri bukan LMS, marketplace tutor, atau software ERP sekolah. Fokus utamanya adalah **administrasi bimbel yang cepat, mudah dipindai, dan tetap berjalan tanpa internet**.

### 1.1 Positioning

> Administrasi bimbel rumahan yang rapi dan cepat, tanpa harus bergantung pada Excel, buku catatan, atau pesan WhatsApp yang tersebar.

### 1.2 Nilai utama

1. **Langsung ke masalah utama:** pengelola dapat melihat jadwal hari ini, kehadiran, dan pembayaran yang perlu ditindaklanjuti.
2. **Mudah dipahami sejak pertama dibuka:** navigasi jelas, istilah sederhana, dan alur kerja pendek.
3. **Tidak memaksa migrasi besar:** data awal dapat diimpor dari CSV; file Excel dapat dikonversi ke CSV.
4. **Offline-first:** data dan fungsi operasional inti tetap tersedia tanpa koneksi internet.
5. **Kontrol tetap di tangan pengguna:** pengingat muncul sebagai notifikasi lokal; pengguna sendiri yang memutuskan apakah akan membagikan pesan ke orang tua/siswa.
6. **Data portabel:** pengguna dapat mengekspor data dan membuat backup penuh kapan saja.

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

Target berikut adalah **target validasi awal**, bukan klaim performa yang sudah terbukti.

| Metrik | Definisi | Target validasi awal |
|---|---|---:|
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

**Hanya satu peran aplikasi: Owner/Admin.**

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

Gunakan prinsip **wide but clean** yang disukai dari TutorDesk:

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

Keputusan struktur navigasi MVP:

**Bottom navigation pada ponsel:**

1. **Beranda** — dashboard dan tindakan hari ini.
2. **Siswa** — daftar, profil, dan enrollment siswa.
3. **Kelas** — kelas privat/kelompok, siswa, tutor, dan jadwal terkait.
4. **Jadwal** — agenda, sesi, dan kehadiran.
5. **Keuangan** — tagihan, pembayaran, paket sesi, dan tunggakan.

**Hamburger drawer untuk menu sekunder:** Tutor, Laporan, Impor CSV, Ekspor Data, Backup & Restore, dan Pengaturan.

Pada tablet/layar lebar, side navigation atau navigation rail menggantikan bottom navigation; keduanya tidak ditampilkan bersamaan. Detail lengkap halaman, menu, komponen bar, dan user journey ada di dokumen pendukung terkait.

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

- **P0 — Wajib MVP:** tidak boleh dihilangkan dari rilis awal.
- **P1 — Penting:** masuk MVP bila kapasitas implementasi memungkinkan; jika tidak, rilis minor berikutnya.
- **Later — Setelah MVP:** bukan bagian dari acceptance criteria rilis pertama.

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

**Acceptance criteria:**

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

**Acceptance criteria:**

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

**Acceptance criteria:**

- Satu kelas dapat memiliki satu tutor utama pada MVP.
- Mengarsipkan tutor tidak menghapus sesi atau riwayat kelas sebelumnya.

### FR-04 — Kelas privat dan kelompok

**Prioritas:** P0

Jenis kelas:

- **Privat:** satu siswa per enrollment/kelas.
- **Kelompok:** beberapa siswa dalam satu kelas.

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

**Acceptance criteria:**

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

**Acceptance criteria:**

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

**Acceptance criteria:**

- Mencatat attendance untuk kelas kelompok tidak memerlukan membuka profil setiap siswa satu per satu.
- Mengubah attendance memperbarui perhitungan paket/per sesi sesuai aturan dan menampilkan perubahan sebelum disimpan jika berdampak pada tagihan.
- Attendance historis tidak terhapus ketika siswa atau tutor diarsipkan.

### FR-07 — Skema pembayaran dan tagihan

**Prioritas:** P0

Setiap enrollment dapat menggunakan salah satu skema pembayaran berikut:

1. **Bulanan:** biaya tetap per periode/bulan.
2. **Paket sesi:** siswa membeli sejumlah sesi di muka.
3. **Per sesi:** biaya dikenakan berdasarkan sesi yang memenuhi aturan penagihan.

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
- Riwayat pembayaran: nominal, tanggal, metode, dan catatan/referensi opsional.

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

**Acceptance criteria:**

- Saldo tagihan = total tagihan dikurangi total pembayaran yang valid.
- Status Lunas hanya jika saldo tagihan nol atau kurang.
- Pembayaran sebagian menampilkan saldo tersisa secara jelas.
- Mengubah atau menghapus transaksi harus menjaga konsistensi total tagihan dan saldo paket.
- Perhitungan nominal memakai integer satuan mata uang (misalnya rupiah), bukan floating point.

### FR-08 — Dashboard “pain payer”

**Prioritas:** P0

Dashboard harus memprioritaskan apa yang perlu dilakukan pemilik, bukan hanya menampilkan statistik dekoratif.

Komponen utama:

1. **Tunggakan saat ini:** total nilai tagihan belum lunas.
2. **Siswa belum lunas:** jumlah siswa dengan saldo tagihan.
3. **Tagihan jatuh tempo/terlambat:** jumlah dan nominal.
4. **Jadwal hari ini:** sesi, jam, kelas, dan tutor.
5. **Kehadiran belum dicatat:** sesi selesai/terlewat yang attendance-nya belum lengkap.
6. **Paket hampir habis:** jumlah siswa dengan saldo sesi rendah/habis.
7. **Pemasukan bulan ini:** total pembayaran yang diterima pada bulan berjalan.

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

**Story visual (P1):**

- Template vertikal 9:16 untuk promosi bimbel atau pengumuman umum.
- Data pribadi siswa tidak dimasukkan secara default.
- Pengguna dapat mengedit teks utama sebelum menghasilkan gambar.
- Sharing dilakukan melalui Android Sharesheet; tidak memposting otomatis ke Story.

### FR-11 — Import CSV

**Prioritas:** P0

Import ditujukan untuk bimbel yang sudah memiliki data siswa di Excel/Google Sheets/CSV.

**Keputusan implementasi hemat ukuran aplikasi:** MVP tidak membaca file `.xlsx` secara langsung. Pengguna membuka file Excel lalu memilih **Save As / Download as CSV UTF-8**, kemudian mengimpor file CSV ke aplikasi.

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

- `student_name` (wajib)
- `guardian_name`
- `guardian_phone`
- `school_grade`
- `class_name`
- `class_type` (`private` / `group`)
- `subject`
- `tutor_name`
- `schedule_day`
- `schedule_start_time`
- `schedule_duration_minutes`
- `billing_type` (`monthly` / `package` / `per_session`)
- `fee_amount`
- `package_session_count`
- `due_day`

Ketentuan import:

1. Pengguna memilih file CSV dari perangkat.
2. Sistem membaca header dan menawarkan mapping kolom.
3. Sistem menyediakan preview beberapa baris sebelum import.
4. Sistem memvalidasi field wajib, tipe data, nomor telepon, tanggal/jam, dan nominal.
5. Sistem mendeteksi kemungkinan duplikat berdasarkan kombinasi data yang masuk akal; jangan menggabungkan otomatis tanpa konfirmasi.
6. Pengguna melihat jumlah baris valid, baris bermasalah, dan kandidat duplikat.
7. Pengguna dapat memilih lanjut hanya dengan baris valid atau membatalkan seluruh import.
8. Hasil import menampilkan jumlah record berhasil, dilewati, dan gagal.
9. Import tidak boleh menghapus data yang sudah ada.

Format CSV:

- UTF-8; toleransi UTF-8 BOM.
- Deteksi delimiter umum: koma, titik koma, atau tab.
- Mendukung field yang dikutip dan karakter newline di dalam field.
- Nomor telepon diperlakukan sebagai teks.
- Pesan error harus menyebut nomor baris dan kolom.

**Acceptance criteria:**

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
- MVP menggunakan pola **replace local data** agar relasi konsisten dan implementasi sederhana; tampilkan peringatan tegas bahwa data lokal saat ini akan diganti.
- Sebelum restore, buat snapshot lokal sementara sebagai mekanisme pemulihan dari kegagalan proses.
- Restore harus bersifat transaksional: jika gagal, database lama tetap utuh.
- Setelah restore berhasil, jadwalkan ulang notifikasi lokal.

**Catatan batasan:** CSV digunakan untuk pertukaran dataset; JSON digunakan untuk backup penuh yang mempertahankan relasi. CSV bukan pengganti backup.

**Acceptance criteria:**

- Backup dan restore dapat dilakukan tanpa internet dan tanpa akun.
- Backup dari versi aplikasi yang sama dapat dipulihkan 100% pada instalasi uji.
- File backup invalid tidak boleh mengosongkan atau merusak data saat ini.
- Restore menampilkan konfirmasi sebelum mengganti data lokal.

### FR-13 — Bahasa aplikasi

**Prioritas:** P0

- Bahasa yang didukung: Bahasa Indonesia (`id`) dan English (`en`).
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

**Prinsip:** jangan memaksa pengguna mengisi seluruh konfigurasi sebelum aplikasi dapat dipakai.

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

Daftar ringkas berikut menjelaskan cakupan layar. Screen map pendamping `lespri-screen-map.md` menjadi acuan lebih rinci untuk inventaris halaman, akses menu, navigasi, komponen top/bottom/side bar, dan user journey.

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
| `OwnerProfile` | Profil lokal bimbel | id, business_name, created_at, updated_at |
| `Student` | Data siswa | id, name, guardian_name, guardian_phone, school_grade, notes, status |
| `Tutor` | Data tutor | id, name, phone, expertise, notes, status |
| `ClassGroup` | Kelas privat/kelompok | id, name, class_type, subject, tutor_id, location_note, status |
| `Enrollment` | Relasi siswa-kelas dan skema tagihan | id, student_id, class_id, billing_type, fee_amount, due_day, package_session_count, status |
| `ScheduleRule` | Pola jadwal berulang | id, class_id, tutor_id, weekday, start_time, duration_minutes, effective_from, effective_until, status |
| `LessonSession` | Sesi spesifik pada tanggal/jam tertentu | id, class_id, tutor_id, session_date, start_time, duration_minutes, status, source_rule_id |
| `AttendanceRecord` | Kehadiran siswa pada sesi | id, session_id, enrollment_id, attendance_status, note, updated_at |
| `Charge` | Tagihan/biaya | id, enrollment_id, charge_type, period_start, period_end, amount, due_date, status |
| `Payment` | Pembayaran terhadap tagihan | id, charge_id, amount, paid_at, method, reference_note |
| `PackageLedger` | Perubahan saldo sesi paket | id, enrollment_id, session_id, delta_sessions, reason, created_at |
| `NotificationPreference` | Pengaturan notifikasi lokal | id, notification_type, enabled, lead_time |
| `AppSettings` | Preferensi aplikasi | language, date_format, schema_version, other_settings |

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

1. **Satu siswa di beberapa kelas:** diperbolehkan; skema pembayaran dapat berbeda per enrollment.
2. **Satu kelas dengan beberapa siswa:** hanya untuk kelas kelompok.
3. **Kelas tanpa tutor:** dapat disimpan sebagai draft, tetapi sistem memberi peringatan sebelum jadwal dibuat.
4. **Tutor nonaktif:** tetap terlihat dalam histori; tidak dapat dipilih untuk penugasan baru.
5. **Siswa nonaktif:** tidak muncul sebagai default pilihan enrollment baru; histori tetap tersedia.
6. **Jadwal rutin diubah:** hanya sesi mendatang yang belum diselesaikan yang mengikuti aturan perubahan; sesi historis tetap.
7. **Sesi dibatalkan:** tidak menghasilkan biaya per sesi dan tidak mengurangi saldo paket secara otomatis.
8. **Attendance berubah dari Hadir:** sistem membalikkan dampak paket/per sesi yang sebelumnya berasal dari attendance tersebut.
9. **Pembayaran sebagian:** sisa tagihan harus tetap terlihat dan dapat ditagih.
10. **Pembayaran melebihi tagihan:** MVP sebaiknya menolak nominal berlebih atau meminta pengguna mengonfirmasi sebagai saldo/kredit terpisah. Default yang direkomendasikan: tolak nominal yang melebihi tagihan.
11. **Tagihan bulanan terbuat dua kali:** sistem harus mendeteksi enrollment dan periode yang sama agar tidak membuat duplikat.
12. **Paket sesi habis:** tampilkan saldo nol dan CTA untuk mencatat pembelian paket baru.
13. **Restore saat data sudah ada:** wajib menampilkan peringatan bahwa data lokal akan diganti.
14. **CSV dengan nomor telepon berawalan nol:** harus tetap disimpan sebagai teks.
15. **CSV memakai titik koma:** importer harus mendeteksi delimiter atau memberi pilihan kepada pengguna.
16. **Nama siswa sama:** jangan otomatis menganggap duplikat; gunakan kombinasi nama, wali, nomor telepon, dan/atau pilihan pengguna.
17. **Tidak ada internet:** semua CRUD, jadwal, attendance, transaksi, export, backup, dan restore tetap berjalan.
18. **Iklan gagal dimuat:** tidak boleh menampilkan error blocking atau menghalingi layar.
19. **Izin notifikasi ditolak:** fitur lokal tetap dapat digunakan; pengaturan menunjukkan status izin sistem.
20. **Perangkat ganti zona waktu:** jadwal dan notifikasi harus dihitung ulang tanpa mengubah tanggal historis secara keliru.

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

## 13. Monetisasi dan Ad Placement Strategy (Final MVP Specification)

### 13.1 Prinsip

Lespri adalah aplikasi administrasi yang dipakai untuk tugas singkat dan berulang. Pendapatan iklan tidak boleh merusak kecepatan kerja dan kenyamanan pengguna. Keputusan monetisasi memprioritaskan retensi jangka panjang dan kepatuhan penuh pada kebijakan Google AdMob & Google Play Developer Policy.

### 13.2 Keputusan Matriks Format Iklan MVP

Berdasarkan validasi kebutuhan pengguna dan kepatuhan kebijakan AdMob:

| Format Iklan | Status MVP | Penempatan / Trigger | Aturan & Batasan UX |
| :--- | :---: | :--- | :--- |
| **Adaptive Banner** | **AKTIF** | Sticky di bagian paling bawah layar Dashboard (Beranda) dan List Siswa | Diberikan padding/safety margin minimal `16dp` dari tombol navigasi/FAB untuk mencegah *accidental clicks*. Disembunyikan saat offline. |
| **Interstitial** | **AKTIF (Capped)** | Transisi natural setelah aksi selesai (misal: kembali ke list setelah menyimpan data siswa/jadwal/pembayaran) | **Wajib Capping:** Maksimal 1x per 3 aksi simpan berhasil DAN memiliki interval jeda minimal 180 detik antar-iklan. Dilarang muncul langsung saat tombol Simpan ditekan atau saat user menekan Back untuk exit. |
| **App Open Ad** | **NONAKTIF** | Saat aplikasi dibuka | Dimatikan (`isAppOpenAdsEnabled: false`) agar pengguna dapat langsung mencatat data tanpa jeda. |
| **Rewarded Ad** | **NONAKTIF** | Fitur bernilai tambah (opsional) | Dimatikan (`isRewardedAdsEnabled: false`) pada v1.0 karena belum ada tier Pro. Disiapkan untuk rilis v1.1 sebagai syarat opsional Export/Backup Laporan ke Excel/PDF. |
| **Native Ads** | **OPSIONAL** | List item non-kritis | Siap diaktifkan via Remote Config jika rasio impresi banner kurang optimal. |

### 13.3 Area yang Dilarang Keras untuk Placement Iklan

- Di atas atau berdekatan langsung (< 16dp) dengan tombol Simpan/Submit/FAB.
- Di dalam form ketika pengguna sedang mengetik input teks.
- Di alur cepat pencatatan attendance (kehadiran).
- Di alur pencatatan pembayaran atau saat membuat/membagikan rincian tagihan via WhatsApp.
- Di tengah-tengah proses migrasi data (Import CSV / Export / Backup / Restore).
- Pada dialog konfirmasi krusial atau dialog error/peringatan.
- Tepat saat tombol Back ditekan untuk keluar aplikasi (melanggar AdMob Policy).

### 13.4 Konfigurasi Firebase Remote Config & Fallback Defaults

Seluruh ID unit iklan dan toggle aktivasi dikelola secara dinamis melalui Firebase Remote Config SDK dengan fallback parameter lokal default:

```json
{
  "appOpenAdUnitId": "ca-app-pub-3940256099942544/9257395921",
  "bannerAdUnitId": "ca-app-pub-3940256099942544/6300978111",
  "interstitialAdUnitId": "ca-app-pub-3940256099942544/1033173712",
  "rewardedAdUnitId": "ca-app-pub-3940256099942544/5224354917",
  "nativeAdUnitId": "ca-app-pub-3940256099942544/2247696110",
  "isAdsEnabled": true,
  "isBannerAdsEnabled": true,
  "isInterstitialAdsEnabled": true,
  "isAppOpenAdsEnabled": false,
  "isRewardedAdsEnabled": false,
  "interstitialFrequencyCap": 3,
  "interstitialMinIntervalSeconds": 180
}
```

*Catatan:* Build testing/QA menggunakan sample AdMob App ID (`ca-app-pub-3940256099942544~3347511713`) di `AndroidManifest.xml`. Pergantian ke unit production dilakukan via parameter Firebase Console saat rilis store.

### 13.5 Instrumentasi Firebase Analytics & Ad Revenue Tracking

1. **Singleton Logger:** Implementasikan singleton `AnalyticsLogger` yang diinisialisasi saat Application startup.
2. **Core Event Tracking:**
   - `first_open`, `session_start`
   - `screen_view` (otomatis via lifecycle atau manual untuk composables)
   - `student_created`, `session_created`, `attendance_saved`, `payment_recorded`
   - `csv_imported`, `backup_created`, `backup_restored`
3. **Ad Monetization Tracking:**
   - Pasang `OnPaidEventListener` pada seluruh format iklan AdMob yang aktif.
   - Teruskan metrik ke Firebase Analytics (`ad_impression`) beserta parameter: `value` (mikro-revenue), `currency` (USD/IDR), `precision`, `ad_unit_id`, dan `ad_platform = "AdMob"`.

### 13.6 Spesifikasi Arsitektur & Teknologi (Tech Stack)

| Komponen | Spesifikasi / Pilihan | Keterangan |
| :--- | :--- | :--- |
| **Platform Target** | Android API 36 (`compileSdk = 36`, `targetSdk = 36`) | Sesuai regulasi rilis Google Play terbaru |
| **Minimum SDK** | Android API 24 (`minSdk = 24` / Android 7.0) | Menjangkau $\sim$95%+ perangkat aktif pengguna bimbel |
| **Bahasa & UI Framework** | Kotlin 2.x + Jetpack Compose + Material 3 | Pola deklaratif modern, clean code, scalable |
| **Arsitektur Kode** | MVVM / MVI + Repository Pattern | Clean separation of concerns, reactive StateFlow |
| **Database Lokal** | Android Jetpack Room (SQLite ORM) | 100% Offline-first, fast read/write, zero server cost |
| **Visual Direction** | Referensi TutorDesk (Wide but Clean) | Dominan Deep Slate/Navy (`#1E293B`), Accent Blue (`#2563EB`) |
| **Konfigurasi Cloud** | Firebase Remote Config, Analytics, Crashlytics | Monitoring ANR/Crash & Dynamic Feature Toggling |
| **Privasi & Regulasi** | Google UMP SDK (User Messaging Platform) | Menangani consent GDPR (EEA/UK) & CCPA otomatis |
| **Identitas Paket** | `edu.lespri.app` | Terhubung dengan project Firebase `tempat-sambung` |
| **Kebijakan Privasi** | `https://susantohenri.github.io/privacy-policy` | Wajib tercantum di menu About/Settings dan Store Listing |

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

## 18. Open Decisions & Resolved Decisions

### 18.1 Resolved for MVP (Hasil Final Diskusi)
- [x] **AdMob Formats & Frequency:** Adaptive Banner aktif (bawah dashboard & siswa), Interstitial aktif dengan capping (1 per 3 aksi simpan & interval 180s), App Open & Rewarded nonaktif untuk v1.0.
- [x] **Tech Stack & ID Paket:** Kotlin, Jetpack Compose, Room DB, Firebase Suite, compileSdk/targetSdk 36, Package ID `edu.lespri.app`.
- [x] **Consent & Privacy Policy:** Menggunakan Google UMP SDK, link resmi `https://susantohenri.github.io/privacy-policy` sudah diverifikasi mencakup AdMob, Analytics, Crashlytics, dan Remote Config.

### 18.2 Post-MVP Considerations
Hal-hal berikut ditunda hingga traksi pasar terbukti:
1. Menambahkan Rewarded Ads untuk membuka fitur *Export Laporan Excel/PDF*.
2. Apakah Story template masuk MVP pertama atau rilis minor berikutnya.
3. Apakah perlu opsi merge saat restore; MVP menggunakan replace untuk menyederhanakan konsistensi data.
4. Apakah perlu sinkronisasi cloud dan multi-user setelah kebutuhan pasar terbukti.
5. Apakah perlu skema honor tutor; jangan dibangun sebelum aturan pembayaran tutor tervalidasi.
6. Apakah perlu batas gratis atau paket Pro/IAP; tidak termasuk MVP.

---

## 19. Referensi Produk dan Dokumentasi

- [TutorDesk – Coaching Manager di Google Play](https://play.google.com/store/apps/details?id=com.tutordesk.app) — referensi pembanding untuk pendekatan offline-first, daftar siswa/kelas, kehadiran, dan jadwal.
- [Google AdMob — Ad units, ad formats, and ad types](https://support.google.com/admob/answer/6128738) — daftar format banner, interstitial, rewarded, native, app open, dan jenis iklan.
- [Google AdMob — Android banner ads](https://developers.google.com/admob/android/banner) — panduan banner/adaptive banner Android.
- [Google AdMob — Interstitial ad guidance](https://support.google.com/admob/answer/6066980) — panduan penempatan interstitial pada titik jeda/transisi yang sesuai dan risiko gangguan UX.

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
