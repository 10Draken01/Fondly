package lat.virgotp.fondly.domain.port

import kotlinx.coroutines.flow.Flow
import lat.virgotp.fondly.domain.model.Balance

interface BalanceRepository {
    suspend fun insert(balance: Balance): Long
    suspend fun update(balance: Balance)
    fun getActiveBalances(): Flow<List<Balance>>
    suspend fun getById(id: Long): Balance?
    suspend fun getActiveChildrenOf(parentId: Long): List<Balance>
    suspend fun deactivate(id: Long)
    suspend fun deleteById(id: Long)
    suspend fun countActiveChildren(parentId: Long): Int
}