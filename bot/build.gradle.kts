dependencies {
    implementation(project(":kryo-discord-whitelist-common"))

    implementation("net.dv8tion:JDA:5.0.0-alpha.21") {
        exclude(module = "opus-java")
    }
}

tasks {
    withType<Jar> {
        manifest {
            attributes["Main-Class"] = "org.kryonite.kryodiscordwhitelist.bot.KryoDiscordWhitelistBot"
        }
    }
}
