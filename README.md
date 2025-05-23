# Compose for Desktop Wizard

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-1.8.0-blue.svg?logo=jetpack-compose)](https://www.jetbrains.com/lp/compose-multiplatform/)

A web-based project generator that creates ready-to-use Kotlin Compose for Desktop applications with a single click.

![Screenshot of Wizard](media/screenshot.png)

## 🚀 Try It Now

**[Launch the Generator](https://composefordesktop.vercel.app/)**

## ✨ Features

- **Instant Project Setup**: Generate a complete Compose for Desktop project in seconds
- **Custom Configuration**: Tailor your project with various dependencies and settings
- **Interactive Dependency Selection**: Visual cards for easy selection of libraries and components
- **Live Code Preview**: See your build scripts and `Main.kt` file in real-time as you customize your project
- **Modern UI**: Material 3 theming with dark mode support
- **Production Ready**: Includes Gradle wrapper, proper project structure, and testing setup
- **Cross-Platform**: Works on Windows, macOS, and Linux

![Dependency Selection and Live Preview](media/screenshot2.png)

## 🛠️ How It Works

The wizard creates a customized Kotlin project with:

- Proper Gradle configuration
- Compose for Desktop dependencies
- Material 3 theming
- Dark mode support
- Dependency injection with Koin
- Optional libraries based on your selection

## 📁 Generated Project Structure

```text
.
├── build.gradle.kts
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradle.properties
├── gradlew
├── gradlew.bat
├── README.md
├── settings.gradle.kts
└── src
    ├── main
    │   ├── kotlin
    │   │   ├── App.kt
    │   │   ├── AppModule.kt
    │   │   ├── Database.kt
    │   │   ├── Main.kt
    │   │   ├── MainViewModel.kt
    │   │   ├── Models.kt
    │   │   └── theme
    │   │       ├── Color.kt
    │   │       ├── Theme.kt
    │   │       └── Type.kt
    │   └── resources
    └── test
        ├── kotlin
        └── resources
```

## 📖 Documentation

For more detailed information, check out [Wiki](https://github.com/zahid4kh/compose-for-desktop/wiki):

- [Architecture Overview](https://github.com/zahid4kh/compose-for-desktop/wiki/Architecture)
- [Troubleshooting](https://github.com/zahid4kh/compose-for-desktop/wiki/Troubleshooting)

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE.txt) file for details.
