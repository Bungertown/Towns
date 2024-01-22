plugins {
    id("java")
    id("java-library")
}

group = "town.bunger.towns"
version = "0.1.0"

repositories {
    mavenCentral()
    // Paper
    maven("https://repo.papermc.io/repository/maven-public/")
    // Cloud command framework
    maven("https://oss.sonatype.org/content/groups/public/")
    // Cloud v2
    maven("https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-snapshots"
        mavenContent {
            snapshotsOnly()
        }
    }
}

dependencies {
    // Paper
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
    // JSON support
    api("com.google.code.gson:gson:2.10.1")
    // Command framework
    // TODO: NoClassDefFoundError: cloud.commandframework.types.range.Range
//    api("cloud.commandframework:cloud-core:2.0.0-SNAPSHOT")
    api("cloud.commandframework:cloud-core:2.0.0-20240120.200824-18")
    // Annotations
    compileOnly("org.apiguardian:apiguardian-api:1.1.2")
    compileOnly("org.jspecify:jspecify:0.3.0")
}
