repositories {
    jcenter()
    mavenCentral()
    gradlePluginPortal()
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    mavenLocal()
}

plugins {
    `kotlin-dsl`
//    id("org.jetbrains.kotlin.multiplatform") apply false
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.0-rc")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}