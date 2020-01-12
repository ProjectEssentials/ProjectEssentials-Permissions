## 🔒 Project Essentials: PermissionsAPI

[![](https://jitpack.io/v/projectessentials/ProjectEssentials-Permissions.svg)](https://jitpack.io/#projectessentials/ProjectEssentials-Permissions)
![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/ProjectEssentials/ProjectEssentials-Permissions?include_prereleases)
![GitHub](https://img.shields.io/github/license/ProjectEssentials/ProjectEssentials-Permissions)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)
[![Telegram Group](https://img.shields.io/badge/Telegram-Group-blue.svg)](https://t.me/minecraftforge)

> ## Permissions API for Forge mods.

#### ❗❗❗ Compatibility with forge version `28.0.X` and `28.1.X`.

## 🧐 Install using Gradle (for developers):
> ##### If your project uses Gradle as Build Tool, then use the code below to add dependencies:

```groovy
repositories {
    maven { url("https://jitpack.io") }
}

dependencies {
    compile(
        group: "com.github.projectessentials",
        name: "ProjectEssentials-Permissions",
        version: "v1.14.4-1.0.0.0"
    )
}
```

## 🤔 Install using Maven (for developers):
> ##### If your project uses Maven as Build Tool, then use the code below to add dependencies:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
	<groupId>com.github.projectessentials</groupId>
	<artifactId>ProjectEssentials-Permissions</artifactId>
	<version>v1.14.4-1.0.0.0</version>
</dependency>
```

## 🎮 Installation instructions (for playing):
> ##### Just move Project Essentials Permissions-1.14.4-1.X.X.X.jar to mods directory:

> ##### Important note: don't forget install mod dependencies!
  - core: https://github.com/ProjectEssentials/ProjectEssentials-Core/releases

```
.
├── assets
├── config
├── libraries
├── mods (that's how it should be)
│   ├── Project Essentials Core-MOD-1.14.4-1.X.X.X.jar.
│   └── Project Essentials Permissions-1.14.4-1.X.X.X.jar.
└── ...
```

```
Additional information:
    - kotlin-std lib version: 1.3.60
    - kotlinx serialization version: 0.14.0
    - target jvm version: 1.8
```

### After you got the dependencies and the library itself:

# [Read the documentation to get started!](./documentation/in-using.md)

### **Note:** This library is written in Kotlin, but this API is also compatible with Java. Keep in mind though that you will not get a perfect experience using Java. One of these points is that you will see all the properties and fields with internal modifiers (in my library).

> ## Made with 💕 by [MairwunNx](https://mairwunnx.github.io/)

## 🥰 Powered by JetBrains product licenses, Intellij Ultimate, Kotlin language and the Forge modding API!

<p align="center">
    <a href="https://www.jetbrains.com">
        <img style="padding-right: 20px" src="logos/jetbrains.svg" width="132">
    </a>
    <a href="https://www.jetbrains.com/idea/">
        <img src="logos/intellijidea.svg" width="142">
    </a>
    <a href="https://kotlinlang.org/">
        <img style="padding-left: 20px" src="logos/kotlin.svg" width="142">
    </a>
</p>

<p align="center">
    <a href="https://www.minecraftforge.net/forum/">
        <img style="padding-left: 20px; padding-top: 30px" src="logos/forge.svg" height="160">
    </a>
</p>

