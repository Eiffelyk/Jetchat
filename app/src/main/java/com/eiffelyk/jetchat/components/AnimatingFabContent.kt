package com.eiffelyk.jetchat.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.util.lerp
import kotlin.math.roundToInt

private enum class ExpandableFabStates { Collapsed, Extended }

private const val transitionDuration = 200

@Composable
fun AnimatingFabContent(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    extended: Boolean = true
) {
    val currentState = if (extended) ExpandableFabStates.Extended else ExpandableFabStates.Collapsed
    val transition = updateTransition(currentState, label = "")
    val textOpacity by transition.animateFloat(transitionSpec = {
        if (targetState == ExpandableFabStates.Collapsed) {
            tween(
                easing = LinearEasing,
                durationMillis = (transitionDuration / 12f * 5).roundToInt()
            )
        } else {
            tween(
                easing = LinearEasing,
                delayMillis = (transitionDuration / 3f).roundToInt(),
                durationMillis = (transitionDuration / 12f * 5).roundToInt()
            )
        }
    }, label = "") { progress ->
        if (progress == ExpandableFabStates.Collapsed) 0f else 1f
    }

    val fabWidthFactor by transition.animateFloat(label = "", transitionSpec = {
        if (targetState == ExpandableFabStates.Collapsed) {
            tween(easing = FastOutSlowInEasing, durationMillis = transitionDuration)
        } else {
            tween(easing = FastOutSlowInEasing, durationMillis = transitionDuration)
        }
    }) { progress ->
        if (progress == ExpandableFabStates.Collapsed) 0f else 1f
    }

    IconAndTextRow(
        icon = icon,
        text = text,
        { textOpacity },
        { fabWidthFactor },
        modifier = modifier
    )
}

@Composable
private fun IconAndTextRow(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    opacityProgress: () -> Float,
    withProgress: () -> Float,
    modifier: Modifier
) {
    Layout(modifier = modifier, content = {
        icon()
        Box(modifier = Modifier.alpha(opacityProgress())) {
            text()
        }
    }) { measurables, constraints ->
        val iconPlaceable = measurables[0].measure(constraints)
        val textPlaceable = measurables[1].measure(constraints)

        val height = constraints.maxHeight
        val initialWidth = height.toFloat()
        val iconPadding = (initialWidth - iconPlaceable.width) / 2f
        val expandedWidth = iconPlaceable.width + textPlaceable.width + iconPadding * 3
        val width = lerp(initialWidth, expandedWidth, withProgress())
        layout(width = width.roundToInt(), height = height) {
            iconPlaceable.place(
                iconPadding.roundToInt(),
                constraints.maxHeight / 2 - iconPlaceable.height / 2
            )
            textPlaceable.place(
                (iconPlaceable.width + iconPadding * 2).roundToInt(),
                constraints.maxHeight / 2 - textPlaceable.height / 2
            )
        }
    }
}