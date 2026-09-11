package lat.virgotp.fondly.infrastructure.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import lat.virgotp.fondly.domain.port.BalanceRepository
import lat.virgotp.fondly.infrastructure.persistence.room.repository.BalanceRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBalanceRepository(
        impl: BalanceRepositoryImpl
    ): BalanceRepository
}