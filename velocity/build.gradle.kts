repositories {
    maven(url = "https://nexus.velocitypowered.com/repository/maven-public/")
}

dependencies {
    val velocityVersion = "3.1.0"

    implementation(project(":kryo-discord-whitelist-common"))

    compileOnly("com.velocitypowered:velocity-api:$velocityVersion")
    annotationProcessor("com.velocitypowered:velocity-api:$velocityVersion")
}
