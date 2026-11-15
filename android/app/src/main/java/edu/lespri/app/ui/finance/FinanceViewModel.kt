package edu.lespri.app.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.model.ChargeStatus
import edu.lespri.app.domain.model.ChargeWithBalance
import edu.lespri.app.domain.model.FinanceTab
import edu.lespri.app.domain.model.FinancialSummary
import edu.lespri.app.domain.model.Payment
import edu.lespri.app.domain.model.PaymentListItem
import edu.lespri.app.domain.model.PaymentMethod
import edu.lespri.app.domain.model.PackageStatusItem
import edu.lespri.app.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * State UI untuk layar Keuangan (P16).
 */
data class FinanceOverviewUiState(
    val isLoading: Boolean = true,
    val activeTab: FinanceTab = FinanceTab.CHARGES,
    val financialSummary: FinancialSummary? = null,
    val chargesList: List<ChargeWithBalance> = emptyList(),
    val paymentsList: List<PaymentListItem> = emptyList(),
    val packagesList: List<PackageStatusItem> = emptyList(),
    val chargeStatusFilter: ChargeStatus? = null,
    val isRecordingPayment: Boolean = false,
    val paymentSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel untuk layar Keuangan (P16).
 * Menangani agregasi utang, filter tagihan, riwayat pembayaran, dan status paket.
 */
@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val financeRepository: FinanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinanceOverviewUiState())
    val uiState: StateFlow<FinanceOverviewUiState> = _uiState.asStateFlow()

    init {
        loadFinancialSummary()
        loadCharges()
    }

    /**
     * Ganti tab aktif (Tagihan, Pembayaran, Paket).
     */
    fun onTabSelected(tab: FinanceTab) {
        _uiState.update { it.copy(activeTab = tab) }
        when (tab) {
            FinanceTab.CHARGES -> loadCharges()
            FinanceTab.PAYMENTS -> loadPayments()
            FinanceTab.PACKAGES -> loadPackages()
        }
    }

    /**
     * Terapkan filter status tagihan.
     */
    fun onChargeStatusFilterChanged(status: ChargeStatus?) {
        _uiState.update { it.copy(chargeStatusFilter = status) }
        loadCharges(status)
    }

    /**
     * Catat pembayaran baru untuk tagihan tertentu.
     */
    fun recordPayment(chargeId: String, amount: Long, method: PaymentMethod, note: String?) {
        if (amount <= 0L) {
            _uiState.update {
                it.copy(errorMessage = "Nominal pembayaran harus lebih besar dari Rp 0")
            }
            return
        }

        _uiState.update { it.copy(isRecordingPayment = true, errorMessage = null, paymentSuccess = false) }

        val payment = Payment(
            id = UUID.randomUUID().toString(),
            chargeId = chargeId,
            amount = amount,
            method = method,
            referenceNote = note
        )

        viewModelScope.launch {
            financeRepository.recordPayment(payment)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isRecordingPayment = false,
                            paymentSuccess = true
                        )
                    }
                    loadCharges(_uiState.value.chargeStatusFilter)
                    loadFinancialSummary()
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isRecordingPayment = false,
                            errorMessage = throwable.message ?: "Gagal mencatat pembayaran"
                        )
                    }
                }
        }
    }

    /**
     * Bersihkan pesan sukses/error.
     */
    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, paymentSuccess = false) }
    }

    private fun loadFinancialSummary() {
        viewModelScope.launch {
            financeRepository.getFinancialSummary()
                .catch { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = throwable.message)
                    }
                }
                .collect { summary ->
                    _uiState.update { it.copy(financialSummary = summary, isLoading = false) }
                }
        }
    }

    private fun loadCharges(statusFilter: ChargeStatus? = null) {
        viewModelScope.launch {
            financeRepository.getCharges(statusFilter)
                .catch { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = throwable.message)
                    }
                }
                .collect { charges ->
                    _uiState.update { it.copy(chargesList = charges, isLoading = false) }
                }
        }
    }

    private fun loadPayments() {
        // Riwayat pembayaran diambil dari charges yang sudah dibayar
        // dan dipetakan ke PaymentListItem
        viewModelScope.launch {
            financeRepository.getCharges(ChargeStatus.PAID)
                .catch { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = throwable.message)
                    }
                }
                .collect { paidCharges ->
                    val paymentItems = paidCharges.map { chargeWithBalance ->
                        PaymentListItem(
                            id = chargeWithBalance.charge.id,
                            chargeId = chargeWithBalance.charge.id,
                            studentName = chargeWithBalance.studentName,
                            className = chargeWithBalance.className,
                            amount = chargeWithBalance.charge.amount,
                            paidAt = chargeWithBalance.charge.createdAt,
                            method = PaymentMethod.CASH,
                            referenceNote = null
                        )
                    }
                    _uiState.update { it.copy(paymentsList = paymentItems, isLoading = false) }
                }
        }
    }

    private fun loadPackages() {
        // Paket sesi: saat ini diambil dari charges tipe PACKAGE
        // yang masih memiliki sisa saldo
        viewModelScope.launch {
            financeRepository.getCharges(null)
                .catch { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = throwable.message)
                    }
                }
                .collect { allCharges ->
                    val packageItems = allCharges
                        .filter { it.charge.chargeType.name == "PACKAGE" }
                        .map { chargeWithBalance ->
                            PackageStatusItem(
                                enrollmentId = chargeWithBalance.charge.enrollmentId,
                                studentName = chargeWithBalance.studentName,
                                className = chargeWithBalance.className,
                                totalPurchasedSessions = 10,
                                remainingSessions = chargeWithBalance.remainingBalance.toInt().coerceAtLeast(0)
                            )
                        }
                    _uiState.update { it.copy(packagesList = packageItems, isLoading = false) }
                }
        }
    }
}
