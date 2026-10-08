plugins {
    id("fondly.android.library")
    id("fondly.compose")
    id("fondly.hilt")
}
android { namespace = "lat.virgotp.fondly.feature.settings" }
dependencies {
    implementation(project(":core:ui_common"))
    implementation("androidx.appcompat:appcompat:1.7.0") // AppCompatDelegate (idioma)
}