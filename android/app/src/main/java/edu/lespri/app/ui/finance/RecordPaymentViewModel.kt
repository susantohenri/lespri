package edu.lespri.app.ui.finance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.model.Payment
import edu.lespri.app.domain.model.PaymentMethod
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
 * State UI untuk layar Catat Pembayaran (P18).
 */
data class RecordPaymentUiState(
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val remainingBalance: Long? = null,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel untuk layar Catat Pembayaran (P18).
 */
@HiltViewModel
class RecordPaymentViewModel @Inject constructor(
    private val financeRepository: FinanceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val chargeId: String = savedStateHandle.get<String>("chargeId").orEmpty()

    private val _uiState = MutableStateFlow(RecordPaymentUiState())
    val uiState: StateFlow<RecordPaymentUiState> = _uiState.asStateFlow()

    init {
        loadChargeBalance()
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun recordPayment(
        amount: Long,
        method: PaymentMethod,
        note: String?,
        onSuccess: () -> Unit
    ) {
        if (amount <= 0L) {
            _uiState.update {
                it.copy(errorMessage = "Nominal pembayaran harus lebih besar dari Rp 0")
            }
            return
        }

        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

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
                        it.copy(isSubmitting = false, isSuccess = true)
                    }
                    onSuccess()
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = throwable.message ?: "Gagal mencatat pembayaran"
                        )
                    }
                }
        }
    }

    private fun loadChargeBalance() {
        if (chargeId.isBlank()) {
            _uiState.update {
                it.copy(isLoading = false, errorMessage = "ID tagihan tidak valid")
            }
            return
        }

        viewModelScope.launch {
            financeRepository.getCharges(null)
                .catch { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = throwable.message)
                    }
                }
                .collect { charges ->
                    val target = charges.find { it.charge.id == chargeId }
                    _uiState.update {
                        it.copy(
                            remainingBalance = target?.remainingBalance,
                            isLoading = false
                        )
                    }
                }
        }
    }
}
