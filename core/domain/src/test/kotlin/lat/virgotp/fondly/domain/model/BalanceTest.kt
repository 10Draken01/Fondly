package lat.virgotp.fondly.domain.model

import java.math.BigDecimal
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Clock

class BalanceTest {

    @Test
    fun `crear balance valido no lanza excepcion`() {
        Balance(
            name = "Gastos Semanales",
            targetAmount = BigDecimal("2000.00"),
            available = BigDecimal("2000.00"),
            createdAt = Clock.System.now()
        )
    }

    @Test
    fun `nombre vacio lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            Balance(
                name = "",
                targetAmount = BigDecimal("2000.00"),
                available = BigDecimal("2000.00"),
                createdAt = Clock.System.now()
            )
        }
    }

    @Test
    fun `monto objetivo negativo lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            Balance(
                name = "Test",
                targetAmount = BigDecimal("-100.00"),
                available = BigDecimal.ZERO,
                createdAt = Clock.System.now()
            )
        }
    }

    @Test
    fun `money normaliza a escala 2 con HALF_EVEN`() {
        assertEquals(BigDecimal("10.00"), BigDecimal("10.005").money()) // 5 impar -> abajo (par)
        assertEquals(BigDecimal("10.02"), BigDecimal("10.015").money()) // 1 impar -> 2 (par)
        assertEquals(BigDecimal("10.01"), BigDecimal("10.014").money())
    }

    @Test
    fun `igualdad monetaria usa compareTo, no equals`() {
        val a = BigDecimal("10.0")
        val b = BigDecimal("10.00")
        assertTrue(a.compareTo(b) == 0)
        assertTrue(a != b) // equals compara escala; este test documenta el riesgo
    }
}
