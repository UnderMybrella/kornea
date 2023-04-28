version = project(":kornea-base").version

dependencies {
    implementation(project(":kornea-base"))
    annotationProcessor(JCSTRESS_DEPENDENCY)
}