pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

rootProject.name = "kornea"

fun includeSubprojects(path: List<String>, dir: File) {
    dir.listFiles(File::isDirectory)
        ?.forEach { projectDir ->
            if (projectDir.name.equals("buildSrc", true)) return@forEach

            val newPath = path + projectDir.name
            if (File(projectDir, "build.gradle").exists() || File(projectDir, "build.gradle.kts").exists()) {
                val pathName = newPath.joinToString(":", prefix = ":")
                val projectName = newPath.joinToString("-", prefix = "kornea-")
                include(pathName)
                project(pathName).name = projectName

                println("Loading $projectName @ $pathName")
            }

            includeSubprojects(newPath, projectDir)
        }
}

includeSubprojects(emptyList(), rootDir)

//include ':base'
//
//include ':annotations'
//include ':apollo'
//include ':benchmarks'
//include ':config'
//include ':config-plugin'
//include ':config-plugin-compiler'
//
//include ':errors'
//include ':io'
//include ':img'
//include ':modelling'
//include ':office'
//include ':toolkit'
//include ':bootstrap'
//
//include ':serialisation:errors'
//
//rootProject.children.each { child -> child.name = "kornea-${child.name}"}