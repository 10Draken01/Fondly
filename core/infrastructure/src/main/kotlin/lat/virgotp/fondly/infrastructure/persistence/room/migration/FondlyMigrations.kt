package lat.virgotp.fondly.infrastructure.persistence.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migraciones explícitas de Room (ADR-0003). Nunca se usa
 * fallbackToDestructiveMigration: los datos financieros del usuario no se
 * destruyen jamás por una actualización.
 *
 * v1 -> v2:
 * 1. Dinero REAL -> INTEGER centavos en `balances` (con ROUND(x*100) para
 *    absorber residuos de punto flotante heredados de v1).
 * 2. Nueva tabla `transactions` (ledger inmutable, FKs RESTRICT).
 * 3. Nueva tabla `recurring_transaction_rules` (D3: la crea v2 aunque su
 *    ejecución llegue en Sprint 3, porque transactions tiene FK hacia ella).
 * 4. Índices aprobados (ver docs/architecture/database.md).
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Difiere el enforcement de FKs hasta el COMMIT: imprescindible para
        // recrear `balances` (autorreferenciada) sin violaciones durante el
        // DROP/RENAME.
        db.execSQL("PRAGMA defer_foreign_keys=true")

        // --- 1. Recrear `balances` con dinero en INTEGER (centavos) ---
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `balances_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `target_amount` INTEGER NOT NULL,
                `available` INTEGER NOT NULL,
                `periodicity` TEXT,
                `renewal_date` TEXT,
                `parent_balance_id` INTEGER,
                `type` TEXT NOT NULL,
                `rollover_strategy` TEXT NOT NULL,
                `rebalance_strategy` TEXT NOT NULL,
                `allow_overdraft` INTEGER NOT NULL,
                `is_active` INTEGER NOT NULL,
                `description` TEXT,
                `notification_threshold` INTEGER,
                `created_at` TEXT NOT NULL,
                FOREIGN KEY(`parent_balance_id`) REFERENCES `balances`(`id`)
                    ON UPDATE NO ACTION ON DELETE RESTRICT
            )
            """.trimIndent()
        )
        // ROUND(v*100): 19.9999999 de v1 -> 2000 centavos, no 1999.
        db.execSQL(
            """
            INSERT INTO `balances_new`
            SELECT `id`, `name`,
                   CAST(ROUND(`target_amount` * 100) AS INTEGER),
                   CAST(ROUND(`available` * 100) AS INTEGER),
                   `periodicity`, `renewal_date`, `parent_balance_id`,
                   `type`, `rollover_strategy`, `rebalance_strategy`,
                   `allow_overdraft`, `is_active`, `description`,
                   `notification_threshold`, `created_at`
            FROM `balances`
            """.trimIndent()
        )
        db.execSQL("DROP TABLE `balances`")
        db.execSQL("ALTER TABLE `balances_new` RENAME TO `balances`")
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_balances_parent_balance_id` " +
                "ON `balances` (`parent_balance_id`)"
        )

        // --- 2. Ledger inmutable ---
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `transactions` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `balance_id` INTEGER NOT NULL,
                `type` TEXT NOT NULL,
                `amount` INTEGER NOT NULL,
                `quantity` INTEGER NOT NULL,
                `name` TEXT NOT NULL,
                `category` TEXT,
                `description` TEXT,
                `date` TEXT NOT NULL,
                `recurring_rule_id` INTEGER,
                `created_at` TEXT NOT NULL,
                FOREIGN KEY(`balance_id`) REFERENCES `balances`(`id`)
                    ON UPDATE NO ACTION ON DELETE RESTRICT,
                FOREIGN KEY(`recurring_rule_id`) REFERENCES `recurring_transaction_rules`(`id`)
                    ON UPDATE NO ACTION ON DELETE RESTRICT
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_transactions_balance_id_date` " +
                "ON `transactions` (`balance_id`, `date`)"
        )

        // --- 3. Reglas recurrentes (D3: tabla ya; funcionalidad en Sprint 3) ---
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `recurring_transaction_rules` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `balance_id` INTEGER NOT NULL,
                `type` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `amount` INTEGER NOT NULL,
                `periodicity` TEXT NOT NULL,
                `day_of_execution` INTEGER NOT NULL,
                `start_date` TEXT NOT NULL,
                `end_date` TEXT,
                `is_active` INTEGER NOT NULL,
                `last_executed_date` TEXT,
                `description` TEXT,
                FOREIGN KEY(`balance_id`) REFERENCES `balances`(`id`)
                    ON UPDATE NO ACTION ON DELETE RESTRICT
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_recurring_transaction_rules_balance_id` " +
                "ON `recurring_transaction_rules` (`balance_id`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_recurring_transaction_rules_is_active` " +
                "ON `recurring_transaction_rules` (`is_active`)"
        )
    }
}
