package cz.jindrichspacekv.norton360_dashboard.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import javax.inject.Inject

/**
 * Repository interface for performing security health operations.
 */
interface SecurityRepository {
    /**
     * Initiates a security scan and returns a [Flow] of [ScanState] updates.
     */
    fun performSecurityScan(): Flow<ScanState>
}

/**
 * Mock implementation of [SecurityRepository] for testing and UI development.
 * Simulates scan progress and returns hardcoded mock data.
 */
class MockSecurityRepositoryImpl @Inject constructor() : SecurityRepository {

    override fun performSecurityScan(): Flow<ScanState> = flow {
        // Simulate scan progress updates
        emit(ScanState.Scanning(0))
        delay(500)
        
        emit(ScanState.Scanning(20))
        delay(600)
        
        emit(ScanState.Scanning(50))
        delay(800)
        
        emit(ScanState.Scanning(80))
        delay(500)
        
        emit(ScanState.Scanning(100))
        delay(300)

        // Mock data as per requirements
        val mockSummary = SecurityHealthSummary(
            overallScore = 75,
            categories = listOf(
                SecurityCategory(
                    id = "os_version",
                    title = "OS Version",
                    description = "Up to date",
                    status = SecurityStatus.SAFE
                ),
                SecurityCategory(
                    id = "app_threats",
                    title = "App Threats",
                    description = "No malicious apps found",
                    status = SecurityStatus.SAFE
                ),
                SecurityCategory(
                    id = "wifi_safety",
                    title = "Wi-Fi Safety",
                    description = "Unsecured network detected",
                    status = SecurityStatus.WARNING
                ),
                SecurityCategory(
                    id = "password_strength",
                    title = "Password Strength",
                    description = "2 compromised passwords",
                    status = SecurityStatus.CRITICAL
                )
            )
        )

        emit(ScanState.Completed(mockSummary))
    }
}
