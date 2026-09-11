package lat.virgotp.fondly.infrastructure.persistence.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import lat.virgotp.fondly.infrastructure.persistence.room.entity.BalanceEntity

@Dao
interface BalanceDao {

    @Insert
    suspend fun insert(balance: BalanceEntity): Long

    @Update
    suspend fun update(balance: BalanceEntity)

    @Query("SELECT * FROM balances WHERE is_active = 1 ORDER BY name ASC")
    fun getActiveBalances(): Flow<List<BalanceEntity>>

    @Query("SELECT * FROM balances WHERE id = :id")
    suspend fun getById(id: Long): BalanceEntity?

    @Query("SELECT * FROM balances WHERE parent_balance_id = :parentId AND is_active = 1")
    suspend fun getActiveChildrenOf(parentId: Long): List<BalanceEntity>

    @Query("UPDATE balances SET is_active = 0 WHERE id = :id")
    suspend fun deactivate(id: Long)

    @Query("DELETE FROM balances WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM balances WHERE parent_balance_id = :parentId AND is_active = 1")
    suspend fun countActiveChildren(parentId: Long): Int
}