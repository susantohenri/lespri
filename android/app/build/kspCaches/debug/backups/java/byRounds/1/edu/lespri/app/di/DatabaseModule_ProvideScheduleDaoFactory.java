package edu.lespri.app.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import edu.lespri.app.data.local.LespriDatabase;
import edu.lespri.app.data.local.dao.ScheduleDao;
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
public final class DatabaseModule_ProvideScheduleDaoFactory implements Factory<ScheduleDao> {
  private final Provider<LespriDatabase> databaseProvider;

  public DatabaseModule_ProvideScheduleDaoFactory(Provider<LespriDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public ScheduleDao get() {
    return provideScheduleDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideScheduleDaoFactory create(
      Provider<LespriDatabase> databaseProvider) {
    return new DatabaseModule_ProvideScheduleDaoFactory(databaseProvider);
  }

  public static ScheduleDao provideScheduleDao(LespriDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideScheduleDao(database));
  }
}
