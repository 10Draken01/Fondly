package lat.virgotp.fondly.ui_common.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Scaffold base Fondly:
 * - Fondo con degradado (background -> surface) y halo dorado sutil arriba.
 * - TopAppBar transparente con titulo en Playfair Display.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FondlyScreenScaffold(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (paddingValues: PaddingValues) -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = navigationIcon,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = scheme.onBackground,
                    navigationIconContentColor = scheme.onBackground
                )
            )
        },
        floatingActionButton = floatingActionButton
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(scheme.background, scheme.surface)
                    )
                )
                .drawBehind {
                    // Halo dorado premium en la parte superior
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                scheme.primary.copy(alpha = 0.10f),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2f, -size.height * 0.05f),
                            radius = size.width * 0.95f
                        )
                    )
                }
        ) {
            content(innerPadding)
        }
    }
}