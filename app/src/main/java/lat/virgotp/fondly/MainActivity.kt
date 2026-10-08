package lat.virgotp.fondly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import lat.virgotp.fondly.home.FondlyApp
import lat.virgotp.fondly.ui_common.theme.FondlyCurrencyController
import lat.virgotp.fondly.ui_common.theme.FondlyTheme
import lat.virgotp.fondly.ui_common.theme.FondlyThemeController

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FondlyThemeController.init(applicationContext)
        FondlyCurrencyController.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            FondlyTheme(darkTheme = FondlyThemeController.isDarkTheme()) {
                FondlyApp()
            }
        }
    }
}