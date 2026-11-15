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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.lespri.app.ui.components.ChargeStatusChip
import edu.lespri.app.ui.components.EmptyStateView
import edu.lespri.app.ui.components.StatusBadge
import edu.lespri.app.ui.theme.StatusSuccess
import edu.lespri.app.ui.theme.StatusSuccessContainer

/**
 * Layar Kuitansi Pembayaran (P19).
 * Menampilkan detail kuitansi yang bisa dibagikan.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentReceiptScreen(
    onBack: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    viewModel: PaymentReceiptViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kwitansi Pembayaran") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.shareReceipt(context)
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Bagikan Kuitansi")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
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
                    title = "Kwitansi tidak ditemukan",
                    description = "Data pembayaran yang Anda cari tidak ada.",
                    actionLabel = "Kembali",
                    onActionClick = onBack
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = StatusSuccess,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.padding(12.dp))
            Text(
                text = "KWITANSI PEMBAYARAN",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Bimbel Lespri",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.padding(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.padding(16.dp))

            // Student info
            ReceiptInfoRow(
                label = "Siswa",
                value = charge.charge.studentName
            )
            ReceiptInfoRow(
                label = "Kelas",
                value = charge.charge.className
            )
            ReceiptInfoRow(
                label = "Tanggal",
                value = formatDate(charge.charge.createdAt)
            )

            Spacer(modifier = Modifier.padding(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.padding(16.dp))

            // Financial breakdown
            ReceiptInfoRow(
                label = "Total Tagihan",
                value = formatRupiah(charge.charge.amount),
                isBold = true
            )
            ReceiptInfoRow(
                label = "Total Dibayar",
                value = formatRupiah(charge.totalPaid),
                color = StatusSuccess,
                isBold = true
            )
            ReceiptInfoRow(
                label = "Sisa Belum Lunas",
                value = formatRupiah(charge.remainingBalance),
                isBold = true
            )

            Spacer(modifier = Modifier.padding(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.padding(16.dp))

            // Status
            ChargeStatusChip(status = charge.charge.status)

            Spacer(modifier = Modifier.padding(24.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Kembali")
                }
                TextButton(
                    onClick = {
                        viewModel.shareReceipt(context)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bagikan")
                }
            }

            Spacer(modifier = Modifier.padding(32.dp))
        }
    }
}

@Composable
private fun ReceiptInfoRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    color: androidx.compose.ui.graphics.Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
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
        val sdf = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale("id"))
        sdf.format(java.util.Date(timestamp))
    } catch (_: Exception) {
        ""
    }
}
