plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    implementation(project(":kryo-discord-whitelist-common"))

    implementation("net.dv8tion:JDA:5.0.0-alpha.4") {
        exclude(module = "opus-java")
    }
}

tasks {
    withType<Jar> {
        manifest {
            attributes["Main-Class"] = "org.kryonite.kryodiscordwhitelist.bot.KryoDiscordWhitelistBot"
        }
    }

    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        minimize()
    }
}
