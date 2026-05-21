package cz.jindrichspacekv.norton360_dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jindrichspacekv.norton360_dashboard.data.ScanState
import cz.jindrichspacekv.norton360_dashboard.data.SecurityHealthSummary
import cz.jindrichspacekv.norton360_dashboard.data.SecurityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Sealed interface representing the UI states of the Security Dashboard.
 */
sealed interface SecurityUiState {
    data object Idle : SecurityUiState
    data class Scanning(val progress: Int) : SecurityUiState
    data class Completed(val summary: SecurityHealthSummary) : SecurityUiState
}

/**
 * ViewModel for the Security Health Dashboard.
 * Manages the scan lifecycle and exposes state to the UI via [StateFlow].
 *
 * @property repository The [SecurityRepository] source for security scans.
 */
@HiltViewModel
class SecurityDashboardViewModel @Inject constructor(
    private val repository: SecurityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SecurityUiState>(SecurityUiState.Idle)
    /**
     * Observable state for the UI to collect.
     */
    val uiState: StateFlow<SecurityUiState> = _uiState.asStateFlow()

    /**
     * Initiates the security scan process.
     * Updates the [_uiState] based on the repository's emission.
     */
    fun startScan() {
        viewModelScope.launch {
            repository.performSecurityScan().collect { scanState ->
                _uiState.value = when (scanState) {
                    is ScanState.Scanning -> SecurityUiState.Scanning(scanState.progress)
                    is ScanState.Completed -> SecurityUiState.Completed(scanState.summary)
                }
            }
        }
    }
}
