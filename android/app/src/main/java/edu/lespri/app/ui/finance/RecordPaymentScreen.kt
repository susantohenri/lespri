package edu.lespri.app.ui.finance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.lespri.app.domain.model.PaymentMethod
import kotlinx.coroutines.launch

/**
 * Layar Catat Pembayaran (P18).
 * Form untuk memasukkan nominal, metode pembayaran, dan catatan referensi.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPaymentScreen(
    onBack: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    viewModel: RecordPaymentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var amountText by remember { mutableStateOf("") }
    var method by remember { mutableStateOf(PaymentMethod.CASH) }
    var note by remember { mutableStateOf("") }
    var methodExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar("Pembayaran berhasil dicatat")
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catat Pembayaran") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val remainingBalance = uiState.remainingBalance
        if (remainingBalance == null || remainingBalance <= 0L) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(24.dp)
                    )
                    Text(
                        text = "Tagihan sudah lunas",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.TextButton(onClick = onBack) {
                        Text("Kembali")
                    }
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Balance info
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Sisa Tagihan",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatRupiah(remainingBalance),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Amount input
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    if (it.all { c -> c.isDigit() }) {
                        amountText = it
                    }
                },
                label = { Text("Nominal Pembayaran") },
                placeholder = { Text("Contoh: 50000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Method dropdown
            var menuExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = menuExpanded,
                onExpandedChange = { menuExpanded = !menuExpanded }
            ) {
                OutlinedTextField(
                    value = when (method) {
                        PaymentMethod.CASH -> "Tunai"
                        PaymentMethod.TRANSFER -> "Transfer"
                        PaymentMethod.OTHER -> "Lainnya"
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Metode Pembayaran") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Tunai") },
                        onClick = {
                            method = PaymentMethod.CASH
                            menuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Transfer") },
                        onClick = {
                            method = PaymentMethod.TRANSFER
                            menuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Lainnya") },
                        onClick = {
                            method = PaymentMethod.OTHER
                            menuExpanded = false
                        }
                    )
                }
            }

            // Note input
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Catatan (opsional)") },
                placeholder = { Text("Nomor referensi, dll.") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            // Preview
            val amountLong = amountText.toLongOrNull() ?: 0L
            if (amountLong > 0L) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Sisa setelah bayar:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatRupiah((remainingBalance - amountLong).coerceAtLeast(0L)),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Submit button
            Button(
                onClick = {
                    val amount = amountText.toLongOrNull() ?: 0L
                    viewModel.recordPayment(amount, method, note.ifBlank { null }) {
                        onBack()
                    }
                },
                enabled = amountText.isNotBlank() && amountText.toLongOrNull()?.let { it > 0 && it <= remainingBalance } == true,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Menyimpan...")
                } else {
                    Text("Catat Pembayaran")
                }
            }
        }
    }
}

private fun formatRupiah(amount: Long): String {
    return "Rp ${String.format("%,d", amount).replace(',', '.')} "
}
