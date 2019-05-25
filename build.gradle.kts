import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.70"
    id("com.github.johnrengelman.shadow") version "5.0.0"
    id("com.google.cloud.tools.jib") version "2.1.0"
}

group = "flatjaeger"
version = "0.0.1"

application {
    mainClassName = "flathunter.script.ScriptKt"
}

jib {
    jib.to.image = "flathunter.script"
    jib.container.mainClass = "flathunter.script.ScriptKt"
}

repositories {
    jcenter()
}

dependencies {
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-jdk8", version = "1.3.70")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.3.5")

    val fuelVersion = "2.1.0"
    implementation(group = "com.github.kittinunf.fuel", name = "fuel", version = fuelVersion)
    implementation(group = "com.github.kittinunf.fuel", name = "fuel-coroutines", version = fuelVersion)

    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = "2.10.2")

    implementation(group = "com.amazonaws", name = "aws-lambda-java-core", version = "1.2.0")
    implementation(group = "com.amazonaws", name = "aws-lambda-java-events", version = "2.2.7")

    implementation(group = "org.jsoup", name = "jsoup", version = "1.12.1")
    implementation(group = "com.google.maps", name = "google-maps-services", version = "0.9.3")

    implementation(group = "com.sendgrid", name = "sendgrid-java", version = "4.4.7")
    implementation(group = "com.github.spullara.mustache.java", name = "compiler", version = "0.9.6")

    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.1")

    val jUnitVersion = "5.6.2"
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = jUnitVersion)
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = jUnitVersion)
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi,kotlinx.coroutines.ObsoleteCoroutinesApi"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
    }

    withType<Jar> {
        manifest {
            attributes(
                mapOf("Main-Class" to "flathunter.script.ScriptKt")
            )
        }
    }

    test {
        useJUnitPlatform()
    }

    fun JavaExec.loadEnv(filename: String) {
        file(filename)
            .readLines()
            .filter { it.matches("^[^#].+=.*".toRegex()) }
            .map { it.split("=", limit = 2) }
            .forEach { (key, value) -> environment(key, value) }
    }

    task<JavaExec>("runScript") {
        classpath = sourceSets["main"].runtimeClasspath
        main = "flathunter.script.ScriptKt"

        args =
            System.getProperty("envFile")
                ?.let { listOf(it) }
                ?: listOf()
    }
}
