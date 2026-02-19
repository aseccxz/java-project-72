plugins {
    id("java")
    id("org.sonarqube") version "7.1.0.6387"
    jacoco
    id("com.github.ben-manes.versions") version "0.53.0"
    checkstyle
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

sonar {
    properties {
        property("sonar.projectKey", "aseccxz_java-project-72")
        property("sonar.organization", "aseccxz")
    }
}
tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}
