package dev.arnv.bluke

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.unit.dp
import dev.arnv.bluke.ui.SettingsCardGroup
import dev.arnv.bluke.ui.SettingsItemData
import dev.arnv.bluke.ui.theme.MyApplicationTheme

class SettingsActivity : LocalizedActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                val lifecycleOwner = LocalLifecycleOwner.current
                var isDevMode by remember { mutableStateOf(sharedPrefs.getBoolean("is_developer_mode", false)) }

                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            isDevMode = sharedPrefs.getBoolean("is_developer_mode", false)
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                }

                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        LargeTopAppBar(
                            title = { Text(appText("设置")) },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = appText("返回"))
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
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Spacer(Modifier.height(8.dp))
                        SettingsCardGroup(
                            title = appText("显示与语言"),
                            items = listOf(
                                SettingsItemData(
                                    title = appText("语言"),
                                    subtitle = appText(AppLanguageManager.current().label),
                                    icon = { Icon(Icons.Default.Language, null, tint = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        startActivity(Intent(this@SettingsActivity, LanguageSettingsActivity::class.java))
                                    },
                                ),
                                SettingsItemData(
                                    title = appText("方向设置"),
                                    subtitle = appText("自动、顺时针或逆时针旋转输入界面"),
                                    icon = { Icon(Icons.Default.ScreenRotation, null, tint = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        startActivity(Intent(this@SettingsActivity, DisplaySettingsActivity::class.java))
                                    },
                                ),
                            ),
                        )
                        SettingsCardGroup(
                            title = appText("个性化"),
                            items = listOf(
                                SettingsItemData(
                                    title = appText("外观与体验"),
                                    subtitle = appText("应用颜色、深色主题、触感反馈和按键音"),
                                    icon = { Icon(Icons.Default.ColorLens, null, tint = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        startActivity(Intent(this@SettingsActivity, LookAndFeelActivity::class.java))
                                    }
                                ),
                                SettingsItemData(
                                    title = appText("键盘"),
                                    subtitle = appText("输入行为、布局、主题和按键音"),
                                    icon = { Icon(Icons.Default.Keyboard, null, tint = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        startActivity(Intent(this@SettingsActivity, KeyboardSettingsActivity::class.java))
                                    }
                                ),
                                SettingsItemData(
                                    title = appText("手柄"),
                                    subtitle = appText("手柄行为与兼容性"),
                                    icon = { Icon(Icons.Default.Gamepad, null, tint = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        startActivity(Intent(this@SettingsActivity, ControllerSettingsActivity::class.java))
                                    }
                                ),
                            )
                        )
                        SettingsCardGroup(
                            title = appText("控制与连接"),
                            items = listOf(
                                SettingsItemData(
                                    title = appText("行为"),
                                    subtitle = appText("蓝牙发现、重新连接和远程控制"),
                                    icon = { Icon(Icons.Default.Settings, null, tint = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        startActivity(Intent(this@SettingsActivity, BehaviorActivity::class.java))
                                    }
                                ),
                            )
                        )
                        SettingsCardGroup(
                            title = appText("支持"),
                            items = listOf(
                                SettingsItemData(
                                    title = appText("帮助与故障排除"),
                                    subtitle = appText("配对修复、手柄设置、浏览器游戏和使用限制"),
                                    icon = { Icon(Icons.AutoMirrored.Filled.HelpOutline, null, tint = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        startActivity(Intent(this@SettingsActivity, HelpActivity::class.java))
                                    }
                                ),
                                SettingsItemData(
                                    title = appText("关于"),
                                    subtitle = appText("版本、贡献者、许可证和支持"),
                                    icon = { Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        startActivity(Intent(this@SettingsActivity, AboutActivity::class.java))
                                    }
                                )
                            ).let { baseList ->
                                if (isDevMode) {
                                    baseList + SettingsItemData(
                                        title = appText("开发者选项"),
                                        subtitle = appText("测试与调试控制"),
                                        icon = { Icon(Icons.Default.Code, null, tint = MaterialTheme.colorScheme.primary) },
                                        onClick = {
                                            startActivity(Intent(this@SettingsActivity, DeveloperOptionsActivity::class.java))
                                        }
                                    )
                                } else {
                                    baseList
                                }
                            }
                        )
                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}
