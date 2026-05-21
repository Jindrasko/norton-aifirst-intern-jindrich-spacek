package cz.jindrichspacekv.norton360_dashboard.ui.dashboard.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import cz.jindrichspacekv.norton360_dashboard.ui.theme.CriticalRed
import cz.jindrichspacekv.norton360_dashboard.ui.theme.SafeGreen
import cz.jindrichspacekv.norton360_dashboard.ui.theme.WarningOrange
import cz.jindrichspacekv.norton360_dashboard.viewmodel.SecurityUiState

@Composable
fun SecurityScoreGauge(
    uiState: SecurityUiState,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        val diameter = min(maxWidth, maxHeight)
        val strokeWidth = 20.dp

        val infiniteTransition = rememberInfiniteTransition(label = "Rotation")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "GaugeRotation"
        )

        val animatedScore = remember { Animatable(0f) }
        LaunchedEffect(uiState) {
            if (uiState is SecurityUiState.Completed) {
                animatedScore.animateTo(
                    targetValue = uiState.summary.overallScore.toFloat(),
                    animationSpec = tween(durationMillis = 1000)
                )
            } else {
                animatedScore.snapTo(0f)
            }
        }

        // Gauge Ring
        Canvas(modifier = Modifier
            .size(diameter)
            .rotate(if (uiState is SecurityUiState.Scanning) rotation else 0f)
        ) {
            // Background Track
            drawCircle(
                color = Color.LightGray.copy(alpha = 0.45f),
                style = Stroke(width = strokeWidth.toPx())
            )

            val sweepAngle = when (uiState) {
                is SecurityUiState.Idle -> 0f
                is SecurityUiState.Scanning -> (uiState.progress / 100f) * 360f
                is SecurityUiState.Completed -> (animatedScore.value / 100f) * 360f
            }

            // Progress / Spinning Arc
            drawArc(
                color = if (uiState is SecurityUiState.Scanning) Color(0xFFFEEC2A) else Color(0xFFFEEC2A),
                startAngle = -90f,
                sweepAngle = if (uiState is SecurityUiState.Scanning) 90f else sweepAngle.coerceAtLeast(0.1f),
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        // Inner Circular Button
        val buttonSize = diameter - strokeWidth * 2 + 6.dp
        val isScanning = uiState is SecurityUiState.Scanning
        
        val buttonColor by animateColorAsState(
            targetValue = when (uiState) {
                is SecurityUiState.Idle -> MaterialTheme.colorScheme.primary
                is SecurityUiState.Scanning -> MaterialTheme.colorScheme.primary
                is SecurityUiState.Completed -> MaterialTheme.colorScheme.background
                /*    {
                    val score = uiState.summary.overallScore
                    when {
                        score >= 80 -> SafeGreen
                        score >= 60 -> WarningOrange
                        else -> CriticalRed
                    }
                }  */
            },
            label = "ButtonColor"
        )

        Button(
            onClick = onScanClick,
            enabled = !isScanning,
            modifier = Modifier
                .size(buttonSize),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = Color.Black,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                disabledContentColor = Color.Black
            ),
            border = BorderStroke(6.dp, Color.Black.copy(alpha = if (uiState is SecurityUiState.Idle) 1f else 0f)),
            contentPadding = PaddingValues(0.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                when (uiState) {
                    is SecurityUiState.Idle -> {
                        Text(
                            text = "Start Scan",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Last scan 1 day ago",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray
                        )
                    }
                    is SecurityUiState.Scanning -> {
                        Text(
                            text = "Scanning...",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.progress}%",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    is SecurityUiState.Completed -> {
                        Text(
                            text = animatedScore.value.toInt().toString(),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Overall Score",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "(Scan again)",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
