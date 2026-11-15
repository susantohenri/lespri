package edu.lespri.app.ui.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.lespri.app.domain.model.AttendanceStatus
import edu.lespri.app.ui.components.AttendanceStatusChip
import edu.lespri.app.ui.components.EmptyStateView
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.components.LoadingOverlay
import edu.lespri.app.ui.theme.StatusWarning
import edu.lespri.app.ui.theme.StatusWarningContainer
import edu.lespri.app.util.DateTimeUtil

/**
 * Lembar Pencatatan Kehadiran (P15).
 * Menandai Hadir/Izin/Sakit/Alpa per siswa, mendukung "Tandai Semua Hadir",
 * menampilkan preview pemotongan saldo paket sebelum disimpan.
 */
@Composable
fun AttendanceSheetScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AttendanceSheetViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var noteTarget by remember { mutableStateOf<AttendanceDraftItem?>(null) }
    var noteBuffer by remember { mutableStateOf("") }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onSaved()
        }
    }

    LespriScaffold(
        currentRoute = null,
        onNavigateToRoute = {},
        title = "Catat Kehadiran",
        subtitle = uiState.className.ifBlank { "Lembar Presensi" },
        showBottomBar = false,
        onBack = onBack,
        actions = {
            if (uiState.studentAttendanceList.isNotEmpty()) {
                TextButton(onClick = viewModel::onMarkAllPresent) {
                    Text("Semua Hadir")
                }
            }
        }
    ) {
        when {
            uiState.isLoading -> LoadingOverlay()

            uiState.studentAttendanceList.isEmpty() -> {
                EmptyStateView(
                    title = "Belum ada peserta",
                    description = uiState.errorMessage ?: "Kelas ini belum memiliki siswa aktif.",
                    actionLabel = "Kembali",
                    onActionClick = onBack
                )
            }

            else -> {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    ) {
                        uiState.session?.let { session ->
                            Text(
                                text = "${DateTimeUtil.formatDisplayDayDate(session.sessionDate)} • ${session.startTime}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        if (uiState.hasDeductionImpact) {
                            DeductionPreviewCard(preview = uiState.deductionPreview)
                        }

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.studentAttendanceList, key = { it.enrollmentId }) { draft ->
                                AttendanceDraftRow(
                                    draft = draft,
                                    onStatusSelected = { status ->
                                        viewModel.onStatusSelected(draft.enrollmentId, status)
                                    },
                                    onEditNote = {
                                        noteTarget = draft
                                        noteBuffer = draft.note.orEmpty()
                                    }
                                )
                            }
                        }

                        uiState.errorMessage?.let { message ->
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }

                    Surface(tonalElevation = 3.dp) {
                        Button(
                            onClick = viewModel::saveAttendance,
                            enabled = !uiState.isSaving && uiState.modifiedCount > 0,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(50.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(
                                    text = if (uiState.modifiedCount > 0) {
                                        "Simpan Kehadiran (${uiState.modifiedCount} perubahan)"
                                    } else {
                                        "Simpan Kehadiran"
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    noteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { noteTarget = null },
            title = { Text("Catatan ${target.studentName}") },
            text = {
                OutlinedTextField(
                    value = noteBuffer,
                    onValueChange = { noteBuffer = it },
                    label = { Text("Catatan (opsional)") },
                    placeholder = { Text("misal: izin acara sekolah") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onNoteChanged(target.enrollmentId, noteBuffer)
                        noteTarget = null
                    }
                ) { Text("Simpan Catatan") }
            },
            dismissButton = {
                TextButton(onClick = { noteTarget = null }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun DeductionPreviewCard(preview: List<DeductionPreviewItem>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = StatusWarningContainer)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${preview.size} sesi paket akan terpotong",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = StatusWarning
            )
            Spacer(modifier = Modifier.height(4.dp))
            preview.forEach { item ->
                Text(
                    text = "${item.studentName}: ${item.currentBalance} → ${item.projectedBalance} sesi",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AttendanceDraftRow(
    draft: AttendanceDraftItem,
    onStatusSelected: (AttendanceStatus) -> Unit,
    onEditNote: () -> Unit
) {
    val selectableStatuses = listOf(
        AttendanceStatus.PRESENT to "Hadir",
        AttendanceStatus.EXCUSED to "Izin",
        AttendanceStatus.SICK to "Sakit",
        AttendanceStatus.ABSENT to "Alpa"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = draft.studentName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                AttendanceStatusChip(status = draft.status)
            }

            val balanceInfo = draft.remainingPackageSessions?.let {
                val projected = if (draft.willDeductSession) " → ${(it - 1).coerceAtLeast(0)}" else ""
                "Sisa paket: $it$projected sesi"
            }
            if (balanceInfo != null) {
                Text(
                    text = balanceInfo,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (draft.willDeductSession) StatusWarning else MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(selectableStatuses, key = { it.first.name }) { (status, label) ->
                    FilterChip(
                        selected = draft.status == status,
                        onClick = { onStatusSelected(status) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = draft.note ?: "Tanpa catatan",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onEditNote) {
                    Text("Catatan", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
