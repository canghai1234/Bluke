package dev.arnv.bluke.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.BluetoothSearching
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.arnv.bluke.bluetooth.BluetoothState

@Composable
internal fun ProfileNotSupportedScreen(
    bluetoothState: BluetoothState,
    onEnableBluetooth: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (bluetoothState is BluetoothState.CheckingCapabilities) {
                Icons.AutoMirrored.Filled.BluetoothSearching
            } else {
                Icons.Default.BluetoothDisabled
            },
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = when (bluetoothState) {
                is BluetoothState.CheckingCapabilities -> appText("正在检查兼容性")
                is BluetoothState.Unsupported -> appText("不支持蓝牙")
                is BluetoothState.ProfileNotSupported -> appText("设备不兼容")
                else -> appText("蓝牙已关闭")
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        if (bluetoothState is BluetoothState.CheckingCapabilities) {
            Text(
                text = appText("正在确认此手机固件是否提供蓝牙 HID Device 角色，这可能需要几秒钟。"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        } else if (bluetoothState is BluetoothState.ProfileNotSupported) {
            Text(
                text = appText("多次尝试后，Bluke 仍无法访问蓝牙 HID Device 角色。此设备固件可能缺少或禁用了键盘、鼠标和手柄输出所需的角色。"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = appText("兼容性取决于具体固件，而不仅是手机品牌或型号。在判定设备不支持前，请先重启蓝牙并重试。"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = when (bluetoothState) {
                    is BluetoothState.Unsupported -> appText("此设备没有蓝牙硬件。")
                    else -> appText("请开启蓝牙以连接设备。")
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(32.dp))
        if (bluetoothState is BluetoothState.CheckingCapabilities) {
            CircularProgressIndicator(modifier = Modifier.size(32.dp))
        } else if (bluetoothState is BluetoothState.BluetoothOff) {
            Button(
                onClick = onEnableBluetooth,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(appText("开启蓝牙"))
            }
        } else if (bluetoothState is BluetoothState.ProfileNotSupported) {
            OutlinedButton(onClick = onRetry) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(appText("重试"))
            }
        }
    }
}
