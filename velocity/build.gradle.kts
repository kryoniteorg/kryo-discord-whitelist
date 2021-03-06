repositories {
    maven(url = "https://jitpack.io")
    maven(url = "https://nexus.velocitypowered.com/repository/maven-public/")
}

dependencies {
    val velocityVersion = "3.1.0"

    implementation(project(":kryo-discord-whitelist-common"))

    implementation("com.github.kryoniteorg:kryo-messaging:2.0.1")
    compileOnly("com.velocitypowered:velocity-api:$velocityVersion")
    annotationProcessor("com.velocitypowered:velocity-api:$velocityVersion")

    testImplementation("com.velocitypowered:velocity-api:$velocityVersion")
    testAnnotationProcessor("com.velocitypowered:velocity-api:$velocityVersion")
}
