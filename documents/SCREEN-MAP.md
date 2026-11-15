# Lespri — Screen Map, Navigation & User Journeys

> **Status:** UX / Information Architecture draft v1.0  
> **Platform:** Android  
> **Target:** Pemilik/pengelola bimbel rumahan, sekitar 5–30 siswa  
> **UX direction:** *Wide but clean* — informasi padat, mudah dipindai, dan aksi utama selalu mudah ditemukan.

Dokumen ini melengkapi `lespri-prd.md`. Ini adalah rancangan struktur layar dan navigasi MVP, bukan desain visual final.

---

## 1. Keputusan Navigasi

### 1.1 Pola utama

| Area | Pola yang dipakai | Fungsi |
|---|---|---|
| Top bar | Top App Bar | Judul halaman, navigasi kembali/menu, dan aksi kontekstual |
| Bottom bar | Bottom Navigation, 5 item | Pindah antar area kerja utama pada ponsel |
| Side bar | Navigation Rail / Navigation Drawer permanen pada layar lebar | Menggantikan bottom navigation saat ruang layar cukup |
| Hamburger | Modal Navigation Drawer pada ponsel | Membuka menu sekunder: Tutor, Laporan, Data & Backup, Pengaturan |
| FAB | Floating Action Button kontekstual | Membuat item baru pada halaman yang relevan |
| Tabs / segmented control | Di dalam halaman | Berpindah antar sub-view yang masih satu konteks |
| Overflow (⋮) | Menu aksi kontekstual | Edit, nonaktifkan, hapus, bagikan, dan aksi sekunder |
| Bottom sheet | Aksi/opsi singkat | Pilih filter, status, atau aksi cepat tanpa pindah halaman |
| Android Sharesheet | Komponen sistem Android | Membagikan invoice, pengingat, atau ringkasan secara manual |

### 1.2 Bottom navigation — ponsel

Gunakan tepat lima tujuan utama berikut:

| Urutan | Menu | Isi | Ikon konseptual |
|---:|---|---|---|
| 1 | **Beranda** | Dashboard dan tindakan yang perlu dilakukan | Home |
| 2 | **Siswa** | Daftar, tambah, dan detail siswa | Users |
| 3 | **Kelas** | Kelas privat/kelompok dan daftar anggota | Book / Graduation cap |
| 4 | **Jadwal** | Agenda, sesi, dan kehadiran | Calendar |
| 5 | **Keuangan** | Tagihan, pembayaran, paket sesi, tunggakan | Wallet |

Bottom navigation tampil pada halaman root dan halaman detail dalam lima area tersebut. Sembunyikan pada flow fokus seperti form penuh, pencatatan kehadiran, import, dan restore agar area kerja tidak sempit.

### 1.3 Menu hamburger / drawer — menu sekunder

Drawer dibuka dari ikon hamburger di top app bar pada halaman root.

| Grup | Menu | Tujuan |
|---|---|---|
| Data operasional | **Tutor** | Daftar dan pengelolaan tutor yang ditangani pemilik |
| Analisis | **Laporan** | Rekap keuangan dan kehadiran |
| Data | **Impor CSV** | Memindahkan data dari aplikasi/spreadsheet lama |
| Data | **Ekspor data** | Mengeluarkan data agar mudah dipakai di tempat lain |
| Data | **Backup & Restore** | Backup penuh dan pemulihan data lokal |
| Preferensi | **Pengaturan** | Profil bimbel, bahasa, notifikasi, kebijakan privasi, informasi aplikasi |

Tidak ada menu **Login**, **Akun Tutor**, atau **Portal Orang Tua** pada MVP karena penggunaan lokal tidak wajib registrasi dan hanya pemilik yang mengoperasikan aplikasi.

### 1.4 Side navigation — tablet / layar lebar

- Side navigation menggantikan bottom navigation; jangan tampilkan keduanya bersamaan.
- Tampilkan lima menu utama dan grup menu sekunder dalam satu panel.
- Pada lebar menengah, gunakan navigation rail dengan ikon dan label saat diperluas.
- Pada layar lebar, gunakan side navigation permanen dengan label.
- Untuk halaman Siswa, Kelas, dan Tutor, pertimbangkan pola dua panel: daftar di kiri, detail terpilih di kanan.
- Pada ponsel, jangan tampilkan sidebar permanen.

### 1.5 Top app bar

**Halaman root:**
- Kiri: ikon hamburger.
- Tengah: judul halaman; nama bimbel dapat menjadi subtitle bila konteks diperlukan.
- Kanan: aksi kontekstual seperti pencarian atau filter jika relevan.

**Halaman turunan:**
- Kiri: tombol kembali.
- Tengah: judul halaman atau nama item.
- Kanan: aksi utama sekunder yang relevan, misalnya edit atau share; sisanya di overflow.

Catatan:
- Jangan pasang ikon lonceng yang seolah membuka notification inbox jika MVP hanya menggunakan notifikasi lokal Android.
- Notifikasi lokal tampil melalui sistem Android. Ketika diketuk, notifikasi membuka halaman tujuan yang relevan.
- Pencarian ditampilkan pada halaman daftar yang memerlukannya, bukan sebagai tombol global yang tidak punya konteks.

### 1.6 Komponen navigasi lain

- **FAB:** `+` untuk tambah siswa di halaman Siswa, tambah kelas di Kelas, atau tambah jadwal di Jadwal. Satu halaman hanya memiliki satu FAB utama.
- **Tabs / segmented control:** untuk subkategori, contohnya `Hari | Minggu | Agenda` pada Jadwal atau `Tagihan | Pembayaran | Paket` pada Keuangan.
- **Filter chips:** status aktif, tutor, kelas, tanggal, dan status pembayaran.
- **Overflow:** aksi yang jarang digunakan; jangan sembunyikan aksi utama di sini.
- **Sticky bottom action bar:** dipakai pada form panjang atau pencatatan kehadiran jika tombol Simpan perlu selalu terlihat.
- **System Back:** kembali ke layar sebelumnya; pada form yang berubah dan belum disimpan, minta konfirmasi sebelum membuang perubahan.

---

## 2. Daftar Halaman MVP

### 2.1 Setup dan beranda

| ID | Halaman | Akses | Fitur utama | Navigasi keluar |
|---|---|---|---|---|
| P01 | First Launch / Setup Bimbel | Otomatis saat pertama buka | Pilih/konfirmasi bahasa, nama bimbel, informasi dasar opsional, lanjut tanpa akun | P02 Dashboard |
| P02 | Dashboard | Bottom: Beranda | Ringkasan tunggakan, siswa belum lunas, pemasukan bulan ini, jadwal hari ini, sesi belum diabsen, paket hampir habis, quick actions | Siswa, Kelas, Jadwal, Keuangan, Tutor, Laporan |

### 2.2 Siswa

| ID | Halaman | Akses | Fitur utama | Navigasi keluar |
|---|---|---|---|---|
| P03 | Daftar Siswa | Bottom: Siswa | Cari, filter aktif/nonaktif, filter kelas/status pembayaran, urutkan, tambah siswa | P04, P05, P08, P17 |
| P04 | Form Siswa | P03 → Tambah/Edit | Nama, jenjang/kelas sekolah, kontak siswa/orang tua, catatan, status aktif; opsi memasukkan ke kelas | P03 atau P05 |
| P05 | Detail Siswa | P03 → pilih siswa | Ringkasan, kelas yang diikuti, jadwal, riwayat kehadiran, riwayat pembayaran, catatan, share ringkasan | P04, P08, P14, P17, P18 |

### 2.3 Kelas

| ID | Halaman | Akses | Fitur utama | Navigasi keluar |
|---|---|---|---|---|
| P06 | Daftar Kelas | Bottom: Kelas | Cari/filter kelas, jenis privat/kelompok, mata pelajaran, tutor, jumlah siswa, jadwal berikutnya | P07, P08, P09, P13 |
| P07 | Form Kelas | P06 → Tambah/Edit | Nama kelas, privat/kelompok, mata pelajaran, tutor penanggung jawab, siswa/anggota, skema dan tarif pembayaran, jadwal | P06 atau P08 |
| P08 | Detail Kelas | P06 → pilih kelas | Ringkasan kelas, daftar siswa, tutor, jadwal, riwayat sesi, aksi tambah siswa dan mulai absensi | P07, P13, P14, P15 |

### 2.4 Tutor

| ID | Halaman | Akses | Fitur utama | Navigasi keluar |
|---|---|---|---|---|
| P09 | Daftar Tutor | Drawer: Tutor | Cari tutor, status aktif, jumlah kelas yang diampu | P10, P11, P07 |
| P10 | Form Tutor | P09 → Tambah/Edit | Nama, nomor kontak, mata pelajaran/keahlian, catatan, status aktif | P09 atau P11 |
| P11 | Detail Tutor | P09 → pilih tutor | Kontak, kelas yang diampu, jadwal mengajar terkait | P10, P07, P13 |

**Batasan MVP:** tutor belum memiliki akun atau login sendiri. Pemilik yang menambah, mengubah, dan mengelola semua data tutor. Payroll/honor kompleks tidak termasuk MVP.

### 2.5 Jadwal dan kehadiran

| ID | Halaman | Akses | Fitur utama | Navigasi keluar |
|---|---|---|---|---|
| P12 | Jadwal | Bottom: Jadwal | Tampilan Hari/Minggu/Agenda, filter kelas/tutor, sesi mendatang, status sesi | P13, P14, P15, P08, P11 |
| P13 | Form Jadwal / Sesi | P12 atau P08 → Tambah/Edit | Pilih kelas, tanggal, jam, tutor, pengulangan bila didukung, catatan, reschedule/batal | P12 atau P14 |
| P14 | Detail Sesi | P12 → pilih sesi | Kelas, tutor, waktu, peserta, status absensi, catatan, aksi mulai/ubah sesi | P13, P15, P18 |
| P15 | Pencatatan Kehadiran | P14 → Catat Kehadiran | Tandai Hadir/Izin/Sakit/Tidak Hadir, tandai semua hadir, catatan opsional, simpan | P14, P12, P05, P08 |

### 2.6 Keuangan

| ID | Halaman | Akses | Fitur utama | Navigasi keluar |
|---|---|---|---|---|
| P16 | Keuangan | Bottom: Keuangan | Ringkasan pemasukan/tunggakan; sub-view Tagihan, Pembayaran, Paket; filter tanggal dan status | P17, P18, P19, P05 |
| P17 | Detail Tagihan | P16 atau P05 → pilih tagihan | Rincian biaya, periode/paket/sesi, jumlah dibayar, sisa, jatuh tempo, riwayat pembayaran, share invoice/pengingat | P18, P05, P16 |
| P18 | Catat Pembayaran | P17 → Catat Pembayaran | Nominal, tanggal, metode/catatan opsional, pembayaran penuh/parsial, simpan | P17, P19 |
| P19 | Detail Pembayaran / Bukti | Setelah P18 atau dari riwayat | Rincian transaksi, status, bagikan bukti pembayaran melalui Android Sharesheet | P17, P16 |

### 2.7 Laporan, data, dan pengaturan

| ID | Halaman | Akses | Fitur utama | Navigasi keluar |
|---|---|---|---|---|
| P20 | Laporan | Drawer: Laporan | Rekap keuangan dan kehadiran, filter rentang tanggal/kelas/tutor, ringkasan yang dapat dibagikan atau diekspor | P21, P22 |
| P21 | Impor CSV | Drawer: Impor CSV | Pilih jenis data/file, mapping kolom, preview, deteksi baris invalid/duplikat, konfirmasi import | P02 atau halaman daftar terkait |
| P22 | Ekspor Data | Drawer: Ekspor Data | Pilih data yang diekspor, rentang data bila relevan, hasil file CSV | Kembali ke halaman asal |
| P23 | Backup & Restore | Drawer: Backup & Restore | Buat backup JSON (kompresi GZIP opsional), pilih file backup, validasi, konfirmasi restore | Kembali ke halaman asal |
| P24 | Pengaturan | Drawer: Pengaturan | Profil bimbel, bahasa Indonesia/English, preferensi pengingat, info penyimpanan lokal, privasi, versi aplikasi | Halaman terkait |

### 2.8 Halaman yang bukan screen tersendiri

Hal-hal berikut cukup menjadi komponen, dialog, state, atau bottom sheet agar jumlah halaman tidak membengkak:

- Search result dan filter pada halaman daftar.
- Konfirmasi hapus/nonaktifkan.
- Pemilih tanggal dan jam.
- Pemilih siswa/tutor/kelas.
- Pilihan status kehadiran.
- Pemilih metode pembayaran.
- Permission prompt notifikasi Android.
- Konfirmasi overwrite saat restore.
- Pilihan aplikasi tujuan saat share (ditangani Android Sharesheet).
- Empty state, error state, dan loading state.

---

## 3. Struktur Menu

```text
Lespri
│
├── Bottom Navigation / Side Navigation
│   ├── Beranda
│   ├── Siswa
│   ├── Kelas
│   ├── Jadwal
│   └── Keuangan
│
└── Hamburger Drawer (menu sekunder)
    ├── Tutor
    ├── Laporan
    ├── Data
    │   ├── Impor CSV
    │   ├── Ekspor Data
    │   └── Backup & Restore
    └── Pengaturan
        ├── Profil Bimbel
        ├── Bahasa
        ├── Pengingat / Notifikasi
        ├── Privasi
        └── Tentang Lespri
```

---

## 4. Fitur dan Navigasi — User Journeys

### UJ-01 — Pengguna baru mulai dari nol

`First Launch → Setup Bimbel → Dashboard → Siswa → Tambah Siswa → Detail Siswa`

1. Pengguna membuka Lespri; tidak diminta membuat akun.
2. Bahasa mengikuti perangkat atau pilihan pengguna.
3. Pengguna mengisi nama bimbel; data lain boleh dilengkapi belakangan.
4. Dashboard tampil dengan empty state dan CTA yang jelas.
5. Pengguna menambah siswa pertama.
6. Dari detail siswa, pengguna dapat membuat kelas atau memasukkan siswa ke kelas yang sudah ada.

### UJ-02 — Pindah dari Excel / aplikasi lama

`Dashboard atau Drawer → Impor CSV → Pilih File → Mapping Kolom → Preview → Konfirmasi → Daftar Siswa/Kelas`

1. Pengguna memilih jenis data yang akan diimpor.
2. Aplikasi menampilkan format/template CSV yang didukung.
3. Pengguna memilih file CSV UTF-8.
4. Pengguna mencocokkan kolom file dengan field Lespri.
5. Aplikasi menampilkan preview, jumlah baris valid, invalid, dan duplikat.
6. Pengguna mengonfirmasi proses import.
7. Hasil import menampilkan jumlah data berhasil dan baris yang perlu diperbaiki.

**Catatan XLSX:** pengguna menyimpan workbook Excel sebagai CSV UTF-8 melalui Excel/Google Sheets, lalu mengimpor file CSV tersebut.

### UJ-03 — Membuat kelas dan menugaskan tutor

`Kelas → Tambah Kelas → Isi Detail → Pilih Tutor → Pilih Siswa → Atur Pembayaran/Jadwal → Simpan → Detail Kelas`

1. Pemilik membuat kelas privat atau kelompok.
2. Pemilik mengisi mata pelajaran dan informasi kelas.
3. Pemilik memilih tutor yang sudah terdaftar atau menambahkan tutor baru.
4. Pemilik memilih satu atau beberapa siswa sesuai jenis kelas.
5. Pemilik mengatur skema pembayaran dan jadwal.
6. Kelas tersimpan dan muncul di daftar Kelas serta Jadwal.

### UJ-04 — Menambah siswa ke kelas yang sudah ada

`Siswa → Tambah Siswa → Simpan → Detail Siswa → Tambahkan ke Kelas → Pilih Kelas → Simpan`

Alternatif:

`Kelas → Detail Kelas → Tambah Siswa → Pilih Siswa → Simpan`

### UJ-05 — Mengelola tutor

`Hamburger → Tutor → Tambah Tutor → Simpan → Detail Tutor → Tugaskan ke Kelas`

1. Pemilik membuat data tutor.
2. Pemilik membuka detail tutor untuk melihat kelas yang diampu.
3. Penugasan tutor dilakukan dari form/detail kelas.
4. Tutor tidak perlu login atau memasang aplikasi pada MVP.

### UJ-06 — Melihat jadwal dan mencatat kehadiran

`Dashboard (Jadwal Hari Ini) atau Jadwal → Detail Sesi → Catat Kehadiran → Simpan → Detail Sesi`

1. Pengguna membuka sesi dari jadwal hari ini atau kalender.
2. Pengguna melihat kelas, tutor, waktu, dan daftar siswa.
3. Pengguna menandai status kehadiran setiap siswa.
4. Pengguna dapat memakai aksi “Tandai Semua Hadir”, lalu mengoreksi siswa tertentu.
5. Pengguna menyimpan kehadiran.
6. Riwayat kehadiran siswa dan kelas diperbarui.
7. Pemakaian paket sesi diperbarui mengikuti business rule pembayaran yang ditetapkan di PRD.

### UJ-07 — Mencatat pembayaran

`Dashboard (Tunggakan) atau Keuangan → Detail Tagihan → Catat Pembayaran → Simpan → Detail Pembayaran`

1. Pengguna membuka tagihan dari daftar tunggakan atau profil siswa.
2. Pengguna memeriksa jumlah tagihan dan sisa pembayaran.
3. Pengguna mencatat pembayaran penuh atau parsial.
4. Status tagihan dan ringkasan keuangan diperbarui.
5. Pengguna dapat memilih Bagikan Bukti/Invoice.
6. Android Sharesheet terbuka; pengguna sendiri memilih WhatsApp atau aplikasi lain.

### UJ-08 — Mengirim pengingat secara manual

`Notifikasi Lokal Android → Ketuk Notifikasi → Detail Tagihan/Sesi → Bagikan → Android Sharesheet → Pilih Aplikasi`

1. Lespri menampilkan notifikasi lokal sesuai pengingat yang diaktifkan.
2. Pengguna mengetuk notifikasi.
3. Aplikasi membuka tagihan atau sesi terkait.
4. Pengguna meninjau teks yang disiapkan.
5. Pengguna memilih apakah akan membagikannya dan melalui aplikasi apa.

Lespri tidak mengirim WhatsApp/DM secara otomatis.

### UJ-09 — Melihat laporan dan mengekspor data

`Hamburger → Laporan → Pilih Jenis Laporan → Atur Filter → Lihat Rekap → Ekspor/Bagikan`

1. Pengguna memilih laporan keuangan atau kehadiran.
2. Pengguna memilih rentang tanggal dan filter opsional.
3. Aplikasi menampilkan ringkasan.
4. Pengguna mengekspor data yang tersedia atau membagikan ringkasan.

### UJ-10 — Backup dan restore saat ganti perangkat

`Hamburger → Backup & Restore → Buat Backup → Simpan File`

Restore:

`Instal Lespri di perangkat tujuan → Lewati Login → Backup & Restore → Pilih File → Validasi → Konfirmasi Restore → Dashboard`

1. Pengguna membuat backup penuh ke file JSON terkompresi bila kompresi diaktifkan.
2. Pengguna menyimpan file di lokasi yang dipilih melalui Android Storage Access Framework.
3. Pada perangkat baru, pengguna memilih file backup.
4. Aplikasi memvalidasi versi dan integritas file.
5. Pengguna mengonfirmasi pemulihan; aplikasi menjelaskan apakah data lokal yang ada akan diganti atau digabung.
6. Setelah restore, dashboard menampilkan data yang dipulihkan.

---

## 5. Peta Navigasi Ringkas

```text
First Launch
   └── Setup Bimbel
         └── Dashboard
              ├── Siswa
              │    ├── Form Siswa
              │    └── Detail Siswa
              │         ├── Kelas
              │         ├── Kehadiran
              │         └── Keuangan → Detail Tagihan → Catat Pembayaran
              │
              ├── Kelas
              │    ├── Form Kelas
              │    └── Detail Kelas
              │         ├── Daftar Siswa
              │         ├── Tutor
              │         └── Jadwal → Detail Sesi → Kehadiran
              │
              ├── Jadwal
              │    ├── Form Jadwal/Sesi
              │    └── Detail Sesi → Kehadiran
              │
              ├── Keuangan
              │    ├── Tagihan → Detail Tagihan → Catat Pembayaran
              │    ├── Pembayaran → Detail/Bukti Pembayaran
              │    └── Paket Sesi
              │
              └── Hamburger Drawer
                   ├── Tutor → Form/Detail Tutor
                   ├── Laporan
                   ├── Impor CSV
                   ├── Ekspor Data
                   ├── Backup & Restore
                   └── Pengaturan
```

---

## 6. Komponen UI per Bar

### 6.1 Top App Bar

| Komponen | Kapan tampil | Perilaku |
|---|---|---|
| Hamburger | Halaman root | Membuka drawer menu sekunder |
| Back | Halaman turunan / flow | Kembali satu tingkat; cek perubahan belum tersimpan |
| Title | Semua halaman | Menjelaskan konteks aktif dengan label singkat |
| Subtitle | Jika dibutuhkan | Nama bimbel, nama siswa, kelas, atau konteks tambahan |
| Search | Halaman daftar | Membuka/menampilkan pencarian pada daftar aktif |
| Filter | Halaman daftar/jadwal/keuangan | Membuka filter kontekstual |
| Overflow | Detail/list item | Menyimpan aksi sekunder yang jarang dipakai |

### 6.2 Bottom Navigation

- Beranda
- Siswa
- Kelas
- Jadwal
- Keuangan

Aturan:
- Label teks selalu ditampilkan bersama ikon.
- Tab aktif memiliki indikator yang jelas.
- Jangan memakai badge angka kecuali benar-benar actionable dan akurat.
- Tidak ada tombol “+” permanen sebagai item keenam; gunakan FAB kontekstual.

### 6.3 Side Navigation

- Mengandung tujuan yang sama dengan bottom navigation dan menu drawer.
- Ditampilkan permanen hanya pada layar dengan ruang cukup.
- Menggantikan, bukan menduplikasi, bottom navigation.
- Boleh dikelompokkan: Operasional, Analisis, Data, Preferensi.

### 6.4 FAB dan sticky action bar

| Lokasi | Aksi utama |
|---|---|
| Siswa | Tambah Siswa |
| Kelas | Tambah Kelas |
| Jadwal | Tambah Jadwal/Sesi |
| Tutor | Tambah Tutor |
| Form | Simpan sebagai sticky bottom action jika form panjang |
| Kehadiran | Simpan Kehadiran sebagai sticky bottom action |

### 6.5 Tabs dan segmented controls

Gunakan hanya untuk perpindahan sub-view di halaman yang sama:

- Jadwal: `Hari | Minggu | Agenda`
- Keuangan: `Tagihan | Pembayaran | Paket`
- Detail siswa: gunakan section/tab `Ringkasan | Kelas | Kehadiran | Keuangan` bila jumlah konten membuat halaman terlalu panjang.
- Laporan: `Keuangan | Kehadiran`

Pada layar sempit, tab boleh scroll horizontal; jangan membuat tab bertingkat.

---

## 7. Aturan UX “Wide but Clean”

1. Utamakan list padat dengan nama, informasi sekunder, dan status pada satu row; jangan ubah setiap record menjadi card besar.
2. Gunakan summary card hanya untuk angka penting yang membutuhkan tindakan.
3. Tampilkan status pembayaran/kehadiran sebagai label teks plus warna, bukan warna saja.
4. Search dan filter berada dekat dengan daftar yang dikendalikan.
5. Aksi utama tampil langsung; aksi jarang masuk overflow.
6. Form dibagi per kelompok informasi, bukan wizard panjang.
7. Pada layar lebar, manfaatkan dua panel dan ruang horizontal; pada ponsel kecil, gunakan layout satu kolom dengan row yang tetap mudah disentuh.
8. Hindari duplikasi navigasi: bottom nav tidak tampil bersamaan dengan side nav permanen.
9. Jangan membuat halaman terpisah untuk setiap filter, status, atau dialog.
10. Setiap halaman harus punya empty state yang mengarahkan pengguna ke tindakan berikutnya.

---

## 8. Di Luar Navigasi MVP

- Login/registrasi wajib dan pemulihan akun cloud.
- Dashboard/login khusus tutor.
- Portal orang tua/siswa.
- Multi-device sync dan cloud backup otomatis.
- Chat atau pengiriman WhatsApp otomatis.
- Payroll tutor dan approval honor.
- Marketplace tutor dan pendaftaran siswa publik.
- LMS, materi, ujian, tryout, dan video conference.

---

## 9. Hal yang Perlu Diputuskan Sebelum Wireframe

1. **Default landing setelah setup:** Dashboard atau wizard singkat untuk tambah/impor siswa. Rekomendasi: Dashboard dengan CTA `Tambah Siswa` dan `Impor CSV`.
2. **Bentuk daftar:** compact row sebagai default; tabel hanya untuk layar lebar. Rekomendasi: compact row responsif.
3. **Detail siswa:** tab atau satu halaman panjang ber-section. Rekomendasi: tab/section ringkas, ditetapkan setelah wireframe.
4. **Tutor:** menu sekunder di drawer; penugasan dilakukan dari form/detail kelas. Rekomendasi: gunakan struktur ini di MVP.
5. **Kelas di bottom nav:** tetap menjadi tujuan utama karena kelas menghubungkan siswa, tutor, jadwal, dan skema pembayaran.

---

## 10. Kurasi Layar untuk Google Play Store Listing & Showcase Assets

Pemilihan screenshot untuk Play Store listing dirancang untuk memecahkan *pain point* utama pengelola bimbel rumahan dan memaksimalkan konversi instalasi (ASO). Diambil dalam format portrait (1080x2400) menggunakan data bimbel realistis berbahasa Indonesia.

| No | ID Layar | Nama Layar | Headline Promosi (Banner Atas) | Elemen Kunci yang Ditonjolkan | Alasan Pemilihan & Nilai Konversi (ASO) |
|:---|:---|:---|:---|:---|:---|
| **01** | **P02** | Dashboard / Beranda | **"Pusat Kontrol Bimbel: Jadwal, Tunggakan & Sesi Hari Ini"** | • Card Total Tunggakan (Merah lembut)<br>• Pemasukan Bulan Ini (Hijau)<br>• Jadwal Sesi Hari Ini (2 sesi)<br>• Warning sesi kemarin belum diabsen | **First Impression Hook:** Penentu utama konversi (70%+ viewer). Menunjukkan bahwa Lespri adalah command center operasional terpadu, bukan sekadar buku catatan pasif. |
| **02** | **P15** | Pencatatan Kehadiran | **"Presensi Kilat, Saldo Paket Siswa Terpotong Otomatis"** | • Toggle Hadir/Izin/Sakit/Alpa<br>• Preview saldo kuota (Sisa 4 $\rightarrow$ 3 sesi)<br>• Badge peringatan "Sesi Terakhir (8/8)"<br>• Sticky button "Simpan Presensi" | **Killer Feature Hook:** Menjawab ketakutan terbesar pemilik bimbel: lupa sisa sesi paket dan salah potong saat siswa izin/sakit. Otomatisasi ledger terbukti di layar ini. |
| **03** | **P16** | Keuangan & Tagihan | **"Pantau Tagihan & Siswa Menunggak Tanpa Repot"** | • Tab Tagihan aktif<br>• Kartu total tagihan tertunda<br>• Chip status merah **Jatuh Tempo (H+3)**<br>• Status bayar sebagian (kuning) | **Cashflow Guardian Hook:** Pemilik bimbel butuh aplikasi untuk mengatasi masalah uang dan kebiasaan lupa menagih. Menampilkan kejelasan status jatuh tempo. |
| **04** | **P19** | Detail Pembayaran / Bukti | **"Kuitansi Resmi & Rincian Pembayaran Siap Kirim via WhatsApp"** | • No. Kuitansi resmi (`#KW-202611-0042`)<br>• Rincian paket & nominal lunas<br>• Tombol aksi utama "Kirim via WhatsApp" (Android Sharesheet) | **Trust & Professionalism Hook:** Meningkatkan wibawa bimbel di hadapan orang tua murid dengan kuitansi profesional tanpa perlu printer fisik. |
| **05** | **P12** | Agenda & Jadwal Sesi | **"Kelola Jadwal & Tutor Rapi Bebas Bentrok"** | • Agenda mingguan/harian<br>• Kartu sesi: jam, mapel, nama tutor<br>• Status chip ("Selesai", "Segera Dimulai") | **Peace of Mind Hook:** Menjawab kerumitan mengatur jadwal banyak kelas dan multi-tutor dalam ruang/waktu terbatas tanpa tumpang tindih. |
| **06** | **P05** | Detail Siswa | **"Data Siswa, Riwayat Kehadiran & Paket Terpusat"** | • Profil siswa & tombol WhatsApp ortu<br>• Ringkasan paket aktif (Sisa 3/8 sesi)<br>• Riwayat kehadiran 100%<br>• Tagihan lunas | **Single Source of Truth Hook:** Membantu pemilik bimbel menjawab pertanyaan orang tua murid mengenai absensi dan sisa pertemuan dalam 5 detik. |
| **07** | **P21/P23** | Impor CSV & Portabilitas | **"100% Offline Tanpa Biaya Bulanan & Impor Mudah dari Excel"** | • Auto-mapping kolom CSV Excel<br>• Preview baris valid instan<br>• Badge "100% Data Lokal & Aman di HP" | **Zero Friction & Privacy Hook:** Menghilangkan keengganan beralih karena repot ketik ulang dari Excel, sekaligus meyakinkan bahwa aplikasi bebas biaya langganan cloud. |

### Spesifikasi Seeder Data Realistis Play Store (`PlayStoreDataSeeder.kt`)
Agar tangkapan layar terlihat meyakinkan bagi pengguna di Indonesia, data seeder wajib menggunakan data berikut:
* **Entitas Bimbel:** *"Bimbel Prestasi Cendekia"*, Jl. Melati No. 14, Jakarta Timur (Kontak: `0812-3456-7890`).
* **Tutor:** Bu Ratna (Matematika SMA), Kak Hendra (Fisika & Sains), Kak Kevin (Bahasa Inggris & Kimia).
* **Siswa Contoh:**
  1. *Ahmad Fauzi* (SMA 12 IPA) — Paket 8 Sesi Fisika (Sisa 3 sesi, status Lunas, riwayat kehadiran 100%).
  2. *Clarissa Aurelia* (SMA 11 IPA) — Paket 8 Sesi Matematika (Tagihan Rp 800.000, Jatuh Tempo H+3).
  3. *Dimas Pratama* (SMP 9) — SPP Bulanan (Tagihan Rp 450.000, bayar sebagian Rp 250.000, sisa Rp 200.000).
  4. *Nabila Putri* (SMA 10) — Kelas Kelompok Bahasa Inggris (Status izin pada sesi terakhir).
  5. *Raditya Pratama* (SD Kelas 6) — Paket 8 Sesi (Sisa 1 sesi $\rightarrow$ alert kuota hampir habis).
* **Kelas & Jadwal:**
  - Fisika Intensif UTBK (Privat, Kak Hendra, Rp 900.000/paket)
  - Matematika SMA 12 (Kelompok, Bu Ratna, Rp 500.000/bln)
  - English Conversation SMP (Kelompok, Kak Kevin, Rp 75.000/sesi)
* **Kuitansi Sah:** `#KW-202611-0042` atas nama Ibu Maya (Wali Ahmad Fauzi), nominal Rp 900.000 Lunas.
