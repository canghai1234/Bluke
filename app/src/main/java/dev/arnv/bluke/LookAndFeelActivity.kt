package dev.arnv.bluke

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import dev.arnv.bluke.ui.SettingsCardGroup
import dev.arnv.bluke.ui.SettingsItemData
import dev.arnv.bluke.ui.theme.MyApplicationTheme
import dev.arnv.bluke.ui.theme.AccentColors
import dev.arnv.bluke.ui.theme.getCookieShape
import kotlinx.coroutines.launch

import androidx.core.content.edit

private fun paletteStyleDisplayName(value: String): String = when (value) {
    "Tonal Spot" -> "色调焦点"
    "Vibrant" -> "鲜艳"
    "Expressive" -> "表现力"
    "Rainbow" -> "彩虹"
    "Fruit Salad" -> "水果沙拉"
    "Fidelity" -> "保真"
    "Content" -> "内容"
    "Neutral" -> "中性"
    "Monochrome" -> "单色"
    else -> value
}

class LookAndFeelActivity : ComponentActivity() {
    private val themeModeState = mutableIntStateOf(0)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isDynamicColorDefault = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        themeModeState.intValue = sharedPrefs.getInt("theme_mode", 0)
        
        setContent {
            MyApplicationTheme {
                var dynamicColor by remember { mutableStateOf(sharedPrefs.getBoolean("dynamic_color", isDynamicColorDefault)) }
                var accentColorIndex by remember { mutableIntStateOf(sharedPrefs.getInt("accent_color_index", 0)) }
                var paletteStyleState by remember { mutableStateOf(sharedPrefs.getString("palette_style", "Tonal Spot") ?: "Tonal Spot") }
                
                var hapticsEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("haptics_enabled", true)) }
                var keySoundEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("key_sound_enabled", true)) }
                
                var showPaletteDialog by remember { mutableStateOf(false) }
                val themeMode by themeModeState
                
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
                
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        LargeTopAppBar(
                            title = { Text("外观与体验") },
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
                        
                        // Top illustration mock
                        Icon(
                            Icons.Default.FormatPaint, 
                            contentDescription = null, 
                            modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally), 
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        
                        if (!dynamicColor || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                            val pageCount = (AccentColors.size + 3) / 4
                            val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { pageCount })
                            
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                androidx.compose.foundation.pager.HorizontalPager(
                                    state = pagerState,
                                    contentPadding = PaddingValues(horizontal = 24.dp),
                                    pageSpacing = 16.dp
                                ) { page ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        val startIndex = page * 4
                                        for (i in 0 until 4) {
                                            val index = startIndex + i
                                            if (index < AccentColors.size) {
                                                val color = AccentColors[index]
                                                val isSelected = accentColorIndex == index
                                                Box(
                                                    modifier = Modifier
                                                        .size(64.dp)
                                                        .clip(CircleShape)
                                                        .background(color)
                                                        .clickable {
                                                            accentColorIndex = index
                                                            sharedPrefs.edit {
                                                                putInt("accent_color_index", index)
                                                            }
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (isSelected) {
                                                        Surface(
                                                            shape = CircleShape,
                                                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                                            modifier = Modifier.size(32.dp)
                                                        ) {
                                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.padding(6.dp), tint = MaterialTheme.colorScheme.onSurface)
                                                        }
                                                    }
                                                }
                                            } else {
                                                Spacer(modifier = Modifier.size(64.dp))
                                            }
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                val cookieShape7 = getCookieShape(7)
                                val pebbleShape = androidx.compose.foundation.shape.RoundedCornerShape(percent = 35)
                                val archShapeRound = androidx.compose.foundation.shape.RoundedCornerShape(
                                    topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 4.dp
                                )
                                val cookieShape5 = getCookieShape(5)

                                val paginationShapes = listOf(cookieShape7, pebbleShape, archShapeRound, cookieShape5)
                                val coroutineScope = rememberCoroutineScope()

                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    repeat(pageCount) { iteration ->
                                        val isSelected = pagerState.currentPage == iteration
                                        val color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                        val shape = if (isSelected) {
                                            paginationShapes.getOrElse(iteration) { cookieShape7 }
                                        } else {
                                            CircleShape
                                        }
                                        val size = if (isSelected) 14.dp else 10.dp
                                        Box(
                                            modifier = Modifier
                                                .padding(6.dp)
                                                .size(size)
                                                .clip(shape)
                                                .background(color)
                                                .clickable {
                                                    coroutineScope.launch {
                                                        pagerState.animateScrollToPage(iteration)
                                                    }
                                                }
                                        )
                                    }
                                }
                            }
                        }

                        SettingsCardGroup(
                            title = "颜色方案",
                            items = listOf(
                                SettingsItemData(
                                    title = "动态取色",
                                    subtitle = "根据设备壁纸自动设置应用主题颜色",
                                    icon = { Icon(Icons.Default.FormatPaint, null, tint = MaterialTheme.colorScheme.primary) },
                                    action = {
                                        Switch(
                                            checked = dynamicColor,
                                            enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
                                            onCheckedChange = { 
                                                dynamicColor = it
                                                sharedPrefs.edit { putBoolean("dynamic_color", it) }
                                            }
                                        )
                                    }
                                ),
                                SettingsItemData(
                                    title = "方案风格",
                                    subtitle = paletteStyleDisplayName(paletteStyleState),
                                    icon = { Icon(Icons.Default.Palette, null, tint = MaterialTheme.colorScheme.primary) },
                                    onClick = { showPaletteDialog = true }
                                )
                            )
                        )
                        
                        if (showPaletteDialog) {
                            AlertDialog(
                                onDismissRequest = { showPaletteDialog = false },
                                title = { Text("方案风格") },
                                text = {
                                    Column {
                                        listOf("Tonal Spot", "Vibrant", "Expressive", "Rainbow", "Fruit Salad", "Fidelity", "Content", "Neutral", "Monochrome").forEach { option ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth().clickable { 
                                                    paletteStyleState = option
                                                    sharedPrefs.edit { putString("palette_style", option) }
                                                    showPaletteDialog = false 
                                                }.padding(vertical = 8.dp)
                                            ) {
                                                RadioButton(selected = option == paletteStyleState, onClick = null)
                                                Spacer(Modifier.width(16.dp))
                                                Text(paletteStyleDisplayName(option))
                                            }
                                        }
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = { showPaletteDialog = false }) { Text("确定") }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showPaletteDialog = false }) { Text("取消") }
                                }
                            )
                        }
                        
                        SettingsCardGroup(
                            title = "其他设置",
                            items = listOf(
                                SettingsItemData(
                                    title = "深色主题",
                                    subtitle = when(themeMode) {
                                        0 -> "跟随系统"
                                        1 -> "关闭"
                                        else -> "开启"
                                    },
                                    icon = { Icon(Icons.Default.DarkMode, null, tint = MaterialTheme.colorScheme.primary) },
                                    onClick = { 
                                        startActivity(Intent(this@LookAndFeelActivity, DarkThemeActivity::class.java))
                                    }
                                ),
                                SettingsItemData(
                                    title = "触感与振动",
                                    subtitle = "触摸操作时提供触感反馈",
                                    icon = { Icon(Icons.Default.Vibration, null, tint = MaterialTheme.colorScheme.primary) },
                                    action = {
                                        Switch(
                                            checked = hapticsEnabled,
                                            onCheckedChange = { 
                                                hapticsEnabled = it
                                                sharedPrefs.edit { putBoolean("haptics_enabled", it) }
                                            }
                                        )
                                    }
                                ),
                                SettingsItemData(
                                    title = "启用声音",
                                    subtitle = "按键时播放音效",
                                    icon = { Icon(Icons.AutoMirrored.Filled.VolumeUp, null, tint = MaterialTheme.colorScheme.primary) },
                                    action = {
                                        Switch(
                                            checked = keySoundEnabled,
                                            onCheckedChange = { 
                                                keySoundEnabled = it
                                                sharedPrefs.edit {
                                                    putBoolean("key_sound_enabled", it)
                                                    putBoolean("sound_toggle", it)
                                                }
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

    override fun onResume() {
        super.onResume()
        val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        themeModeState.intValue = sharedPrefs.getInt("theme_mode", 0)
    }
}
