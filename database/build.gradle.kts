plugins {
    kotlin("jvm")
}

group = "com.commitAttack"
version = "0.0.5"

val querydslVersion = "5.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // JPA
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    // ULID
    api("com.github.f4b6a3:ulid-creator:5.2.1")

    api("com.querydsl:querydsl-jpa:$querydslVersion:jakarta")
    kapt("com.querydsl:querydsl-apt:$querydslVersion:jakarta")
    api("jakarta.annotation:jakarta.annotation-api")
    api("jakarta.persistence:jakarta.persistence-api")
}

allOpen {
    annotation("jakarta.persistence.MappedSuperclass")
}

noArg {
    annotation("jakarta.persistence.MappedSuperclass")
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

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

val generated = file("src/main/generated")
tasks.withType<JavaCompile>{
    options.generatedSourceOutputDirectory.set(generated)
}

sourceSets {
    main {
        kotlin.srcDirs += generated
    }
}

kapt {
    generateStubs = true
}