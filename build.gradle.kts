plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.sonarqube") version "3.3"
    id("io.freefair.lombok") version "6.3.0"
    checkstyle
    jacoco
}

group = "org.kryonite"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    val junitVersion = "5.8.2"

    implementation("net.dv8tion:JDA:5.0.0-alpha.3") {
        exclude(module = "opus-java")
    }
    implementation("ch.qos.logback:logback-classic:1.2.10")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.7.4")

    testImplementation("ch.qos.logback:logback-classic:1.2.10")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:4.2.0")
}

tasks.test {
    finalizedBy("jacocoTestReport")
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

checkstyle {
    toolVersion = "9.2.1"
    config = project.resources.text.fromUri("https://kryonite.org/checkstyle.xml")
}

sonarqube {
    properties {
        property("sonar.projectKey", "kryonitelabs_kryo-discord-bot")
        property("sonar.organization", "kryonitelabs")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

val tokens = mapOf("VERSION" to project.version)
