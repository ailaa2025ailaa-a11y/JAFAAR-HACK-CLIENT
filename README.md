# JAFAAR HACK CLIENT

A client-side Fabric utility client for **Minecraft Java 1.21.11**.

## Current modules

- **Fullbright** — client gamma toggle.
- **Auto Sprint** — sprint while moving forward.
- **Zoom** — hold the configured zoom key (default C).
- **Coordinates HUD** — live XYZ display.
- **FPS HUD** — live frame-rate display.
- **Keystrokes HUD** — W/A/S/D and jump key display.
- **ClickGUI** — press Right Shift to open the module panel.

This first clean rebuild establishes the 1.21.11 client foundation. More visual and singleplayer-only utilities can be added in later commits. It does not include combat automation or anti-cheat bypasses.

## Build

Requires JDK 21 and Gradle 9.2.1 or compatible.

```bash
gradle build
```

The distributable JAR is generated in `build/libs/`.
