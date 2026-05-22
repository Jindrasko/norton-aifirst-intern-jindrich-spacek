package cz.jindrichspacekv.norton360_dashboard.ui.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.jindrichspacekv.norton360_dashboard.R
import cz.jindrichspacekv.norton360_dashboard.data.SecurityCategory
import cz.jindrichspacekv.norton360_dashboard.data.SecurityStatus
import cz.jindrichspacekv.norton360_dashboard.ui.theme.CriticalRed
import cz.jindrichspacekv.norton360_dashboard.ui.theme.SafeGreen
import cz.jindrichspacekv.norton360_dashboard.ui.theme.WarningOrange

@Composable
fun SecurityCategoryTile(
    category: SecurityCategory,
    isIdle: Boolean,
    modifier: Modifier = Modifier
) {
    // Ensure background is opaque to prevent shadow bleeding into the card
    val backgroundColor = if (isIdle) {
        Color.White
    } else {
        val statusColor = when (category.status) {
            SecurityStatus.SAFE -> SafeGreen
            SecurityStatus.WARNING -> WarningOrange
            SecurityStatus.CRITICAL -> CriticalRed
        }
        // Blend the semi-transparent status color over White to get an opaque color
        statusColor.compositeOver(Color.White)
    }

    val icon: ImageVector = when (category.id) {
        "os_version" -> Icons.Default.SystemUpdate
        "app_threats" -> Icons.Default.BugReport
        "wifi_safety" -> Icons.Default.Wifi
        "password_strength" -> Icons.Default.Lock
        else -> Icons.Default.BugReport
    }

    val title = when (category.id) {
        "os_version" -> stringResource(R.string.os_version_title)
        "app_threats" -> stringResource(R.string.app_threats_title)
        "wifi_safety" -> stringResource(R.string.wifi_safety_title)
        "password_strength" -> stringResource(R.string.password_strength_title)
        else -> category.title
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp) // Fixed height to ensure 2 lines of text fit
            .padding(6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val descriptionText = if (isIdle) "" else category.description
                if (descriptionText.isNotEmpty()) {
                    Text(
                        text = descriptionText,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}
