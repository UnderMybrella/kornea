apply(plugin = KOTLIN_MULTIPLATFORM_PLUGIN)

version = "1.1.1-alpha"

multiplatform {
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */
    jvm()
//    js()
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
//        commonMain {
//            dependencies {
//                implementation kotlin('stdlib-common')
//                implementation project(':kornea-toolkit')
//            }
//        }
//        commonTest {
//            dependencies {
//                implementation kotlin('test-common')
//                implementation kotlin('test-annotations-common')
//            }
//        }
        val jvmMain by getting {
            dependencies {
//                implementation kotlin('reflect')

                implementation(project(":kornea-toolkit"))
            }
        }

        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                explicitApi()
            }
        }
    }
}

//defineSourceSet("reflective", ["common"]) { it in ["jvm"] }
