package edu.lespri.app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.lespri.app.data.local.LespriDatabase
import edu.lespri.app.data.local.dao.AttendanceDao
import edu.lespri.app.data.local.dao.ClassDao
import edu.lespri.app.data.local.dao.FinanceDao
import edu.lespri.app.data.local.dao.ScheduleDao
import edu.lespri.app.data.local.dao.SettingDao
import edu.lespri.app.data.local.dao.StudentDao
import edu.lespri.app.data.local.dao.TutorDao
import javax.inject.Singleton

/**
 * Hilt Dependency Injection Module untuk penyediaan Room Database dan seluruh DAO.
 * Dipasang di [SingletonComponent] sehingga instance database dan DAO hidup selama lifecycle aplikasi.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideLespriDatabase(
        @ApplicationContext context: Context
    ): LespriDatabase {
        return Room.databaseBuilder(
            context,
            LespriDatabase::class.java,
            LespriDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun provideStudentDao(database: LespriDatabase): StudentDao {
        return database.studentDao()
    }

    @Provides
    fun provideTutorDao(database: LespriDatabase): TutorDao {
        return database.tutorDao()
    }

    @Provides
    fun provideClassDao(database: LespriDatabase): ClassDao {
        return database.classDao()
    }

    @Provides
    fun provideScheduleDao(database: LespriDatabase): ScheduleDao {
        return database.scheduleDao()
    }

    @Provides
    fun provideAttendanceDao(database: LespriDatabase): AttendanceDao {
        return database.attendanceDao()
    }

    @Provides
    fun provideFinanceDao(database: LespriDatabase): FinanceDao {
        return database.financeDao()
    }

    @Provides
    fun provideSettingDao(database: LespriDatabase): SettingDao {
        return database.settingDao()
    }
}
