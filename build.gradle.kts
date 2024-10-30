import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.22"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.9.22"
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.22"
    kotlin("jvm") version "1.9.22"
    kotlin("kapt") version "1.9.22"
    `java-library`
    `maven-publish`
}

group = "com.commitAttack"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

subprojects{
    apply{
        plugin("kotlin")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
        plugin("org.jetbrains.kotlin.plugin.allopen")
        plugin("org.jetbrains.kotlin.plugin.noarg")
        plugin("org.jetbrains.kotlin.plugin.spring")
        apply(plugin = "kotlin-kapt")
        apply(plugin = "maven-publish")
        apply(plugin = "java-library")
    }
    dependencies {
        testApi("org.springframework.boot:spring-boot-starter-test")
        testApi("org.jetbrains.kotlin:kotlin-test")
        testApi("org.springframework.security:spring-security-test")
        //kotest
        testApi("io.kotest:kotest-runner-junit5-jvm:5.8.0")
        testApi("io.kotest:kotest-assertions-core-jvm:5.8.0")
    }

}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}