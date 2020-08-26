apply(plugin = "org.jetbrains.kotlin.multiplatform")
apply(plugin = "kotlinx-atomicfu")
//apply plugin: 'kotlinx-atomicfu'

version = "3.5.2-alpha"

multiplatform {
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */
    jvm()
    js()
//    mingwX64()
//    mingwX64() {
//        binaries {
//            sharedLib {
//            }
//
//            staticLib {
//            }
//        }
//    }
//    linuxX64()
//    linuxArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":kornea-annotations"))
                api(project(":kornea-errors"))
                api(project(":kornea-toolkit"))
            }
        }

        defineSourceSet("coroutine", dependsOn = "common", includedIn = listOf("jvm", "js")) {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$KOTLINX_COROUTINES_VERSION")
                implementation("org.jetbrains.kotlinx:atomicfu:$KOTLINX_ATOMICFU_VERSION")
            }
        }

        defineSourceSet("native", dependsOn = "common")
        defineSourceSet("nativeCoroutine", dependsOn = listOf("native", "coroutine"))
        defineSourceSet("nativeWithoutCoroutine", dependsOn = "native")


        all {
            languageSettings.apply {
                enableLanguageFeature("InlineClasses")
                useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
                explicitApi()
            }
        }
    }
}

configure<kotlinx.atomicfu.plugin.gradle.AtomicFUPluginExtension> {
    dependenciesVersion = null
}