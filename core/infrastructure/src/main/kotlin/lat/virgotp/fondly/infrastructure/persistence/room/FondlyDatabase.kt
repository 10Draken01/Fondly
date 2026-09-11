package lat.virgotp.fondly.infrastructure.persistence.room

import androidx.room.Database
import androidx.room.RoomDatabase
import lat.virgotp.fondly.infrastructure.persistence.room.dao.BalanceDao
import lat.virgotp.fondly.infrastructure.persistence.room.entity.BalanceEntity

@Database(
    entities = [BalanceEntity::class],
    version = 1,
    exportSchema = true
)
abstract class FondlyDatabase : RoomDatabase() {
    abstract fun balanceDao(): BalanceDao
}