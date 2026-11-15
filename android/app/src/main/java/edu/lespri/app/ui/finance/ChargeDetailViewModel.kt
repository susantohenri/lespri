package edu.lespri.app.ui.finance

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.model.ChargeWithBalance
import edu.lespri.app.domain.model.Payment
import edu.lespri.app.domain.model.PaymentListItem
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
 * State UI untuk layar Detail Tagihan (P17).
 */
data class ChargeDetailUiState(
    val isLoading: Boolean = true,
    val chargeWithBalance: ChargeWithBalance? = null,
    val paymentHistory: List<PaymentListItem> = emptyList(),
    val paymentSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val charge = chargeWithBalance?.charge
}

/**
 * ViewModel untuk layar Detail Tagihan (P17).
 */
@HiltViewModel
class ChargeDetailViewModel @Inject constructor(
    private val financeRepository: FinanceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val chargeId: String = savedStateHandle.get<String>("chargeId").orEmpty()

    private val _uiState = MutableStateFlow(ChargeDetailUiState())
    val uiState: StateFlow<ChargeDetailUiState> = _uiState.asStateFlow()

    init {
        loadChargeDetail()
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, paymentSuccess = false) }
    }

    fun shareCharge(context: Context) {
        val state = _uiState.value
        val item = state.chargeWithBalance ?: return

        val message = SharesheetHelper.formatChargeMessage(
            bimbelName = "Bimbel Lespri",
            studentName = item.studentName,
            className = item.className,
            chargeAmount = item.charge.amount,
            remainingBalance = item.remainingBalance,
            dueDate = item.charge.dueDate
        )

        SharesheetHelper.shareGeneric(context, "Tagihan — ${item.studentName}", message)
    }

    private fun loadChargeDetail() {
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
                            chargeWithBalance = target,
                            isLoading = false
                        )
                    }
                }
        }
    }
}
