package edu.lespri.app.ui.finance

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.model.ChargeWithBalance
import edu.lespri.app.domain.repository.FinanceRepository
import edu.lespri.app.util.SharesheetHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State UI untuk layar Kuitansi Pembayaran (P19).
 */
data class PaymentReceiptUiState(
    val isLoading: Boolean = true,
    val chargeWithBalance: ChargeWithBalance? = null,
    val errorMessage: String? = null
)

/**
 * ViewModel untuk layar Kuitansi Pembayaran (P19).
 * Menampilkan detail kuitansi berdasarkan chargeId yang sama.
 */
@HiltViewModel
class PaymentReceiptViewModel @Inject constructor(
    private val financeRepository: FinanceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // paymentId di route sebenarnya adalah chargeId untuk kemudahan navigasi
    val paymentId: String = savedStateHandle.get<String>("paymentId").orEmpty()

    private val _uiState = MutableStateFlow(PaymentReceiptUiState())
    val uiState: StateFlow<PaymentReceiptUiState> = _uiState.asStateFlow()

    init {
        loadReceipt()
    }

    fun shareReceipt(context: Context) {
        val charge = _uiState.value.chargeWithBalance ?: return

        val message = SharesheetHelper.formatReceiptMessage(
            bimbelName = "Bimbel Lespri",
            studentName = charge.studentName,
            className = charge.className,
            paymentAmount = charge.totalPaid,
            chargeAmount = charge.charge.amount,
            remainingBalance = charge.remainingBalance,
            paymentDate = java.text.SimpleDateFormat(
                "dd MMM yyyy", java.util.Locale("id")
            ).format(java.util.Date(charge.charge.createdAt)),
            method = "Tunai"
        )

        SharesheetHelper.shareGeneric(context, "Kwitansi — ${charge.studentName}", message)
    }

    private fun loadReceipt() {
        if (paymentId.isBlank()) {
            _uiState.update {
                it.copy(isLoading = false, errorMessage = "ID pembayaran tidak valid")
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
                    val target = charges.find { it.charge.id == paymentId }
                    _uiState.update {
                        it.copy(
                            chargeWithBalance = target,
                            isLoading = false
                        )
                    }
                }
        }
    }
}
