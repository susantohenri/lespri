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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.lespri.app.domain.model.ChargeStatus
import edu.lespri.app.domain.model.ChargeWithBalance
import edu.lespri.app.domain.model.FinanceTab
import edu.lespri.app.domain.model.PackageStatusItem
import edu.lespri.app.domain.model.PaymentListItem
import edu.lespri.app.ui.components.ChargeStatusChip
import edu.lespri.app.ui.components.CompactRow
import edu.lespri.app.ui.components.EmptyStateView
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.components.StatusBadge
import edu.lespri.app.ui.navigation.NavRoutes
import edu.lespri.app.ui.theme.StatusError
import edu.lespri.app.ui.theme.StatusErrorContainer
import edu.lespri.app.ui.theme.StatusInfo
import edu.lespri.app.ui.theme.StatusInfoContainer
import edu.lespri.app.ui.theme.StatusSuccess
import edu.lespri.app.ui.theme.StatusSuccessContainer
import edu.lespri.app.ui.theme.StatusWarning
import edu.lespri.app.ui.theme.StatusWarningContainer
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Layar Keuangan Bimbel (P16).
 * Menampilkan ringkasan keuangan, daftar tagihan, riwayat pembayaran, dan status paket.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceOverviewScreen(
    onNavigateToRoute: (String) -> Unit,
    viewModel: FinanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (uiState.errorMessage != null) {
            // Auto-clear handled by user action
        }
    }

    LespriScaffold(
        currentRoute = NavRoutes.Finance.route,
        onNavigateToRoute = onNavigateToRoute,
        title = "Keuangan"
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Summary cards
            FinanceSummaryCards(summary = uiState.financialSummary)

            // Tab row
            val tabs = listOf(
                FinanceTabItem(FinanceTab.CHARGES, "Tagihan", Icons.Default.ReceiptLong),
                FinanceTabItem(FinanceTab.PAYMENTS, "Pembayaran", Icons.Default.CheckCircle),
                FinanceTabItem(FinanceTab.PACKAGES, "Paket", Icons.Default.AccountBalanceWallet)
            )

            var selectedTabIndex by androidx.compose.runtime.mutableStateOf(
                tabs.indexOfFirst { it.tab == uiState.activeTab }.coerceAtLeast(0)
            )

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.PrimaryIndicator(
                        pos = tabPositions[selectedTabIndex],
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            viewModel.onTabSelected(tab.tab)
                        },
                        text = { Text(tab.label) },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }

            // Tab content
            when (uiState.activeTab) {
                FinanceTab.CHARGES -> ChargesTab(
                    state = uiState,
                    onChargeClick = { chargeId ->
                        onNavigateToRoute(NavRoutes.ChargeDetail.createRoute(chargeId))
                    },
                    onFilterChanged = { status ->
                        viewModel.onChargeStatusFilterChanged(status)
                    },
                    currentFilter = uiState.chargeStatusFilter
                )
                FinanceTab.PAYMENTS -> PaymentsTab(
                    payments = uiState.paymentsList,
                    isLoading = uiState.isLoading
                )
                FinanceTab.PACKAGES -> PackagesTab(
                    packages = uiState.packagesList,
                    isLoading = uiState.isLoading
                )
            }

            // Error snackbar
            uiState.errorMessage?.let { error ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = StatusError,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        color = StatusError,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private data class FinanceTabItem(
    val tab: FinanceTab,
    val label: String,
    val icon: ImageVector
)

@Composable
private fun FinanceSummaryCards(summary: edu.lespri.app.domain.model.FinancialSummary?) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                title = "Tunggakan",
                value = summary?.totalOutstandingDebt?.let { formatRupiah(it) } ?: "Rp 0",
                subtitle = "${summary?.unpaidChargesCount ?: 0} tagihan",
                color = StatusError,
                bgColor = StatusErrorContainer,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "Pemasukan",
                value = summary?.currentMonthIncome?.let { formatRupiah(it) } ?: "Rp 0",
                subtitle = "Bulan ini",
                color = StatusSuccess,
                bgColor = StatusSuccessContainer,
                modifier = Modifier.weight(1f)
            )
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    subtitle: String,
    color: androidx.compose.ui.graphics.Color,
    bgColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        StatusBadge(
            text = title,
            backgroundColor = bgColor,
            contentColor = color
        )
        Spacer(modifier = androidx.compose.foundation.layout.Modifier.padding(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ChargesTab(
    state: FinanceOverviewUiState,
    onChargeClick: (String) -> Unit,
    onFilterChanged: (ChargeStatus?) -> Unit,
    currentFilter: ChargeStatus?
) {
    if (state.isLoading && state.chargesList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // Filter chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    label = "Semua",
                    selected = currentFilter == null,
                    onClick = { onFilterChanged(null) }
                )
                FilterChip(
                    label = "Belum Lunas",
                    selected = currentFilter == ChargeStatus.UNPAID,
                    onClick = { onFilterChanged(ChargeStatus.UNPAID) }
                )
                FilterChip(
                    label = "Cicil",
                    selected = currentFilter == ChargeStatus.PARTIALLY_PAID,
                    onClick = { onFilterChanged(ChargeStatus.PARTIALLY_PAID) }
                )
                FilterChip(
                    label = "Lunas",
                    selected = currentFilter == ChargeStatus.PAID,
                    onClick = { onFilterChanged(ChargeStatus.PAID) }
                )
            }
        }

        if (state.chargesList.isEmpty()) {
            item {
                EmptyStateView(
                    title = "Belum ada tagihan",
                    description = "Tagihan akan muncul setelah Anda membuat tagihan baru.",
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            items(state.chargesList, key = { it.charge.id }) { chargeWithBalance ->
                ChargeRow(
                    chargeWithBalance = chargeWithBalance,
                    onClick = { onChargeClick(chargeWithBalance.charge.id) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    androidx.compose.material3.FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) }
    )
}

@Composable
private fun ChargeRow(
    chargeWithBalance: ChargeWithBalance,
    onClick: () -> Unit
) {
    CompactRow(
        title = "${chargeWithBalance.studentName} — ${chargeWithBalance.className}",
        subtitle = chargeWithBalance.charge.dueDate?.let { "Jatuh tempo: $it" } ?: "Tagihan",
        extraInfo = "Sisa: ${formatRupiah(chargeWithBalance.remainingBalance)}",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        onClick = onClick,
        trailingContent = {
            ChargeStatusChip(status = chargeWithBalance.charge.status)
        }
    )
}

@Composable
private fun PaymentsTab(
    payments: List<PaymentListItem>,
    isLoading: Boolean
) {
    if (isLoading && payments.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (payments.isEmpty()) {
        EmptyStateView(
            title = "Belum ada pembayaran",
            description = "Riwayat pembayaran tagihan yang sudah lunas akan muncul di sini.",
            modifier = Modifier.padding(32.dp)
        )
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(payments, key = { it.id }) { payment ->
                CompactRow(
                    title = "${payment.studentName} — ${payment.className}",
                    subtitle = formatDate(payment.paidAt),
                    extraInfo = formatRupiah(payment.amount),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    trailingContent = {
                        StatusBadge(
                            text = "Lunas",
                            backgroundColor = StatusSuccessContainer,
                            contentColor = StatusSuccess
                        )
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
private fun PackagesTab(
    packages: List<PackageStatusItem>,
    isLoading: Boolean
) {
    if (isLoading && packages.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (packages.isEmpty()) {
        EmptyStateView(
            title = "Belum ada paket",
            description = "Paket sesi yang aktif akan muncul di sini.",
            modifier = Modifier.padding(32.dp)
        )
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(packages, key = { it.enrollmentId }) { pkg ->
                val isLow = pkg.remainingSessions <= 3
                CompactRow(
                    title = "${pkg.studentName} — ${pkg.className}",
                    subtitle = "Total: ${pkg.totalPurchasedSessions} sesi",
                    extraInfo = "Sisa: ${pkg.remainingSessions} sesi",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    trailingContent = {
                        if (isLow) {
                            StatusBadge(
                                text = "Hampir Habis",
                                backgroundColor = StatusWarningContainer,
                                contentColor = StatusWarning
                            )
                        } else {
                            StatusBadge(
                                text = "Aktif",
                                backgroundColor = StatusSuccessContainer,
                                contentColor = StatusSuccess
                            )
                        }
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

private fun formatRupiah(amount: Long): String {
    return "Rp ${String.format("%,d", amount).replace(',', '.')} "
}

private fun formatDate(timestamp: Long): String {
    return try {
        SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id")).format(java.util.Date(timestamp))
    } catch (_: Exception) {
        ""
    }
}
