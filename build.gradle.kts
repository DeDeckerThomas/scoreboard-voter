plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.2.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.thaumesd"
version = System.getenv("VERSION") ?: "dev"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation("cloud.commandframework", "cloud-paper", "1.8.4")
    implementation("cloud.commandframework", "cloud-annotations", "1.8.4")
    implementation("cloud.commandframework", "cloud-minecraft-extras", "1.8.4")
}

tasks.processResources {
    filesMatching("paper-plugin.yml") {
        expand(project.properties)
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    runServer {
        minecraftVersion("1.20.4")
    }
    shadowJar {
        minimize()
        relocate("cloud.commandframework", "com.thaumesd.scoreboardvoter.cloud")
        relocate("io.leangen.geantyref", "com.thaumesd.scoreboardvoter.geantyref")
    }
}