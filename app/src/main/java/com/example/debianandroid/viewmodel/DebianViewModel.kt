package com.example.debianandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.debianandroid.model.Architecture
import com.example.debianandroid.model.ContainerStatus
import com.example.debianandroid.model.DebianDistro
import com.example.debianandroid.model.DebianPackage
import com.example.debianandroid.model.DebianSettings
import com.example.debianandroid.model.DesktopEnv
import com.example.debianandroid.model.DesktopWindow
import com.example.debianandroid.model.LineType
import com.example.debianandroid.model.ProcessInfo
import com.example.debianandroid.model.SystemMetrics
import com.example.debianandroid.model.TerminalLine
import com.example.debianandroid.model.WindowType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DebianViewModel : ViewModel() {

    private val _containerStatus = MutableStateFlow(ContainerStatus.STOPPED)
    val containerStatus: StateFlow<ContainerStatus> = _containerStatus.asStateFlow()

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

    private val _currentDirectory = MutableStateFlow("/home/debian")
    val currentDirectory: StateFlow<String> = _currentDirectory.asStateFlow()

    private val _editorContent = MutableStateFlow("#!/bin/bash\n# Debian Android automated bootstrap script\necho 'Welcome to Debian on Android'\necho 'PRoot virtualization initialized successfully.'\n")
    val editorContent: StateFlow<String> = _editorContent.asStateFlow()

    private val _processes = MutableStateFlow<List<ProcessInfo>>(emptyList())
    val processes: StateFlow<List<ProcessInfo>> = _processes.asStateFlow()

    init {
        initializePackages()
        initializeDefaultProcesses()
        addTerminalLine("Debian GNU/Linux compatibility layer for Android (PRoot + XSDL)", LineType.SYSTEM)
        addTerminalLine("Tap 'Launch Debian' or enter commands below. Type 'help' for available commands.", LineType.INFO)
    }

    private fun initializePackages() {
        val initialPackages = listOf(
            DebianPackage("gimp", "gimp", "2.8.14-1+b1", "Graphics", "GNU Image Manipulation Program (with fast-redraw rate patch)", 18.4f, true, false),
            DebianPackage("cares", "c-ares-utils", "1.18.1-3", "Networking", "Asynchronous DNS query library & CLI utilities (adig, ahost)", 1.2f, true, true),
            DebianPackage("build-essential", "build-essential", "12.9", "Development", "Informational list of build-essential packages (gcc, g++, make)", 42.0f, true, false),
            DebianPackage("git", "git", "2.39.2-1", "Development", "Fast, scalable, distributed revision control system", 28.5f, true, false),
            DebianPackage("python3", "python3", "3.11.2-1", "Development", "Interactive high-level object-oriented language (Python 3.11)", 22.1f, true, false),
            DebianPackage("htop", "htop", "3.2.2-1", "Utilities", "Interactive process viewer and system resource monitor", 0.9f, true, false),
            DebianPackage("tmux", "tmux", "3.3a-3", "Utilities", "Terminal multiplexer with split panes and background sessions", 1.8f, true, false),
            DebianPackage("curl", "curl", "7.88.1-10", "Utilities", "Command line tool for transferring data with URL syntax", 2.4f, true, true),
            DebianPackage("neofetch", "neofetch", "7.1.0-4", "Utilities", "Fast, highly customizable system information tool", 0.5f, true, false),
            DebianPackage("tightvncserver", "tightvncserver", "1.3.10-7", "Networking", "Virtual network computing server software for X11", 5.2f, false, false),
            DebianPackage("wbox", "wbox", "5-2", "Networking", "HTTP testing tool and lightweight local web server", 0.8f, false, false),
            DebianPackage("nodejs", "nodejs", "18.19.0-1", "Development", "Evented I/O for V8 JavaScript engine", 34.0f, false, false),
            DebianPackage("rustc", "rustc", "1.70.0+dfsg1", "Development", "Rust systems programming language compiler", 88.0f, false, false),
            DebianPackage("leafpad", "leafpad", "0.8.18.1-5", "Desktop", "GTK+ based simple and lightweight text editor", 1.5f, true, false),
            DebianPackage("thunar", "thunar", "4.18.4-1", "Desktop", "Fast and easy to use file manager for the Xfce Desktop", 8.4f, true, false),
            DebianPackage("inkscape", "inkscape", "1.2.2-2", "Graphics", "Vector-based drawing program using SVG standard", 65.0f, false, false),
            DebianPackage("nginx", "nginx", "1.22.1-9", "Networking", "Small, powerful, scalable web/reverse proxy server", 6.8f, false, false),
            DebianPackage("vim", "vim", "9.0.1378-2", "Utilities", "Vi IMproved - enhanced vi editor with syntax highlighting", 14.2f, false, false)
        )
        _packages.value = initialPackages
    }

    private fun initializeDefaultProcesses() {
        _processes.value = listOf(
            ProcessInfo(1, "root", 0.1f, 0.4f, "/init (proot init)"),
            ProcessInfo(24, "root", 0.0f, 0.2f, "/usr/bin/disableselinux-daemon"),
            ProcessInfo(42, "debian", 0.8f, 1.2f, "bash --login"),
            ProcessInfo(105, "debian", 2.4f, 4.8f, "/usr/bin/Xorg :0 -listen tcp"),
            ProcessInfo(112, "debian", 1.1f, 3.2f, "xfce4-session"),
            ProcessInfo(120, "debian", 0.5f, 2.1f, "xfwm4 --compositor=off"),
            ProcessInfo(134, "debian", 0.4f, 1.8f, "xfce4-panel")
        )
    }

    fun startContainer() {
        if (_containerStatus.value == ContainerStatus.RUNNING) return
        viewModelScope.launch {
            _containerStatus.value = ContainerStatus.STARTING
            addTerminalLine("[*] Initializing Debian rootfs container via PRoot...", LineType.SYSTEM)
            delay(400)
            addTerminalLine("[*] Architecture: ${_settings.value.architecture.tag} (${_settings.value.architecture.label})", LineType.INFO)
            delay(300)
            addTerminalLine("[*] Injecting libandroid-shmem-disableselinux.so for accelerated drawing", LineType.INFO)
            delay(350)
            addTerminalLine("[*] Mounting /proc, /sys, /dev, /storage/emulated/0 -> /sdcard", LineType.INFO)
            delay(300)
            addTerminalLine("[*] Initializing c-ares DNS resolver: ${_settings.value.dnsServer}, ${_settings.value.secondaryDns}", LineType.INFO)
            delay(400)
            addTerminalLine("[*] Launching XSDL display server (:0) at ${_settings.value.xsdlResolution}...", LineType.INFO)
            delay(350)
            addTerminalLine("[✔] Debian ${_settings.value.distro.codeName} started successfully!", LineType.SUCCESS)
            addTerminalLine("debian@localhost:~$ ", LineType.SYSTEM)
            _containerStatus.value = ContainerStatus.RUNNING

            // Start metrics tick
            startMetricsLoop()
        }
    }

    fun stopContainer() {
        if (_containerStatus.value == ContainerStatus.STOPPED) return
        viewModelScope.launch {
            _containerStatus.value = ContainerStatus.STOPPING
            addTerminalLine("[*] Sending SIGTERM to PRoot container processes...", LineType.SYSTEM)
            delay(400)
            addTerminalLine("[*] Unmounting virtual pseudo-filesystems and shmem buffers...", LineType.INFO)
            delay(300)
            addTerminalLine("[✔] Debian environment cleanly halted.", LineType.SUCCESS)
            _containerStatus.value = ContainerStatus.STOPPED
            _desktopWindows.value = emptyList()
            _activeWindowId.value = null
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

        // Record history
        _commandHistory.update { listOf(trimmed) + it.take(20) }

        val prompt = if (_containerStatus.value == ContainerStatus.RUNNING) "debian@localhost:~$ $trimmed" else "$ $trimmed"
        addTerminalLine(prompt, LineType.COMMAND)

        val parts = trimmed.split("\\s+".toRegex())
        val cmd = parts[0].lowercase()
        val args = parts.drop(1)

        when (cmd) {
            "help" -> {
                addTerminalLine("Debian on Android Shell Commands:", LineType.INFO)
                addTerminalLine("  help             - Show this help menu", LineType.OUTPUT)
                addTerminalLine("  neofetch         - Print Debian logo & system overview", LineType.OUTPUT)
                addTerminalLine("  apt update       - Refresh Debian package repositories", LineType.OUTPUT)
                addTerminalLine("  apt install <pkg>- Install package from repository", LineType.OUTPUT)
                addTerminalLine("  apt list         - List available/installed packages", LineType.OUTPUT)
                addTerminalLine("  uname -a         - Print kernel & architecture details", LineType.OUTPUT)
                addTerminalLine("  cat /etc/os-release - Print Debian release info", LineType.OUTPUT)
                addTerminalLine("  ps aux / top     - Show running processes in container", LineType.OUTPUT)
                addTerminalLine("  df -h / free -m  - Display disk space & memory usage", LineType.OUTPUT)
                addTerminalLine("  ls [-la] [path]  - List files and directories", LineType.OUTPUT)
                addTerminalLine("  cd [dir] / pwd   - Change or print working directory", LineType.OUTPUT)
                addTerminalLine("  whoami           - Print active username", LineType.OUTPUT)
                addTerminalLine("  gimp             - Launch GIMP graphics editor in Desktop GUI", LineType.OUTPUT)
                addTerminalLine("  clear            - Clear terminal display buffer", LineType.OUTPUT)
            }
            "clear" -> {
                _terminalLines.value = emptyList()
            }
            "uname", "uname -a" -> {
                addTerminalLine("Linux localhost 5.15.0-proot-debian #1 SMP PREEMPT ${_settings.value.architecture.tag} GNU/Linux", LineType.OUTPUT)
            }
            "whoami" -> {
                addTerminalLine(if (_settings.value.enableFakeRoot) "root" else "debian", LineType.OUTPUT)
            }
            "pwd" -> {
                addTerminalLine(_currentDirectory.value, LineType.OUTPUT)
            }
            "cd" -> {
                val target = args.firstOrNull() ?: "/home/debian"
                _currentDirectory.value = when (target) {
                    "~" -> "/home/debian"
                    ".." -> "/home"
                    "/" -> "/"
                    else -> if (target.startsWith("/")) target else "${_currentDirectory.value}/$target".replace("//", "/")
                }
                addTerminalLine("[dir changed to: ${_currentDirectory.value}]", LineType.INFO)
            }
            "ls" -> {
                val isDetailed = args.contains("-la") || args.contains("-l") || args.contains("-a")
                val dir = _currentDirectory.value
                if (isDetailed) {
                    addTerminalLine("total 48", LineType.OUTPUT)
                    addTerminalLine("drwxr-xr-x 8 debian debian 4096 Sep 27 16:30 .", LineType.OUTPUT)
                    addTerminalLine("drwxr-xr-x 3 root   root   4096 Sep 27 16:20 ..", LineType.OUTPUT)
                    addTerminalLine("-rw-r--r-- 1 debian debian  220 Sep 27 16:20 .bash_logout", LineType.OUTPUT)
                    addTerminalLine("-rw-r--r-- 1 debian debian 3526 Sep 27 16:20 .bashrc", LineType.OUTPUT)
                    addTerminalLine("-rw-r--r-- 1 debian debian  807 Sep 27 16:20 .profile", LineType.OUTPUT)
                    addTerminalLine("drwxr-xr-x 2 debian debian 4096 Sep 27 16:25 Desktop", LineType.OUTPUT)
                    addTerminalLine("drwxr-xr-x 2 debian debian 4096 Sep 27 16:25 Documents", LineType.OUTPUT)
                    addTerminalLine("drwxr-xr-x 2 debian debian 4096 Sep 27 16:25 Downloads", LineType.OUTPUT)
                    addTerminalLine("lrwxrwxrwx 1 debian debian   19 Sep 27 16:22 sdcard -> /storage/emulated/0", LineType.OUTPUT)
                } else {
                    addTerminalLine("Desktop   Documents   Downloads   sdcard   workspace.sh", LineType.OUTPUT)
                }
            }
            "cat" -> {
                val file = args.firstOrNull() ?: ""
                when {
                    file.contains("os-release") -> {
                        addTerminalLine("PRETTY_NAME=\"${_settings.value.distro.codeName}\"", LineType.OUTPUT)
                        addTerminalLine("NAME=\"Debian GNU/Linux\"", LineType.OUTPUT)
                        addTerminalLine("VERSION_ID=\"${_settings.value.distro.versionNumber}\"", LineType.OUTPUT)
                        addTerminalLine("VERSION=\"${_settings.value.distro.versionNumber} (${_settings.value.distro.name.lowercase()})\"", LineType.OUTPUT)
                        addTerminalLine("VERSION_CODENAME=${_settings.value.distro.name.lowercase()}", LineType.OUTPUT)
                        addTerminalLine("ID=debian", LineType.OUTPUT)
                        addTerminalLine("HOME_URL=\"https://www.debian.org/\"", LineType.OUTPUT)
                    }
                    file.contains("debian_version") -> {
                        addTerminalLine(_settings.value.distro.versionNumber, LineType.OUTPUT)
                    }
                    file.contains("sources.list") -> {
                        addTerminalLine("deb http://deb.debian.org/debian ${_settings.value.distro.name.lowercase()} main contrib non-free", LineType.OUTPUT)
                        addTerminalLine("deb http://security.debian.org/debian-security ${_settings.value.distro.name.lowercase()}-security main", LineType.OUTPUT)
                    }
                    else -> {
                        addTerminalLine("cat: $file: No such file or directory", LineType.ERROR)
                    }
                }
            }
            "neofetch" -> {
                printNeofetch()
            }
            "apt" -> {
                handleAptCommand(args)
            }
            "ps", "ps aux" -> {
                addTerminalLine("USER       PID %CPU %MEM    VSZ   RSS TTY      STAT START   TIME COMMAND", LineType.OUTPUT)
                _processes.value.forEach { p ->
                    addTerminalLine(String.format("%-8s %5d %4.1f %4.1f %6d %5d ?        S    16:30   0:01 %s", p.user, p.pid, p.cpuPercent, p.memPercent, 12400, 3200, p.command), LineType.OUTPUT)
                }
            }
            "top" -> {
                addTerminalLine("top - 16:35:00 up ${_metrics.value.uptimeSeconds}s, 1 user, load average: 0.14, 0.08, 0.03", LineType.OUTPUT)
                addTerminalLine("Tasks: 7 total, 1 running, 6 sleeping, 0 stopped, 0 zombie", LineType.OUTPUT)
                addTerminalLine("%Cpu(s): ${_metrics.value.cpuUsagePercent}.0 us, 2.1 sy, 0.0 ni, 83.9 id, 0.0 wa", LineType.OUTPUT)
                addTerminalLine("MiB Mem : ${_metrics.value.ramTotalMb}.0 total, ${_metrics.value.ramTotalMb - _metrics.value.ramUsedMb}.0 free, ${_metrics.value.ramUsedMb}.0 used", LineType.OUTPUT)
            }
            "free", "free -m" -> {
                addTerminalLine("               total        used        free      shared  buff/cache   available", LineType.OUTPUT)
                addTerminalLine("Mem:            4096         ${_metrics.value.ramUsedMb}        ${4096 - _metrics.value.ramUsedMb}          24         480        3500", LineType.OUTPUT)
                addTerminalLine("Swap:           2048           0        2048", LineType.OUTPUT)
            }
            "df", "df -h" -> {
                addTerminalLine("Filesystem      Size  Used Avail Use% Mounted on", LineType.OUTPUT)
                addTerminalLine("/dev/root        16G  ${_metrics.value.diskUsedGb}G   14G  10% /", LineType.OUTPUT)
                addTerminalLine("tmpfs           2.0G     0  2.0G   0% /dev/shm", LineType.OUTPUT)
                addTerminalLine("/sdcard          64G   18G   46G  28% /sdcard", LineType.OUTPUT)
                addTerminalLine("/storage/0       64G   18G   46G  28% /storage/emulated/0", LineType.OUTPUT)
            }
            "gimp" -> {
                openWindow(WindowType.GIMP, "GNU Image Manipulation Program")
                addTerminalLine("[*] Launching GIMP with redraw rate patch on :0 display...", LineType.INFO)
            }
            "date" -> {
                addTerminalLine(java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", java.util.Locale.US).format(java.util.Date()), LineType.OUTPUT)
            }
            "echo" -> {
                addTerminalLine(args.joinToString(" "), LineType.OUTPUT)
            }
            else -> {
                addTerminalLine("bash: $cmd: command not found. Type 'help' for available commands.", LineType.ERROR)
            }
        }
    }

    private fun handleAptCommand(args: List<String>) {
        if (args.isEmpty()) {
            addTerminalLine("apt: requires subcommand (update, install, remove, list)", LineType.ERROR)
            return
        }
        when (args[0]) {
            "update" -> {
                viewModelScope.launch {
                    addTerminalLine("Get:1 http://deb.debian.org/debian ${_settings.value.distro.name.lowercase()} InRelease [151 kB]", LineType.INFO)
                    delay(300)
                    addTerminalLine("Get:2 http://deb.debian.org/debian ${_settings.value.distro.name.lowercase()}-updates InRelease [52.1 kB]", LineType.INFO)
                    delay(300)
                    addTerminalLine("Get:3 http://security.debian.org/debian-security ${_settings.value.distro.name.lowercase()}-security InRelease [48.4 kB]", LineType.INFO)
                    delay(400)
                    addTerminalLine("Reading package lists... Done", LineType.OUTPUT)
                    addTerminalLine("Building dependency tree... Done", LineType.OUTPUT)
                    addTerminalLine("All packages are up to date.", LineType.SUCCESS)
                }
            }
            "install" -> {
                val pkgName = args.getOrNull(1)
                if (pkgName == null) {
                    addTerminalLine("apt install: missing package name", LineType.ERROR)
                    return
                }
                installPackage(pkgName)
            }
            "remove" -> {
                val pkgName = args.getOrNull(1)
                if (pkgName == null) {
                    addTerminalLine("apt remove: missing package name", LineType.ERROR)
                    return
                }
                uninstallPackage(pkgName)
            }
            "list" -> {
                addTerminalLine("Listing Debian packages (${_packages.value.size} items)...", LineType.INFO)
                _packages.value.forEach { p ->
                    val status = if (p.isInstalled) "[installed]" else "[available]"
                    addTerminalLine("${p.name}/${_settings.value.distro.name.lowercase()} ${p.version} ${_settings.value.architecture.tag} $status", LineType.OUTPUT)
                }
            }
            else -> {
                addTerminalLine("apt: unknown subcommand '${args[0]}'", LineType.ERROR)
            }
        }
    }

    fun installPackage(packageName: String) {
        viewModelScope.launch {
            addTerminalLine("Reading package lists... Done", LineType.INFO)
            addTerminalLine("Building dependency tree... Done", LineType.INFO)
            delay(300)
            addTerminalLine("The following NEW packages will be installed: $packageName", LineType.OUTPUT)
            addTerminalLine("0 upgraded, 1 newly installed, 0 to remove.", LineType.OUTPUT)
            addTerminalLine("Need to get archive and unpack: $packageName...", LineType.INFO)
            delay(500)
            addTerminalLine("Setting up $packageName ...", LineType.INFO)
            delay(300)
            addTerminalLine("[✔] Package '$packageName' installed successfully.", LineType.SUCCESS)

            _packages.update { list ->
                list.map { if (it.name.equals(packageName, ignoreCase = true) || it.id.equals(packageName, ignoreCase = true)) it.copy(isInstalled = true) else it }
            }
        }
    }

    fun uninstallPackage(packageName: String) {
        viewModelScope.launch {
            addTerminalLine("Removing $packageName ...", LineType.INFO)
            delay(400)
            addTerminalLine("[✔] Package '$packageName' removed.", LineType.SUCCESS)
            _packages.update { list ->
                list.map { if (it.name.equals(packageName, ignoreCase = true) || it.id.equals(packageName, ignoreCase = true)) it.copy(isInstalled = false) else it }
            }
        }
    }

    private fun printNeofetch() {
        val distro = _settings.value.distro.codeName
        val arch = _settings.value.architecture.tag
        val de = _settings.value.desktopEnv.displayName
        val uptime = "${_metrics.value.uptimeSeconds / 60}m ${_metrics.value.uptimeSeconds % 60}s"

        addTerminalLine("       _,met\$\$\$\$\$gg.          debian@localhost", LineType.OUTPUT)
        addTerminalLine("    ,g\$\$\$\$\$\$\$\$\$\$\$\$\$\$\$P.       ----------------", LineType.OUTPUT)
        addTerminalLine("  ,g\$\$P\"\"       \"\"\"Y\$\$.\"\"g.    OS: $distro on Android (PRoot)", LineType.OUTPUT)
        addTerminalLine(" ,\$\$P'              `\$\$\$.     Host: Android Compatibility Layer", LineType.OUTPUT)
        addTerminalLine("',\$\$P       ,ggs.     `\$\$b:   Kernel: 5.15.0-proot-android ($arch)", LineType.OUTPUT)
        addTerminalLine("d\$\$'     ,\$P\"'   .    \$\$\$     Uptime: $uptime", LineType.OUTPUT)
        addTerminalLine("\$\$\$P      d\$\$'     ,    \$\$\$P    Packages: ${_packages.value.count { it.isInstalled }} (dpkg)", LineType.OUTPUT)
        addTerminalLine("\$\$\$b:     \$\$.d\$'         \$\$\$     Shell: bash 5.2.15", LineType.OUTPUT)
        addTerminalLine("Y\$\$\$.    '\"            ,\$\$\$'    DE: $de (XSDL X11 :0)", LineType.OUTPUT)
        addTerminalLine(" `\$\$b.          _.-  ,\$\$\$'     WM: xfwm4 / openbox", LineType.OUTPUT)
        addTerminalLine("  `\"Y\$\$b..____,g\$\$\$\$\$\"\"'       CPU: Virtualized AArch64 (8) @ 2.8GHz", LineType.OUTPUT)
        addTerminalLine("      `\"\"\"\"\"\"\"\"'               Memory: ${_metrics.value.ramUsedMb}MiB / ${_metrics.value.ramTotalMb}MiB", LineType.OUTPUT)
    }

    private fun addTerminalLine(text: String, type: LineType) {
        _terminalLines.update { (it + TerminalLine(text = text, type = type)).takeLast(200) }
    }

    fun openWindow(type: WindowType, title: String) {
        val existing = _desktopWindows.value.find { it.type == type }
        if (existing != null) {
            _activeWindowId.value = existing.id
            if (existing.isMinimized) {
                _desktopWindows.update { list -> list.map { if (it.id == existing.id) it.copy(isMinimized = false) else it } }
            }
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

    fun toggleMinimizeWindow(windowId: String) {
        _desktopWindows.update { list ->
            list.map { if (it.id == windowId) it.copy(isMinimized = !it.isMinimized) else it }
        }
    }

    fun updateEditorContent(content: String) {
        _editorContent.value = content
    }

    fun updateSettings(newSettings: DebianSettings) {
        _settings.value = newSettings
    }
}
