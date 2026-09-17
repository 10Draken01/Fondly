plugins {
    id("fondly.android.library")
    id("fondly.compose")
}

android {
    namespace = "lat.virgotp.fondly.uicommon"
}


dependencies {
    implementation(project(":core:domain"))
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.ink.geometry)
    implementation(libs.androidx.material.icons.extended)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.8.0")
}