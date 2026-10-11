package dev.arnv.bluke

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.arnv.bluke.ui.theme.MyApplicationTheme
import androidx.compose.ui.input.nestedscroll.nestedScroll
import kotlin.time.Duration.Companion.milliseconds

class HelpActivity : LocalizedActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isFirstRun = intent.getBooleanExtra("first_run", false)

        setContent {
            MyApplicationTheme {
                var timer by remember { mutableIntStateOf(if (isFirstRun) 5 else 0) }
                LaunchedEffect(timer) {
                    if (timer > 0) {
                        delay(1000L.milliseconds)
                        timer -= 1
                    }
                }
                androidx.activity.compose.BackHandler(enabled = isFirstRun && timer > 0) {
                    // Do nothing until timer completes
                }

                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        LargeTopAppBar(
                            title = { Text(appText("帮助与指南")) },
                            navigationIcon = {
                                if (!isFirstRun || timer == 0) {
                                    IconButton(onClick = { finish() }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = appText("返回"))
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.largeTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            scrollBehavior = scrollBehavior
                        )
                    },
                    bottomBar = {
                        if (isFirstRun) {
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 8.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = { finish() },
                                    enabled = timer == 0,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(if (timer > 0) appText("我明白了（$timer）") else appText("我明白了"))
                                }
                            }
                        }
                    }
                ) { innerPadding ->

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {

                        StepSection(
                            icon = Icons.Default.Bluetooth,
                            title = appText("连接新主机"),
                            steps = listOf(
                                appText("在电脑、电视、平板或手机的蓝牙设置中开启可见性。"),
                                appText("在 Bluke 中扫描并点击主机名称。"),
                                appText("在两台设备上接受匹配的配对提示。"),
                                appText("等待 Bluke 的状态指示点变绿后再发送输入。")
                            )
                        )

                        StepSection(
                            icon = Icons.Default.BluetoothConnected,
                            title = appText("修复失效配对"),
                            steps = listOf(
                                appText("在本机蓝牙设置中忘记该主机。"),
                                appText("在主机蓝牙设置中移除 Bluke 或本机。"),
                                appText("关闭再重新打开双方的蓝牙，然后从 Bluke 重新配对。"),
                                appText("仅重新安装 Bluke 不会清除主机缓存的 HID 布局。")
                            )
                        )

                        StepSection(
                            icon = Icons.Default.Warning,
                            title = appText("配对被拒绝"),
                            isWarning = true,
                            steps = listOf(
                                appText("被拒绝只表示主机或手机拒绝了请求，并不能证明设备不支持 HID。"),
                                appText("重试一次，并在两台设备上接受所有提示。"),
                                appText("如果问题重复出现，请按上面的完整步骤修复。")
                            )
                        )

                        StepSection(
                            icon = Icons.Default.ErrorOutline,
                            title = appText("手柄与浏览器游戏"),
                            isWarning = true,
                            steps = listOf(
                                appText("Windows/Linux 游戏请选择原生模式，Android 游戏请选择 Android 模式，浏览器游戏无法识别方向时请选择 Web 模式。"),
                                appText("点击手柄模式按钮可在原生 / Android / Web 之间切换，长按可打开手柄兼容性设置。所有模式都可以实时切换，无需重新连接。更新到共享手柄描述符后，请在两台设备上忘记 Bluke 并重新配对一次；之后切换模式无需再次配对。"),
                                appText("使用 Steam 时，请在 Steam Input 中启用手柄，并在 Steam 手柄测试或系统游戏手柄面板中确认。"),
                                appText("Bluke 是通用 HID 手柄，不是 Xbox XInput 设备。部分较新的 Windows 游戏没有兼容层时只接受 XInput。")
                            )
                        )

                        StepSection(
                            icon = Icons.Default.Warning,
                            title = appText("平台限制"),
                            steps = listOf(
                                appText("是否支持取决于手机固件是否提供 Android 蓝牙 HID Device 角色。"),
                                appText("Apple 主机可能可以配对，但会限制组合键盘、指针或手柄功能；具体行为取决于 iOS/iPadOS 版本。"),
                                appText("阻止主机接管音频是针对 Linux 的尽力而为解决方案，可能被主机或手机固件覆盖。"),
                                appText("如果 Android 反复拒绝 HID 注册，请点击一次重试。达到注册尝试上限后才会显示不兼容设备页面。")
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StepSection(icon: ImageVector, title: String, steps: List<String>, isWarning: Boolean = false) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = if (isWarning) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isWarning) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isWarning) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                steps.forEachIndexed { index, step ->
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(if (isWarning) MaterialTheme.colorScheme.error.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                }
            }
        }
    }
}
