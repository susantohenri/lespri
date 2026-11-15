package edu.lespri.app.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import edu.lespri.app.data.local.LespriDatabase;
import edu.lespri.app.data.local.dao.FinanceDao;
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
public final class DatabaseModule_ProvideFinanceDaoFactory implements Factory<FinanceDao> {
  private final Provider<LespriDatabase> databaseProvider;

  public DatabaseModule_ProvideFinanceDaoFactory(Provider<LespriDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public FinanceDao get() {
    return provideFinanceDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideFinanceDaoFactory create(
      Provider<LespriDatabase> databaseProvider) {
    return new DatabaseModule_ProvideFinanceDaoFactory(databaseProvider);
  }

  public static FinanceDao provideFinanceDao(LespriDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFinanceDao(database));
  }
}
