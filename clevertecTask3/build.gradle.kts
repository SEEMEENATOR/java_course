plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.6.0") 
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    testImplementation("org.mockito:mockito-core:3.7.0")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "ru.clevertec.check.CheckRunner"
    }
}
application {
    mainClass.set("ru.clevertec.check.CheckRunner")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveBaseName.set("clevertec-check")
    archiveVersion.set("0.1.0")
    archiveClassifier.set("")
}
