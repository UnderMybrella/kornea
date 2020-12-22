apply(plugin = KOTLIN_MULTIPLATFORM_PLUGIN)

version = "1.2.1-alpha"

multiplatform {
    /* Targets configuration omitted.
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */
    jvm()
    js()
//    mingwX64() {
//
//    }
//    linuxX64 {
//        compilations.main {
//            cinterops {
//                libpng {
//                    // A shortcut for includeDirs.allHeaders.
//                    includeDirs("${project.projectDir.absolutePath}/src/headers/linux_x64")
//                }
//            }
//        }
//
//        binaries {
//            executable {
//                linkerOpts = ['-L/usr/lib/x86_64-linux-gnu/', '-lpng', '-lz']
//            }
//        }
//    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":kornea-io"))
                implementation(project(":kornea-errors"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$KOTLINX_COROUTINES_VERSION")
            }
        }
        all {
            languageSettings.apply {
                enableLanguageFeature("InlineClasses")
                useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
            }
        }
    }
}