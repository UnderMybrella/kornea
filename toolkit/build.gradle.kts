apply(plugin = "org.jetbrains.kotlin.multiplatform")
apply(plugin = "kotlinx-atomicfu")

version = "3.5.1-alpha"

multiplatform {
    jvm()
    js(BOTH) {
        browser()
        nodejs()
    }

    /** Android/Native */
    androidNativeArm32()
    androidNativeArm64()

    /** iOS */
    iosArm32()
    iosArm64()
    iosX64()

    /** watchOS */
    watchosArm32()
    watchosArm64()
    watchosX86()

    /** tvOS */
    tvosArm64()
    tvosX64()

    /** Linux */
//    linuxArm64()
//    linuxArm32Hfp()
//    linuxMips32()
//    linuxMipsel32()
    linuxX64()

    /** MacOS */
    macosX64()

    /** Windows */
    mingwX86()
    mingwX64()

    /** WebAssembly */
    wasm32()

    sourceSets {
        val commonMain by getting {
            dependencies {
//                implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinx_coroutines"

                implementation(project(":kornea-annotations"))
                implementation(project(":kornea-errors"))

//                implementation("dev.neontech.lumberjack:lumberjack:0.6.0")
            }
        }

        defineSourceSet("commonCoroutines", dependsOn = "common", includedIn = listOf("jvm", "js")) {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$KOTLINX_COROUTINES_VERSION")
            }
        }

        defineSourceSet("native", dependsOn = "common")
        defineSourceSet("nativeCoroutines", dependsOn = listOf("native", "commonCoroutines"), includedIn = listOf("linuxX64", "mingwX64"))
        defineSourceSet("nativeWithoutCoroutines", dependsOn = "native", includedIn = listOf("mingwX86", "wasm32"))
        defineSourceSet("androidNative", dependsOn = "nativeWithoutCoroutines", includedIn = listOf("androidNativeArm32", "androidNativeArm64"))
        defineSourceSet("iOS", dependsOn = "nativeCoroutines", includedIn = listOf("iosArm32", "iosArm64", "iosX64"))
        defineSourceSet("watchOS", dependsOn = "nativeWithoutCoroutines", includedIn = listOf("watchosArm32", "watchosArm64", "watchosX86"))
        defineSourceSet("tvOS", dependsOn = "nativeWithoutCoroutines", includedIn = listOf("tvosArm64", "tvosX64"))
        defineSourceSet("linux", dependsOn = "native", includedIn = "linuxX64")
        defineSourceSet("linuxWithoutCoroutines", dependsOn = listOf("linux", "nativeWithoutCoroutines"), includedIn = listOf("linuxArm64", "linuxArm32Hfp", "linuxMips32", "linuxMipsel32"))

        defineSourceSet("macOS", dependsOn = "nativeCoroutines", includedIn = "macosX64")
        defineSourceSet("windows", dependsOn = "native", includedIn = listOf("mingwX86", "mingwX64"))

        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                explicitApi()
            }
        }
    }
}

configure<kotlinx.atomicfu.plugin.gradle.AtomicFUPluginExtension> {
    dependenciesVersion = null
}