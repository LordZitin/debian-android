package com.example.debianandroid.model

enum class ContainerStatus {
    STOPPED,
    STARTING,
    RUNNING,
    STOPPING
}

enum class DebianDistro(val codeName: String, val versionNumber: String, val isRecommended: Boolean) {
    BOOKWORM("Debian 12 Bookworm", "12.5", true),
    BULLSEYE("Debian 11 Bullseye", "11.9", false),
    BUSTER("Debian 10 Buster (Original)", "10.13", false)
}

enum class Architecture(val tag: String, val label: String) {
    ARM64("arm64-v8a", "AArch64 / ARM64"),
    X86_64("x86_64", "x86_64 (64-bit PC)"),
    ARMEABI_V7A("armeabi-v7a", "ARMv7 32-bit")
}

enum class DesktopEnv(val displayName: String, val memoryFootprint: String, val description: String) {
    XFCE4("XFCE 4.18", "Medium (~220 MB)", "Full-featured, responsive desktop with panel, menu & window manager"),
    LXDE("LXDE Desktop", "Light (~120 MB)", "Ultra-fast, lightweight desktop environment ideal for mobile"),
    OPENBOX("Openbox Minimal", "Ultra-Light (~45 MB)", "Minimalist stacking window manager with right-click root menu"),
    CLI_HEADLESS("Headless / CLI", "Minimal (~25 MB)", "No graphical desktop server, pure high-performance PRoot shell")
}

data class SystemMetrics(
    val cpuUsagePercent: Int = 14,
    val ramUsedMb: Int = 340,
    val ramTotalMb: Int = 4096,
    val diskUsedGb: Float = 1.4f,
    val diskTotalGb: Float = 16.0f,
    val uptimeSeconds: Long = 0L
)

enum class LineType {
    COMMAND,
    OUTPUT,
    SUCCESS,
    ERROR,
    INFO,
    SYSTEM
}

data class TerminalLine(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val type: LineType = LineType.OUTPUT,
    val timestamp: Long = System.currentTimeMillis()
)

data class DebianPackage(
    val id: String,
    val name: String,
    val version: String,
    val category: String,
    val description: String,
    val sizeMb: Float,
    val isInstalled: Boolean,
    val isCore: Boolean = false
)

data class ProcessInfo(
    val pid: Int,
    val user: String,
    val cpuPercent: Float,
    val memPercent: Float,
    val command: String
)

enum class WindowType {
    GIMP,
    TERMINAL,
    EDITOR,
    FILES,
    MONITOR,
    NEOFETCH
}

data class DesktopWindow(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val type: WindowType,
    val isMinimized: Boolean = false,
    val isMaximized: Boolean = false
)

data class DebianSettings(
    val distro: DebianDistro = DebianDistro.BOOKWORM,
    val architecture: Architecture = Architecture.ARM64,
    val desktopEnv: DesktopEnv = DesktopEnv.XFCE4,
    val dnsServer: String = "8.8.8.8",
    val secondaryDns: String = "1.1.1.1",
    val enableSELinuxBypass: Boolean = true,
    val enableFakeRoot: Boolean = true,
    val bindSdCard: Boolean = true,
    val enableLink2Symlink: Boolean = true,
    val xsdlResolution: String = "1280x720",
    val colorDepth: String = "24-bit TrueColor"
)
