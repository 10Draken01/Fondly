package lat.virgotp.fondly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import lat.virgotp.fondly.feature.balances.BalancesNavHost
import lat.virgotp.fondly.uicommon.theme.FondlyTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FondlyTheme {
                BalancesNavHost()
            }
        }
    }
}