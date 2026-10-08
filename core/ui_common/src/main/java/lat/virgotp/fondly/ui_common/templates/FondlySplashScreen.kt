package lat.virgotp.fondly.ui_common.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import lat.virgotp.fondly.uicommon.R
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import lat.virgotp.fondly.ui_common.theme.FondlySpacing

@Composable
fun FondlySplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    minDurationMs: Long = 1600L
) {
    val scheme = MaterialTheme.colorScheme
    LaunchedEffect(Unit) {
        delay(minDurationMs)
        onFinished()
    }
    Box(modifier.fillMaxSize().background(scheme.background), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(96.dp).clip(CircleShape).background(scheme.primary.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Text("F", style = MaterialTheme.typography.displayMedium, color = scheme.primary)
            }
            Spacer(Modifier.height(FondlySpacing.lg))
            Text(stringResource(R.string.app_brand), style = MaterialTheme.typography.displaySmall, color = scheme.onBackground, textAlign = TextAlign.Center)
            Spacer(Modifier.height(FondlySpacing.xl))
            CircularProgressIndicator(color = scheme.primary, strokeWidth = 3.dp, modifier = Modifier.size(32.dp))
        }
    }
}