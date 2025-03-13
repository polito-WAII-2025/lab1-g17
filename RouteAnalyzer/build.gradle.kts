plugins {
    kotlin("jvm") version "2.1.10"
    application
}

group = "com.carrental"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-csv:1.9.0")
    implementation("org.yaml:snakeyaml:2.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(22)
}

application {
    mainClass = "com.carrental.MainKt"
}