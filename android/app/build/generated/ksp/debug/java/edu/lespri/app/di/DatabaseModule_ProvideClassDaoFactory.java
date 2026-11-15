package edu.lespri.app.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import edu.lespri.app.data.local.LespriDatabase;
import edu.lespri.app.data.local.dao.ClassDao;
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
public final class DatabaseModule_ProvideClassDaoFactory implements Factory<ClassDao> {
  private final Provider<LespriDatabase> databaseProvider;

  public DatabaseModule_ProvideClassDaoFactory(Provider<LespriDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public ClassDao get() {
    return provideClassDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideClassDaoFactory create(
      Provider<LespriDatabase> databaseProvider) {
    return new DatabaseModule_ProvideClassDaoFactory(databaseProvider);
  }

  public static ClassDao provideClassDao(LespriDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideClassDao(database));
  }
}
