package lat.virgotp.fondly.infrastructure.persistence.room.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.domain.port.BalanceRepository
import lat.virgotp.fondly.infrastructure.persistence.room.dao.BalanceDao
import lat.virgotp.fondly.infrastructure.persistence.room.mapper.toDomain
import lat.virgotp.fondly.infrastructure.persistence.room.mapper.toEntity
import javax.inject.Inject

class BalanceRepositoryImpl @Inject constructor(
    private val dao: BalanceDao
) : BalanceRepository {

    override suspend fun insert(balance: Balance): Long =
        dao.insert(balance.toEntity())

    override suspend fun update(balance: Balance) {
        dao.update(balance.toEntity())
    }

    override fun getActiveBalances(): Flow<List<Balance>> =
        dao.getActiveBalances().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: Long): Balance? =
        dao.getById(id)?.toDomain()

    override suspend fun getActiveChildrenOf(parentId: Long): List<Balance> =
        dao.getActiveChildrenOf(parentId).map { it.toDomain() }

    override suspend fun deactivate(id: Long) {
        dao.deactivate(id)
    }

    override suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    override suspend fun countActiveChildren(parentId: Long): Int =
        dao.countActiveChildren(parentId)
}