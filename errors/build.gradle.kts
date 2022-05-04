apply(plugin = "org.jetbrains.kotlin.multiplatform")

version = "3.0.3-alpha"

multiplatform {
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */
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
    mingwX64()
    mingwX86()

    /** WebAssembly */
    wasm32()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":kornea-annotations"))
                api(project(":kornea-config"))
                api(project(":kornea-base"))
            }
        }

        defineSourceSet("commonAtomicfu", dependsOn = "common", includedIn = listOf("jvm", "js", "linuxX64", "mingwX64"))
        defineSourceSet("native", dependsOn = "common")
        defineSourceSet("nativeSelfAtomic", dependsOn = "native", includedIn = listOf("mingwX86", "wasm32"))
        defineSourceSet("androidNative", dependsOn = "nativeSelfAtomic", includedIn = listOf("androidNativeArm32", "androidNativeArm64"))
        defineSourceSet("iOS", dependsOn = listOf("native", "commonAtomicfu"), includedIn = listOf("iosArm32", "iosArm64", "iosX64"))
        defineSourceSet("watchOS", dependsOn = "nativeSelfAtomic", includedIn = listOf("watchosArm32", "watchosArm64", "watchosX86"))
        defineSourceSet("tvOS", dependsOn = "nativeSelfAtomic", includedIn = listOf("tvosArm64", "tvosX64"))
        defineSourceSet("linux", dependsOn = "native", includedIn = "linuxX64")
        defineSourceSet("linuxSelfAtomic", dependsOn = listOf("linux", "nativeSelfAtomic"), includedIn = listOf("linuxArm64", "linuxArm32Hfp", "linuxMips32", "linuxMipsel32"))

        defineSourceSet("macOS", dependsOn = listOf("native", "commonAtomicfu"), includedIn = "macosX64")
        defineSourceSet("windows", dependsOn = "native", includedIn = listOf("mingwX86", "mingwX64"))

        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                explicitApi()
            }
        }
    }

    addCompilerArgs("-Xopt-in=kotlin.RequiresOptIn")
}