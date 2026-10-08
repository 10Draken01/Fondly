package lat.virgotp.fondly.infrastructure.persistence.room

import androidx.room.Database
import androidx.room.RoomDatabase
import lat.virgotp.fondly.infrastructure.persistence.room.dao.BalanceDao
import lat.virgotp.fondly.infrastructure.persistence.room.entity.BalanceEntity
import lat.virgotp.fondly.infrastructure.persistence.room.entity.RecurringTransactionRuleEntity
import lat.virgotp.fondly.infrastructure.persistence.room.entity.TransactionEntity

/**
 * v2: dinero en INTEGER centavos + ledger `transactions` +
 * `recurring_transaction_rules` (D3). Los DAOs de transactions/recurring
 * llegan con su funcionalidad (Sprint 2/3); las tablas ya existen.
 */
@Database(
    entities = [
        BalanceEntity::class,
        TransactionEntity::class,
        RecurringTransactionRuleEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class FondlyDatabase : RoomDatabase() {
    abstract fun balanceDao(): BalanceDao
}
