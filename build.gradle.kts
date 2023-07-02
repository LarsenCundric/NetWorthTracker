plugins {
    kotlin("jvm") version "1.8.0"

    id("org.jmailen.kotlinter") version "3.8.0"
    id("io.gitlab.arturbosch.detekt") version "1.19.0"

    distribution
    application
}

application {
    mainClass.set("MainKt")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("com.github.ajalt.mordant:mordant:2.0.0-beta9")
    implementation("com.github.ajalt.clikt:clikt:3.4.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("org.kodein.di:kodein-di:7.10.0")
    implementation("org.kodein.di:kodein-di-conf-jvm:7.10.0")
    implementation("io.ktor:ktor-server-core:1.6.7")
    implementation("io.ktor:ktor-server-netty:1.6.7")
    implementation("ch.qos.logback:logback-classic:1.2.5")
    implementation("io.ktor:ktor-html-builder:1.6.7")
    implementation("io.ktor:ktor-freemarker:1.6.7")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

detekt {
    ignoreFailures = false

    // This takes sane language defaults (such as correct naming)
    buildUponDefaultConfig = true
    parallel = true
}

kotlinter {
    ignoreFailures = false
    // Temporarily such that we support for enforcement of trailing comma's
    experimentalRules = true
}