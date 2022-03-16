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