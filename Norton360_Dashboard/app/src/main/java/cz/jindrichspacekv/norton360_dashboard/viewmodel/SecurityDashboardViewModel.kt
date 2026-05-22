package cz.jindrichspacekv.norton360_dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jindrichspacekv.norton360_dashboard.data.ScanState
import cz.jindrichspacekv.norton360_dashboard.data.SecurityCategory
import cz.jindrichspacekv.norton360_dashboard.data.SecurityHealthSummary
import cz.jindrichspacekv.norton360_dashboard.data.SecurityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Sealed interface representing the UI states of the Security Dashboard.
 */
sealed interface SecurityUiState {
    data object Idle : SecurityUiState
    data class Scanning(val progress: Int, val partialCategories: List<SecurityCategory> = emptyList()) : SecurityUiState
    data class Completed(val summary: SecurityHealthSummary) : SecurityUiState
    data class Error(val message: String? = null) : SecurityUiState
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
     * Prevents concurrent scans and handles errors.
     */
    fun startScan() {
        // Prevent concurrent scans
        if (_uiState.value is SecurityUiState.Scanning) return

        viewModelScope.launch {
            repository.performSecurityScan()
                .catch { exception ->
                    _uiState.value = SecurityUiState.Error(exception.message)
                }
                .collect { scanState ->
                    _uiState.value = when (scanState) {
                        is ScanState.Scanning -> SecurityUiState.Scanning(
                            scanState.progress,
                            scanState.partialCategories
                        )
                        is ScanState.Completed -> SecurityUiState.Completed(scanState.summary)
                    }
                }
        }
    }
}
