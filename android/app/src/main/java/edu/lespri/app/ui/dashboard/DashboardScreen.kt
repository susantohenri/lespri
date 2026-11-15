package edu.lespri.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.lespri.app.domain.model.LessonSession
import edu.lespri.app.ui.components.CompactRow
import edu.lespri.app.ui.components.EmptyStateView
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.navigation.NavRoutes
import edu.lespri.app.ui.theme.PrimaryBlue
import edu.lespri.app.ui.theme.SecondaryTeal
import edu.lespri.app.ui.theme.StatusError
import edu.lespri.app.ui.theme.StatusWarning
import edu.lespri.app.util.CurrencyFormatter
import edu.lespri.app.util.DateTimeUtil

/**
 * Layar Dashboard Utama Bimbel (P02).
 * Pusat kendali operasional harian: ringkasan keuangan, sesi hari ini, dan peringatan aksi cepat.
 */
@Composable
fun DashboardScreen(
    onNavigateToRoute: (String) -> Unit,
    onNavigateToSession: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LespriScaffold(
        currentRoute = NavRoutes.Dashboard.route,
        onNavigateToRoute = onNavigateToRoute,
        title = uiState.businessName.ifBlank { "Lespri" },
        subtitle = "Ringkasan Operasional Bimbel"
    ) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Ringkasan Keuangan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // 3 Kolom Metrik Keuangan
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            label = "Tunggakan",
                            value = CurrencyFormatter.formatCompactRupiah(uiState.totalOutstandingDebt),
                            subtext = "${uiState.unpaidStudentsCount} tagihan",
                            accentColor = if (uiState.totalOutstandingDebt > 0) StatusError else SecondaryTeal,
                            modifier = Modifier.weight(1f)
                        )

                        MetricCard(
                            label = "Pemasukan",
                            value = CurrencyFormatter.formatCompactRupiah(uiState.currentMonthIncome),
                            subtext = "Bulan ini",
                            accentColor = PrimaryBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Alert Action Banners (jika ada sesi belum diabsen atau paket hampir habis)
                if (uiState.unrecordedSessionsCount > 0 || uiState.lowPackageStudentsCount > 0) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (uiState.unrecordedSessionsCount > 0) {
                                AlertBanner(
                                    icon = Icons.Default.WarningAmber,
                                    title = "${uiState.unrecordedSessionsCount} Sesi Belum Diabsen",
                                    description = "Ketuk untuk membuka agenda dan mencatat presensi.",
                                    tint = StatusWarning,
                                    onClick = { onNavigateToRoute(NavRoutes.Schedule.route) }
                                )
                            }

                            if (uiState.lowPackageStudentsCount > 0) {
                                AlertBanner(
                                    icon = Icons.Default.NotificationsActive,
                                    title = "${uiState.lowPackageStudentsCount} Siswa Saldo Paket Rendah",
                                    description = "Saldo paket ≤ 2 sesi tersisa. Kirim pengingat perpanjangan.",
                                    tint = PrimaryBlue,
                                    onClick = { onNavigateToRoute(NavRoutes.Finance.route) }
                                )
                            }
                        }
                    }
                }

                // Aksi Cepat (Quick Actions)
                item {
                    Text(
                        text = "Aksi Cepat",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionButton(
                            icon = Icons.Default.PersonAdd,
                            label = "Tambah Siswa",
                            onClick = { onNavigateToRoute(NavRoutes.StudentForm.createRoute()) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            icon = Icons.Default.CalendarMonth,
                            label = "Buat Sesi",
                            onClick = { onNavigateToRoute(NavRoutes.SessionForm.createRoute()) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            icon = Icons.Default.Payment,
                            label = "Keuangan",
                            onClick = { onNavigateToRoute(NavRoutes.Finance.route) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Sesi Hari Ini
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Jadwal Hari Ini (${DateTimeUtil.formatDisplayDate(DateTimeUtil.getTodayIsoDate())})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                if (uiState.todaySessions.isEmpty()) {
                    item {
                        EmptyStateView(
                            title = "Tidak ada sesi hari ini",
                            description = "Gunakan tombol 'Buat Sesi' atau atur jadwal berulang di menu Kelas.",
                            actionLabel = "Lihat Semua Jadwal",
                            onActionClick = { onNavigateToRoute(NavRoutes.Schedule.route) }
                        )
                    }
                } else {
                    items(uiState.todaySessions, key = { it.id }) { session ->
                        CompactRow(
                            title = "Sesi Pelajaran: ${session.startTime} (${session.durationMinutes} mnt)",
                            subtitle = "Status: ${session.status.name}",
                            extraInfo = "Ketuk untuk melihat lembar presensi dan detail sesi",
                            trailingContent = {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            },
                            onClick = { onNavigateToSession(session.id) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    subtext: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun AlertBanner(
    icon: ImageVector,
    title: String,
    description: String,
    tint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = tint.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = tint)
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = tint)
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
        }
    }
}
