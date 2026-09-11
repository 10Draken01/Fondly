package lat.virgotp.fondly.domain.model

import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.time.Clock

class BalanceTest {

    @Test
    fun `crear balance valido no lanza excepcion`() {
        Balance(
            name = "Gastos Semanales",
            targetAmount = 2000.0,
            available = 2000.0,
            createdAt = Clock.System.now()
        )
    }

    @Test
    fun `nombre vacio lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            Balance(
                name = "",
                targetAmount = 2000.0,
                available = 2000.0,
                createdAt = Clock.System.now()
            )
        }
    }

    @Test
    fun `monto objetivo negativo lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            Balance(
                name = "Test",
                targetAmount = -100.0,
                available = 0.0,
                createdAt = Clock.System.now()
            )
        }
    }
}