package dev.arnv.bluke.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer

/**
 * The direction of the input surface inside the current application window.
 *
 * AUTO is deliberately a window policy, not a device-orientation policy. This
 * is important in split-screen mode where Android owns the window orientation
 * and may ignore Activity#setRequestedOrientation().
 */
internal enum class InputSurfaceRotationPreference(
    val preferenceValue: String,
    private val labelSource: String,
) {
    AUTO("auto", "自动"),
    CLOCKWISE("clockwise", "顺时针"),
    COUNTERCLOCKWISE("counterclockwise", "逆时针");

    val label: String
        get() = appText(labelSource)

    fun next(): InputSurfaceRotationPreference = entries[(ordinal + 1) % entries.size]

    companion object {
        fun fromPreference(value: String?): InputSurfaceRotationPreference =
            entries.firstOrNull { it.preferenceValue == value } ?: AUTO
    }
}

internal data class InputSurfaceLayoutInfo(
    val windowIsPortrait: Boolean,
    val contentIsLandscape: Boolean,
    val rotationDegrees: Float,
    val isMultiWindow: Boolean,
)

private const val PORTRAIT_WINDOW_RATIO = 1.08f

/**
 * Hosts the existing landscape-oriented input surfaces in both full-screen and
 * narrow split-screen windows. The child is measured in the dimensions it
 * will occupy before the optional quarter-turn, so its pointer input and
 * internal layout receive matching constraints.
 */
@Composable
internal fun InputSurfaceFrame(
    rotationPreference: InputSurfaceRotationPreference,
    isMultiWindow: Boolean,
    content: @Composable (InputSurfaceLayoutInfo) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val windowIsPortrait = maxHeight > maxWidth * PORTRAIT_WINDOW_RATIO
        val rotationDegrees = when (rotationPreference) {
            InputSurfaceRotationPreference.AUTO -> if (windowIsPortrait) 90f else 0f
            InputSurfaceRotationPreference.CLOCKWISE -> 90f
            InputSurfaceRotationPreference.COUNTERCLOCKWISE -> -90f
        }
        val quarterTurn = rotationDegrees != 0f
        val contentWidth = if (quarterTurn) maxHeight else maxWidth
        val contentHeight = if (quarterTurn) maxWidth else maxHeight
        val layoutInfo = InputSurfaceLayoutInfo(
            windowIsPortrait = windowIsPortrait,
            contentIsLandscape = contentWidth >= contentHeight,
            rotationDegrees = rotationDegrees,
            isMultiWindow = isMultiWindow,
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .requiredWidth(contentWidth)
                    .requiredHeight(contentHeight)
                    .graphicsLayer {
                        rotationZ = rotationDegrees
                        transformOrigin = TransformOrigin.Center
                    },
            ) {
                content(layoutInfo)
            }
        }
    }
}
