plugins {
    id("fondly.android.library")
    id("fondly.compose")
    id("fondly.hilt")
}

android {
    namespace = "lat.virgotp.fondly.feature.settings"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:application"))
    implementation(project(":core:ui-common"))
}