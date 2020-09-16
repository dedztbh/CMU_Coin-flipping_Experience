import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "6.0.0"
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
    implementation("org.slf4j:slf4j-nop:1.7.13")
//    testImplementation(kotlin("test-junit"))
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("cmu_coin_flipping_experience")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "MainKt"))
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}