package lat.virgotp.fondly.infrastructure.persistence.room

import androidx.room.migration.AutoMigrationSpec
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.IOException
import lat.virgotp.fondly.infrastructure.persistence.room.migration.MIGRATION_1_2
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Cobertura de la migración 1 -> 2 (ADR-0003): conversión REAL -> centavos
 * (incl. residuos de punto flotante), preservación de filas, FKs, índices y
 * esquema final validado contra el JSON exportado.
 */
@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val testDbName = "migration-test.db"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        FondlyDatabase::class.java,
        emptyList<AutoMigrationSpec>(),
        FrameworkSQLiteOpenHelperFactory()
    )

    private fun createV1(db: SupportSQLiteDatabase) {
        // Esquema EXACTO de Room v1 (REAL para dinero).
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `balances` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `target_amount` REAL NOT NULL,
                `available` REAL NOT NULL,
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
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_balances_parent_balance_id` " +
                "ON `balances` (`parent_balance_id`)"
        )
        // Valor normal, valor con residuo de punto flotante, hijo (jerarquía).
        db.execSQL(
            "INSERT INTO balances VALUES (1, 'Normal', 2000.0, 1500.5, NULL, NULL, NULL, " +
                "'REGULAR','RESET','EVEN',0,1,NULL,NULL,'2026-09-05T00:00:00Z')"
        )
        db.execSQL(
            "INSERT INTO balances VALUES (2, 'Residuo', 19.999999999999996, 0.30000000000000004, NULL, NULL, NULL, " +
                "'REGULAR','RESET','EVEN',0,1,NULL,NULL,'2026-09-05T00:00:00Z')"
        )
        db.execSQL(
            "INSERT INTO balances VALUES (3, 'Hijo', 500.25, 500.25, NULL, NULL, 1, " +
                "'REGULAR','RESET','EVEN',0,1,NULL,NULL,'2026-09-05T00:00:00Z')"
        )
    }

    @Test
    @Throws(IOException::class)
    fun migrate1To2_convierteDineroACentavosYConservaFilas() {
        helper.createDatabase(testDbName, 1).apply {
            createV1(this)
            close()
        }

        val db = helper.runMigrationsAndValidate(testDbName, 2, true, MIGRATION_1_2)

        // Filas preservadas + conversión exacta a centavos.
        db.query("SELECT id, target_amount, available FROM balances ORDER BY id").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals(200_000L, c.getLong(1)); assertEquals(150_050L, c.getLong(2))
            assertTrue(c.moveToNext())
            assertEquals(2_000L, c.getLong(1))                      // 19.9999999 -> 2000c
            assertEquals(30L, c.getLong(2))                         // 0.3000..04 -> 30c
            assertTrue(c.moveToNext())
            assertEquals(50_025L, c.getLong(1)); assertEquals(50_025L, c.getLong(2))
            assertTrue(c.isLast || !c.moveToNext())
        }

        // FK de jerarquía preservada.
        db.query("SELECT parent_balance_id FROM balances WHERE id = 3").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals(1L, c.getLong(0))
        }

        // Nuevas tablas existen.
        db.query("SELECT name FROM sqlite_master WHERE type='table'").use { c ->
            val tables = mutableSetOf<String>()
            while (c.moveToNext()) tables += c.getString(0)
            assertTrue("transactions" in tables)
            assertTrue("recurring_transaction_rules" in tables)
        }

        // Índices aprobados existen.
        db.query("SELECT name FROM sqlite_master WHERE type='index'").use { c ->
            val indices = mutableSetOf<String>()
            while (c.moveToNext()) indices += c.getString(0)
            assertTrue("index_balances_parent_balance_id" in indices)
            assertTrue("index_transactions_balance_id_date" in indices)
            assertTrue("index_recurring_transaction_rules_balance_id" in indices)
            assertTrue("index_recurring_transaction_rules_is_active" in indices)
        }
    }

    @Test
    @Throws(IOException::class)
    fun migrate1To2_fksRestrictActivas() {
        helper.createDatabase(testDbName, 1).apply {
            createV1(this)
            close()
        }
        val db = helper.runMigrationsAndValidate(testDbName, 2, true, MIGRATION_1_2)

        // INSERT huérfano en transactions debe fallar por FK RESTRICT/NOT NULL.
        var fkRejected = false
        try {
            db.execSQL(
                "INSERT INTO transactions (balance_id, type, amount, quantity, name, date, created_at) " +
                    "VALUES (9999, 'EXPENSE', 100, 1, 'huérfana', '2026-01-01', '2026-01-01T00:00:00Z')"
            )
        } catch (e: Exception) {
            fkRejected = true
        }
        assertTrue("La FK transactions.balance_id debe rechazar huérfanos", fkRejected)
    }
}
