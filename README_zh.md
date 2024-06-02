![MCUtilityKit](./MCUtilityKit.png)
# MCUtilityKit

##### [English](./README.md) | 简体中文

#### MCUtilityKit，简化启动、管理和定制，助您轻松开发出高效、易用的Minecraft工具

MCUtilityKit是专为Minecraft启动器开发者和其他Minecraft相关应用程序开发者量身打造的基础库。拥有强大的API和实用工具，它简化了将Minecraft启动、实例下载和账户管理功能集成到您的项目中。无论您是开发自定义启动器还是打造Minecraft服务器管理工具，MCUtilityKit都能为您提供必要的支持，加速开发，提升用户体验。

### 主要功能

- 轻松启动Minecraft: 提供直观的API，让用户能够快速启动Minecraft实例，轻松进入游戏。
- 简化实例下载: 内置实用工具，无缝下载Minecraft实例，确保用户获取最新的游戏版本和模组。
- 高效数据存储: 利用数据存储功能高效管理和存储用户数据，确保账户信息和设置的无缝访问。
- 简化账户管理: 提供身份验证和账户操作实用工具，简化账户管理任务。
- 跨平台兼容性: 兼容各种平台，包括Windows、macOS和Linux，确保在不同环境中实现无缝集成。

### 使用方式

你需要根据你的构建系统来选择正确的代码添加到你的项目中。

#### Maven (pom.xml):
```xml
<dependencies>
    <dependency>
        <groupId>top.alazeprt</groupId>
        <artifactId>MCUtilityKit</artifactId>
        <version>1.2</version>
    </dependency>
</dependencies>
```

#### Gradle (Groovy DSL, build.gradle): 
```groovy
dependencies {
    implementation 'top.alazeprt:MCUtilityKit:1.2'
}
```

#### Gradle (Kotlin DSL, build.gradle.kts):
```kotlin
dependencies {
    implementation("top.alazeprt:MCUtilityKit:1.2")
}
```

接着，你可以通过[JavaDoc](https://mcutilitykit.alazeprt.top/)查询它的使用方式

### 需求

- JDK 17或以上版本

### 构建

MCUtilityKit使用Gradle来构建项目:

```bash
./gradlew clean build
```

您需要JDK 17来构建此项目。

### 协议

MCUtilityKit使用[LGPL 3.0](https://www.gnu.org/licenses/lgpl-3.0.txt)协议。

### 贡献者

- [alazeprt](https://github.com/alazeprt)