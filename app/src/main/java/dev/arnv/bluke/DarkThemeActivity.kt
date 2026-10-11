package dev.arnv.bluke

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import dev.arnv.bluke.ui.SettingsCardGroup
import dev.arnv.bluke.ui.SettingsItemData
import dev.arnv.bluke.ui.theme.MyApplicationTheme

class DarkThemeActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        
        setContent {
            MyApplicationTheme {
                var themeMode by remember { mutableIntStateOf(sharedPrefs.getInt("theme_mode", 0)) } // 0: System, 1: Off, 2: On
                var highContrastMode by remember { mutableStateOf(sharedPrefs.getBoolean("high_contrast_mode", false)) }
                
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
                
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        LargeTopAppBar(
                            title = { Text("深色主题") },
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
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Spacer(Modifier.height(8.dp))
                        
                        SettingsCardGroup(
                            items = listOf(
                                SettingsItemData(
                                    title = "跟随系统",
                                    action = {
                                        RadioButton(
                                            selected = themeMode == 0,
                                            onClick = {
                                                themeMode = 0
                                                sharedPrefs.edit { putInt("theme_mode", 0) }
                                            }
                                        )
                                    },
                                    onClick = {
                                        themeMode = 0
                                        sharedPrefs.edit { putInt("theme_mode", 0) }
                                    }
                                ),
                                SettingsItemData(
                                    title = "关闭",
                                    action = {
                                        RadioButton(
                                            selected = themeMode == 1,
                                            onClick = {
                                                themeMode = 1
                                                sharedPrefs.edit { putInt("theme_mode", 1) }
                                            }
                                        )
                                    },
                                    onClick = {
                                        themeMode = 1
                                        sharedPrefs.edit { putInt("theme_mode", 1) }
                                    }
                                ),
                                SettingsItemData(
                                    title = "开启",
                                    action = {
                                        RadioButton(
                                            selected = themeMode == 2,
                                            onClick = {
                                                themeMode = 2
                                                sharedPrefs.edit { putInt("theme_mode", 2) }
                                            }
                                        )
                                    },
                                    onClick = {
                                        themeMode = 2
                                        sharedPrefs.edit { putInt("theme_mode", 2) }
                                    }
                                )
                            )
                        )

                        SettingsCardGroup(
                            title = "其他设置",
                            items = listOf(
                                SettingsItemData(
                                    title = "高对比度深色模式",
                                    subtitle = "适用于 OLED 屏幕设备的纯黑深色主题",
                                    icon = { Icon(Icons.Default.Contrast, null, tint = MaterialTheme.colorScheme.primary) },
                                    action = {
                                        Switch(
                                            checked = highContrastMode,
                                            onCheckedChange = { 
                                                highContrastMode = it
                                                sharedPrefs.edit { putBoolean("high_contrast_mode", it) }
                                            }
                                        )
                                    }
                                )
                            )
                        )
                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}
