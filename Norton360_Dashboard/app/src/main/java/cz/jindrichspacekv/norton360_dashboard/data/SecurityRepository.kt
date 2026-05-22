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
        val allCategories = listOf(
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

        // Simulate scan progress updates with partial results
        emit(ScanState.Scanning(0, emptyList()))
        delay(500)
        
        emit(ScanState.Scanning(25, allCategories.take(1)))
        delay(1800)
        emit(ScanState.Scanning(40, allCategories.take(1) ))
        delay(800)

        emit(ScanState.Scanning(50, allCategories.take(2)))
        delay(800)
        emit(ScanState.Scanning(63, allCategories.take(2) ))
        delay(600)
        emit(ScanState.Scanning(75, allCategories.take(3)))
        delay(1400)
        emit(ScanState.Scanning(92, allCategories.take(3) ))
        delay(800)
        emit(ScanState.Scanning(100, allCategories))
        delay(300)

        // Final Completed state
        val mockSummary = SecurityHealthSummary(
            overallScore = 75,
            categories = allCategories
        )

        emit(ScanState.Completed(mockSummary))
    }
}
