package cz.jindrichspacekv.norton360_dashboard.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.jindrichspacekv.norton360_dashboard.R
import cz.jindrichspacekv.norton360_dashboard.data.SecurityCategory
import cz.jindrichspacekv.norton360_dashboard.data.SecurityStatus
import cz.jindrichspacekv.norton360_dashboard.ui.dashboard.components.SecurityCategoryTile
import cz.jindrichspacekv.norton360_dashboard.ui.dashboard.components.SecurityScoreGauge
import cz.jindrichspacekv.norton360_dashboard.viewmodel.SecurityDashboardViewModel
import cz.jindrichspacekv.norton360_dashboard.viewmodel.SecurityUiState

@Composable
fun SecurityDashboardScreenRoute(
    viewModel: SecurityDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SecurityDashboardScreen(
        uiState = uiState,
        onScanClick = viewModel::startScan
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityDashboardScreen(
    uiState: SecurityUiState,
    onScanClick: () -> Unit
) {
    val idleCategories = remember {
        listOf(
            SecurityCategory("os_version", "OS Version", "", SecurityStatus.SAFE),
            SecurityCategory("app_threats", "App Threats", "", SecurityStatus.SAFE),
            SecurityCategory("wifi_safety", "Wi-Fi Safety", "", SecurityStatus.SAFE),
            SecurityCategory("password_strength", "Password Strength", "", SecurityStatus.SAFE)
        )
    }

    var selectedItem by remember { mutableIntStateOf(0) }
    val navItems = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Menu", Icons.Default.Menu),
        NavItem("Alerts", Icons.Default.Notifications, hasBadge = true),
        NavItem("Account", Icons.Default.AccountCircle, hasBadge = true)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.norton),
                        contentDescription = "Norton Logo",
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(128.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1C1B1F),
                contentColor = Color.White,
                tonalElevation = 0.dp
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        label = {
                            Text(
                                text = item.title,
                                color = if (selectedItem == index) Color.White else Color.Gray
                            )
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (item.hasBadge) {
                                        Badge(
                                            containerColor = Color.Red,
                                            modifier = Modifier.size(6.dp)
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = if (selectedItem == index) Color.Black else Color.White
                                )
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            unselectedIconColor = Color.White,
                            indicatorColor = Color(0xFFFEEC2A), // Norton Yellow
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Gauge Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                SecurityScoreGauge(
                    uiState = uiState,
                    onScanClick = onScanClick
                )
            }

            // Category Grid Area
            val categories = when (uiState) {
                is SecurityUiState.Completed -> uiState.summary.categories
                else -> idleCategories
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(categories) { category ->
                    SecurityCategoryTile(
                        category = category,
                        isIdle = uiState is SecurityUiState.Idle || uiState is SecurityUiState.Scanning
                    )
                }
            }
        }
    }
}

data class NavItem(val title: String, val icon: ImageVector, val hasBadge: Boolean = false)
