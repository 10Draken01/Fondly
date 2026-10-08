package lat.virgotp.fondly.infrastructure.persistence.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.math.BigDecimal
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.time.Clock
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.infrastructure.persistence.room.repository.BalanceRepositoryImpl
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BalanceRepositoryImplTest {

    private lateinit var db: FondlyDatabase
    private lateinit var repository: BalanceRepositoryImpl

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FondlyDatabase::class.java
        ).build()
        repository = BalanceRepositoryImpl(db.balanceDao())
    }

    @After
    fun teardown() {
        db.close()
    }

    private fun balance(
        name: String,
        amount: String,
        parentId: Long? = null
    ) = Balance(
        name = name,
        targetAmount = BigDecimal(amount),
        available = BigDecimal(amount),
        parentBalanceId = parentId,
        createdAt = Clock.System.now()
    )

    @Test
    fun insertarYRecuperarBalancePorId() = runBlocking {
        val id = repository.insert(balance("Gastos Semanales", "2000.00"))

        val recuperado = repository.getById(id)

        assertEquals("Gastos Semanales", recuperado?.name)
        // Round-trip BigDecimal <-> centavos exacto (compareTo, no equals).
        assertTrue(recuperado!!.targetAmount.compareTo(BigDecimal("2000.00")) == 0)
        assertTrue(recuperado.available.compareTo(BigDecimal("2000.00")) == 0)
    }

    @Test
    fun desactivarBalanceLoQuitaDeActivos() = runBlocking {
        val id = repository.insert(balance("Temporal", "500.00"))

        repository.deactivate(id)

        assertEquals(0, repository.getActiveBalances().first().size)
    }

    @Test
    fun contarHijosActivosDeUnPadre() = runBlocking {
        val padreId = repository.insert(balance("Nómina", "10000.00"))
        repository.insert(balance("Comida", "2000.00", parentId = padreId))

        assertEquals(1, repository.countActiveChildren(padreId))
    }

    /**
     * Regresión: getDeactiveChildrenOf llamaba al método de hijos ACTIVOS.
     * Padre activo + hijo desactivado -> debe encontrar al hijo y poder
     * reactivarlo.
     */
    @Test
    fun getDeactiveChildrenOfYReactivacion() = runBlocking {
        val padreId = repository.insert(balance("Padre", "1000.00"))
        val hijoId = repository.insert(balance("Hijo", "100.00", parentId = padreId))
        repository.deactivate(hijoId)

        val inactivos = repository.getDeactiveChildrenOf(padreId)
        assertEquals(1, inactivos.size)
        assertEquals(hijoId, inactivos[0].id)

        repository.activate(hijoId)
        assertTrue(repository.getById(hijoId)!!.isActive)
        assertEquals(0, repository.getDeactiveChildrenOf(padreId).size)
    }
}
