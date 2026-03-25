plugins {
    application
    id("org.sonarqube") version "7.1.0.6387"
    jacoco
    id("com.github.ben-manes.versions") version "0.53.0"
    checkstyle
    id("io.freefair.lombok") version "9.2.0"
    id("com.gradleup.shadow") version "9.4.0"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation ("com.squareup.okhttp3:mockwebserver:4.12.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.27.3")
    implementation("io.javalin:javalin:6.7.0")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("gg.jte:jte:3.2.3")
    implementation("io.javalin:javalin-rendering:6.7.0")
    implementation("io.javalin:javalin-bundle:6.7.0")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("com.h2database:h2:2.4.240")
    implementation("com.konghq:unirest-java:3.14.5")
    implementation("org.jsoup:jsoup:1.22.1")
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
application {
    mainClass = "hexlet.code.App"
}
