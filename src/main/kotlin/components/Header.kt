package components
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import composefordesktop.resources.Res
import composefordesktop.resources.maximize
import composefordesktop.resources.minimize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import projectgen.ViewIntent
import projectgen.ViewState
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Header(
    windowState: WindowState,
    state: ViewState,
    onIntent: (ViewIntent) -> Unit,
    onToggleDarkMode: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var expandClicked by remember { mutableStateOf(false) }
    if (expandClicked) {
        windowState.size = windowState.size.copy(width = 900.dp)
    } else {
        windowState.size = windowState.size.copy(width = 480.dp)
    }

    val animatedAngle by rememberInfiniteTransition("gradient_angle_animation").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val radians = Math.toRadians(animatedAngle.toDouble())
    val startOffset = Offset(
        x = (cos(radians) * 200 + 400).toFloat(),
        y = (sin(radians) * 200 + 400).toFloat()
    )
    val endOffset = Offset(
        x = (cos(radians + Math.PI) * 200 + 400).toFloat(),
        y = (sin(radians + Math.PI) * 200 + 400).toFloat()
    )
    val headerGradientBackground = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.primary
        ),
        start = startOffset,
        end = endOffset
    )
    val colorOnGradient = MaterialTheme.colorScheme.onPrimary

    val themeIconRotation by animateFloatAsState(
        targetValue = if (state.darkMode) 360f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = LinearEasing,
            delayMillis = 400
        )
    )
    var themeIconScale by remember { mutableStateOf(1f) }
    val themeIconScaleAnimation by animateFloatAsState(
        targetValue = themeIconScale,
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearEasing
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(headerGradientBackground)
            .padding(vertical = 10.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Compose for Desktop Wizard",
            style = MaterialTheme.typography.titleLarge,
            color = colorOnGradient
        )

        Text(
            text = "Desktop Client",
            style = MaterialTheme.typography.titleSmall,
            color = colorOnGradient
        )

        Row(
            modifier = Modifier.fillMaxWidth().animateContentSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    expandClicked = !expandClicked
                },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            ) {
                Icon(
                    painter = painterResource(if(expandClicked) Res.drawable.minimize else Res.drawable.maximize),
                    contentDescription = "Window size toggle",
                    tint = colorOnGradient
                )
            }

            IconButton(
                onClick = { onIntent(ViewIntent.ShowPreview) },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            ) {
                Icon(
                    imageVector = Icons.Default.Preview,
                    contentDescription = "Preview",
                    tint = colorOnGradient
                )
            }

            IconButton(
                onClick = {
                    onToggleDarkMode()
                    scope.launch {
                        themeIconScale = 1.3f
                        delay(300)
                        themeIconScale = 1f
                    }
                },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            ) {
                Icon(
                    imageVector = if (state.darkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = "Toggle Theme",
                    tint = colorOnGradient,
                    modifier = Modifier.graphicsLayer{
                        rotationZ = themeIconRotation
                        scaleX = themeIconScaleAnimation
                        scaleY = themeIconScaleAnimation
                    }
                )
            }
        }
    }
}