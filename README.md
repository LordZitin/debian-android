# Debian Android

A modern Android application built with Kotlin and Jetpack Compose to manage and run a Debian Linux environment, interactive bash terminal, X11 desktop applications, and APT package management directly on Android without requiring root access.

## Features

- **PRoot Virtualization Engine**: Compatibility layer allowing unrooted Android devices to run full Debian userlands.
- **Interactive Bash Terminal**: Monospaced Linux console emulator with ANSI color support, bash accessory keys (`TAB`, `CTRL`, `ALT`, `ESC`, `|`), command history, and suggestions.
- **X11 / XSDL Desktop Environment**: Graphical desktop with application launcher, interactive window manager, and built-in utilities:
  - **GIMP 2.8**: Paint canvas with multi-color palette, brush resizing, and clear functions (honoring original repo's redraw rate patch).
  - **Leafpad**: Text and shell script editor.
  - **Thunar File Manager**: Browse Debian rootfs hierarchies (`/bin`, `/etc`, `/home/debian`, `/sdcard`).
  - **Task Manager**: Process viewer with live CPU and memory metrics.
- **Debian APT Package Manager**: Search, install, and manage popular development tools, graphics suites, utilities, and network daemons (`gcc`, `gimp`, `git`, `python3`, `htop`, `tmux`, etc.).
- **c-ares DNS & SELinux Optimization**: Configurable asynchronous DNS resolver and shared memory acceleration hooks.

## Architecture

- **Language**: Kotlin 2.2.10
- **UI Framework**: Jetpack Compose with Material 3 (M3)
- **Design**: Centralized theme with dynamic dark mode, responsive layouts, and edge-to-edge support.
- **State Management**: MVVM Architecture with Kotlin Coroutines and StateFlow.
