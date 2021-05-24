apply(plugin = "org.jetbrains.kotlin.multiplatform")

version = "2.1.0-alpha"

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

        all {
            languageSettings.apply {
                enableLanguageFeature("InlineClasses")
                useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
                explicitApi()
            }
        }
    }

    addCompilerArgs("-Xopt-in=kotlin.RequiresOptIn")
}