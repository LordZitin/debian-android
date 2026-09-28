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
    BUSTER("Debian 10 Buster (Original)", "10.13", false),
    UBUNTU_FOCAL("Ubuntu 20.04 LTS Focal", "20.04", false)
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
    NEOFETCH,
    VULKAN_GEARS
}

data class DesktopWindow(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val type: WindowType,
    val isMinimized: Boolean = false,
    val isMaximized: Boolean = false
)

// Winlator Container Model
data class WinlatorContainer(
    val id: String,
    val name: String,
    val screenSize: String = "1280x720 (16:9)",
    val graphicsDriver: String = "Turnip (Adreno) + Zink",
    val dxvkVersion: String = "DXVK 2.3",
    val vkd3dVersion: String = "VKD3D-Proton 2.11",
    val audioDriver: String = "PulseAudio (TCP 4713)",
    val cpuAffinity: String = "All Cores (8 Cores)",
    val rootfsDistro: DebianDistro = DebianDistro.BOOKWORM,
    val openAtBoot: Boolean = true,
    val isRunning: Boolean = false
)

// Winlator Shortcut Model
data class WinlatorShortcut(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconType: WindowType,
    val containerId: String
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
    val colorDepth: String = "24-bit TrueColor",
    val openAtBoot: Boolean = true,
    val builtInMouseEnabled: Boolean = true,
    val box64Preset: String = "Performance (Fast)",
    val mouseSensitivity: Float = 1.2f
)

// PulseAudio Audio System Model
data class PulseAudioState(
    val isRunning: Boolean = true,
    val port: Int = 4713,
    val sampleRate: Int = 44100,
    val channels: Int = 2,
    val bufferLatencyMs: Int = 20,
    val volume: Float = 0.8f,
    val isMuted: Boolean = false,
    val sinkName: String = "auto_null / AudioTrack Sink",
    val clientCount: Int = 1,
    val isTestingSound: Boolean = false
)

// Vulkan Graphics System Model
data class VulkanState(
    val isEnabled: Boolean = true,
    val wrapperName: String = "Bionic Vulkan Wrapper (leegao / pipetto-crypto)",
    val driver: String = "Turnip (Adreno Open Source) + Zink",
    val apiVersion: String = "1.3.268",
    val dxvkVersion: String = "DXVK 2.3",
    val vkd3dVersion: String = "VKD3D-Proton 2.11",
    val gpuRenderer: String = "Qualcomm Adreno (TM) / ARM Mali",
    val extensionsCount: Int = 192,
    val isSupportedOnDevice: Boolean = true
)

// SSH Server Model
data class SshServerState(
    val isRunning: Boolean = false,
    val port: Int = 2222,
    val username: String = "debian",
    val hostFingerprint: String = "SHA256:4vQp2Zf9MbL... (ED25519)",
    val activeConnections: Int = 0,
    val logs: List<String> = emptyList()
)

// Docker / Podman Support Model
data class DockerState(
    val isRunning: Boolean = false,
    val engine: String = "Rootless Podman / Docker CLI",
    val socketPath: String = "/var/run/docker.sock",
    val containers: List<DockerContainerItem> = emptyList(),
    val images: List<DockerImageItem> = emptyList()
)

data class DockerContainerItem(
    val id: String,
    val name: String,
    val image: String,
    val status: String,
    val ports: String
)

data class DockerImageItem(
    val repository: String,
    val tag: String,
    val id: String,
    val sizeMb: Float
)

// Hardware & Network Diagnostics Model
data class HardwareInfo(
    val deviceModel: String,
    val manufacturer: String,
    val socModel: String,
    val cpuCores: Int,
    val cpuArch: String,
    val supportedAbis: String,
    val ramTotalMb: Long,
    val ramAvailMb: Long,
    val batteryPct: Int,
    val batteryTempC: Float,
    val isCharging: Boolean,
    val displayResolution: String,
    val refreshRateHz: Float,
    val sensorsCount: Int,
    val sensorList: List<String>
)

data class NetworkDiagnosticInfo(
    val isConnected: Boolean,
    val localIp: String,
    val gateway: String,
    val subnetMask: String,
    val wifiSsid: String,
    val linkSpeedMbps: Int,
    val dnsList: List<String>,
    val interfaces: List<String>
)

data class PingSession(
    val targetHost: String = "8.8.8.8",
    val isRunning: Boolean = false,
    val packetsSent: Int = 0,
    val packetsReceived: Int = 0,
    val minLatencyMs: Float = 0f,
    val avgLatencyMs: Float = 0f,
    val maxLatencyMs: Float = 0f,
    val consoleLines: List<String> = emptyList()
)

// Library Credits from GameNative / Winlator image
data class LibraryCredit(
    val name: String,
    val url: String,
    val purpose: String
)

val LIBRARIES_USED = listOf(
    LibraryCredit("Pluvia", "https://github.com/oxters168/Pluvia", "Fast native container management & execution bridge"),
    LibraryCredit("JavaSteam", "https://github.com/Longi94/JavaSteam", "Steam client protocol library in pure Java"),
    LibraryCredit("Winlator & Vortek", "https://github.com/brunodev85/winlator", "Android x86/ARM translation & direct Wine/Vulkan runtime"),
    LibraryCredit("Winlator Cmod", "https://github.com/coffincolors/winlator", "Enhanced graphics drivers, Turnip & audio patches"),
    LibraryCredit("Bionic Vulkan Wrapper", "https://github.com/leegao/bionic-vulkan-wrapper & pipetto-crypto", "Direct native Vulkan loader without bionic library collisions"),
    LibraryCredit("Ubuntu RootFs", "https://releases.ubuntu.com/focal", "Ubuntu 20.04 LTS (Focal Fossa) base root filesystem"),
    LibraryCredit("c-ares", "https://c-ares.org", "Asynchronous DNS query library & resolver"),
    LibraryCredit("PRoot", "https://proot-me.github.io", "User-space chroot/mount virtualizer without root access")
)
