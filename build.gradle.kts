plugins {
    `java-library`
    id("org.sonarqube") version "3.3"
    id("io.freefair.lombok") version "6.5.0.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    checkstyle
    jacoco
}

allprojects {
    group = "org.kryonite"
    version = "1.0.0"

    apply(plugin = "java-library")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "org.sonarqube")
    apply(plugin = "io.freefair.lombok")
    apply(plugin = "checkstyle")
    apply(plugin = "jacoco")

    repositories {
        mavenCentral()
    }

    dependencies {
        val junitVersion = "5.8.2"

        implementation("ch.qos.logback:logback-classic:1.2.11")

        testImplementation("ch.qos.logback:logback-classic:1.2.10")
        testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
        testImplementation("org.mockito:mockito-junit-jupiter:4.6.1")
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
            property("sonar.projectKey", "kryoniteorg_kryo-discord-whitelist")
            property("sonar.organization", "kryoniteorg")
            property("sonar.host.url", "https://sonarcloud.io")
        }
    }
}
