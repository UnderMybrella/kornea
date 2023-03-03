%GENERATED%
# kornea
A set of libraries for use in Kotlin MPP projects

## Badges
%PROJECT_BADGES%


## Gradle (Groovy)

```groovy
repositories {
    maven { url "https://maven.brella.dev" }
}

dependencies {
%PROJECT_GROOVY_IMPLEMENTATION%
}
```

## Gradle (Kotlin)

```kotlin
repositories {
    maven(url = "https://maven.brella.dev")
}

dependencies {
%PROJECT_KOTLIN_IMPLEMENTATION%
}
```

## [kornea-gradle](https://github.com/UnderMybrella/kornea-gradle)

```kotlin
plugins {
    id("dev.brella.kornea") version "1.4.1"
}

repositories {
    mavenBrella()
}

dependencies {
%PROJECT_KORNEA_IMPLEMENTATION%
}
```