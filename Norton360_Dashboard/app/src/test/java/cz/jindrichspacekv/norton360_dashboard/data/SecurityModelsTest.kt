package cz.jindrichspacekv.norton360_dashboard.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SecurityModelsTest {

    // AI-generated
    @Test
    fun `SecurityHealthSummary handles empty categories list`() {
        val summary = SecurityHealthSummary(
            overallScore = 0,
            categories = emptyList()
        )
        
        assertEquals(0, summary.overallScore)
        assertEquals(0, summary.categories.size)
    }

    // AI-generated
    @Test
    fun `ScanState Scanning preserves data integrity`() {
        val categories = listOf(
            SecurityCategory("1", "Title", "Desc", SecurityStatus.SAFE)
        )
        val scanningState = ScanState.Scanning(
            progress = 75,
            partialCategories = categories
        )
        
        assertEquals(75, scanningState.progress)
        assertEquals(categories, scanningState.partialCategories)
        assertEquals(1, scanningState.partialCategories.size)
    }
}
