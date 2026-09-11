plugins {
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain(11)
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
    }
}

dependencies {
    "testImplementation"("org.jetbrains.kotlin:kotlin-test:2.2.10")
    "testImplementation"("junit:junit:4.13.2")
}