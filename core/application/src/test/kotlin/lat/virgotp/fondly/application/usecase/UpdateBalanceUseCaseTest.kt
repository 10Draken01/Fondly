package lat.virgotp.fondly.application.usecase

import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import kotlin.time.Clock
import lat.virgotp.fondly.domain.exception.BalanceCycleException
import lat.virgotp.fondly.domain.model.Balance
import org.junit.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class UpdateBalanceUseCaseTest {

    private fun balance(
        id: Long,
        name: String,
        target: String,
        parentId: Long? = null
    ) = Balance(
        id = id,
        name = name,
        targetAmount = BigDecimal(target),
        available = BigDecimal(target),
        parentBalanceId = parentId,
        createdAt = Clock.System.now()
    )

    @Test
    fun `asignar como padre a un descendiente directo se rechaza (ciclo)`() = runTest {
        val repository = FakeBalanceRepository()
        val padreId = repository.insert(balance(0, "Padre", "1000.00"))
        val hijoId = repository.insert(balance(0, "Hijo", "500.00", parentId = padreId))

        val padre = repository.getById(padreId)!!
        val result = UpdateBalanceUseCase(repository)(padre.copy(parentBalanceId = hijoId))

        assertTrue(result.isFailure)
        assertIs<BalanceCycleException>(result.exceptionOrNull())
    }

    @Test
    fun `asignar como padre a un descendiente indirecto (nieto) se rechaza (ciclo profundo)`() = runTest {
        val repository = FakeBalanceRepository()
        val aId = repository.insert(balance(0, "A", "1000.00"))
        val bId = repository.insert(balance(0, "B", "500.00", parentId = aId))
        val cId = repository.insert(balance(0, "C", "200.00", parentId = bId))

        // A <- B <- C ; intentar A.parent = C crearia el ciclo A -> C -> B -> A
        val a = repository.getById(aId)!!
        val result = UpdateBalanceUseCase(repository)(a.copy(parentBalanceId = cId))

        assertTrue(result.isFailure)
        assertIs<BalanceCycleException>(result.exceptionOrNull())
    }

    @Test
    fun `asignar un padre valido (no descendiente) funciona`() = runTest {
        val repository = FakeBalanceRepository()
        val aId = repository.insert(balance(0, "A", "1000.00"))
        val bId = repository.insert(balance(0, "B", "500.00", parentId = aId))
        val otroId = repository.insert(balance(0, "Otro", "5000.00"))

        val b = repository.getById(bId)!!
        val result = UpdateBalanceUseCase(repository)(b.copy(parentBalanceId = otroId))

        assertTrue(result.isSuccess)
        kotlin.test.assertEquals(otroId, repository.getById(bId)?.parentBalanceId)
    }

    @Test
    fun `asignarse a si mismo como padre se rechaza`() = runTest {
        val repository = FakeBalanceRepository()
        val aId = repository.insert(balance(0, "A", "1000.00"))

        val a = repository.getById(aId)!!
        val result = UpdateBalanceUseCase(repository)(a.copy(parentBalanceId = aId))

        assertTrue(result.isFailure)
        assertIs<BalanceCycleException>(result.exceptionOrNull())
    }
}
