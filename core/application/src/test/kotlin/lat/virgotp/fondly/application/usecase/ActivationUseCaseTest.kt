package lat.virgotp.fondly.application.usecase

import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import kotlin.time.Clock
import lat.virgotp.fondly.domain.model.Balance
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Regresión del bug de getDeactiveChildrenOf: reactivar un saldo padre
 * debe encontrar y reactivar también a sus hijos DESACTIVADOS.
 */
class ActivationUseCaseTest {

    private fun balance(id: Long, name: String, parentId: Long? = null, active: Boolean = true) =
        Balance(
            id = id,
            name = name,
            targetAmount = BigDecimal("1000.00"),
            available = BigDecimal("1000.00"),
            parentBalanceId = parentId,
            isActive = active,
            createdAt = Clock.System.now()
        )

    @Test
    fun `desactivar padre desactiva hijos directos`() = runTest {
        val repository = FakeBalanceRepository()
        val padreId = repository.insert(balance(0, "Padre"))
        val hijoId = repository.insert(balance(0, "Hijo", parentId = padreId))

        val result = DeactivateBalanceByIdUseCase(repository)(padreId)

        assertTrue(result.isSuccess)
        assertFalse(repository.getById(padreId)!!.isActive)
        assertFalse(repository.getById(hijoId)!!.isActive)
    }

    @Test
    fun `activar padre reactiva hijos desactivados`() = runTest {
        val repository = FakeBalanceRepository()
        val padreId = repository.insert(balance(0, "Padre"))
        val hijoId = repository.insert(balance(0, "Hijo", parentId = padreId))
        DeactivateBalanceByIdUseCase(repository)(padreId)

        val result = ActivateBalanceByIdUseCase(repository)(padreId)

        assertTrue(result.isSuccess)
        assertTrue(repository.getById(padreId)!!.isActive)
        assertTrue(repository.getById(hijoId)!!.isActive)
    }
}
