package lat.virgotp.fondly.infrastructure.persistence.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.time.Clock
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.infrastructure.persistence.room.FondlyDatabase
import lat.virgotp.fondly.infrastructure.persistence.room.repository.BalanceRepositoryImpl
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNull

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

    @Test
    fun insertarYRecuperarBalancePorId() = runBlocking {
        val balance = Balance(
            name = "Gastos Semanales",
            targetAmount = 2000.0,
            available = 2000.0,
            createdAt = Clock.System.now()
        )

        val id = repository.insert(balance)
        val recuperado = repository.getById(id)

        assertEquals("Gastos Semanales", recuperado?.name)
    }

    @Test
    fun desactivarBalanceLoQuitaDeActivos() = runBlocking {
        val balance = Balance(
            name = "Temporal",
            targetAmount = 500.0,
            available = 500.0,
            createdAt = Clock.System.now()
        )
        val id = repository.insert(balance)

        repository.deactivate(id)

        val activos = repository.getActiveBalances().first()
        assertEquals(0, activos.size)
    }

    @Test
    fun contarHijosActivosDeUnPadre() = runBlocking {
        val padre = Balance(
            name = "Nómina",
            targetAmount = 10000.0,
            available = 10000.0,
            createdAt = Clock.System.now()
        )
        val padreId = repository.insert(padre)

        val hijo = Balance(
            name = "Comida",
            targetAmount = 2000.0,
            available = 2000.0,
            parentBalanceId = padreId,
            createdAt = Clock.System.now()
        )
        repository.insert(hijo)

        val cantidad = repository.countActiveChildren(padreId)
        assertEquals(1, cantidad)
    }
}