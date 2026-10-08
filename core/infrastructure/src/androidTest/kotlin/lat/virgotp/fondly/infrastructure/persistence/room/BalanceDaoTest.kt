package lat.virgotp.fondly.infrastructure.persistence.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import lat.virgotp.fondly.infrastructure.persistence.room.entity.BalanceEntity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

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

    private fun entity(
        name: String,
        cents: Long,
        parentId: Long? = null,
        active: Boolean = true
    ) = BalanceEntity(
        name = name,
        targetAmount = cents,
        available = cents,
        periodicity = null,
        renewalDate = null,
        parentBalanceId = parentId,
        type = "REGULAR",
        rolloverStrategy = "RESET",
        rebalanceStrategy = "EVEN",
        allowOverdraft = false,
        isActive = active,
        description = null,
        notificationThreshold = null,
        createdAt = "2026-09-05T00:00:00Z"
    )

    @Test
    fun insertarYConsultarSaldoActivo() = runBlocking {
        db.balanceDao().insert(entity(name = "Gastos Semanales", cents = 200_000)) // $2,000.00

        val activos = db.balanceDao().getActiveBalances().first()
        assertEquals(1, activos.size)
        assertEquals("Gastos Semanales", activos[0].name)
        assertEquals(200_000L, activos[0].targetAmount)
    }

    @Test
    fun getDeactiveChildrenOfSoloDevuelveHijosInactivos() = runBlocking {
        val padreId = db.balanceDao().insert(entity(name = "Padre", cents = 100_000))
        db.balanceDao().insert(entity(name = "HijoInactivo", cents = 10_000, parentId = padreId, active = false))
        db.balanceDao().insert(entity(name = "HijoActivo", cents = 20_000, parentId = padreId, active = true))

        val inactivos = db.balanceDao().getDeactiveChildrenOf(padreId)

        assertEquals(1, inactivos.size)
        assertEquals("HijoInactivo", inactivos[0].name)
    }
}
