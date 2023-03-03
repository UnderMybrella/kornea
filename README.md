[//]: README_TEMPLATE.md (Note: This file is auto-generated; edit README_TEMPLATE.md and run fillReadme)
# kornea
A set of libraries for use in Kotlin MPP projects

## Badges
- ![kornea-annotations](https://img.shields.io/maven-metadata/v?label=kornea-annotations&metadataUrl=https%3A%2F%2Fmaven.brella.dev%2Fdev%2Fbrella%2Fkornea-annotations%2Fmaven-metadata.xml)
- ![kornea-apollo](https://img.shields.io/maven-metadata/v?label=kornea-apollo&metadataUrl=https%3A%2F%2Fmaven.brella.dev%2Fdev%2Fbrella%2Fkornea-apollo%2Fmaven-metadata.xml)
- ![kornea-base](https://img.shields.io/maven-metadata/v?label=kornea-base&metadataUrl=https%3A%2F%2Fmaven.brella.dev%2Fdev%2Fbrella%2Fkornea-base%2Fmaven-metadata.xml)
- ![kornea-composite](https://img.shields.io/maven-metadata/v?label=kornea-composite&metadataUrl=https%3A%2F%2Fmaven.brella.dev%2Fdev%2Fbrella%2Fkornea-composite%2Fmaven-metadata.xml)
- ![kornea-config](https://img.shields.io/maven-metadata/v?label=kornea-config&metadataUrl=https%3A%2F%2Fmaven.brella.dev%2Fdev%2Fbrella%2Fkornea-config%2Fmaven-metadata.xml)
- ![kornea-errors](https://img.shields.io/maven-metadata/v?label=kornea-errors&metadataUrl=https%3A%2F%2Fmaven.brella.dev%2Fdev%2Fbrella%2Fkornea-errors%2Fmaven-metadata.xml)
- ![kornea-img](https://img.shields.io/maven-metadata/v?label=kornea-img&metadataUrl=https%3A%2F%2Fmaven.brella.dev%2Fdev%2Fbrella%2Fkornea-img%2Fmaven-metadata.xml)
- ![kornea-io](https://img.shields.io/maven-metadata/v?label=kornea-io&metadataUrl=https%3A%2F%2Fmaven.brella.dev%2Fdev%2Fbrella%2Fkornea-io%2Fmaven-metadata.xml)
- ![kornea-modelling](https://img.shields.io/maven-metadata/v?label=kornea-modelling&metadataUrl=https%3A%2F%2Fmaven.brella.dev%2Fdev%2Fbrella%2Fkornea-modelling%2Fmaven-metadata.xml)
- ![kornea-toolkit](https://img.shields.io/maven-metadata/v?label=kornea-toolkit&metadataUrl=https%3A%2F%2Fmaven.brella.dev%2Fdev%2Fbrella%2Fkornea-toolkit%2Fmaven-metadata.xml)
- ![kornea-serialisation-core](https://img.shields.io/maven-metadata/v?label=kornea-serialisation-core&metadataUrl=https%3A%2F%2Fmaven.brella.dev%2Fdev%2Fbrella%2Fkornea-serialisation-core%2Fmaven-metadata.xml)
- ![kornea-serialisation-errors](https://img.shields.io/maven-metadata/v?label=kornea-serialisation-errors&metadataUrl=https%3A%2F%2Fmaven.brella.dev%2Fdev%2Fbrella%2Fkornea-serialisation-errors%2Fmaven-metadata.xml)
- ![kornea-serialisation-io](https://img.shields.io/maven-metadata/v?label=kornea-serialisation-io&metadataUrl=https%3A%2F%2Fmaven.brella.dev%2Fdev%2Fbrella%2Fkornea-serialisation-io%2Fmaven-metadata.xml)


## Gradle (Groovy)

```groovy
repositories {
    maven { url "https://maven.brella.dev" }
}

dependencies {
    implementation "dev.brella:kornea-annotations:1.3.0-alpha"
    implementation "dev.brella:kornea-apollo:1.1.0-alpha"
    implementation "dev.brella:kornea-base:1.1.0-alpha"
    implementation "dev.brella:kornea-composite:1.0.0-indev"
    implementation "dev.brella:kornea-config:1.2.0-indev"
    implementation "dev.brella:kornea-errors:3.1.0-alpha"
    implementation "dev.brella:kornea-img:1.4.0-alpha"
    implementation "dev.brella:kornea-io:6.0.0-alpha"
    implementation "dev.brella:kornea-modelling:1.2.0-alpha"
    implementation "dev.brella:kornea-toolkit:3.5.0-alpha"
    implementation "dev.brella:kornea-serialisation-core:1.0.0-alpha"
    implementation "dev.brella:kornea-serialisation-errors:1.0.0-alpha"
    implementation "dev.brella:kornea-serialisation-io:1.0.0-alpha"
}
```

## Gradle (Kotlin)

```kotlin
repositories {
    maven(url = "https://maven.brella.dev")
}

dependencies {
    implementation("dev.brella:kornea-annotations:1.3.0-alpha")
    implementation("dev.brella:kornea-apollo:1.1.0-alpha")
    implementation("dev.brella:kornea-base:1.1.0-alpha")
    implementation("dev.brella:kornea-composite:1.0.0-indev")
    implementation("dev.brella:kornea-config:1.2.0-indev")
    implementation("dev.brella:kornea-errors:3.1.0-alpha")
    implementation("dev.brella:kornea-img:1.4.0-alpha")
    implementation("dev.brella:kornea-io:6.0.0-alpha")
    implementation("dev.brella:kornea-modelling:1.2.0-alpha")
    implementation("dev.brella:kornea-toolkit:3.5.0-alpha")
    implementation("dev.brella:kornea-serialisation-core:1.0.0-alpha")
    implementation("dev.brella:kornea-serialisation-errors:1.0.0-alpha")
    implementation("dev.brella:kornea-serialisation-io:1.0.0-alpha")
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
    implementation(korneaAnnotations("1.3.0-alpha"))
    implementation(korneaApollo("1.1.0-alpha"))
    implementation(korneaBase("1.1.0-alpha"))
    implementation(korneaComposite("1.0.0-indev"))
    implementation(korneaConfig("1.2.0-indev"))
    implementation(korneaErrors("3.1.0-alpha"))
    implementation(korneaImg("1.4.0-alpha"))
    implementation(korneaIo("6.0.0-alpha"))
    implementation(korneaModelling("1.2.0-alpha"))
    implementation(korneaToolkit("3.5.0-alpha"))
    implementation(korneaSerialisationCore("1.0.0-alpha"))
    implementation(korneaSerialisationErrors("1.0.0-alpha"))
    implementation(korneaSerialisationIo("1.0.0-alpha"))
}
```
