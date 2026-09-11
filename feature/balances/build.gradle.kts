plugins {
    id("fondly.android.library")
    id("fondly.compose")
    id("fondly.hilt")
}

android {
    namespace = "lat.virgotp.fondly.feature.balances"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:application"))
    implementation(project(":core:ui-common"))

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.4.0")

    implementation("androidx.navigation:navigation-compose:2.9.8")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
}