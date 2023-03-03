import dev.brella.kornea.gradle.registerFillReadmeTask

buildscript {
    dependencies {
        classpath(KOTLINX_ATOMICFU_GRADLE_PLUGIN)
    }
}

plugins {
//    id 'java' apply false
//    id("org.jetbrains.dokka") version "0.10.1"
    id(KOTLIN_MULTIPLATFORM_PLUGIN) apply false
    id(KOTLIN_JVM_PLUGIN) apply false
    id(KOTLIN_SERIALISATION_PLUGIN) version KOTLIN_VERSION apply false

    id(SHADOW_PLUGIN) version SHADOW_PLUGIN_VERSION apply false

    id(JMH_PLUGIN) version JMH_PLUGIN_VERSION apply false

    id("org.jetbrains.dokka") version "1.6.21"
    id("dev.brella.kornea") version "1.4.1"

//    id("debuglog.plugin") version "1.0.0-indev" apply false
}

allprojects {
    group = "dev.brella"

    repositories {
        mavenCentral()
        maven(url = "https://maven.brella.dev")
    }
}

configure(subprojects) {
    apply(plugin = "maven-publish")

    group = "dev.brella"

//    group = artifactGroup

//    jar {
//        baseName = "spiral"
//        appendix = "$project.name"
//        version = ""
//    }
//
//    task sourcesJar(type: Jar, dependsOn: classes) {
//        classifier = 'sources'
//        baseName = jar.baseName
//        appendix = jar.appendix
//        version = jar.version
//        from sourceSets.main.allSource
//    }
//
//    task javadocJar(type: Jar, dependsOn: javadoc) {
//        classifier = 'javadoc'
//        baseName = jar.baseName
//        appendix = jar.appendix
//        version = jar.version
//        from javadoc.destinationDir
//    }
//
//    artifacts {
//        archives sourcesJar
//        archives javadocJar
//    }

    configure<PublishingExtension> {
        repositories {
            maven(url = "${rootProject.buildDir}/repo")
        }
    }
}

//tasks {
//    wrapper {
//        gradleVersion = '6.4.1'
//        distributionType = Wrapper.DistributionType.ALL
//    }
//}

rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion = "16.0.0"
//    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().n
}

registerFillReadmeTask("fillReadme") {
    inputFile.set(File(rootDir, "README_TEMPLATE.md"))
    outputFile.set(File(rootDir, "README.md"))

    addReplacement("%PROJECT_BADGES%") {
        buildString {
            rootProject.subprojects
                .filter { subproject -> subproject.version.toString() != "unspecified" }
                .forEachIndexed { i, subproject ->
                    if (i > 0) appendLine()
                    append("- ![")
                    append(subproject.name)
                    append("](https://img.shields.io/maven-metadata/v?label=")
                    append(subproject.name)
                    append("&metadataUrl=https%3A%2F%2Fmaven.brella.dev%2F")
                    append(subproject.group.toString().replace(".", "%2F"))
                    append("%2F")
                    append(subproject.name)
                    append("%2Fmaven-metadata.xml)")
                }
        }
    }

    addReplacement("%PROJECT_GROOVY_IMPLEMENTATION%") {
        buildString {
            //implementation "dev.brella:kornea-annotations:%KORNEA-ANNOTATIONS-VERSION%"
            rootProject.subprojects
                .filter { subproject -> subproject.version.toString() != "unspecified" }
                .forEachIndexed { i, subproject ->
                    if (i > 0) appendLine()
                    append("    implementation \"")
                    append(subproject.group)
                    append(':')
                    append(subproject.name)
                    append(':')
                    append(subproject.version)
                    append('"')
                }
        }
    }

    addReplacement("%PROJECT_KOTLIN_IMPLEMENTATION%") {
        buildString {
            rootProject.subprojects
                .filter { subproject -> subproject.version.toString() != "unspecified" }
                .forEachIndexed { i, subproject ->
                    if (i > 0) appendLine()
                    append("    implementation(\"")
                    append(subproject.group)
                    append(':')
                    append(subproject.name)
                    append(':')
                    append(subproject.version)
                    append("\")")
                }
        }
    }

    addReplacement("%PROJECT_KORNEA_IMPLEMENTATION%") {
        buildString {
            rootProject.subprojects
                .filter { subproject -> subproject.version.toString() != "unspecified" }
                .forEachIndexed { i, subproject ->
                    if (i > 0) appendLine()
                    append("    implementation(")
                    subproject.name.split('-')
                        .forEachIndexed { j, word ->
                            if (j > 0) {
                                append(word.first().toUpperCase())
                                append(word.drop(1).toLowerCase())
                            } else {
                                append(word.toLowerCase())
                            }
                        }

                    append("(\"")
                    append(subproject.version)
                    append("\"))")
                }
        }
    }

    addReplacement("%GENERATED%") { "[//]: README_TEMPLATE.md (Note: This file is auto-generated; edit README_TEMPLATE.md and run fillReadme)" }
}