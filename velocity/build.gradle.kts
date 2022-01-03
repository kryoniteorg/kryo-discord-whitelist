repositories {
    maven(url = "https://jitpack.io")
    maven(url = "https://nexus.velocitypowered.com/repository/maven-public/")
}

dependencies {
    val velocityVersion = "3.1.0"

    implementation(project(":kryo-discord-whitelist-common"))

    implementation("com.github.kryonitelabs:kryo-messaging:bugfix~fix-jitpack-build-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-api:$velocityVersion")
    annotationProcessor("com.velocitypowered:velocity-api:$velocityVersion")

    testImplementation("com.velocitypowered:velocity-api:$velocityVersion")
    testAnnotationProcessor("com.velocitypowered:velocity-api:$velocityVersion")
}
