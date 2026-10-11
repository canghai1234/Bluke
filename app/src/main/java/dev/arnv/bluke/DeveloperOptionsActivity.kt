package dev.arnv.bluke

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.content.edit
import dev.arnv.bluke.ui.SettingsCardGroup
import dev.arnv.bluke.ui.SettingsItemData
import dev.arnv.bluke.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

class DeveloperOptionsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                
                var isDevMode by remember { mutableStateOf(sharedPrefs.getBoolean("is_developer_mode", false)) }
                var mockUpdatePopup by remember { mutableStateOf(sharedPrefs.getBoolean("mock_update_popup", false)) }
                var mockBtDisabled by remember { mutableStateOf(sharedPrefs.getBoolean("mock_bt_disabled", false)) }
                var mockUnsupported by remember { mutableStateOf(sharedPrefs.getBoolean("mock_device_unsupported", false)) }
                var mockGamepadGuide by remember { mutableStateOf(sharedPrefs.getBoolean("mock_gamepad_guide", false)) }
                var mockTroubleshootingNudge by remember { mutableStateOf(sharedPrefs.getBoolean("mock_troubleshooting_nudge", false)) }

                // Easter egg states
                val catEmojis = listOf("🐱", "😹", "😼")
                var catIndex by remember { mutableIntStateOf(0) }
                var catClicked by remember { mutableStateOf(false) }

                LaunchedEffect(catClicked) {
                    if (catClicked) {
                        catIndex = (catIndex + 1) % catEmojis.size
                        if (catIndex == 2) {
                            // Hit smirk
                            delay(500)
                            context.startActivity(Intent(Intent.ACTION_VIEW, "https://www.youtube.com/watch?v=dQw4w9WgXcQ".toUri()))
                        }
                        catClicked = false
                    }
                }

                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        LargeTopAppBar(
                            title = { Text("开发者选项") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                                }
                            },
                            colors = TopAppBarDefaults.largeTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            scrollBehavior = scrollBehavior
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(Modifier.height(8.dp))
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Text(
                                text = "警告：这些设置仅供开发使用，可能导致应用异常、模拟错误或影响使用体验。",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // Master Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("开发者模式", style = MaterialTheme.typography.titleMedium)
                            Switch(
                                checked = isDevMode,
                                onCheckedChange = { 
                                    isDevMode = it
                                    sharedPrefs.edit { putBoolean("is_developer_mode", it) }
                                    if (!it) {
                                        // Auto turn off all mocks if dev mode disabled
                                        sharedPrefs.edit {
                                            putBoolean("mock_update_popup", false)
                                            putBoolean("mock_bt_disabled", false)
                                            putBoolean("mock_device_unsupported", false)
                                            putBoolean("mock_hid_unsupported", false)
                                            putBoolean("mock_gamepad_guide", false)
                                            putBoolean("mock_troubleshooting_nudge", false)
                                        }
                                        finish() // Exit screen
                                    }
                                }
                            )
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                        // Debug & Mocks
                        SettingsCardGroup(
                            items = listOf(
                                SettingsItemData(
                                    title = "开发者日志",
                                    subtitle = "实时蓝牙数据包和系统日志",
                                    icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, null) },
                                    onClick = { 
                                        startActivity(Intent(this@DeveloperOptionsActivity, DeveloperLogsActivity::class.java))
                                    }
                                ),
                                SettingsItemData(
                                    title = "强制显示应用更新弹窗",
                                    subtitle = "在主界面模拟应用更新",
                                    action = {
                                        Switch(
                                            checked = mockUpdatePopup,
                                            onCheckedChange = {
                                                mockUpdatePopup = it
                                                sharedPrefs.edit { putBoolean("mock_update_popup", it) }
                                            }
                                        )
                                    }
                                ),
                                SettingsItemData(
                                    title = "强制显示手柄指南",
                                    subtitle = "在主界面显示首次使用手柄指南",
                                    action = {
                                        Switch(
                                            checked = mockGamepadGuide,
                                            onCheckedChange = {
                                                mockGamepadGuide = it
                                                sharedPrefs.edit { putBoolean("mock_gamepad_guide", it) }
                                            },
                                        )
                                    },
                                ),
                                SettingsItemData(
                                    title = "强制显示故障排除提示",
                                    subtitle = "在主界面显示连接帮助卡片",
                                    action = {
                                        Switch(
                                            checked = mockTroubleshootingNudge,
                                            onCheckedChange = {
                                                mockTroubleshootingNudge = it
                                                sharedPrefs.edit { putBoolean("mock_troubleshooting_nudge", it) }
                                            },
                                        )
                                    },
                                ),
                                SettingsItemData(
                                    title = "主界面错误状态",
                                    subtitle = "强制界面进入指定错误状态",
                                    action = {
                                        var expanded by remember { mutableStateOf(false) }
                                        val errorOptions = listOf("None", "Bluetooth Disabled", "Device Unsupported", "HID Unsupported")
                                        val mockHidUnsupported = sharedPrefs.getBoolean("mock_hid_unsupported", false)
                                        val currentSelection = when {
                                            mockBtDisabled -> "Bluetooth Disabled"
                                            mockUnsupported -> "Device Unsupported"
                                            mockHidUnsupported -> "HID Unsupported"
                                            else -> "None"
                                        }
                                        val currentSelectionLabel = when (currentSelection) {
                                            "Bluetooth Disabled" -> "蓝牙已关闭"
                                            "Device Unsupported" -> "设备不支持"
                                            "HID Unsupported" -> "HID 不支持"
                                            else -> "无"
                                        }
                                        Box {
                                            TextButton(onClick = { expanded = true }) {
                                                Text(currentSelectionLabel)
                                            }
                                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                                errorOptions.forEach { option ->
                                                    DropdownMenuItem(
                                                        text = {
                                                            Text(
                                                                when (option) {
                                                                    "Bluetooth Disabled" -> "蓝牙已关闭"
                                                                    "Device Unsupported" -> "设备不支持"
                                                                    "HID Unsupported" -> "HID 不支持"
                                                                    else -> "无"
                                                                }
                                                            )
                                                        },
                                                        onClick = {
                                                            when (option) {
                                                                "None" -> {
                                                                    mockBtDisabled = false
                                                                    mockUnsupported = false
                                                                    sharedPrefs.edit { putBoolean("mock_hid_unsupported", false) }
                                                                }
                                                                "Bluetooth Disabled" -> {
                                                                    mockBtDisabled = true
                                                                    mockUnsupported = false
                                                                    sharedPrefs.edit { putBoolean("mock_hid_unsupported", false) }
                                                                }
                                                                "Device Unsupported" -> {
                                                                    mockBtDisabled = false
                                                                    mockUnsupported = true
                                                                    sharedPrefs.edit { putBoolean("mock_hid_unsupported", false) }
                                                                }
                                                                "HID Unsupported" -> {
                                                                    mockBtDisabled = false
                                                                    mockUnsupported = false
                                                                    sharedPrefs.edit { putBoolean("mock_hid_unsupported", true) }
                                                                }
                                                            }
                                                            sharedPrefs.edit {
                                                                putBoolean("mock_bt_disabled", mockBtDisabled)
                                                                putBoolean("mock_device_unsupported", mockUnsupported)
                                                            }
                                                            expanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                )
                            )
                        )

                        // Easter Egg
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = catEmojis[catIndex],
                                style = MaterialTheme.typography.displayLarge,
                                modifier = Modifier.clickable(
                                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                    indication = null
                                ) {
                                    catClicked = true
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
