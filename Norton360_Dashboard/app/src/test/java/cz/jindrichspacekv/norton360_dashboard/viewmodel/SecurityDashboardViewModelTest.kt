package cz.jindrichspacekv.norton360_dashboard.viewmodel

import app.cash.turbine.test
import cz.jindrichspacekv.norton360_dashboard.data.ScanState
import cz.jindrichspacekv.norton360_dashboard.data.SecurityCategory
import cz.jindrichspacekv.norton360_dashboard.data.SecurityHealthSummary
import cz.jindrichspacekv.norton360_dashboard.data.SecurityRepository
import cz.jindrichspacekv.norton360_dashboard.data.SecurityStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SecurityDashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: SecurityRepository = mock()
    private lateinit var viewModel: SecurityDashboardViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SecurityDashboardViewModel(repository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }


    // AI-generated
    @Test
    fun `startScan transitions through Scanning to Completed with correct data`() = runTest {
        val mockSummary = SecurityHealthSummary(
            overallScore = 90,
            categories = listOf(
                SecurityCategory("id", "Title", "Description", SecurityStatus.SAFE)
            )
        )
        
        whenever(repository.performSecurityScan()).thenReturn(
            flowOf(
                ScanState.Scanning(50),
                ScanState.Completed(mockSummary)
            )
        )

        viewModel.uiState.test {
            assertEquals(SecurityUiState.Idle, awaitItem())
            
            viewModel.startScan()
            
            val scanningState = awaitItem() as SecurityUiState.Scanning
            assertEquals(50, scanningState.progress)
            
            val completedState = awaitItem() as SecurityUiState.Completed
            assertEquals(90, completedState.summary.overallScore)
            assertEquals(1, completedState.summary.categories.size)
        }
    }


    // AI-generated
    @Test
    fun `startScan reflects partial categories during scanning`() = runTest {
        val partialCategory = SecurityCategory("id1", "Title1", "Desc1", SecurityStatus.SAFE)
        
        whenever(repository.performSecurityScan()).thenReturn(
            flowOf(
                ScanState.Scanning(25, listOf(partialCategory)),
                ScanState.Scanning(50, listOf(partialCategory))
            )
        )

        viewModel.uiState.test {
            assertEquals(SecurityUiState.Idle, awaitItem())
            
            viewModel.startScan()
            
            val scanningState1 = awaitItem() as SecurityUiState.Scanning
            assertEquals(25, scanningState1.progress)
            assertEquals(listOf(partialCategory), scanningState1.partialCategories)
            
            val scanningState2 = awaitItem() as SecurityUiState.Scanning
            assertEquals(50, scanningState2.progress)
            assertEquals(listOf(partialCategory), scanningState2.partialCategories)
        }
    }
}
