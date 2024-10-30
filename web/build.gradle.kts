import org.jetbrains.kotlin.js.translate.context.Namer

plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
}

group = "com.commitAttack"
version = "0.0.3"

java {
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.jetbrains.kotlin:kotlin-reflect")
    // JWT
    api("io.jsonwebtoken:jjwt-api:0.11.5")
    api("io.jsonwebtoken:jjwt-impl:0.11.5")
    api("io.jsonwebtoken:jjwt-jackson:0.11.5")
    //jackson
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.2")
    api("com.fasterxml.jackson.core:jackson-core:2.15.2")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    // Web
    api("org.springframework.boot:spring-boot-starter-web")
    // Swagger
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    api("org.springframework.cloud:spring-cloud-starter-openfeign")
    //	coroutines
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    // validation
    api("org.springframework.boot:spring-boot-starter-validation")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Commit-Attack/CA-Be-Library")
            credentials {
                username = project.findProperty("GITHUB_USER") as String?
                password = project.findProperty("GITHUB_TOKEN") as String?
            }
        }
        publications {
            register<MavenPublication>("gpr") {
                from(components["java"])
            }
        }
    }
}


dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}