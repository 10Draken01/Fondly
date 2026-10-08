package lat.virgotp.fondly.ui_common.navigation

/**
 * Fuente única de rutas de navegación de Fondly.
 *
 * Regla permanente: NINGÚN feature construye strings de ruta; recibe
 * callbacks (ej. onBalanceClick(id)) y la navegación se arma aquí.
 * El grafo/navhosts viven en feature:home (shell de navegación).
 */
object FondlyRoutes {
    // Top-level (NavHost raíz)
    const val SPLASH = "splash"
    const val MAIN = "main"

    // Secciones de la bottom navigation
    const val BALANCES = "balances"
    const val BALANCE_SECTIONS = "balance_sections"
    const val SETTINGS = "settings"

    // Destinos con argumentos
    const val DETAIL_PATTERN = "detail/{balanceId}"
    const val EDIT_PATTERN = "edit/{balanceId}"
    const val CREATE_PATTERN = "create?parentId={parentId}"

    const val ARG_BALANCE_ID = "balanceId"
    const val ARG_PARENT_ID = "parentId"
    const val NO_PARENT = -1L

    fun detail(balanceId: Long) = "detail/$balanceId"
    fun edit(balanceId: Long) = "edit/$balanceId"
    fun create(parentId: Long? = null) =
        if (parentId == null) "create" else "create?parentId=$parentId"
}
