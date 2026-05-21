package cz.jindrichspacekv.norton360_dashboard.ui.dashboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
    val backgroundColor = if (isIdle) {
        Color.White
    } else {
        when (category.status) {
            SecurityStatus.SAFE -> SafeGreen
            SecurityStatus.WARNING -> WarningOrange
            SecurityStatus.CRITICAL -> CriticalRed
        }
    }

    val icon: ImageVector = when (category.id) {
        "os_version" -> Icons.Default.SystemUpdate
        "app_threats" -> Icons.Default.BugReport
        "wifi_safety" -> Icons.Default.Wifi
        "password_strength" -> Icons.Default.Lock
        else -> Icons.Default.BugReport
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp) // Fixed height for equal-size cards
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
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
                    text = category.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = category.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
