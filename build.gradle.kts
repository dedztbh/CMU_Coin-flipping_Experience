import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
}
group = "com.dedztbh"
version = "1.0-SNAPSHOT"

val dl4jVersion = "1.0.0-beta7"


repositories {
    mavenCentral()
    maven("https://dl.bintray.com/mipt-npm/scientifik")
}
dependencies {
    implementation("org.deeplearning4j:deeplearning4j-core:${dl4jVersion}")
    implementation("org.nd4j:nd4j-native-platform:${dl4jVersion}")
    testImplementation(kotlin("test-junit"))
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
