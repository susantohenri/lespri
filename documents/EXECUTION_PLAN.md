# EXECUTION PLAN: LESPRI ANDROID (MVP v1.1)

> **Document Type:** Final Architecture, Batch-Based Execution Plan & WBS  
> **Created by:** Lead Software Architect (Opus & Gemini Flash)  
> **Date:** 2026-11-15 (Updated)  
> **Source:** `documents/ARCHITECTURE_CONTRACT.md`, `documents/PRD.md`, `documents/SCREEN-MAP.md`  
> **Target Project Directory:** `android/` (Relative to workspace root)  
> **Target Root Package:** `edu.lespri.app`  
> **Status:** ✅ APPROVED FOR EXECUTION

---

## PART 1: ARSITEKTUR FINAL & VALIDASI LOGIKA

### 1.1 Workspace Directory & Project Structure Awareness

Aplikasi Android Lespri ditempatkan secara terisolasi di dalam subdirektori `android/` agar rapi dan terpisah dari dokumen/aset proyek:

```
lespri/                                      <-- Workspace Root
├── documents/                               <-- Dokumen Arsitektur, PRD, WBS
├── assets/
│   └── videos/                              <-- Output Rekaman Video Showcase
└── android/                                 <-- ANDROID PROJECT ROOT
    ├── build.gradle.kts                     <-- Root build script
    ├── settings.gradle.kts                  <-- Project & repo configuration
    ├── gradle/
    │   └── libs.versions.toml               <-- Version Catalog
    ├── app/
    │   ├── build.gradle.kts                 <-- App module build script
    │   ├── google-services.json             <-- Firebase config (moved from android/)
    │   └── src/
    │       ├── main/
    │       │   ├── AndroidManifest.xml
    │       │   ├── java/edu/lespri/app/     <-- Kotlin Source Code Root
    │       │   └── res/                     <-- Android Resources
    │       └── androidTest/                 <-- Instrumentation & Demo Automation
    └── scripts/                             <-- Script Otomasi Demo Video
```

> [!IMPORTANT]
> **ATURAN PATH KODE:**  
> Seluruh kode sumber Kotlin berada di:  
> `android/app/src/main/java/edu/lespri/app/...`  
> Jangan pernah membuat file kode sumber langsung di root workspace `lespri/`!

---

### 1.2 Layer Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                          │
│  Pattern: MVI (Model-View-Intent) + MVVM StateFlow Hybrid       │
│  ┌──────────────┐   ┌──────────────┐   ┌────────────────────┐   │
│  │  Screen.kt   │──▶│ ViewModel.kt │──▶│ UiState (data class)│  │
│  │ (Composable) │◀──│ (StateFlow)  │◀──│ + UiEvent (sealed) │  │
│  └──────────────┘   └──────┬───────┘   └────────────────────┘   │
│                            │ collect/emit                        │
├────────────────────────────┼────────────────────────────────────┤
│                     DOMAIN LAYER                                 │
│  ┌──────────────────┐  ┌───┴───────────────┐                    │
│  │ UseCase.kt       │  │ Repository (I/F)  │                    │
│  │ (Single-purpose) │──│ (Coroutines Flow) │                    │
│  └──────────────────┘  └───────────────────┘                    │
│  Domain Models: Pure Kotlin data classes, ZERO Android imports   │
├─────────────────────────────────────────────────────────────────┤
│                      DATA LAYER                                  │
│  ┌────────────────────┐  ┌──────────────────┐                   │
│  │ RepositoryImpl.kt  │  │ Room DAOs        │                   │
│  │ (Mapper: Entity ↔  │──│ (Flow<List<E>>)  │                   │
│  │  Domain Model)     │  └────────┬─────────┘                   │
│  └────────────────────┘           │                              │
│  ┌────────────────────┐  ┌────────┴─────────┐                   │
│  │ Entity.kt          │  │ LespriDatabase   │                   │
│  │ (Room @Entity)     │  │ (.db SQLite)     │                   │
│  └────────────────────┘  └──────────────────┘                   │
└─────────────────────────────────────────────────────────────────┘
```

### 1.3 Data Flow Pattern (MVI + StateFlow)

```
User Intent (click/input)
       │
       ▼
  ViewModel.onEvent(UiEvent)
       │
       ▼
  UseCase / Repository (suspend / Flow)
       │
       ▼
  Room DAO → SQLite (Single Source of Truth)
       │
       ▼
  Flow<Entity> → Mapper → Flow<DomainModel>
       │
       ▼
  ViewModel._uiState.update { ... }
       │
       ▼
  Screen.collectAsStateWithLifecycle()
       │
       ▼
  Recomposition (UI reflects new state)
```

**Aturan StateFlow:**
- Setiap ViewModel memiliki SATU `private val _uiState = MutableStateFlow(InitialState)`
- Expose sebagai `val uiState: StateFlow<XxxUiState> = _uiState.asStateFlow()`
- Side effects (snackbar, navigation) via `SharedFlow<UiEffect>` (Channel-backed, replay=0)

---

### 1.4 Validasi Skema Database: Race Condition Analysis

#### ⚠️ CRITICAL PATH 1: Package Session Balance Deduction
* **Mitigasi:** Saldo **TIDAK DISIMPAN** sebagai kolom mutable di tabel `enrollments`. Saldo paket dihitung secara dinamis via SQL:
  $$\text{RemainingSessions} = \sum (\text{delta\_sessions})$$
* Seluruh operasi kehadiran di-wrap dalam Room `@Transaction`. Mengubah status ke `PRESENT` meng-insert ledger entry `delta_sessions = -1` (`ATTENDANCE_DEDUCTION`).

#### ⚠️ CRITICAL PATH 2: Attendance Reversal (Pembatalan Kehadiran)
* **Mitigasi:** Mengubah status dari `PRESENT` ke status lain (`EXCUSED`/`SICK`/`ABSENT`/`UNRECORDED`) **TIDAK MENGHAPUS** ledger lama, melainkan meng-insert record baru: `delta_sessions = +1` (`ATTENDANCE_REVERSAL`). Audit trail terjaga utuh.

#### ⚠️ CRITICAL PATH 3: Session Cancellation Impact
* **Mitigasi:** Pembatalan sesi (`CANCELLED`) meng-cascade pembatalan kehadiran dan membalikkan sesi paket yang sempat terpotong secara atomik di dalam satu `@Transaction`.

#### ⚠️ CRITICAL PATH 4: Payment Overpayment Prevention
* **Mitigasi:** Pengecekan sisa tagihan (`charge.amount - totalPaid`) dan insert `Payment` dieksekusi atomik di dalam satu Room `@Transaction`. Pembayaran yang melebihi sisa tagihan langsung di-reject dengan `Result.failure`.

---

### 1.5 Panduan Dependensi Dasar (Version Catalog)

File: `android/gradle/libs.versions.toml`

```toml
[versions]
kotlin = "2.1.0"
agp = "8.7.3"
compileSdk = "36"
minSdk = "24"
targetSdk = "36"

# Core
coreKtx = "1.15.0"
lifecycleRuntime = "2.8.7"
activityCompose = "1.9.3"

# Compose BOM
composeBom = "2024.12.01"

# Room
room = "2.7.0"

# DI - Hilt
hilt = "2.53.1"
hiltNavigationCompose = "1.2.0"

# Navigation
navigationCompose = "2.8.5"

# Coroutines
coroutines = "1.9.0"

# Serialization (JSON backup)
kotlinxSerialization = "1.7.3"

# AdMob & Firebase
playServicesAds = "23.6.0"
firebaseBom = "33.7.0"

# Testing & UI Automator
junit = "4.13.2"
turbine = "1.2.0"
mockk = "1.13.13"
roomTesting = "2.7.0"
uiautomator = "2.3.0"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntime" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntime" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycleRuntime" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }

compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }

room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigationCompose" }

navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }

kotlinx-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "coroutines" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }

kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerialization" }

play-services-ads = { group = "com.google.android.gms", name = "play-services-ads", version.ref = "playServicesAds" }
firebase-bom = { group = "com.google.firebase", name = "firebase-bom", version.ref = "firebaseBom" }
firebase-config-ktx = { group = "com.google.firebase", name = "firebase-config-ktx" }

junit = { group = "junit", name = "junit", version.ref = "junit" }
turbine = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
room-testing = { group = "androidx.room", name = "room-testing", version.ref = "roomTesting" }
androidx-uiautomator = { group = "androidx.test.uiautomator", name = "uiautomator", version.ref = "uiautomator" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ksp = { id = "com.google.devtools.ksp", version = "2.1.0-1.0.29" }
google-services = { id = "com.google.gms.google-services", version = "4.4.2" }
```

---

## PART 2: STRATEGI EKSEKUSI & WORKFLOW PENGGUNA

### 2.1 Alur Kerja Pengguna (User Experience)

Proyek ini menggunakan **sistem Batch per Model** agar kamu **TIDAK PERLU** mengganti model setiap kali 1 file selesai. Pergantian model hanya terjadi **1-2 kali per Milestone**:

```
[Kamu] Buka Chat ──▶ Switch Model ke [Model Label Batch] ──▶ Ketik: "Jalankan Batch X.Y"
                                                                     │
                                                                     ▼
                                                           [Agent mengeksekusi semua
                                                            task dalam batch tersebut]
                                                                     │
                                                                     ▼
                                                           [Agent lapor & checkpoint]:
                                           "✅ Batch X.Y SELESAI.
                                            Langkah berikutnya: Switch model ke [Model Berikutnya],
                                            lalu ketik 'Jalankan Batch X.Z'"
```

### 2.2 Fase Sekuensial vs Fase Paralel

```
┌────────────────────────────────────────────────────────────────────────┐
│                      FASE 1: FONDASI (SEKUENSIAL)                      │
│     Milestone 1 (Database) ──▶ Milestone 2 (Domain) ──▶ Milestone 3 (Nav)│
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Fondasi selesai & terkunci
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                       FASE 2: FITUR (BISA PARALEL)                     │
│  Bisa dikerjakan bersamaan di chat terpisah / multi-window / subagents: │
│                                                                        │
│  ├──▶ TRACK A: Milestone 4 (Student, Tutor, Class CRUD)                │
│  ├──▶ TRACK B: Milestone 5 (Schedule & Attendance Engine)              │
│  ├──▶ TRACK C: Milestone 6 (Finance & Reminders)                       │
│  └──▶ TRACK D: Milestone 7 (CSV/JSON Portability & Ads)                │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Aplikasi lengkap & stabil
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│               FASE 3: SHOWCASE AUTOMATION (RECORDING)                  │
│  └──▶ Milestone 8: Automated App Demo Video Recording Pipeline         │
└────────────────────────────────────────────────────────────────────────┘
```

> **Apakah di Fase Paralel juga ada yang pakai Sonnet?**  
> **YA!** Setiap fitur paralel tetap dibagi menjadi 2 Batch:
> * **Batch .A `[MODEL: CLAUDE SONNET]`:** Untuk ViewModel, state logic, calculation, & repo logic.
> * **Batch .B `[MODEL: GEMINI FLASH HIGH]`:** Untuk UI Screens Compose, Material 3 layout, dialog, & chip.
> *(Catatan: Jika kamu ingin satu model mengerjakan 1 track fitur secara penuh tanpa ganti model, kamu bisa menggunakan Claude Sonnet untuk menjalankan Batch A & B sekaligus pada track tersebut).*

---

## PART 3: WORK BREAKDOWN STRUCTURE (WBS BATCH-BASED)

---

### 🏗️ MILESTONE 1: Foundation, Room Database & Core Models
**Root Directory:** `android/`  
**Goal:** Setup gradle, domain enums, 13 Room entities, 6 DAOs, dan Hilt DatabaseModule. Project bisa dikompilasi.

#### 🔹 BATCH 1.A `[MODEL: CLAUDE SONNET]` — Gradle & Core Enums
* [ ] **Task 1.1:** Setup Gradle Version Catalog & Scripts. Pindahkan `android/google-services.json` ke `android/app/google-services.json`.
  * *Files:* `android/gradle/libs.versions.toml`, `android/build.gradle.kts`, `android/settings.gradle.kts`, `android/app/build.gradle.kts`
* [ ] **Task 1.2:** Implement Domain Enums & Invariant Constants.
  * *File:* `android/app/src/main/java/edu/lespri/app/domain/model/DomainEnums.kt`

#### 🔹 BATCH 1.B `[MODEL: GEMINI FLASH HIGH]` — Entity Boilerplate
* [ ] **Task 1.3:** Create Room Entity: `OwnerProfileEntity.kt`
* [ ] **Task 1.4:** Create Room Entity: `StudentEntity.kt`
* [ ] **Task 1.5:** Create Room Entity: `TutorEntity.kt`
* [ ] **Task 1.6:** Create Room Entity: `ClassGroupEntity.kt`
* [ ] **Task 1.8:** Create Room Entity: `ScheduleRuleEntity.kt`
* [ ] **Task 1.11:** Create Room Entity: `ChargeEntity.kt`
* [ ] **Task 1.12:** Create Room Entity: `PaymentEntity.kt`
* [ ] **Task 1.14:** Create Room Entity: `NotificationPreferenceEntity.kt` & `AppSettingEntity.kt`
  * *Dir:* `android/app/src/main/java/edu/lespri/app/data/local/entity/`

#### 🔹 BATCH 1.C `[MODEL: CLAUDE SONNET]` — Critical Entities, DAOs & Database
* [ ] **Task 1.7:** Create Room Entity: `EnrollmentEntity.kt` (Unique index: student_id + class_id + status)
* [ ] **Task 1.9:** Create Room Entity: `LessonSessionEntity.kt` (Unique slot: class_id + date + start_time)
* [ ] **Task 1.10:** Create Room Entity: `AttendanceRecordEntity.kt` (Unique: session_id + enrollment_id)
* [ ] **Task 1.13:** Create Room Entity: `PackageLedgerEntity.kt` (Audit-trail append-only ledger)
* [ ] **Task 1.15:** Create DAO: `StudentDao.kt`
* [ ] **Task 1.16:** Create DAO: `TutorDao.kt`
* [ ] **Task 1.17:** Create DAO: `ClassDao.kt`
* [ ] **Task 1.18:** Create DAO: `ScheduleDao.kt`
* [ ] **Task 1.19:** Create DAO: `AttendanceDao.kt`
* [ ] **Task 1.20:** Create DAO: `FinanceDao.kt`
* [ ] **Task 1.21:** Implement Room Database configuration & builder (`LespriDatabase.kt`)
  * *Dir:* `android/app/src/main/java/edu/lespri/app/data/local/`

#### 🔹 BATCH 1.D `[MODEL: GEMINI FLASH HIGH]` — DI Module
* [ ] **Task 1.22:** Setup Hilt DI Module for Database & all DAOs
  * *File:* `android/app/src/main/java/edu/lespri/app/di/DatabaseModule.kt`

**Quality Gate M1:** `./gradlew assembleDebug` di folder `android/` sukses tanpa error kompilasi.

---

### 🏗️ MILESTONE 2: Domain Layer, Repositories & Use Cases
**Goal:** Pure domain models, Repository implementations dengan transaksi anti race-condition, dan use cases.

#### 🔹 BATCH 2.A `[MODEL: CLAUDE SONNET]` — Domain Models & Repository Contracts
* [ ] **Task 2.1:** Pure Domain Models: `Student.kt`, `Tutor.kt`, `ClassGroup.kt`
* [ ] **Task 2.2:** Pure Domain Models: `Enrollment.kt`, `LessonSession.kt`
* [ ] **Task 2.3:** Pure Domain Models: `AttendanceRecord.kt`, `Charge.kt`, `Payment.kt`, `FinancialSummary.kt`
* [ ] **Task 2.4:** UI List Presentation Models: `UiListModels.kt`
* [ ] **Task 2.5:** Repository Interfaces: All 6 interfaces in `domain/repository/*.kt`
  * *Dir:* `android/app/src/main/java/edu/lespri/app/domain/`

#### 🔹 BATCH 2.B `[MODEL: GEMINI FLASH HIGH]` — Mappers & Basic Repositories
* [ ] **Task 2.6:** Implement Mappers: `StudentMapper.kt`, `TutorMapper.kt`
* [ ] **Task 2.7:** Implement Mappers: `ClassMapper.kt`, `ScheduleMapper.kt`, `AttendanceMapper.kt`, `FinanceMapper.kt`
* [ ] **Task 2.8:** Implement `StudentRepositoryImpl.kt`
* [ ] **Task 2.9:** Implement `TutorRepositoryImpl.kt`
  * *Dir:* `android/app/src/main/java/edu/lespri/app/data/`

#### 🔹 BATCH 2.C `[MODEL: CLAUDE SONNET]` — Core Engine Repositories & Use Cases
* [ ] **Task 2.10:** Implement `ClassRepositoryImpl.kt` (Enrollment invariants & soft-delete cascade)
* [ ] **Task 2.11:** Implement `ScheduleRepositoryImpl.kt` (Recurring session generator engine)
* [ ] **Task 2.12:** Implement `AttendanceRepositoryImpl.kt` (Atomic `@Transaction` session deduction & reversal)
* [ ] **Task 2.13:** Implement `FinanceRepositoryImpl.kt` (Balance math & overpayment guard)
* [ ] **Task 2.14:** Implement Use Case: `RecordAttendanceUseCase.kt`
* [ ] **Task 2.15:** Implement Use Case: `CalculateStudentDebtUseCase.kt`
* [ ] **Task 2.16:** Implement Use Case: `GenerateMonthlyChargesUseCase.kt`
  * *Dir:* `android/app/src/main/java/edu/lespri/app/domain/usecase/` & `data/repository/`

#### 🔹 BATCH 2.D `[MODEL: GEMINI FLASH HIGH]` — Repository DI Modules
* [ ] **Task 2.17:** Setup Hilt DI Modules: `RepositoryModule.kt` & `UseCaseModule.kt`
  * *File:* `android/app/src/main/java/edu/lespri/app/di/RepositoryModule.kt`

**Quality Gate M2:** Unit test untuk `RecordAttendanceUseCase` dan `FinanceRepositoryImpl` berjalan lancar.

---

### 🏗️ MILESTONE 3: Presentation Core, Navigation & Dashboard
**Goal:** Setup tema M3, Navigation graph, Scaffold adaptif, dan layar Dashboard (P02).

#### 🔹 BATCH 3.A `[MODEL: CLAUDE SONNET]` — Formatters & Dashboard State Engine
* [ ] **Task 3.7:** Implement Utilities: `CurrencyFormatter.kt` (Rupiah formatting) & `DateTimeUtil.kt` (ISO dates)
* [ ] **Task 3.8:** Implement `DashboardViewModel.kt` (Aggregates debt, sessions, alerts)
  * *Dir:* `android/app/src/main/java/edu/lespri/app/`

#### 🔹 BATCH 3.B `[MODEL: GEMINI FLASH HIGH]` — Theme, Navigation & Screens
* [ ] **Task 3.1:** Setup Theme, Color Palette & Typography (`Theme.kt`, `Color.kt`, `Type.kt`)
* [ ] **Task 3.2:** Define Navigation Routes & Host (`NavRoutes.kt`, `LespriNavHost.kt`)
* [ ] **Task 3.3:** Implement `LespriScaffold.kt` (Adaptive NavigationBar / NavigationRail)
* [ ] **Task 3.4:** Reusable UI: `CompactRow.kt`
* [ ] **Task 3.5:** Reusable UI: `StatusChip.kt`
* [ ] **Task 3.6:** Reusable UI: `EmptyStateView.kt` & `LoadingOverlay.kt`
* [ ] **Task 3.9:** Implement `DashboardScreen.kt` (P02)
* [ ] **Task 3.10:** Implement `FirstLaunchSetupScreen.kt` (P01)
* [ ] **Task 3.11:** App Entry Points: `LespriApplication.kt` & `MainActivity.kt`
  * *Dir:* `android/app/src/main/java/edu/lespri/app/ui/`

**Quality Gate M3:** Aplikasi dapat dijalankan di emulator/device, menampilkan Setup (P01) atau Dashboard (P02).

---

### 🏗️ MILESTONE 4: Students, Tutors & Classes Flow (Bisa Paralel)
**Goal:** Manajemen data master bimbel (Siswa, Tutor, Kelas, Enrollment).

#### 🔹 BATCH 4.A `[MODEL: CLAUDE SONNET]` — ViewModels & Enrollment Engine
* [ ] **Task 4.1:** Implement `StudentListViewModel.kt`
* [ ] **Task 4.9:** Implement `ClassDetailViewModel.kt` (Enrollment logic & session preview)

#### 🔹 BATCH 4.B `[MODEL: GEMINI FLASH HIGH]` — Compose Screens & Forms
* [ ] **Task 4.2:** Implement `StudentListScreen.kt` (P03)
* [ ] **Task 4.3:** Implement `StudentFormScreen.kt` (P04)
* [ ] **Task 4.4:** Implement `StudentDetailScreen.kt` (P05)
* [ ] **Task 4.5:** Implement `TutorListViewModel.kt` & `TutorListScreen.kt` (P09)
* [ ] **Task 4.6:** Implement `TutorFormScreen.kt` (P10)
* [ ] **Task 4.7:** Implement `ClassListViewModel.kt` & `ClassListScreen.kt` (P06)
* [ ] **Task 4.8:** Implement `ClassFormScreen.kt` (P07)
* [ ] **Task 4.10:** Implement `ClassDetailScreen.kt` (P08)

---

### 🏗️ MILESTONE 5: Schedule & Attendance (Bisa Paralel)
**Goal:** Agenda sesi dan presensi dengan kalkulasi saldo paket.

#### 🔹 BATCH 5.A `[MODEL: CLAUDE SONNET]` — Schedule & Attendance ViewModels
* [ ] **Task 5.1:** Implement `ScheduleAgendaViewModel.kt` (Date navigation & auto-generation trigger)
* [ ] **Task 5.5:** Implement `AttendanceSheetViewModel.kt` (Batch status toggling & deduction preview)

#### 🔹 BATCH 5.B `[MODEL: GEMINI FLASH HIGH]` — Schedule & Attendance Screens
* [ ] **Task 5.2:** Implement `ScheduleAgendaScreen.kt` (P12)
* [ ] **Task 5.3:** Implement `SessionFormScreen.kt` (P13)
* [ ] **Task 5.4:** Implement `SessionDetailScreen.kt` (P14)
* [ ] **Task 5.6:** Implement `AttendanceSheetScreen.kt` (P15)

---

### 🏗️ MILESTONE 6: Financial Operations & Reminders (Bisa Paralel)
**Goal:** Tagihan, pembayaran, cetak kuitansi, pengingat, dan WhatsApp share.

#### 🔹 BATCH 6.A `[MODEL: CLAUDE SONNET]` — Finance Logic, Reminders & System Intents
* [ ] **Task 6.1:** Implement `FinanceViewModel.kt` (Debt aggregation, filtering)
* [ ] **Task 6.6:** Implement `SharesheetHelper.kt` (WhatsApp & generic Android Sharesheet)
* [ ] **Task 6.7:** Implement `ReminderScheduler.kt` (AlarmManager & Notification channels)
* [ ] **Task 6.8:** Implement `BootReceiver.kt` (Re-register alarms on boot)

#### 🔹 BATCH 6.B `[MODEL: GEMINI FLASH HIGH]` — Finance Screens & Receipt
* [ ] **Task 6.2:** Implement `FinanceOverviewScreen.kt` (P16)
* [ ] **Task 6.3:** Implement `ChargeDetailScreen.kt` (P17)
* [ ] **Task 6.4:** Implement `RecordPaymentScreen.kt` (P18)
* [ ] **Task 6.5:** Implement `PaymentReceiptScreen.kt` (P19)

---

### 🏗️ MILESTONE 7: Data Portability & Monetization (Bisa Paralel)
**Goal:** Impor/ekspor CSV, backup/restore JSON, banner iklan AdMob.

#### 🔹 BATCH 7.A `[MODEL: CLAUDE SONNET]` — Parsers, Backup & Ad Engine
* [ ] **Task 7.1:** Implement RFC 4180 Streaming CSV Parser & Generator (`CsvEngine.kt`)
* [ ] **Task 7.2:** Implement `CsvImportViewModel.kt` (Mapping engine & header matching)
* [ ] **Task 7.5:** Implement `BackupManager.kt` (Full JSON serializer + GZIP)
* [ ] **Task 7.6:** Implement `BackupRepositoryImpl.kt`
* [ ] **Task 7.8:** Implement `AdManager.kt` (AdMob init + Remote Config integration)

#### 🔹 BATCH 7.B `[MODEL: GEMINI FLASH HIGH]` — Portability Screens & Settings
* [ ] **Task 7.3:** Implement `CsvImportScreen.kt` (P21)
* [ ] **Task 7.4:** Implement `CsvExportScreen.kt` (P22)
* [ ] **Task 7.7:** Implement `BackupRestoreScreen.kt` (P23)
* [ ] **Task 7.9:** Implement `BannerAdView.kt` (Adaptive banner with 16dp safety margin)
* [ ] **Task 7.10:** Implement `SettingsScreen.kt` (P24)

---

### 🎬 MILESTONE 8: Automated App Demo Video Recording Pipeline (New)
**Goal:** Membuat **2 video demo aplikasi otomatis** tanpa perlu klik manual atau rekam layar manual. Cukup jalankan satu script otomasi (`adb` + UI Automator), aplikasi berjalan sendiri, dan langsung terekam ke file MP4 resolusi tinggi.

#### Spesifikasi 2 Video Demo:
1. **Video 1 — Operasional Harian Bimbel (Durasi: ~30-45 detik):**
   * *Alur Otomasi:* Dashboard $\rightarrow$ Klik sesi hari ini $\rightarrow$ Masuk Lembar Presensi (P15) $\rightarrow$ Centang siswa HADIR $\rightarrow$ Tampilkan preview saldo paket berkurang $\rightarrow$ Simpan $\rightarrow$ Buka Detail Siswa (P05) $\rightarrow$ Tampilkan saldo paket ter-update $\rightarrow$ Catat Pembayaran Tagihan (P18) $\rightarrow$ Tampilkan Kuitansi (P19) $\rightarrow$ Selesai.
2. **Video 2 — Setup Cepat & Portabilitas Data (Durasi: ~30 detik):**
   * *Alur Otomasi:* Setup Bimbel baru (P01) $\rightarrow$ Impor CSV Siswa (P21) $\rightarrow$ Auto-mapping kolom $\rightarrow$ Preview data valid $\rightarrow$ Konfirmasi $\rightarrow$ Seluruh daftar siswa & kelas langsung terisi secara instan $\rightarrow$ Export Backup JSON (P23) $\rightarrow$ Selesai.

#### 🔹 BATCH 8.A `[MODEL: CLAUDE SONNET]` — Automation Scripts & Master Recorder
* [ ] **Task 8.1:** Implement Demo Data Seeder (`android/app/src/androidTest/java/edu/lespri/app/demo/DemoDataSeeder.kt`)
  * Menyediakan seed database realistis (nama bimbel, 5 siswa contoh, 2 tutor, jadwal aktif).
* [ ] **Task 8.2:** Implement UI Automator Script for Video 1 (`android/app/src/androidTest/java/edu/lespri/app/demo/DailyOpsDemoTest.kt`)
  * Menjalankan navigasi otomatis dengan jeda timing yang enak ditonton manusia.
* [ ] **Task 8.3:** Implement UI Automator Script for Video 2 (`android/app/src/androidTest/java/edu/lespri/app/demo/DataPortabilityDemoTest.kt`)
  * Menjalankan otomasi onboarding dan import CSV.
* [ ] **Task 8.4:** Implement Master Recording Script (`android/scripts/record_showcase_videos.ps1`)
  * Menyalakan `adb screenrecord`, mentrigger test runner, menarik file MP4 ke `assets/videos/video_1_daily_ops.mp4` dan `assets/videos/video_2_setup_import.mp4`.

---

## PART 4: RINGKASAN DISTRIBUSI BATCH & MODEL

| Milestone | Batch & Model | Deskripsi Cakupan |
|-----------|---------------|-------------------|
| **M1: Database** | Batch 1.A `[SONNET]`<br>Batch 1.B `[FLASH]`<br>Batch 1.C `[SONNET]`<br>Batch 1.D `[FLASH]` | Gradle & Enums<br>Entity Boilerplate<br>Complex Entities, DAOs & Room DB<br>Hilt DatabaseModule |
| **M2: Domain & Repos** | Batch 2.A `[SONNET]`<br>Batch 2.B `[FLASH]`<br>Batch 2.C `[SONNET]`<br>Batch 2.D `[FLASH]` | Domain Models & Contracts<br>Entity Mappers & Basic Repos<br>Core Repositories & Use Cases<br>DI RepositoryModule |
| **M3: Navigation & Core UI** | Batch 3.A `[SONNET]`<br>Batch 3.B `[FLASH]` | Currency/Date Utils & Dashboard ViewModel<br>Theme, Scaffold, NavHost, Dashboard & Setup Screen |
| **M4: Student, Tutor, Class** *(Paralel)* | Batch 4.A `[SONNET]`<br>Batch 4.B `[FLASH]` | ViewModels & Enrollment Engine<br>Screens (List, Form, Detail) |
| **M5: Schedule & Attendance** *(Paralel)* | Batch 5.A `[SONNET]`<br>Batch 5.B `[FLASH]` | Agenda & Attendance ViewModels<br>Agenda, Session & Attendance Screens |
| **M6: Finance & Reminders** *(Paralel)* | Batch 6.A `[SONNET]`<br>Batch 6.B `[FLASH]` | Finance ViewModel, Sharesheet & ReminderScheduler<br>Overview, Charge, Payment & Receipt Screens |
| **M7: CSV, Backup & Ads** *(Paralel)* | Batch 7.A `[SONNET]`<br>Batch 7.B `[FLASH]` | CSV Engine, BackupManager & AdManager<br>Import/Export, Backup, Banner & Settings Screens |
| **M8: Video Automation** | Batch 8.A `[SONNET]` | Data Seeder, UI Automator Scripts & PowerShell Master Recorder |

---

*Dokumen diperbarui pada 15 November 2026. Siap digunakan sebagai panduan eksekusi.*
