# Universe Launcher

An original Android launcher project for Minecraft: Java Edition.

## Project status

This repository was empty when development started. The initial Android/Jetpack Compose foundation is being established first. A working Minecraft runtime, mod-loader installation, multiplayer services and a distributable APK are **not yet implemented** and must not be represented as complete until built and tested on a real Android device.

## Product direction

- Original Universe interface and navigation (not a recolored copy of another launcher).
- Android-native launcher shell using Kotlin and Jetpack Compose.
- Runtime integration designed around a legally compatible Minecraft Java-on-Android runtime.
- Per-version game profiles and isolated game directories.
- Mod management for compatible Fabric/Forge/NeoForge profiles, with dependency and version checks.
- Multiplayer server list and direct connection; online accounts and public hosting require separate secure services.

## Build

Open this project in Android Studio with Android SDK 35 installed and let Gradle sync. The Gradle wrapper will be added with the first reproducible CI/build environment.

## Licensing

Universe must retain notices and comply with the licenses of any upstream runtime or launcher components. No upstream source is copied into this initial scaffold.
