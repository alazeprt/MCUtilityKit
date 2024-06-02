plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("maven-publish")
}

repositories {
    maven("https://jitpack.io")
    mavenCentral() 
}

version = "1.2"

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
//    implementation("com.github.Querz:NBT:6.1")
    implementation("commons-io:commons-io:2.16.1")
    implementation("org.apache.commons:commons-compress:1.26.1")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1")
    implementation("org.tinylog:tinylog-api:2.7.0")
    implementation("org.tinylog:tinylog-impl:2.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
}

tasks.test {
    useJUnitPlatform()
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "top.alazeprt"
            artifactId = "MCUtilityKit"
            version = "1.2"
            from(components["java"])
            description = "MCUtilityKit, Simplify launching, managing, and customizing, enabling you to effortlessly develop efficient and user-friendly Minecraft tools."
            pom {
                developers {
                    developer {
                        id.set("alazeprt")
                        name.set("alazeprt")
                    }
                }
                licenses {
                    license {
                        name.set("GNU LESSER GENERAL PUBLIC LICENSE VERSION 3.0")
                        url.set("https://www.gnu.org/licenses/lgpl-3.0.txt")
                    }
                }
                url.set("https://github.com/alazeprt/MCUtilityKit")
                scm {
                    connection.set("scm:git:https://github.com/alazeprt/MCUtilityKit.git")
                    url.set("https://github.com/alazeprt/MCUtilityKit")
                    developerConnection.set("scm:git:ssh://github.com/alazeprt/MCUtilityKit.git")
                }
                description.set("MCUtilityKit, Simplify launching, managing, and customizing, enabling you to effortlessly develop efficient and user-friendly Minecraft tools.")
            }
        }
    }

    repositories {
        maven {
            name = "local"
            url = file("${buildDir}/repo").toURI()
        }
    }
}