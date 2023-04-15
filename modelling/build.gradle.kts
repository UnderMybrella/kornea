apply(plugin = "org.jetbrains.kotlin.multiplatform")

version = "1.2.1-alpha"

multiplatform {
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */
    jvm()
    js(BOTH) {
        browser()
        nodejs()
    }
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

    sourceSets {
        val commonMain by getting {
            dependencies {
//                implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinx_coroutines"

                implementation(project(":kornea-io"))
                implementation(project(":kornea-errors"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.9.4")
            }
        }

        all {
            languageSettings.apply {
            }
        }
    }
}