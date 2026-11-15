package edu.lespri.app.ui.schedule

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.lespri.app.domain.model.LessonSessionDetail
import edu.lespri.app.domain.model.SessionStatus
import edu.lespri.app.domain.model.StudentAttendanceItem
import edu.lespri.app.ui.components.AttendanceStatusChip
import edu.lespri.app.ui.components.CompactRow
import edu.lespri.app.ui.components.EmptyStateView
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.components.LoadingOverlay
import edu.lespri.app.ui.components.SessionStatusChip
import edu.lespri.app.util.DateTimeUtil

/**
 * Detail Sesi (P14).
 * Menampilkan ringkasan sesi, peserta dengan status presensi, serta aksi mencatat kehadiran,
 * mengubah jadwal, dan membatalkan sesi.
 */
@Composable
fun SessionDetailScreen(
    onBack: () -> Unit,
    onNavigateToAttendance: (String) -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: SessionDetailViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isCancelled) {
        if (uiState.isCancelled) {
            onBack()
        }
    }

    val detail = uiState.detail

    LespriScaffold(
        currentRoute = null,
        onNavigateToRoute = {},
        title = detail?.className ?: "Detail Sesi",
        subtitle = "Detail & Presensi",
        showBottomBar = false,
        onBack = onBack,
        actions = {
            if (detail?.session?.status == SessionStatus.SCHEDULED) {
                IconButton(onClick = { onNavigateToEdit(viewModel.sessionId) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Ubah sesi")
                }
            }
        }
    ) {
        when {
            uiState.isLoading -> LoadingOverlay()

            detail == null -> {
                EmptyStateView(
                    title = "Sesi tidak ditemukan",
                    description = uiState.errorMessage ?: "Sesi mungkin sudah dihapus atau dibatalkan.",
                    actionLabel = "Kembali",
                    onActionClick = onBack
                )
            }

            else -> {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SessionSummaryCard(detail = detail)

                    if (detail.session.status == SessionStatus.CANCELLED) {
                        Text(
                            text = "Sesi ini dibatalkan. Saldo paket siswa yang sempat terpotong sudah dikembalikan.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Text(
                        text = "Peserta (${detail.attendanceRecords.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (detail.attendanceRecords.isEmpty()) {
                        EmptyStateView(
                            title = "Belum ada peserta",
                            description = "Tambahkan siswa ke kelas ini melalui Detail Kelas.",
                            icon = Icons.Default.Person
                        )
                    } else {
                        detail.attendanceRecords.forEach { student ->
                            StudentAttendanceRow(student = student)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (detail.session.status != SessionStatus.CANCELLED && detail.attendanceRecords.isNotEmpty()) {
                        Button(
                            onClick = { onNavigateToAttendance(detail.session.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Catat Kehadiran", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (detail.session.status == SessionStatus.SCHEDULED) {
                        OutlinedButton(
                            onClick = { showCancelDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !uiState.isCancelling
                        ) {
                            if (uiState.isCancelling) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Batalkan Sesi", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    uiState.errorMessage?.let { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Batalkan sesi ini?") },
            text = {
                Text("Sesi ditandai CANCELLED dan sesi paket siswa yang sudah terpotong dikembalikan secara otomatis.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        viewModel.cancelSession()
                    }
                ) {
                    Text("Batalkan Sesi", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) { Text("Tutup") }
            }
        )
    }
}

@Composable
private fun SessionSummaryCard(detail: LessonSessionDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = detail.className,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                SessionStatusChip(status = detail.session.status)
            }

            HorizontalDivider()

            SummaryRow(
                icon = Icons.Default.Event,
                label = "Tanggal",
                value = DateTimeUtil.formatDisplayDayDate(detail.session.sessionDate)
            )
            SummaryRow(
                icon = Icons.Default.Schedule,
                label = "Waktu",
                value = "${detail.session.startTime} • ${detail.session.durationMinutes} menit"
            )
            SummaryRow(
                icon = Icons.Default.School,
                label = "Tutor",
                value = detail.tutorName ?: "Tanpa tutor"
            )
        }
    }
}

@Composable
private fun SummaryRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun StudentAttendanceRow(student: StudentAttendanceItem) {
    val packageInfo = student.remainingPackageSessions?.let { "Sisa paket: $it sesi" }

    CompactRow(
        title = student.studentName,
        subtitle = packageInfo ?: "Tagihan: ${student.billingType.name}",
        extraInfo = student.note,
        trailingContent = { AttendanceStatusChip(status = student.status) }
    )
}
