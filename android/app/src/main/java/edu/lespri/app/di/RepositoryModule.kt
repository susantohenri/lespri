package edu.lespri.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.lespri.app.data.repository.AttendanceRepositoryImpl
import edu.lespri.app.data.repository.BackupRepositoryImpl
import edu.lespri.app.data.repository.ClassRepositoryImpl
import edu.lespri.app.data.repository.FinanceRepositoryImpl
import edu.lespri.app.data.repository.ScheduleRepositoryImpl
import edu.lespri.app.data.repository.SettingRepositoryImpl
import edu.lespri.app.data.repository.StudentRepositoryImpl
import edu.lespri.app.data.repository.TutorRepositoryImpl
import edu.lespri.app.domain.repository.AttendanceRepository
import edu.lespri.app.domain.repository.BackupRepository
import edu.lespri.app.domain.repository.ClassRepository
import edu.lespri.app.domain.repository.FinanceRepository
import edu.lespri.app.domain.repository.ScheduleRepository
import edu.lespri.app.domain.repository.SettingRepository
import edu.lespri.app.domain.repository.StudentRepository
import edu.lespri.app.domain.repository.TutorRepository
import javax.inject.Singleton

/**
 * Hilt Dependency Injection Module untuk binding seluruh interface Repository ke implementasinya.
 * Dipasang di [SingletonComponent] sehingga seluruh aplikasi menggunakan instance repositori yang konsisten.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStudentRepository(
        impl: StudentRepositoryImpl
    ): StudentRepository

    @Binds
    @Singleton
    abstract fun bindTutorRepository(
        impl: TutorRepositoryImpl
    ): TutorRepository

    @Binds
    @Singleton
    abstract fun bindClassRepository(
        impl: ClassRepositoryImpl
    ): ClassRepository

    @Binds
    @Singleton
    abstract fun bindScheduleRepository(
        impl: ScheduleRepositoryImpl
    ): ScheduleRepository

    @Binds
    @Singleton
    abstract fun bindAttendanceRepository(
        impl: AttendanceRepositoryImpl
    ): AttendanceRepository

    @Binds
    @Singleton
    abstract fun bindFinanceRepository(
        impl: FinanceRepositoryImpl
    ): FinanceRepository

    @Binds
    @Singleton
    abstract fun bindBackupRepository(
        impl: BackupRepositoryImpl
    ): BackupRepository

    @Binds
    @Singleton
    abstract fun bindSettingRepository(
        impl: SettingRepositoryImpl
    ): SettingRepository
}
