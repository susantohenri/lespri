package edu.lespri.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.model.LessonSession
import edu.lespri.app.domain.model.LespriConstants
import edu.lespri.app.domain.repository.AttendanceRepository
import edu.lespri.app.domain.repository.FinanceRepository
import edu.lespri.app.domain.repository.ScheduleRepository
import edu.lespri.app.domain.repository.SettingRepository
import edu.lespri.app.util.DateTimeUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State UI untuk Dashboard (P02).
 */
data class DashboardUiState(
    val isLoading: Boolean = false,
    val businessName: String = "",
    val totalOutstandingDebt: Long = 0L,
    val unpaidStudentsCount: Int = 0,
    val currentMonthIncome: Long = 0L,
    val todaySessions: List<LessonSession> = emptyList(),
    val unrecordedSessionsCount: Int = 0,
    val lowPackageStudentsCount: Int = 0,
    val errorMessage: String? = null
)

/**
 * ViewModel untuk Dashboard (P02).
 * Mengagregasi data tunggakan, sesi hari ini, sesi belum diabsen, dan siswa dengan saldo paket rendah.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val settingRepository: SettingRepository,
    private val financeRepository: FinanceRepository,
    private val scheduleRepository: ScheduleRepository,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        val today = DateTimeUtil.getTodayIsoDate()

        viewModelScope.launch {
            // Auto-generate upcoming recurring sessions untuk 30 hari ke depan
            val future30 = DateTimeUtil.getIsoDatePlusDays(LespriConstants.SCHEDULE_GENERATION_WINDOW_DAYS)
            scheduleRepository.generateRecurringSessions(future30)
        }

        viewModelScope.launch {
            combine(
                settingRepository.getOwnerProfile(),
                financeRepository.getFinancialSummary(),
                scheduleRepository.getAgendaSessions(today, today),
                attendanceRepository.countUnrecordedSessions(today),
                financeRepository.countLowBalanceEnrollments(LespriConstants.DEFAULT_LOW_SESSION_THRESHOLD)
            ) { ownerProfile, financialSummary, sessionsToday, unrecordedCount, lowPackageCount ->
                DashboardUiState(
                    isLoading = false,
                    businessName = ownerProfile?.businessName ?: "Lespri Bimbel",
                    totalOutstandingDebt = financialSummary.totalOutstandingDebt,
                    unpaidStudentsCount = financialSummary.unpaidChargesCount,
                    currentMonthIncome = financialSummary.currentMonthIncome,
                    todaySessions = sessionsToday,
                    unrecordedSessionsCount = unrecordedCount,
                    lowPackageStudentsCount = lowPackageCount,
                    errorMessage = null
                )
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
}
