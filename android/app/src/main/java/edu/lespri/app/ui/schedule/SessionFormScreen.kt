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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.lespri.app.domain.model.SessionStatus
import edu.lespri.app.ui.components.EmptyStateView
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.components.LoadingOverlay
import edu.lespri.app.ui.components.SessionStatusChip
import edu.lespri.app.util.DateTimeUtil
import java.util.Locale

/**
 * Form Jadwal / Sesi (P13).
 * Membuat sesi satu kali, jadwal rutin mingguan, mengubah jadwal (reschedule), dan membatalkan sesi.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionFormScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: SessionFormViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showClassMenu by remember { mutableStateOf(false) }
    var showTutorMenu by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved, uiState.isCancelled) {
        if (uiState.isSaved || uiState.isCancelled) {
            onSaved()
        }
    }

    LespriScaffold(
        currentRoute = null,
        onNavigateToRoute = {},
        title = if (uiState.isEditMode) "Ubah Sesi" else "Buat Sesi Baru",
        subtitle = if (uiState.isEditMode) "Reschedule atau batalkan sesi" else "Sesi khusus atau jadwal rutin",
        showBottomBar = false,
        onBack = onBack,
        actions = {
            if (uiState.isEditMode && uiState.status == SessionStatus.SCHEDULED) {
                IconButton(onClick = { showCancelDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Batalkan sesi",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    ) {
        if (uiState.isLoading) {
            LoadingOverlay()
            return@LespriScaffold
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (uiState.classes.isEmpty()) {
                    EmptyStateView(
                        title = "Belum ada kelas",
                        description = "Buat kelas terlebih dahulu di menu Kelas sebelum menambahkan sesi.",
                        actionLabel = "Kembali",
                        onActionClick = onBack
                    )
                    return@Column
                }

                // ── Kelas ──
                FieldLabel("Kelas")
                Box {
                    OutlinedButton(
                        onClick = { showClassMenu = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = uiState.selectedClassName ?: "Pilih kelas",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = showClassMenu,
                        onDismissRequest = { showClassMenu = false }
                    ) {
                        uiState.classes.forEach { classGroup ->
                            DropdownMenuItem(
                                text = { Text(classGroup.name) },
                                onClick = {
                                    viewModel.onClassSelected(classGroup.id)
                                    showClassMenu = false
                                }
                            )
                        }
                    }
                }

                // ── Tanggal ──
                FieldLabel("Tanggal")
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = DateTimeUtil.formatDisplayDayDate(uiState.sessionDate),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
                }

                // ── Jam mulai & durasi ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel("Jam Mulai")
                        OutlinedButton(
                            onClick = { showTimePicker = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(uiState.startTime)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel("Durasi (menit)")
                        OutlinedTextField(
                            value = uiState.durationMinutes,
                            onValueChange = viewModel::onDurationChanged,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(60, 90, 120).forEach { preset ->
                        FilterChip(
                            selected = uiState.durationMinutes == preset.toString(),
                            onClick = { viewModel.onDurationChanged(preset.toString()) },
                            label = { Text("$preset mnt") }
                        )
                    }
                }

                // ── Tutor ──
                FieldLabel("Tutor (Opsional)")
                Box {
                    OutlinedButton(
                        onClick = { showTutorMenu = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        enabled = uiState.tutors.isNotEmpty()
                    ) {
                        Text(
                            text = uiState.selectedTutorName ?: "Tanpa tutor",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = showTutorMenu,
                        onDismissRequest = { showTutorMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tanpa tutor") },
                            onClick = {
                                viewModel.onTutorSelected(null)
                                showTutorMenu = false
                            }
                        )
                        uiState.tutors.forEach { tutor ->
                            DropdownMenuItem(
                                text = { Text(tutor.name) },
                                onClick = {
                                    viewModel.onTutorSelected(tutor.id)
                                    showTutorMenu = false
                                }
                            )
                        }
                    }
                }

                // ── Pengulangan mingguan ──
                if (!uiState.isEditMode) {
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ulangi setiap minggu",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Jadwal rutin pada hari yang sama untuk 30 hari ke depan.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.repeatWeekly,
                            onCheckedChange = viewModel::onRepeatWeeklyChanged
                        )
                    }
                }

                if (uiState.isEditMode) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Status sesi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        SessionStatusChip(status = uiState.status)
                    }
                }

                uiState.errorMessage?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // ── Sticky action bar ──
            Surface(tonalElevation = 3.dp) {
                Button(
                    onClick = viewModel::saveSession,
                    enabled = !uiState.isSaving && uiState.classes.isNotEmpty(),
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
                            text = if (uiState.isEditMode) "Simpan Perubahan" else "Simpan Sesi",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = DateTimeUtil.isoDateToUtcMillis(uiState.sessionDate)
                ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.onDateSelected(DateTimeUtil.utcMillisToIsoDate(millis))
                        }
                        showDatePicker = false
                    }
                ) { Text("Pilih") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timeParts = uiState.startTime.split(":")
        val timePickerState = rememberTimePickerState(
            initialHour = timeParts.getOrNull(0)?.toIntOrNull() ?: 16,
            initialMinute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Pilih Jam Mulai") },
            text = {
                TimeInput(state = timePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onTimeSelected(
                            String.format(Locale.US, "%02d:%02d", timePickerState.hour, timePickerState.minute)
                        )
                        showTimePicker = false
                    }
                ) { Text("Pilih") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Batal") }
            }
        )
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Batalkan sesi ini?") },
            text = {
                Text("Sesi akan ditandai CANCELLED dan sesi paket siswa yang sudah terpotong dikembalikan secara otomatis.")
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
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
