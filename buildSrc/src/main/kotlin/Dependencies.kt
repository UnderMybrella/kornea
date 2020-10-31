import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithPresetFunctions
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

const val KOTLIN_VERSION = "1.4.0"

const val KOTLINX_ATOMICFU_VERSION = "0.14.4"
const val KOTLINX_COROUTINES_VERSION = "1.4.0"
const val SHADOW_PLUGIN_VERSION = "4.0.3"
const val JMH_PLUGIN_VERSION = "0.5.0"

const val KOTLINX_ATOMICFU_GRADLE_PLUGIN = "org.jetbrains.kotlinx:atomicfu-gradle-plugin:$KOTLINX_ATOMICFU_VERSION"

const val KOTLIN_MULTIPLATFORM_PLUGIN = "org.jetbrains.kotlin.multiplatform"
const val KOTLIN_JVM_PLUGIN = "org.jetbrains.kotlin.jvm"
const val KOTLIN_SERIALISATION_PLUGIN = "org.jetbrains.kotlin.plugin.serialization"

const val SHADOW_PLUGIN = "com.github.johnrengelman.shadow"
const val JMH_PLUGIN = "me.champeau.gradle.jmh"


inline fun Project.defineSourceSet(newName: String, dependsOn: List<String>, noinline includedIn: (String) -> Boolean) =
    project.extensions.getByType<KotlinMultiplatformExtension>()
        .defineSourceSet(newName, dependsOn, includedIn)

fun KotlinMultiplatformExtension.defineSourceSet(
    newName: String,
    dependsOn: String,
    includedIn: List<String>,
    config: (KotlinSourceSet.() -> Unit)? = null
) =
    defineSourceSet(newName, listOf(dependsOn), { it in includedIn }, config)

fun KotlinMultiplatformExtension.defineSourceSet(
    newName: String,
    dependsOn: List<String>,
    includedIn: List<String>,
    config: (KotlinSourceSet.() -> Unit)? = null
) =
    defineSourceSet(newName, dependsOn, { it in includedIn }, config)

fun KotlinMultiplatformExtension.defineSourceSet(
    newName: String,
    dependsOn: String,
    vararg includedIn: String,
    config: (KotlinSourceSet.() -> Unit)? = null
) =
    defineSourceSet(newName, listOf(dependsOn), { it in includedIn }, config)

fun KotlinMultiplatformExtension.defineSourceSet(
    newName: String,
    dependsOn: List<String>,
    vararg includedIn: String,
    config: (KotlinSourceSet.() -> Unit)? = null
) =
    defineSourceSet(newName, dependsOn, { it in includedIn }, config)

fun KotlinMultiplatformExtension.defineSourceSet(
    newName: String,
    dependsOn: String,
    includedIn: String,
    config: (KotlinSourceSet.() -> Unit)? = null
) =
    defineSourceSet(newName, listOf(dependsOn), { includedIn == it }, config)

fun KotlinMultiplatformExtension.defineSourceSet(
    newName: String,
    dependsOn: List<String>,
    includedIn: String,
    config: (KotlinSourceSet.() -> Unit)? = null
) =
    defineSourceSet(newName, dependsOn, { includedIn == it }, config)

fun KotlinMultiplatformExtension.defineSourceSet(
    newName: String,
    dependsOn: String,
    config: (KotlinSourceSet.() -> Unit)? = null
) =
    defineSourceSet(newName, listOf(dependsOn), null, config)

fun KotlinMultiplatformExtension.defineSourceSet(
    newName: String,
    dependsOn: List<String>,
    config: (KotlinSourceSet.() -> Unit)? = null
) =
    defineSourceSet(newName, dependsOn, null, config)

fun KotlinMultiplatformExtension.defineSourceSet(
    newName: String,
    dependsOn: List<String>,
    includedIn: ((String) -> Boolean)? = null,
    config: (KotlinSourceSet.() -> Unit)? = null
) {
    for (suffix in listOf("Main", "Test")) {
        val newSourceSet = sourceSets.maybeCreate("$newName$suffix")
        dependsOn.forEach { dep -> newSourceSet.dependsOn(sourceSets["$dep$suffix"]) }
        sourceSets.forEach { currentSourceSet ->
            val currentName = currentSourceSet.name
            if (currentName.endsWith(suffix)) {
                val prefix = currentName.removeSuffix(suffix)
                if (includedIn?.invoke(prefix) == true) currentSourceSet.dependsOn(newSourceSet)
            }
        }

        config?.invoke(newSourceSet)
    }
}

inline fun Project.multiplatform(noinline configuration: KotlinMultiplatformExtension.() -> Unit): Unit =
    configure(configuration)