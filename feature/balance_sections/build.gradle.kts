plugins {
    id("fondly.android.library")
    id("fondly.compose")
    id("fondly.hilt")
}
android { namespace = "lat.virgotp.fondly.feature.sections" }
dependencies {
    implementation(project(":core:ui_common"))
    implementation(project(":core:domain"))
    implementation(project(":core:application"))

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.4.0")
}