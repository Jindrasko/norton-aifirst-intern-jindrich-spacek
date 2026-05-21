package cz.jindrichspacekv.norton360_dashboard.data

/**
 * Represents the security level of a specific check or overall system.
 */
enum class SecurityStatus {
    SAFE,
    WARNING,
    CRITICAL
}

/**
 * Data class representing an individual security check.
 *
 * @property id Unique identifier for the security category.
 * @property title Display title of the security check.
 * @property description Detailed status or finding of the check.
 * @property status The current [SecurityStatus] for this category.
 */
data class SecurityCategory(
    val id: String,
    val title: String,
    val description: String,
    val status: SecurityStatus
)

/**
 * Summary of the complete security health scan.
 *
 * @property overallScore Health score ranging from 0 to 100.
 * @property categories List of individual [SecurityCategory] results.
 */
data class SecurityHealthSummary(
    val overallScore: Int,
    val categories: List<SecurityCategory>
)

/**
 * Sealed class representing the different states of a security scan.
 */
sealed class ScanState {
    /**
     * Scan is currently in progress.
     * @property progress Current progress percentage (0-100).
     */
    data class Scanning(val progress: Int) : ScanState()

    /**
     * Scan has finished successfully.
     * @property summary The resulting [SecurityHealthSummary].
     */
    data class Completed(val summary: SecurityHealthSummary) : ScanState()
}
