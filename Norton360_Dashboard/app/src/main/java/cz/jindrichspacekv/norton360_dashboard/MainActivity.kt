package cz.jindrichspacekv.norton360_dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cz.jindrichspacekv.norton360_dashboard.ui.dashboard.SecurityDashboardScreenRoute
import cz.jindrichspacekv.norton360_dashboard.ui.theme.Norton360_dashboardTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Norton360_dashboardTheme {
                SecurityDashboardScreenRoute()
            }
        }
    }
}
