package edu.lespri.app.di;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import edu.lespri.app.data.local.LespriDatabase;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseModule_ProvideLespriDatabaseFactory implements Factory<LespriDatabase> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvideLespriDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public LespriDatabase get() {
    return provideLespriDatabase(contextProvider.get());
  }

  public static DatabaseModule_ProvideLespriDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new DatabaseModule_ProvideLespriDatabaseFactory(contextProvider);
  }

  public static LespriDatabase provideLespriDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideLespriDatabase(context));
  }
}
