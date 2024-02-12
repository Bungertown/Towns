import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Logging

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.2"
    // SQL
    id("nu.studer.jooq") version "9.0"
    id("org.flywaydb.flyway") version "10.6.0"
}

group = "town.bunger"
version = "0.2.0-SNAPSHOT"

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
    maven("https://jitpack.io")
}

dependencies {
    // Paper
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
    // Command framework
    implementation("org.incendo:cloud-paper:2.0.0-beta.2")
    implementation("org.incendo:cloud-annotations:2.0.0-beta.2")
    implementation("org.incendo:cloud-minecraft-extras:2.0.0-beta.2")
    // SQL
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.jooq:jooq:3.19.2")
    implementation("com.h2database:h2:2.2.224")
    jooqGenerator("com.h2database:h2:2.2.224")
    implementation("org.flywaydb:flyway-core:10.6.0")
    // Configuration
    implementation("org.spongepowered:configurate-core:4.1.2")
    implementation("org.spongepowered:configurate-hocon:4.1.2")
    // Caching
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    // Nullability annotations
    compileOnly("org.jspecify:jspecify:0.3.0")
    implementation(project(":api"))
}

flyway {
    url = "jdbc:h2:${buildDir}/generated"
    locations = arrayOf("filesystem:${projectDir}/src/main/resources/town/bunger/towns/plugin/db/h2")
    placeholders = mapOf(
            "tablePrefix" to "",
    )
    validateMigrationNaming = true
    baselineOnMigrate = true
}

jooq {
    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "org.h2.Driver"
                    url = flyway.url
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.h2.H2Database"
                        inputSchema = "PUBLIC"
                        includes = ".*"
                        excludes = "(flyway_schema_history)|(?i:information_schema\\..*)|(?i:system_lobs\\..*)"
                        schemaVersionProvider = "SELECT :schema_name || '_' || MAX(\"version\") FROM \"flyway_schema_history\"" // Grab version from Flyway
                        forcedTypes = listOf(
                                ForcedType().apply {
                                    name = "INSTANT"
                                    types = "(?i:TIMESTAMP\\ WITH\\ TIME\\ ZONE)"
                                }
                        )
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isPojosAsJavaRecordClasses = true
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "town.bunger.towns.plugin.db"
                    }
                }
            }
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

val cleanGeneratedDb by tasks.registering {
    delete(file("${buildDir}/generated.mv.db"))
    delete(file("${buildDir}/generated.trace.db"))
}

tasks {
    runServer {
        minecraftVersion("1.20.4")
        downloadPlugins {
            url("https://download.luckperms.net/1529/bukkit/loader/LuckPerms-Bukkit-5.4.116.jar")
        }
    }
    shadowJar {
        archiveBaseName.set(project.name)
        archiveClassifier.set("")
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    processResources {
        filteringCharset = "UTF-8"

        val props = mapOf("version" to version)
        inputs.properties(props)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
    assemble {
        dependsOn(shadowJar)
    }
    flywayMigrate {
        dependsOn(cleanGeneratedDb)
    }
    named<nu.studer.gradle.jooq.JooqGenerate>("generateJooq") {
        dependsOn(flywayMigrate)
    }
    classes {
        dependsOn(named<nu.studer.gradle.jooq.JooqGenerate>("generateJooq"))
    }
}