package lat.virgotp.fondly.infrastructure.di

import android.content.Context
import androidx.room.Room
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
        ).build()

    @Provides
    fun provideBalanceDao(database: FondlyDatabase): BalanceDao =
        database.balanceDao()
}