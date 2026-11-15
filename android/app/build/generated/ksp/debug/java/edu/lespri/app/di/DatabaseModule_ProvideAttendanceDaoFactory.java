package edu.lespri.app.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import edu.lespri.app.data.local.LespriDatabase;
import edu.lespri.app.data.local.dao.AttendanceDao;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DatabaseModule_ProvideAttendanceDaoFactory implements Factory<AttendanceDao> {
  private final Provider<LespriDatabase> databaseProvider;

  public DatabaseModule_ProvideAttendanceDaoFactory(Provider<LespriDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public AttendanceDao get() {
    return provideAttendanceDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideAttendanceDaoFactory create(
      Provider<LespriDatabase> databaseProvider) {
    return new DatabaseModule_ProvideAttendanceDaoFactory(databaseProvider);
  }

  public static AttendanceDao provideAttendanceDao(LespriDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAttendanceDao(database));
  }
}
