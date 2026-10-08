package lat.virgotp.fondly.application.usecase

import java.math.BigDecimal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.time.Clock
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.domain.port.BalanceRepository
import org.junit.Test
import kotlin.test.assertTrue

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

    override suspend fun getDeactiveChildrenOf(parentId: Long): List<Balance> =
        balances.values.filter { it.parentBalanceId == parentId && !it.isActive }

    override suspend fun getActiveChildrenOf(parentId: Long): List<Balance> =
        balances.values.filter { it.parentBalanceId == parentId && it.isActive }

    override suspend fun deactivate(id: Long) {
        balances[id]?.let { balances[id] = it.copy(isActive = false) }
    }

    override suspend fun activate(id: Long) {
        balances[id]?.let { balances[id] = it.copy(isActive = true) }
    }

    override suspend fun deleteById(id: Long) {
        balances.remove(id)
    }

    override suspend fun countActiveChildren(parentId: Long): Int =
        getActiveChildrenOf(parentId).size

    override fun getAllBalances(): Flow<List<Balance>> = flowOf(balances.values.toList())

    override suspend fun getChildrenOf(parentId: Long): List<Balance> =
        balances.values.filter { it.parentBalanceId == parentId }
}

class CreateBalanceUseCaseTest {

    @Test
    fun `crear hijo dentro del limite del padre funciona`() = runTest {
        val repository = FakeBalanceRepository()
        val useCase = CreateBalanceUseCase(repository)

        val parentId = repository.insert(
            Balance(
                name = "Nómina",
                targetAmount = BigDecimal("10000.00"),
                available = BigDecimal("10000.00"),
                createdAt = Clock.System.now()
            )
        )

        val result = useCase(
            name = "Comida",
            targetAmount = BigDecimal("2000.00"),
            parentBalanceId = parentId,
            description = null,
            type = lat.virgotp.fondly.domain.model.BalanceType.REGULAR,
            periodicity = lat.virgotp.fondly.domain.model.Periodicity.NONE,
            renewalDate = null,
            rolloverStrategy = lat.virgotp.fondly.domain.model.RolloverStrategy.RESET,
            rebalanceStrategy = lat.virgotp.fondly.domain.model.RebalanceStrategy.EVEN,
            allowOverdraft = false,
            notificationThreshold = null
        )

        assertTrue(result.isSuccess)
    }

    @Test
    fun `crear hijo que excede el padre falla`() = runTest {
        val repository = FakeBalanceRepository()
        val useCase = CreateBalanceUseCase(repository)

        val parentId = repository.insert(
            Balance(
                name = "Nómina",
                targetAmount = BigDecimal("1000.00"),
                available = BigDecimal("1000.00"),
                createdAt = Clock.System.now()
            )
        )
        repository.insert(
            Balance(
                name = "Comida",
                targetAmount = BigDecimal("800.00"),
                available = BigDecimal("800.00"),
                parentBalanceId = parentId,
                createdAt = Clock.System.now()
            )
        )

        val result = useCase(
            name = "Transporte",
            targetAmount = BigDecimal("500.00"),
            parentBalanceId = parentId,
            description = null,
            type = lat.virgotp.fondly.domain.model.BalanceType.REGULAR,
            periodicity = lat.virgotp.fondly.domain.model.Periodicity.NONE,
            renewalDate = null,
            rolloverStrategy = lat.virgotp.fondly.domain.model.RolloverStrategy.RESET,
            rebalanceStrategy = lat.virgotp.fondly.domain.model.RebalanceStrategy.EVEN,
            allowOverdraft = false,
            notificationThreshold = null
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `montos con mas de 2 decimales se normalizan a escala 2 HALF_EVEN`() = runTest {
        val repository = FakeBalanceRepository()
        val useCase = CreateBalanceUseCase(repository)

        // HALF_EVEN: 10.005 -> 10.00 ; 10.015 -> 10.02
        val parentId = repository.insert(
            Balance(
                name = "Padre",
                targetAmount = BigDecimal("10.005"),
                available = BigDecimal("10.005"),
                createdAt = Clock.System.now()
            ).normalized()
        )

        val result = useCase(
            name = "Hijo",
            targetAmount = BigDecimal("10.015"),
            parentBalanceId = parentId,
            description = null,
            type = lat.virgotp.fondly.domain.model.BalanceType.REGULAR,
            periodicity = lat.virgotp.fondly.domain.model.Periodicity.NONE,
            renewalDate = null,
            rolloverStrategy = lat.virgotp.fondly.domain.model.RolloverStrategy.RESET,
            rebalanceStrategy = lat.virgotp.fondly.domain.model.RebalanceStrategy.EVEN,
            allowOverdraft = false,
            notificationThreshold = null
        )

        // padre normalizado: 10.00 ; hijo propuesto: 10.02 > 10.00 -> falla
        assertTrue(result.isFailure)
    }
}
