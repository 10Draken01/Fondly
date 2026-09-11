plugins {
    id("fondly.android.library")
    id("fondly.hilt")
}

android {
    namespace = "lat.virgotp.fondly.infrastructure"
}

dependencies {
    implementation(project(":core:domain"))

    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.8.0")
}