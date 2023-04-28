import com.github.erizo.gradle.JcstressPluginExtension

apply(plugin = KOTLIN_JVM_PLUGIN)
apply(plugin = "maven-publish")

configure(subprojects) {
    afterEvaluate {
        configure<JcstressPluginExtension> {
            val dir =
                File(rootProject.buildDir, "repo/${group.toString().replace('.', '/')}/${name}/${version}/reports")
            dir.mkdirs()
            reportDir = dir.absolutePath
        }
    }

    apply(plugin = KOTLIN_JVM_PLUGIN)
    apply(plugin = KAPT_PLUGIN)
    apply(plugin = "maven-publish")
    apply(plugin = JCSTRESS_PLUGIN)

    dependencies {
        "implementation"(project(":kornea-jcstress"))
        "kapt"(JCSTRESS_DEPENDENCY)
    }

    configure<JcstressPluginExtension> {
        jcstressDependency = JCSTRESS_DEPENDENCY
    }
}

dependencies {
    "implementation"(JCSTRESS_DEPENDENCY)
}