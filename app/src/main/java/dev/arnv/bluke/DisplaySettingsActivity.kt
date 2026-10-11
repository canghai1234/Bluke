package dev.arnv.bluke

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import dev.arnv.bluke.ui.InputMode
import dev.arnv.bluke.ui.InputSurfaceRotationPreference
import dev.arnv.bluke.ui.SettingsGroup
import dev.arnv.bluke.ui.SettingsItem
import dev.arnv.bluke.ui.enabledInputModes
import dev.arnv.bluke.ui.theme.MyApplicationTheme

class DisplaySettingsActivity : LocalizedActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                DisplaySettingsScreen(onBack = ::finish)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DisplaySettingsScreen(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPrefs = remember(context) {
        context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
    }
    val enabledModes = remember(sharedPrefs) { sharedPrefs.enabledInputModes() }
    var selections by remember(enabledModes) {
        mutableStateOf(
            enabledModes.associateWith { mode ->
                InputSurfaceRotationPreference.fromPreference(
                    sharedPrefs.getString(rotationPreferenceKey(mode), null),
                )
            },
        )
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(appText("方向设置")) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = appText("返回"),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = appText("分屏时 Android 可能不会旋转窗口；这里会旋转输入界面本身。"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
            )
            enabledModes.forEach { mode ->
                SettingsGroup(title = appText(mode.displayName)) {
                    InputSurfaceRotationPreference.entries.forEach { preference ->
                        SettingsItem(
                            title = preference.label,
                            subtitle = when (preference) {
                                InputSurfaceRotationPreference.AUTO -> appText("根据当前窗口比例自动适配")
                                InputSurfaceRotationPreference.CLOCKWISE -> appText("将输入界面顺时针旋转 90°")
                                InputSurfaceRotationPreference.COUNTERCLOCKWISE -> appText("将输入界面逆时针旋转 90°")
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.ScreenRotation,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            },
                            action = {
                                RadioButton(
                                    selected = selections[mode] == preference,
                                    onClick = null,
                                )
                            },
                            onClick = {
                                selections = selections + (mode to preference)
                                sharedPrefs.edit {
                                    putString(
                                        rotationPreferenceKey(mode),
                                        preference.preferenceValue,
                                    )
                                }
                            },
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun rotationPreferenceKey(mode: InputMode): String =
    "input_surface_rotation_${mode.preferenceKey}"
