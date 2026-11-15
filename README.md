# Lespri

> Product Requirements Document (PRD)  
> Status: Final Spec v1.1  
> Updated: 15 November 2026  
> Package Name: `edu.lespri.app`  
> Platform: Android 7.0+ (API 24 - 36) | Kotlin & Jetpack Compose  
> Legal & Privacy: `https://susantohenri.github.io/privacy-policy`  

## 1. Overview

Lespri adalah aplikasi Android untuk membantu pengelola bimbel rumahan mengelola operasional harian secara cepat dan tanpa bergantung pada spreadsheet atau WhatsApp yang tersebar. Fokus utama produk adalah administrasi siswa, kelas, jadwal, kehadiran, tagihan, pembayaran, dan pengingat, dengan pendekatan offline-first.

Lespri dirancang untuk bimbel kecil dengan 5–30 siswa, terutama yang masih dikelola secara manual melalui Excel, buku catatan, atau catatan WhatsApp. Produk ini tidak bertujuan menjadi LMS, marketplace tutor, atau sistem ERP sekolah.

## 2. Problem Statement

Pengelola bimbel kecil sering menghadapi masalah berikut:

- Data siswa, jadwal, dan pembayaran tersebar di beberapa tempat.
- Jadwal dan kehadiran tidak selalu terdokumentasi dengan jelas.
- Tagihan dan pembayaran sulit dilacak secara konsisten.
- Informasi penting tidak mudah diakses saat offline.
- Proses migrasi dari Excel/CSV belum terintegrasi dengan baik.

Lespri hadir untuk menyatukan proses operasional inti dalam satu aplikasi ringan yang dapat digunakan tanpa akun dan tanpa koneksi internet.

## 3. Product Positioning

“Administrasi bimbel rumahan yang rapi dan cepat, tanpa harus bergantung pada Excel, buku catatan, atau pesan WhatsApp yang tersebar.”

## 4. Target Users

### Primary user
- Pemilik atau pengelola bimbel rumahan
- Mengelola sekitar 5–30 siswa
- Memiliki satu atau beberapa tutor
- Mengelola kelas privat dan kelompok
- Biasanya mengoperasikan usaha dari rumah atau lokasi kecil

### User role in MVP
- Hanya ada satu role: Owner/Admin
- Tutor tidak memiliki login khusus di MVP
- Seluruh perubahan data dilakukan dari satu perangkat lokal

## 5. Goals and Success Metrics

### Product goals
- Mengurangi pekerjaan administratif berulang.
- Memudahkan pemilik melihat jadwal hari ini, kehadiran, dan tunggakan.
- Mendukung kelas privat dan kelompok.
- Memungkinkan pengelola mengelola beberapa tutor dalam satu bimbel.
- Mendukung skema pembayaran bulanan, paket sesi, dan per sesi.
- Bekerja tanpa akun dan tanpa internet.
- Menjaga pengalaman pengguna tetap nyaman meskipun monetisasi melalui AdMob.

### Initial validation metrics
- Time to first student: ≤ 3 menit
- Setup completion: ≥ 70% pengguna uji
- First-week activation: ≥ 60% pengguna uji
- Import success: ≥ 95% baris valid
- Backup restore success: 100% pada scenario QA
- Core task completion: ≥ 90% pengguna uji

## 6. Core Features (MVP)

### 6.1 Core data management
- Setup bimbel tanpa login/register
- CRUD siswa
- CRUD tutor
- CRUD kelas privat dan kelompok
- Enrollment siswa ke kelas
- Profil siswa dan histori relevan

### 6.2 Schedule and attendance
- Jadwal rutin dan sesi satu kali
- Agenda hari ini dan tampilan kalender sederhana
- Ubah, batalkan, dan reschedule sesi
- Catat kehadiran per siswa dan per kelas
- Riwayat kehadiran tersimpan

### 6.3 Finance
- Skema pembayaran: bulanan, paket sesi, dan per sesi
- Pembuatan tagihan dan riwayat pembayaran
- Pembayaran penuh dan sebagian
- Saldo paket sesi otomatis dihitung
- Dashboard tunggakan dan pemasukan bulan ini

### 6.4 Data portability
- Import CSV dari Excel/Sheets
- Export CSV data utama
- Backup penuh dalam format JSON
- Restore data lokal dengan konfirmasi

### 6.5 Communication and reminders
- Notifikasi lokal untuk jadwal, tagihan, attendance, dan paket hampir habis
- Share pesan melalui Android Sharesheet
- Pesan siap edit sebelum dikirim
- Tidak mengirim otomatis ke pihak lain

### 6.6 Localization and monetization
- Bahasa Indonesia dan English
- Offline-first dan mendukung mode pesawat
- AdMob sebagai model monetisasi MVP
- Iklan tidak boleh mengganggu alur utama

## 7. Functional Scope (P0, P1, Later)

### P0 — Must have for MVP
- Setup lokal tanpa akun
- Manajemen siswa, tutor, kelas, jadwal, attendance
- Skema pembayaran bulanan / paket / per sesi
- Dashboard operasional dan keuangan
- Notifikasi lokal
- Import CSV
- Export CSV
- Backup dan restore JSON
- Bahasa Indonesia dan English
- Integrasi AdMob yang aman untuk UX

### P1 — Important but can be deferred
- Story/share visual template
- Ringkasan laporan yang bisa dibagikan

### Later
- Cloud sync / multi-device
- Login tutor / role-based access
- Portal orang tua dan siswa
- Payment gateway otomatis
- Payroll tutor
- LMS dan konten pembelajaran
- Multi-branch / multi-owner

## 8. UX Principles

- Fokus pada tugas harian yang paling penting
- Informasi mudah dibaca dalam satu tampilan
- Navigasi singkat dan jelas
- Status kehadiran dan tagihan harus memiliki label teks serta ikon
- Tidak menampilkan iklan di jalur kerja utama
- Empty state memberi arahan tindakan yang jelas

## 9. Product Decisions and Constraints

- Platform: Android
- Model bisnis: Gratis dengan AdMob
- Data utama disimpan lokal di perangkat
- Aplikasi offline-first
- Tidak wajib registrasi
- Tidak ada login tutor di MVP
- Tidak ada cloud sync di MVP
- Tidak ada auto WhatsApp sending
- Tidak ada payment gateway otomatis
- Multi-cabang tidak termasuk MVP

## 10. Key Product Requirements

### FR-01: No login required
- Pengguna dapat mulai memakai aplikasi tanpa akun.
- Data tetap tersedia setelah force close dan saat offline.

### FR-02: Student management
- Tambah, edit, arsipkan, dan cari siswa.
- Satu siswa boleh masuk ke lebih dari satu kelas.
- Nomor telepon disimpan sebagai teks agar angka nol di depan tidak hilang.

### FR-03: Tutor management
- Satu bimbel dapat memiliki beberapa tutor.
- Tutor nonaktif tetap terlihat di histori.

### FR-04: Class management
- Mendukung kelas privat dan kelompok.
- Sistem mencegah duplikasi enrollment pada kelas yang sama.

### FR-05: Schedule and attendance
- Jadwal berulang dan sesi khusus.
- Perubahan jadwal rutin hanya berlaku untuk sesi ke depan.
- Kehadiran default adalah “Belum dicatat”.
- Attendance mengubah saldo paket atau tagihan sesuai aturan.

### FR-06: Billing and payments
- Mendukung bulanan, paket sesi, dan per sesi.
- Satu tagihan dapat memiliki banyak pembayaran.
- Saldo tagihan dihitung otomatis.
- Nominal menggunakan integer (bukan floating point).

### FR-07: Dashboard
- Menampilkan tunggakan, jadwal hari ini, attendance yang belum tercatat, paket hampir habis, dan pemasukan bulan ini.
- Semua ringkasan bisa dibuka ke list yang relevan.

### FR-08: Local notifications
- Notifikasi hanya untuk pemilik dan hanya di perangkat lokal.
- Tidak mengirim pesan otomatis ke siswa, wali, atau tutor.

### FR-09: CSV import/export
- Import dari CSV UTF-8.
- Mapping kolom dengan preview dan validasi.
- Baris valid dan invalid dipisahkan dengan jelas.

### FR-10: Backup and restore
- JSON untuk backup penuh.
- Restore memerlukan konfirmasi dan tidak boleh merusak data lokal saat gagal.

### FR-11: AdMob restrictions & Monetization Tracking
- Iklan tidak boleh mengganggu input, attendance, pembayaran, atau backup/restore.
- Tidak mengirim data personal ke ad network.
- Pendapatan iklan dilacak secara presisi ke Firebase Analytics via `OnPaidEventListener`.

### FR-12: Settings, Bilingual (i18n) & Theme
- Pilihan bahasa bilingual (Bahasa Indonesia & English) dengan auto-detect OS via `AppCompatDelegate.setApplicationLocales`.
- Mode tema tampilan reaktif: Mengikuti Sistem (Auto), Mode Terang (Light), dan Mode Gelap (Dark).
- Informasi legalitas, versi aplikasi dinamis (`BuildConfig.VERSION_NAME`), dan tautan Kebijakan Privasi.

### FR-13: Privacy, Consent & Analytics
- Integrasi Google UMP (User Messaging Platform) SDK untuk alur persetujuan GDPR (EEA/UK).
- Form opsi privasi dapat dibuka kembali kapan saja dari menu Pengaturan.
- Pelacakan navigasi layar otomatis (`SCREEN_VIEW`) dan pelaporan error non-fatal ke Firebase Crashlytics.

## 11. Risks and Mitigations

- Import data dari Excel yang beragam: gunakan template CSV, mapping kolom, preview validasi.
- Kesalahan finansial saat attendance berubah: gunakan ledger dan validasi dampak perubahan.
- AdMob mengganggu workflow inti: gunakan placement pusat dan larangan di jalur pekerjaan utama.
- Backup salah dipahami sebagai export CSV: jelaskan perbedaan fungsi dengan jelas.
- Data lokal hilang: dorong penggunaan backup rutin dan export data.

## 12. Roadmap

### Milestone 1 — Core local data
- Setup lokal tanpa akun
- CRUD siswa, tutor, kelas
- Import CSV dasar
- Param utama database lokal dan backup JSON

### Milestone 2 — Jadwal dan attendance
- Jadwal rutin dan sesi khusus
- Agenda hari ini
- Kehadiran kelas privat dan kelompok

### Milestone 3 — Keuangan
- Tagihan dan pembayaran
- Dashboard tunggakan dan pemasukan
- Logika saldo paket dan pendapatan

### Milestone 4 — Pengingat dan sharing
- Notifikasi lokal
- Share via Android Sharesheet
- Template pesan siap-edit

### Milestone 5 — Release hardening
- Export CSV
- Backup/restore final
- AdMob placement validation
- QA offline dan data integrity

## 13. Definition of Done

MVP siap untuk pilot bila:

- Aplikasi bisa dipakai tanpa login.
- Bahasa Indonesia dan English tersedia.
- Siswa, tutor, kelas, jadwal, kehadiran, tagihan, dan pembayaran bisa dikelola.
- Dashboard menampilkan tindakan yang perlu ditangani.
- Import CSV dan backup/restore berjalan dengan benar.
- Aplikasi berfungsi saat offline.
- Iklan tidak mengganggu alur utama.
- Data lokal tidak hilang saat restore gagal.
- Test scenario utama lulus.

## 14. Summary for Stakeholders

Lespri adalah solusi administrasi lokal untuk bimbel rumahan yang membutuhkan cara cepat, sederhana, dan andal untuk mengelola operasional harian tanpa bergantung pada spreadsheet atau sistem cloud. Dengan fokus pada pengguna pemilik bimbel skala kecil, produk ini berupaya mempermudah pekerjaan rutin, menjaga data tetap aman di perangkat, dan tetap dapat digunakan saat offline. MVP dirancang untuk memecahkan masalah inti secara cepat, dengan ruang ekspansi di fase berikutnya jika kebutuhan pasar berkembang.
