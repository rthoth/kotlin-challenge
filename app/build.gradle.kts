/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.4/userguide/building_java_projects.html in the Gradle documentation.
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    kotlin("jvm") version "1.9.10"

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

object Version {
    val scalaBinary = "2.13"
    val akka = "2.8.5"
    val akkaHttp = "10.5.3"
    val orientdb = "3.2.23"
    val ktorn = "3.6.0"
    val h2 = "2.2.220"
    val flyway = "9.22.3"
    val mockK = "1.13.8"
}

dependencies {
    // This dependency is used by the application.
    // implementation("com.google.guava:guava:32.1.1-jre")
    implementation("com.typesafe.akka:akka-actor-typed_${Version.scalaBinary}:${Version.akka}")
    implementation("com.typesafe.akka:akka-stream_${Version.scalaBinary}:${Version.akka}")
    implementation("com.typesafe.akka:akka-http_${Version.scalaBinary}:${Version.akkaHttp}")
    implementation(platform("com.typesafe.akka:akka-http-bom_${Version.scalaBinary}:${Version.akkaHttp}"))
    implementation("com.typesafe.akka:akka-http-jackson_${Version.scalaBinary}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.ktorm:ktorm-core:${Version.ktorn}")
    implementation("com.h2database:h2:${Version.h2}")
    implementation("org.flywaydb:flyway-core:${Version.flyway}")
    testImplementation("io.mockk:mockk:${Version.mockK}")

    testImplementation(kotlin("test"))
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("1.9.10")
        }
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    // Define the main class for the application.
    mainClass.set("challenge.AppKt")
}