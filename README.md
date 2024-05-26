![MCUtilityKit](./MCUtilityKit.png)
# MCUtilityKit

##### English | [简体中文](./README_zh.md)

#### MCUtilityKit, Simplify launching, managing, and customizing, enabling you to effortlessly develop efficient and user-friendly Minecraft tools.

MCUtilityKit is an essential library tailored for Minecraft launcher developers and other Minecraft-related application developers. Featuring a robust set of APIs and utilities, it simplifies the integration of Minecraft launching, instance downloading, and account management functionalities into your projects. Whether you're developing a custom launcher or crafting tools for Minecraft server management, MCUtilityKit provides the foundation to accelerate development and enhance user experience.

### Key Features

- Effortless Minecraft Launching: MCUtilityKit offers intuitive APIs for launching Minecraft instances, enabling users to swiftly dive into their gaming sessions.
- Streamlined Instance Downloading: Seamlessly download Minecraft instances with built-in utilities, ensuring users have access to the latest game versions and mods.
- Efficient Data Storage: Manage and store user data efficiently with MCUtilityKit's data storage capabilities, ensuring seamless access to account information and settings.
- Simplified Account Management: Simplify account management tasks with built-in utilities for authentication and account-related operations.
- Cross-Platform Compatibility: MCUtilityKit is compatible with various platforms, including Windows, macOS, and Linux, ensuring seamless integration across different environments.

### Usage

You need to choose the right code to add to your project based on your build system.

#### Maven (pom.xml).
``xml
<dependencies
<dependency>
<groupId>top.alazeprt</groupId>
<artifactId>MCUtilityKit</artifactId>
<version>1.1</version>
</dependency
</dependencies
```

#### Gradle (Groovy DSL, build.gradle). 
```groovy
dependencies {
    implementation 'top.alazeprt:MCUtilityKit:1.1'
}
```

#### Gradle (Kotlin DSL, build.gradle.kts).
```kotlin
dependencies {
    implementation("top.alazeprt:MCUtilityKit:1.1")
}
```

Next, you can look up how it's used via [JavaDoc](https://mcutilitykit.alazeprt.top/)

### Requirements

- JDK version 17 or above

### Building

MCUtilityKit uses Gradle to build the project:

```bash
./gradlew clean build
``` 

You will need JDK 17 to build this project.

### License

MCUtilityKit is released under the [LGPL-3.0 license](https://www.gnu.org/licenses/lgpl-3.0.txt).

### Contributors
- [alazeprt](https://github.com/alazeprt)