package lat.virgotp.fondly.infrastructure.persistence.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import lat.virgotp.fondly.infrastructure.persistence.room.FondlyDatabase
import lat.virgotp.fondly.infrastructure.persistence.room.entity.BalanceEntity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class BalanceDaoTest {

    private lateinit var db: FondlyDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FondlyDatabase::class.java
        ).build()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertarYConsultarSaldoActivo() = runBlocking {
        val entity = BalanceEntity(
            name = "Gastos Semanales",
            targetAmount = 2000.0,
            available = 2000.0,
            periodicity = null,
            renewalDate = null,
            parentBalanceId = null,
            type = "REGULAR",
            rolloverStrategy = "RESET",
            rebalanceStrategy = "EVEN",
            allowOverdraft = false,
            isActive = true,
            description = null,
            notificationThreshold = null,
            createdAt = "2026-09-05T00:00:00Z"
        )

        db.balanceDao().insert(entity)

        val activos = db.balanceDao().getActiveBalances().first()
        assertEquals(1, activos.size)
        assertEquals("Gastos Semanales", activos[0].name)
    }
}