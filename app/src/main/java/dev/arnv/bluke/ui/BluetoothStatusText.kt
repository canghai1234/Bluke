package dev.arnv.bluke.ui

/**
 * Bluetooth manager messages are kept in English internally because the connection
 * state policy uses stable message fragments. Translate them only at the UI edge.
 */
internal fun localizedBluetoothStatus(message: String): String = when {
    message == "Initializing Bluetooth Controller..." -> "正在初始化蓝牙控制器……"
    message.contains("Bluetooth HID profile is not supported", ignoreCase = true) ->
        "此设备固件不支持蓝牙 HID 配置文件。"
    message.contains("Bluetooth is not supported on this device", ignoreCase = true) ->
        "此设备硬件不支持蓝牙。"
    message.contains("Bluetooth is currently turned off", ignoreCase = true) ->
        "蓝牙当前已关闭，请开启蓝牙。"
    message.contains("permissions are required", ignoreCase = true) ->
        "需要蓝牙连接、广播和扫描权限。"
    message.contains("Location permission is required", ignoreCase = true) ->
        "Android 10 及更早版本需要位置权限才能使用蓝牙。"
    message.contains("appears incompatible", ignoreCase = true) ->
        "此设备可能与蓝牙 HID Device 角色不兼容。"
    message.contains("Scanning for other Bluetooth hosts", ignoreCase = true) ->
        "正在扫描其他蓝牙主机……"
    message.contains("Failed to start Bluetooth discovery", ignoreCase = true) ->
        "无法启动蓝牙设备扫描。"
    message.contains("Bluetooth recovery is in progress", ignoreCase = true) ->
        "正在恢复蓝牙连接，完成后即可连接。"
    message.contains("Waiting for Bluetooth HID service", ignoreCase = true) ->
        "正在等待蓝牙 HID 服务，稍后将尝试连接。"
    message.contains("Android could not start the connection", ignoreCase = true) ->
        "Android 无法启动连接，请重试或切换蓝牙。"
    message.contains("Previous host has not disconnected", ignoreCase = true) ->
        "上一个主机尚未断开，请重试连接。"
    message.contains("Connection did not complete", ignoreCase = true) ->
        "多次尝试后仍未完成连接，请重试。"
    message.contains("Disconnecting physical link", ignoreCase = true) ->
        "正在断开物理连接……"
    message.contains("did not disconnect", ignoreCase = true) ->
        "设备未能断开，请重试断开或重启 HID。"
    message.contains("Disconnect failed", ignoreCase = true) ->
        "断开连接失败，请重试或重启 HID。"
    message.contains("Connecting to HID service", ignoreCase = true) ->
        "正在连接 HID 服务……"
    message.contains("Bluetooth HID connection interrupted", ignoreCase = true) ->
        "蓝牙 HID 连接中断，请重试连接或切换蓝牙。"
    message.contains("Rechecking Bluetooth HID Device compatibility", ignoreCase = true) ->
        "正在重新检查蓝牙 HID Device 兼容性……"
    message.contains("Bluetooth HID service did not respond", ignoreCase = true) ->
        "蓝牙 HID 服务未响应，请尝试切换蓝牙。"
    message.contains("HID Service Proxy disconnected", ignoreCase = true) ->
        "HID 服务代理已断开，正在重新绑定……"
    message.contains("Custom HID Deck is ready", ignoreCase = true) ->
        "自定义 HID 控制台已就绪并开始广播。"
    message.startsWith("Link established with '", ignoreCase = true) -> {
        val hostName = message.substringAfter("'").substringBefore("'")
        "已与“${hostName.ifBlank { "主机" }}”建立连接，键盘已启用。"
    }
    message.contains("HID registration was lost", ignoreCase = true) ->
        "HID 注册已丢失，请点击“重启 HID 服务”恢复。"
    message.contains("incoming connection did not complete", ignoreCase = true) ->
        "传入连接未完成，请查看下方连接帮助。"
    message.contains("Checking Bluetooth HID Device compatibility", ignoreCase = true) ->
        "正在检查蓝牙 HID Device 兼容性……"
    message.contains("HID recovery failed", ignoreCase = true) ->
        "HID 恢复失败，请重试或切换蓝牙。"
    message.contains("Restarting local HID Service", ignoreCase = true) ->
        "正在重启本地 HID 服务……"
    message.contains("host did not disconnect for recovery", ignoreCase = true) ->
        "主机未为恢复操作断开连接，请重试或切换蓝牙。"
    else -> message
}

internal fun localizedGamepadModeSwitchError(message: String): String = when {
    message.contains("previous connection did not disconnect", ignoreCase = true) ->
        "上一个连接尚未断开，请先断开主机后重试。"
    message.contains("registered, but reconnect failed", ignoreCase = true) ->
        "手柄模式已注册，但重新连接失败，请重试连接主机。"
    message.contains("mode change was interrupted", ignoreCase = true) ->
        "手柄模式切换被中断，请检查蓝牙并重新连接。"
    message.contains("mode could not be changed", ignoreCase = true) ->
        "无法切换手柄模式，请检查蓝牙后重试。"
    message.contains("mode could not be confirmed", ignoreCase = true) ->
        "无法确认手柄模式切换，请检查蓝牙后重试。"
    else -> message
}
