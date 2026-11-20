# JAFAAR CLIENT

A client-side Fabric mod foundation for Minecraft 26.1.2.

## Current project foundation

- Fabric Loader client entrypoint
- Java 25 toolchain
- Module registry with categories
- Persistent per-module configuration in `config/jafaar-client.properties`
- GitHub Actions build workflow (uploads the JAR as an artifact)

## Current modules registered

- FPS Profile (foundation)
- Reduced Visual Effects (foundation)
- Fullbright (toggle registration)
- Coordinates HUD (toggle registration)
- Keystrokes HUD (toggle registration)
- CPS Counter (toggle registration)
- Zoom (toggle registration)
- Toggle Sprint (toggle registration)

The module registry and config persistence are implemented. The listed toggles are currently foundations; their in-game behavior and the ClickGUI/HUD rendering still need implementation.

## Build

Requirements: JDK 25 and Gradle 9.6 or newer.

```bash
gradle build
```

The JAR is created in `build/libs/`.

## GitHub Actions

Pushes to `main`, pull requests targeting `main`, and manual workflow dispatches run the build workflow. Download the `JAFAAR-HACK-CLIENT` artifact from the successful workflow run.

## Scope

This project is focused on client-side performance, visual customization, HUD tools, and local quality-of-life features. It does not implement automated attacks, weapon-use automation, or bypasses for server protections.


## Current client controls
- Press **Right Shift** in-game to open the ClickGUI.
- Click a module to toggle it; module states are saved in `config/jafaar-client.properties`.
- The current build target is Minecraft 26.1.2 with Java 25.
