package lat.virgotp.fondly.infrastructure.di

import android.content.Context
import androidx.room.Room
import lat.virgotp.fondly.infrastructure.persistence.room.migration.MIGRATION_1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import lat.virgotp.fondly.infrastructure.persistence.room.FondlyDatabase
import lat.virgotp.fondly.infrastructure.persistence.room.dao.BalanceDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFondlyDatabase(@ApplicationContext context: Context): FondlyDatabase =
        Room.databaseBuilder(
            context,
            FondlyDatabase::class.java,
            "fondly.db"
        )
            // Migraciones explícitas únicamente — jamás fallbackToDestructiveMigration (ADR-0003).
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideBalanceDao(database: FondlyDatabase): BalanceDao =
        database.balanceDao()
}