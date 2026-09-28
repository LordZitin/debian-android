package com.example.debianandroid.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.debianandroid.audio.PulseAudioManager
import com.example.debianandroid.hardware.HardwareNetworkManager
import com.example.debianandroid.model.Architecture
import com.example.debianandroid.model.ContainerStatus
import com.example.debianandroid.model.DebianDistro
import com.example.debianandroid.model.DebianPackage
import com.example.debianandroid.model.DebianSettings
import com.example.debianandroid.model.DesktopEnv
import com.example.debianandroid.model.DesktopWindow
import com.example.debianandroid.model.DockerContainerItem
import com.example.debianandroid.model.DockerImageItem
import com.example.debianandroid.model.DockerState
import com.example.debianandroid.model.HardwareInfo
import com.example.debianandroid.model.LineType
import com.example.debianandroid.model.NetworkDiagnosticInfo
import com.example.debianandroid.model.PingSession
import com.example.debianandroid.model.ProcessInfo
import com.example.debianandroid.model.PulseAudioState
import com.example.debianandroid.model.SshServerState
import com.example.debianandroid.model.SystemMetrics
import com.example.debianandroid.model.TerminalLine
import com.example.debianandroid.model.VulkanState
import com.example.debianandroid.model.WindowType
import com.example.debianandroid.model.WinlatorContainer
import com.example.debianandroid.model.WinlatorShortcut
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DebianViewModel(application: Application) : AndroidViewModel(application) {

    private val hardwareManager = HardwareNetworkManager(application)

    private val _containerStatus = MutableStateFlow(ContainerStatus.STOPPED)
    val containerStatus: StateFlow<ContainerStatus> = _containerStatus.asStateFlow()

    private val _isDesktopActive = MutableStateFlow(false)
    val isDesktopActive: StateFlow<Boolean> = _isDesktopActive.asStateFlow()

    private val _metrics = MutableStateFlow(SystemMetrics())
    val metrics: StateFlow<SystemMetrics> = _metrics.asStateFlow()

    private val _terminalLines = MutableStateFlow<List<TerminalLine>>(emptyList())
    val terminalLines: StateFlow<List<TerminalLine>> = _terminalLines.asStateFlow()

    private val _commandHistory = MutableStateFlow<List<String>>(emptyList())
    val commandHistory: StateFlow<List<String>> = _commandHistory.asStateFlow()

    private val _packages = MutableStateFlow<List<DebianPackage>>(emptyList())
    val packages: StateFlow<List<DebianPackage>> = _packages.asStateFlow()

    private val _desktopWindows = MutableStateFlow<List<DesktopWindow>>(emptyList())
    val desktopWindows: StateFlow<List<DesktopWindow>> = _desktopWindows.asStateFlow()

    private val _activeWindowId = MutableStateFlow<String?>(null)
    val activeWindowId: StateFlow<String?> = _activeWindowId.asStateFlow()

    private val _settings = MutableStateFlow(DebianSettings())
    val settings: StateFlow<DebianSettings> = _settings.asStateFlow()

    // Winlator Containers List
    private val _containers = MutableStateFlow<List<WinlatorContainer>>(
        listOf(
            WinlatorContainer(
                id = "container-1",
                name = "Container 1 (Debian 12)",
                screenSize = "1280x720 (16:9)",
                graphicsDriver = "Turnip (Adreno) + Zink",
                dxvkVersion = "DXVK 2.3",
                vkd3dVersion = "VKD3D-Proton 2.11",
                audioDriver = "PulseAudio (TCP 4713)",
                cpuAffinity = "All Cores (8 Cores)",
                rootfsDistro = DebianDistro.BOOKWORM,
                openAtBoot = true,
                isRunning = false
            ),
            WinlatorContainer(
                id = "container-2",
                name = "Container 2 (Ubuntu Focal)",
                screenSize = "1920x1080 (16:9)",
                graphicsDriver = "Turnip + Zink Vulkan",
                dxvkVersion = "DXVK 2.3",
                vkd3dVersion = "VKD3D-Proton 2.11",
                audioDriver = "PulseAudio (TCP 4713)",
                cpuAffinity = "Performance Cores",
                rootfsDistro = DebianDistro.UBUNTU_FOCAL,
                openAtBoot = false,
                isRunning = false
            )
        )
    )
    val containers: StateFlow<List<WinlatorContainer>> = _containers.asStateFlow()

    // Winlator Shortcuts
    private val _shortcuts = MutableStateFlow<List<WinlatorShortcut>>(
        listOf(
            WinlatorShortcut("sc-1", "GIMP 2.8", "GNU Image Manipulation", WindowType.GIMP, "container-1"),
            WinlatorShortcut("sc-2", "vkcube Vulkan", "Hardware 3D Gears", WindowType.VULKAN_GEARS, "container-1"),
            WinlatorShortcut("sc-3", "Bash Shell", "Terminal Console", WindowType.TERMINAL, "container-1"),
            WinlatorShortcut("sc-4", "Leafpad", "Text Editor", WindowType.EDITOR, "container-1"),
            WinlatorShortcut("sc-5", "Task Manager", "Processes & Resources", WindowType.MONITOR, "container-1"),
            WinlatorShortcut("sc-6", "Thunar", "File Manager", WindowType.FILES, "container-1")
        )
    )
    val shortcuts: StateFlow<List<WinlatorShortcut>> = _shortcuts.asStateFlow()

    private val _currentDirectory = MutableStateFlow("/home/debian")
    val currentDirectory: StateFlow<String> = _currentDirectory.asStateFlow()

    private val _editorContent = MutableStateFlow("#!/bin/bash\n# Winlator & Debian Android bootstrap\necho 'Winlator Desktop Environment Initialized'\necho 'Built-in Mouse active on right half of screen'\n")
    val editorContent: StateFlow<String> = _editorContent.asStateFlow()

    private val _processes = MutableStateFlow<List<ProcessInfo>>(emptyList())
    val processes: StateFlow<List<ProcessInfo>> = _processes.asStateFlow()

    // PulseAudio State
    private val _pulseAudio = MutableStateFlow(PulseAudioState())
    val pulseAudio: StateFlow<PulseAudioState> = _pulseAudio.asStateFlow()

    // Vulkan State
    private val _vulkan = MutableStateFlow(VulkanState())
    val vulkan: StateFlow<VulkanState> = _vulkan.asStateFlow()

    // SSH Server State
    private val _sshServer = MutableStateFlow(SshServerState())
    val sshServer: StateFlow<SshServerState> = _sshServer.asStateFlow()

    // Docker Support State
    private val _docker = MutableStateFlow(
        DockerState(
            containers = listOf(
                DockerContainerItem("c102a", "web-nginx", "nginx:alpine", "Up 14 minutes", "0.0.0.0:80->80/tcp"),
                DockerContainerItem("d554f", "redis-cache", "redis:7-alpine", "Up 2 hours", "0.0.0.0:6379->6379/tcp")
            ),
            images = listOf(
                DockerImageItem("debian", "bookworm-slim", "deb8140", 112.4f),
                DockerImageItem("ubuntu", "focal", "ub2004", 72.8f),
                DockerImageItem("nginx", "alpine", "ng3401", 23.5f)
            )
        )
    )
    val docker: StateFlow<DockerState> = _docker.asStateFlow()

    // Hardware & Network State
    private val _hardwareInfo = MutableStateFlow<HardwareInfo?>(null)
    val hardwareInfo: StateFlow<HardwareInfo?> = _hardwareInfo.asStateFlow()

    private val _networkInfo = MutableStateFlow<NetworkDiagnosticInfo?>(null)
    val networkInfo: StateFlow<NetworkDiagnosticInfo?> = _networkInfo.asStateFlow()

    private val _pingSession = MutableStateFlow(PingSession())
    val pingSession: StateFlow<PingSession> = _pingSession.asStateFlow()

    // Controller Feedback overlay
    private val _lastInputFeedback = MutableStateFlow<String?>(null)
    val lastInputFeedback: StateFlow<String?> = _lastInputFeedback.asStateFlow()

    init {
        initializePackages()
        initializeDefaultProcesses()
        refreshHardwareAndNetwork()

        addTerminalLine("Winlator compatibility engine (PRoot + Vulkan + PulseAudio)", LineType.SYSTEM)
        addTerminalLine("Built-in mouse enabled on right side of screen; invisible D-pad on left.", LineType.INFO)

        // Open at boot requirement:
        // Automatically start Container 1 and open Desktop with built-in mouse on launch!
        if (_settings.value.openAtBoot) {
            startContainer("container-1", openDesktopImmediately = true)
        }
    }

    fun refreshHardwareAndNetwork() {
        viewModelScope.launch {
            _hardwareInfo.value = hardwareManager.getHardwareInfo()
            _networkInfo.value = hardwareManager.getNetworkInfo()
        }
    }

    private fun initializePackages() {
        val initialPackages = listOf(
            DebianPackage("pulseaudio", "pulseaudio", "16.1+dfsg1-2", "Audio", "PulseAudio sound server daemon with TCP socket sink", 5.8f, true, true),
            DebianPackage("vulkan-tools", "vulkan-tools", "1.3.239.0", "Graphics", "Vulkan utilities, vkcube, vulkaninfo diagnostics", 3.2f, true, true),
            DebianPackage("openssh-server", "openssh-server", "1:9.2p1-2", "Networking", "OpenSSH remote access daemon for PRoot", 4.1f, true, true),
            DebianPackage("docker-ce-cli", "docker-ce-cli", "24.0.7-1", "Development", "Docker CLI for Rootless Podman and containerd", 14.5f, true, false),
            DebianPackage("gimp", "gimp", "2.8.14-1+b1", "Graphics", "GNU Image Manipulation Program (with fast-redraw rate patch)", 18.4f, true, false),
            DebianPackage("c-ares-utils", "c-ares-utils", "1.18.1-3", "Networking", "Asynchronous DNS query library & CLI utilities (adig, ahost)", 1.2f, true, true),
            DebianPackage("build-essential", "build-essential", "12.9", "Development", "Informational list of build-essential packages (gcc, g++, make)", 42.0f, true, false),
            DebianPackage("git", "git", "2.39.2-1", "Development", "Fast, scalable, distributed revision control system", 28.5f, true, false),
            DebianPackage("python3", "python3", "3.11.2-1", "Development", "Interactive high-level object-oriented language (Python 3.11)", 22.1f, true, false),
            DebianPackage("htop", "htop", "3.2.2-1", "Utilities", "Interactive process viewer and system resource monitor", 0.9f, true, false),
            DebianPackage("tmux", "tmux", "3.3a-3", "Utilities", "Terminal multiplexer with split panes and background sessions", 1.8f, true, false),
            DebianPackage("curl", "curl", "7.88.1-10", "Utilities", "Command line tool for transferring data with URL syntax", 2.4f, true, true),
            DebianPackage("neofetch", "neofetch", "7.1.0-4", "Utilities", "Fast, highly customizable system information tool", 0.5f, true, false),
            DebianPackage("iputils-ping", "iputils-ping", "3:20221126-1", "Networking", "Clear network ICMP ping utility for Debian", 0.6f, true, true),
            DebianPackage("nmap", "nmap", "7.93+dfsg1-1", "Networking", "Network exploration tool and security / port scanner", 8.4f, false, false),
            DebianPackage("leafpad", "leafpad", "0.8.18.1-5", "Desktop", "GTK+ based simple and lightweight text editor", 1.5f, true, false),
            DebianPackage("thunar", "thunar", "4.18.4-1", "Desktop", "Fast and easy to use file manager for the Xfce Desktop", 8.4f, true, false)
        )
        _packages.value = initialPackages
    }

    private fun initializeDefaultProcesses() {
        _processes.value = listOf(
            ProcessInfo(1, "root", 0.1f, 0.4f, "/init (proot init)"),
            ProcessInfo(24, "root", 0.0f, 0.2f, "/usr/bin/disableselinux-daemon"),
            ProcessInfo(42, "debian", 0.8f, 1.2f, "bash --login"),
            ProcessInfo(88, "debian", 0.3f, 1.1f, "pulseaudio --start --exit-idle-time=-1"),
            ProcessInfo(105, "debian", 2.4f, 4.8f, "/usr/bin/Xorg :0 -listen tcp"),
            ProcessInfo(112, "debian", 1.1f, 3.2f, "xfce4-session"),
            ProcessInfo(134, "debian", 0.4f, 1.8f, "xfce4-panel")
        )
    }

    fun startContainer(containerId: String = "container-1", openDesktopImmediately: Boolean = true) {
        if (_containerStatus.value == ContainerStatus.RUNNING) {
            if (openDesktopImmediately) _isDesktopActive.value = true
            return
        }
        viewModelScope.launch {
            _containerStatus.value = ContainerStatus.STARTING
            addTerminalLine("[*] Winlator: Booting $containerId...", LineType.SYSTEM)
            delay(250)
            addTerminalLine("[*] Initializing Vulkan (Turnip + Zink) via bionic-vulkan-wrapper", LineType.INFO)
            delay(250)
            addTerminalLine("[*] Starting PulseAudio sound server on 127.0.0.1:4713", LineType.INFO)
            delay(250)
            addTerminalLine("[*] Built-in touch mouse active on right screen; D-pad joystick on left", LineType.INFO)
            delay(250)
            addTerminalLine("[✔] Winlator Container $containerId started successfully!", LineType.SUCCESS)
            _containerStatus.value = ContainerStatus.RUNNING

            _containers.update { list ->
                list.map { it.copy(isRunning = it.id == containerId) }
            }

            if (openDesktopImmediately) {
                _isDesktopActive.value = true
            }
            startMetricsLoop()
        }
    }

    fun stopContainer() {
        if (_containerStatus.value == ContainerStatus.STOPPED) return
        viewModelScope.launch {
            _containerStatus.value = ContainerStatus.STOPPING
            addTerminalLine("[*] Stopping Winlator container processes...", LineType.SYSTEM)
            delay(300)
            addTerminalLine("[✔] Container cleanly stopped.", LineType.SUCCESS)
            _containerStatus.value = ContainerStatus.STOPPED
            _isDesktopActive.value = false
            _containers.update { list -> list.map { it.copy(isRunning = false) } }
            _desktopWindows.value = emptyList()
            _activeWindowId.value = null
        }
    }

    fun exitDesktopToManager() {
        _isDesktopActive.value = false
    }

    fun enterDesktop() {
        if (_containerStatus.value != ContainerStatus.RUNNING) {
            startContainer(openDesktopImmediately = true)
        } else {
            _isDesktopActive.value = true
        }
    }

    fun addContainer(name: String, distro: DebianDistro, res: String, driver: String) {
        val newId = "container-${_containers.value.size + 1}"
        val newCont = WinlatorContainer(
            id = newId,
            name = name,
            screenSize = res,
            graphicsDriver = driver,
            rootfsDistro = distro,
            openAtBoot = false,
            isRunning = false
        )
        _containers.update { it + newCont }
    }

    fun deleteContainer(id: String) {
        _containers.update { list -> list.filterNot { it.id == id } }
    }

    fun launchShortcut(shortcut: WinlatorShortcut) {
        if (_containerStatus.value != ContainerStatus.RUNNING) {
            startContainer(shortcut.containerId, openDesktopImmediately = true)
        } else {
            _isDesktopActive.value = true
        }
        openWindow(shortcut.iconType, shortcut.title)
    }

    // Audio / PulseAudio actions
    fun togglePulseAudio() {
        val willRun = !_pulseAudio.value.isRunning
        _pulseAudio.update { it.copy(isRunning = willRun) }
        addTerminalLine(
            if (willRun) "[*] PulseAudio server started on tcp:127.0.0.1:4713 (AudioTrack Sink active)"
            else "[*] PulseAudio server stopped.",
            LineType.INFO
        )
    }

    fun setAudioVolume(volume: Float) {
        _pulseAudio.update { it.copy(volume = volume, isMuted = volume == 0f) }
    }

    fun toggleAudioMute() {
        _pulseAudio.update { it.copy(isMuted = !it.isMuted) }
    }

    fun playPulseAudioTestSound() {
        viewModelScope.launch {
            _pulseAudio.update { it.copy(isTestingSound = true) }
            addTerminalLine("[*] PulseAudio: paplay chime.wav -> AudioTrack", LineType.INFO)
            val success = PulseAudioManager.playTestSound(
                sampleRate = _pulseAudio.value.sampleRate,
                volume = if (_pulseAudio.value.isMuted) 0f else _pulseAudio.value.volume
            )
            _pulseAudio.update { it.copy(isTestingSound = false) }
            if (success) {
                addTerminalLine("[✔] Audio playback verified via AudioTrack.", LineType.SUCCESS)
            }
        }
    }

    // Vulkan actions
    fun toggleVulkan(enabled: Boolean) {
        _vulkan.update { it.copy(isEnabled = enabled) }
        addTerminalLine(
            if (enabled) "[*] Vulkan hardware acceleration enabled with Bionic Vulkan Wrapper."
            else "[*] Vulkan acceleration disabled.",
            LineType.INFO
        )
    }

    fun setVulkanDriver(driver: String) {
        _vulkan.update { it.copy(driver = driver) }
        addTerminalLine("[*] Vulkan driver switched to: $driver", LineType.INFO)
    }

    // SSH Server actions
    fun toggleSshServer() {
        val willRun = !_sshServer.value.isRunning
        val localIp = _networkInfo.value?.localIp ?: "127.0.0.1"
        _sshServer.update {
            it.copy(
                isRunning = willRun,
                activeConnections = if (willRun) 1 else 0
            )
        }
        addTerminalLine(
            if (willRun) "[*] OpenSSH server running on port ${_sshServer.value.port}. Connect with: ssh -p ${_sshServer.value.port} debian@$localIp"
            else "[*] OpenSSH server stopped.",
            LineType.INFO
        )
    }

    // Docker actions
    fun toggleDocker() {
        val willRun = !_docker.value.isRunning
        _docker.update { it.copy(isRunning = willRun) }
        addTerminalLine(
            if (willRun) "[*] Rootless Podman/Docker socket active at ${_docker.value.socketPath}"
            else "[*] Docker engine stopped.",
            LineType.INFO
        )
    }

    fun runDockerContainer(imageName: String) {
        viewModelScope.launch {
            addTerminalLine("docker run -d $imageName", LineType.COMMAND)
            delay(400)
            val newId = (1000..9999).random().toString(16)
            _docker.update {
                it.copy(
                    containers = it.containers + DockerContainerItem(
                        id = newId,
                        name = "app-$newId",
                        image = imageName,
                        status = "Up Just now",
                        ports = "0.0.0.0:808${(1..9).random()}->80"
                    )
                )
            }
            addTerminalLine("[✔] Container $newId started from $imageName", LineType.SUCCESS)
        }
    }

    // Ping & Network Diagnostics
    fun startPing(host: String) {
        viewModelScope.launch {
            _pingSession.update {
                it.copy(
                    targetHost = host,
                    isRunning = true,
                    consoleLines = listOf("PING $host: 56 data bytes")
                )
            }
            val stats = hardwareManager.runPing(host, count = 4) { line ->
                _pingSession.update { it.copy(consoleLines = it.consoleLines + line) }
            }
            _pingSession.update {
                it.copy(
                    isRunning = false,
                    packetsSent = 4,
                    packetsReceived = 4,
                    minLatencyMs = stats.first,
                    avgLatencyMs = stats.second,
                    maxLatencyMs = stats.third
                )
            }
        }
    }

    // Input Controller Dispatcher (Landscape Split-Touch Controller)
    fun sendKeybinding(key: String) {
        val displayFeedback = when (key) {
            "ENTER" -> "⏎ Enter Key"
            "ESC" -> "⎋ Esc Key"
            "UP" -> "↑ D-Pad Up"
            "DOWN" -> "↓ D-Pad Down"
            "LEFT" -> "← D-Pad Left"
            "RIGHT" -> "→ D-Pad Right"
            else -> "$key Pressed"
        }
        _lastInputFeedback.value = displayFeedback
        viewModelScope.launch {
            delay(1200)
            if (_lastInputFeedback.value == displayFeedback) {
                _lastInputFeedback.value = null
            }
        }
    }

    fun sendMouseClick(type: String) {
        val displayFeedback = when (type) {
            "LEFT" -> "🖱 Left Click"
            "DOUBLE" -> "🖱🖱 Double Click"
            "RIGHT" -> "🖱 Right Click (Hold)"
            else -> "Mouse Click"
        }
        _lastInputFeedback.value = displayFeedback
        viewModelScope.launch {
            delay(1200)
            if (_lastInputFeedback.value == displayFeedback) {
                _lastInputFeedback.value = null
            }
        }
    }

    private fun startMetricsLoop() {
        viewModelScope.launch {
            while (_containerStatus.value == ContainerStatus.RUNNING) {
                delay(2000)
                _metrics.update { current ->
                    val cpuJitter = (8..28).random()
                    val ramJitter = (320..440).random()
                    current.copy(
                        cpuUsagePercent = cpuJitter,
                        ramUsedMb = ramJitter,
                        uptimeSeconds = current.uptimeSeconds + 2
                    )
                }
            }
        }
    }

    fun executeCommand(commandInput: String) {
        val trimmed = commandInput.trim()
        if (trimmed.isEmpty()) return

        _commandHistory.update { listOf(trimmed) + it.take(20) }
        val prompt = if (_containerStatus.value == ContainerStatus.RUNNING) "debian@localhost:~$ $trimmed" else "$ $trimmed"
        addTerminalLine(prompt, LineType.COMMAND)

        val parts = trimmed.split("\\s+".toRegex())
        val cmd = parts[0].lowercase()
        val args = parts.drop(1)

        when (cmd) {
            "help" -> {
                addTerminalLine("Winlator & Debian Shell Commands:", LineType.INFO)
                addTerminalLine("  ping <host>         - Ping a host (ICMP socket)", LineType.OUTPUT)
                addTerminalLine("  pulseaudio --start  - Start PulseAudio audio server", LineType.OUTPUT)
                addTerminalLine("  vulkaninfo          - Print Vulkan GPU API details", LineType.OUTPUT)
                addTerminalLine("  neofetch            - Host info & ASCII art", LineType.OUTPUT)
                addTerminalLine("  termux-info         - Dump Android hardware details", LineType.OUTPUT)
                addTerminalLine("  ip a / ifconfig     - List network interfaces", LineType.OUTPUT)
                addTerminalLine("  docker ps           - List active Docker containers", LineType.OUTPUT)
                addTerminalLine("  clear               - Clear terminal screen", LineType.OUTPUT)
            }
            "ping" -> {
                val host = args.firstOrNull() ?: "8.8.8.8"
                startPing(host)
            }
            "termux-info", "hardware" -> {
                val hw = _hardwareInfo.value ?: hardwareManager.getHardwareInfo()
                addTerminalLine("Device: ${hw.deviceModel} (${hw.manufacturer})", LineType.INFO)
                addTerminalLine("SoC: ${hw.socModel} | CPU: ${hw.cpuCores} cores (${hw.cpuArch})", LineType.OUTPUT)
                addTerminalLine("RAM: ${hw.ramAvailMb} MB free / ${hw.ramTotalMb} MB total", LineType.OUTPUT)
                addTerminalLine("Battery: ${hw.batteryPct}% (${hw.batteryTempC}°C)", LineType.OUTPUT)
            }
            "vulkaninfo" -> {
                val vk = _vulkan.value
                addTerminalLine("Vulkan API: ${vk.apiVersion} | Driver: ${vk.driver}", LineType.OUTPUT)
                addTerminalLine("Extensions: ${vk.extensionsCount} | DXVK: ${vk.dxvkVersion}", LineType.OUTPUT)
            }
            "clear" -> {
                _terminalLines.value = emptyList()
            }
            else -> {
                addTerminalLine("bash: $cmd: command not found. Type 'help' for commands.", LineType.ERROR)
            }
        }
    }

    fun installPackage(packageName: String) {
        viewModelScope.launch {
            addTerminalLine("Setting up $packageName ...", LineType.INFO)
            delay(300)
            addTerminalLine("[✔] Package '$packageName' installed successfully.", LineType.SUCCESS)
            _packages.update { list ->
                list.map { if (it.name.equals(packageName, ignoreCase = true)) it.copy(isInstalled = true) else it }
            }
        }
    }

    fun uninstallPackage(packageName: String) {
        viewModelScope.launch {
            addTerminalLine("Removing $packageName ...", LineType.INFO)
            delay(300)
            addTerminalLine("[✔] Package '$packageName' removed.", LineType.SUCCESS)
            _packages.update { list ->
                list.map { if (it.name.equals(packageName, ignoreCase = true)) it.copy(isInstalled = false) else it }
            }
        }
    }

    fun openWindow(type: WindowType, title: String) {
        val existing = _desktopWindows.value.find { it.type == type }
        if (existing != null) {
            _activeWindowId.value = existing.id
            return
        }
        val newWindow = DesktopWindow(title = title, type = type)
        _desktopWindows.update { it + newWindow }
        _activeWindowId.value = newWindow.id
    }

    fun closeWindow(windowId: String) {
        _desktopWindows.update { list -> list.filterNot { it.id == windowId } }
        if (_activeWindowId.value == windowId) {
            _activeWindowId.value = _desktopWindows.value.lastOrNull()?.id
        }
    }

    fun updateEditorContent(content: String) {
        _editorContent.value = content
    }

    fun updateSettings(newSettings: DebianSettings) {
        _settings.value = newSettings
    }

    fun addTerminalLine(text: String, type: LineType = LineType.OUTPUT) {
        val newLine = TerminalLine(text = text, type = type)
        _terminalLines.update { (it + newLine).takeLast(200) }
    }

    fun toggleContainerOpenAtBoot(id: String) {
        _containers.update { list ->
            list.map { it.copy(openAtBoot = if (it.id == id) !it.openAtBoot else it.openAtBoot) }
        }
    }
}
