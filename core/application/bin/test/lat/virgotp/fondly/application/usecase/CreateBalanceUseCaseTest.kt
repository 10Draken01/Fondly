package lat.virgotp.fondly.application.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.time.Clock
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.domain.port.BalanceRepository
import org.junit.Test
import kotlin.test.assertTrue
import kotlin.test.assertFails

class FakeBalanceRepository(
    private val balances: MutableMap<Long, Balance> = mutableMapOf()
) : BalanceRepository {
    private var nextId = 1L

    override suspend fun insert(balance: Balance): Long {
        val id = nextId++
        balances[id] = balance.copy(id = id)
        return id
    }

    override suspend fun update(balance: Balance) {
        balances[balance.id] = balance
    }

    override fun getActiveBalances(): Flow<List<Balance>> =
        flowOf(balances.values.filter { it.isActive })

    override suspend fun getById(id: Long): Balance? = balances[id]

    override suspend fun getActiveChildrenOf(parentId: Long): List<Balance> =
        balances.values.filter { it.parentBalanceId == parentId && it.isActive }

    override suspend fun deactivate(id: Long) {
        balances[id]?.let { balances[id] = it.copy(isActive = false) }
    }

    override suspend fun deleteById(id: Long) {
        balances.remove(id)
    }

    override suspend fun countActiveChildren(parentId: Long): Int =
        getActiveChildrenOf(parentId).size
}

class CreateBalanceUseCaseTest {

    @Test
    fun `crear hijo dentro del limite del padre funciona`() = runTest {
        val repository = FakeBalanceRepository()
        val useCase = CreateBalanceUseCase(repository)

        val parentId = repository.insert(
            Balance(name = "Nómina", targetAmount = 10000.0, available = 10000.0, createdAt = Clock.System.now())
        )

        val result = useCase(name = "Comida", targetAmount = 2000.0, parentBalanceId = parentId)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `crear hijo que excede el padre falla`() = runTest {
        val repository = FakeBalanceRepository()
        val useCase = CreateBalanceUseCase(repository)

        val parentId = repository.insert(
            Balance(name = "Nómina", targetAmount = 1000.0, available = 1000.0, createdAt = Clock.System.now())
        )
        repository.insert(
            Balance(name = "Comida", targetAmount = 800.0, available = 800.0, parentBalanceId = parentId, createdAt = Clock.System.now())
        )

        val result = useCase(name = "Transporte", targetAmount = 500.0, parentBalanceId = parentId)

        assertTrue(result.isFailure)
    }
}