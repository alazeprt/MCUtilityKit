plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    maven("https://jitpack.io")
    mavenCentral() 
}

version = "1.0-SNAPSHOT"

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
//    implementation("com.github.Querz:NBT:6.1")
    implementation("commons-io:commons-io:2.16.1")
    implementation("org.apache.commons:commons-compress:1.26.1")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
}

tasks.test {
    useJUnitPlatform()
}
