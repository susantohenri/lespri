package edu.lespri.app.ui.finance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.lespri.app.domain.model.ChargeStatus
import edu.lespri.app.ui.components.ChargeStatusChip
import edu.lespri.app.ui.components.CompactRow
import edu.lespri.app.ui.components.EmptyStateView
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.components.StatusBadge
import edu.lespri.app.ui.theme.StatusError
import edu.lespri.app.ui.theme.StatusErrorContainer
import edu.lespri.app.ui.theme.StatusSuccess
import edu.lespri.app.ui.theme.StatusSuccessContainer
import edu.lespri.app.ui.theme.StatusWarning
import edu.lespri.app.ui.theme.StatusWarningContainer
import kotlinx.coroutines.launch

/**
 * Layar Detail Tagihan (P17).
 * Menampilkan informasi tagihan, riwayat pembayaran, dan opsi untuk mencatat pembayaran.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargeDetailScreen(
    onBack: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    viewModel: ChargeDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.paymentSuccess) {
        if (uiState.paymentSuccess) {
            snackbarHostState.showSnackbar("Pembayaran berhasil dicatat")
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Tagihan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.shareCharge(context)
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Bagikan")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (uiState.charge != null && uiState.charge?.status != ChargeStatus.PAID) {
                FloatingActionButton(
                    onClick = {
                        onNavigateToRoute(
                            edu.lespri.app.ui.navigation.NavRoutes.RecordPayment.createRoute(
                                viewModel.chargeId
                            )
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Catat Pembayaran")
                }
            }
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

        val charge = uiState.chargeWithBalance
        if (charge == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    title = "Tagihan tidak ditemukan",
                    description = "Tagihan yang Anda cari tidak ada atau sudah dihapus.",
                    actionLabel = "Kembali",
                    onActionClick = onBack
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Charge info header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = charge.studentName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = charge.className,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        ChargeStatusChip(status = charge.charge.status)
                    }

                    Spacer(modifier = Modifier.padding(16.dp))

                    // Amount breakdown
                    ChargeInfoRow(
                        label = "Total Tagihan",
                        value = formatRupiah(charge.charge.amount),
                        isBold = true
                    )
                    ChargeInfoRow(
                        label = "Sudah Dibayar",
                        value = formatRupiah(charge.totalPaid),
                        color = StatusSuccess
                    )
                    ChargeInfoRow(
                        label = "Sisa Belum Lunas",
                        value = formatRupiah(charge.remainingBalance),
                        color = if (charge.remainingBalance > 0) StatusError else StatusSuccess,
                        isBold = true
                    )

                    charge.charge.dueDate?.let {
                        ChargeInfoRow(
                            label = "Jatuh Tempo",
                            value = it
                        )
                    }
                }
            }

            // Payment history
            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Text(
                    text = "Riwayat Pembayaran",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            if (uiState.paymentHistory.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "Belum ada pembayaran",
                        description = "Belum ada pembayaran untuk tagihan ini.",
                        modifier = Modifier.padding(32.dp)
                    )
                }
            } else {
                items(uiState.paymentHistory, key = { it.id }) { payment ->
                    CompactRow(
                        title = formatRupiah(payment.amount),
                        subtitle = payment.method.name.lowercase().replace("_", " "),
                        extraInfo = formatDate(payment.paidAt),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        trailingContent = {
                            StatusBadge(
                                text = "Diterima",
                                backgroundColor = StatusSuccessContainer,
                                contentColor = StatusSuccess
                            )
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }

            // Bottom padding
            item {
                Spacer(modifier = Modifier.padding(80.dp))
            }
        }
    }
}

@Composable
private fun ChargeInfoRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    color: androidx.compose.ui.graphics.Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = if (isBold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = color ?: MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatRupiah(amount: Long): String {
    return "Rp ${String.format("%,d", amount).replace(',', '.')} "
}

private fun formatDate(timestamp: Long): String {
    return try {
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy HH:mm", java.util.Locale("id"))
        sdf.format(java.util.Date(timestamp))
    } catch (e: Exception) {
        ""
    }
}
