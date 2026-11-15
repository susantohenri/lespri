package edu.lespri.app.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import edu.lespri.app.data.local.LespriDatabase;
import edu.lespri.app.data.local.dao.TutorDao;
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
public final class DatabaseModule_ProvideTutorDaoFactory implements Factory<TutorDao> {
  private final Provider<LespriDatabase> databaseProvider;

  public DatabaseModule_ProvideTutorDaoFactory(Provider<LespriDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public TutorDao get() {
    return provideTutorDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideTutorDaoFactory create(
      Provider<LespriDatabase> databaseProvider) {
    return new DatabaseModule_ProvideTutorDaoFactory(databaseProvider);
  }

  public static TutorDao provideTutorDao(LespriDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideTutorDao(database));
  }
}
