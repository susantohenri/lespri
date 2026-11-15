package edu.lespri.app.ui.classroom

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.lespri.app.domain.model.BillingType
import edu.lespri.app.domain.model.ClassType
import edu.lespri.app.domain.model.EnrolledStudentItem
import edu.lespri.app.ui.components.ActiveStatusChip
import edu.lespri.app.ui.components.BillingTypeChip
import edu.lespri.app.ui.components.BillingTypeSelector
import edu.lespri.app.ui.components.ClassTypeChip
import edu.lespri.app.ui.components.CompactRow
import edu.lespri.app.ui.components.DropdownField
import edu.lespri.app.ui.components.DropdownOption
import edu.lespri.app.ui.components.EmptyStateView
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.components.SessionStatusChip
import edu.lespri.app.ui.components.displayLabel
import edu.lespri.app.ui.navigation.NavRoutes
import edu.lespri.app.ui.theme.StatusError
import edu.lespri.app.ui.theme.StatusSuccess
import edu.lespri.app.util.CurrencyFormatter
import edu.lespri.app.util.DateTimeUtil

/**
 * Layar detail kelas (P08).
 * Menampilkan profil kelas, tutor, siswa terdaftar, preview sesi, dan enrollment baru.
 */
@Composable
fun ClassDetailScreen(
    onBack: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onEditClass: (String) -> Unit,
    onOpenStudent: (String) -> Unit,
    onOpenSession: (String) -> Unit,
    onArchived: () -> Unit,
    viewModel: ClassDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }
    var showArchiveDialog by remember { mutableStateOf(false) }
    var pendingRemoval by remember { mutableStateOf<EnrolledStudentItem?>(null) }

    LespriScaffold(
        currentRoute = NavRoutes.Classes.route,
        onNavigateToRoute = onNavigateToRoute,
        title = uiState.classGroup?.name ?: "Detail Kelas",
        subtitle = "Profil kelas",
        onBack = onBack,
        actions = {
            val classGroup = uiState.classGroup
            if (classGroup != null && classGroup.status == "ACTIVE") {
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Aksi lainnya")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Kelas") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onEditClass(classGroup.id)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Nonaktifkan Kelas") },
                            leadingIcon = { Icon(Icons.Default.Archive, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                showArchiveDialog = true
                            }
                        )
                    }
                }
            }
        }
    ) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.classGroup == null -> {
                EmptyStateView(
                    title = "Kelas tidak ditemukan",
                    description = uiState.errorMessage ?: "Data kelas mungkin sudah tidak tersedia.",
                    icon = Icons.Default.School,
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                val classGroup = requireNotNull(uiState.classGroup)
                val canEnrollMore = classGroup.classType != ClassType.PRIVATE ||
                    uiState.enrolledStudents.isEmpty()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(2.dp)) }

                    item {
                        ClassHeaderCard(
                            name = classGroup.name,
                            classType = classGroup.classType,
                            subject = classGroup.subject,
                            tutorName = uiState.assignedTutor?.name,
                            locationNote = classGroup.locationNote,
                            enrolledCount = uiState.enrolledStudents.size,
                            isActive = classGroup.status == "ACTIVE"
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = viewModel::openEnrollDialog,
                                enabled = canEnrollMore && classGroup.status == "ACTIVE",
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Tambah Siswa")
                            }
                            OutlinedButton(
                                onClick = { onNavigateToRoute(NavRoutes.Schedule.route) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Lihat Jadwal")
                            }
                        }
                    }

                    if (!canEnrollMore) {
                        item {
                            Text(
                                text = "Kelas privat hanya boleh memiliki satu siswa aktif.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    uiState.actionMessage?.let { message ->
                        item {
                            MessageBanner(
                                message = message,
                                containerColor = StatusSuccess.copy(alpha = 0.12f),
                                contentColor = StatusSuccess,
                                onDismiss = viewModel::dismissActionMessage
                            )
                        }
                    }

                    uiState.errorMessage?.let { message ->
                        item {
                            MessageBanner(
                                message = message,
                                containerColor = StatusError.copy(alpha = 0.12f),
                                contentColor = StatusError
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Siswa Terdaftar (${uiState.enrolledStudents.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (uiState.enrolledStudents.isEmpty()) {
                        item {
                            EmptyStateView(
                                title = "Belum ada siswa",
                                description = "Tambahkan siswa ke kelas ini untuk mulai mengelola pembayaran dan presensi.",
                                icon = Icons.Default.PersonAdd,
                                actionLabel = if (classGroup.status == "ACTIVE") "Tambah Siswa" else null,
                                onActionClick = if (classGroup.status == "ACTIVE") {
                                    viewModel::openEnrollDialog
                                } else {
                                    null
                                }
                            )
                        }
                    } else {
                        uiState.enrolledStudents.forEach { student ->
                            item(key = student.enrollmentId) {
                                CompactRow(
                                    title = student.studentName,
                                    subtitle = "${student.billingType.displayLabel()} • " +
                                        CurrencyFormatter.formatRupiah(student.feeAmount),
                                    extraInfo = buildString {
                                        if (student.remainingPackageSessions != null) {
                                            append("Sisa paket: ${student.remainingPackageSessions} sesi")
                                        } else {
                                            append("Tanpa saldo paket")
                                        }
                                        if (student.currentDebt > 0) {
                                            append(" • Tunggakan: ")
                                            append(CurrencyFormatter.formatRupiah(student.currentDebt))
                                        }
                                    },
                                    trailingContent = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            BillingTypeChip(billingType = student.billingType)
                                            IconButton(
                                                onClick = { pendingRemoval = student }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Keluarkan siswa",
                                                    tint = MaterialTheme.colorScheme.outline
                                                )
                                            }
                                        }
                                    },
                                    onClick = { onOpenStudent(student.studentId) }
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Preview Sesi Mendatang",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (uiState.upcomingSessions.isEmpty()) {
                        item {
                            Text(
                                text = "Belum ada sesi terjadwal. Buat jadwal rutin atau sesi manual " +
                                    "dari menu Jadwal.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        uiState.upcomingSessions.take(8).forEach { session ->
                            item(key = "session_${session.id}") {
                                CompactRow(
                                    title = DateTimeUtil.formatDisplayDayDate(session.sessionDate),
                                    subtitle = "${session.startTime} • ${session.durationMinutes} menit",
                                    trailingContent = {
                                        SessionStatusChip(status = session.status)
                                    },
                                    onClick = { onOpenSession(session.id) }
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }

    if (uiState.showEnrollDialog) {
        EnrollStudentDialog(
            uiState = uiState,
            onDismiss = viewModel::dismissEnrollDialog,
            onStudentSelected = viewModel::onStudentSelected,
            onBillingTypeChanged = viewModel::onBillingTypeChanged,
            onFeeAmountChanged = viewModel::onFeeAmountChanged,
            onDueDayChanged = viewModel::onDueDayChanged,
            onPackageSessionCountChanged = viewModel::onPackageSessionCountChanged,
            onConfirm = viewModel::enrollStudent
        )
    }

    if (showArchiveDialog) {
        AlertDialog(
            onDismissRequest = { showArchiveDialog = false },
            title = { Text("Nonaktifkan kelas?") },
            text = {
                Text(
                    "Kelas tidak akan muncul di daftar aktif. Riwayat sesi, kehadiran, " +
                        "dan transaksi tetap tersimpan."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showArchiveDialog = false
                        viewModel.archiveClass(onArchived)
                    }
                ) {
                    Text("Nonaktifkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showArchiveDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    pendingRemoval?.let { student ->
        AlertDialog(
            onDismissRequest = { pendingRemoval = null },
            title = { Text("Keluarkan siswa?") },
            text = {
                Text(
                    "${student.studentName} akan dikeluarkan dari kelas ini. Riwayat kehadiran " +
                        "dan tagihan tetap tersimpan."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val enrollmentId = student.enrollmentId
                        pendingRemoval = null
                        viewModel.removeEnrollment(enrollmentId)
                    }
                ) {
                    Text("Keluarkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingRemoval = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun ClassHeaderCard(
    name: String,
    classType: ClassType,
    subject: String?,
    tutorName: String?,
    locationNote: String?,
    enrolledCount: Int,
    isActive: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = listOfNotNull(subject, "${enrolledCount} siswa").joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    ClassTypeChip(classType = classType)
                    Spacer(modifier = Modifier.height(4.dp))
                    ActiveStatusChip(isActive = isActive)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            InfoRow(label = "Tutor", value = tutorName ?: "Belum ditugaskan")
            InfoRow(label = "Lokasi", value = locationNote?.takeIf { it.isNotBlank() } ?: "—")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(64.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun MessageBanner(
    message: String,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    onDismiss: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
            if (onDismiss != null) {
                TextButton(onClick = onDismiss) {
                    Text("Tutup")
                }
            }
        }
    }
}

@Composable
private fun EnrollStudentDialog(
    uiState: ClassDetailUiState,
    onDismiss: () -> Unit,
    onStudentSelected: (String) -> Unit,
    onBillingTypeChanged: (BillingType) -> Unit,
    onFeeAmountChanged: (String) -> Unit,
    onDueDayChanged: (String) -> Unit,
    onPackageSessionCountChanged: (String) -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Siswa ke Kelas") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (uiState.availableStudents.isEmpty()) {
                    Text(
                        text = "Semua siswa aktif sudah terdaftar di kelas ini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val studentOptions = uiState.availableStudents.map { student ->
                        DropdownOption(
                            id = student.id,
                            label = listOfNotNull(
                                student.name,
                                student.schoolGrade?.takeIf { it.isNotBlank() }
                            ).joinToString(" • ")
                        )
                    }
                    val selectedStudentName = uiState.availableStudents
                        .firstOrNull { it.id == uiState.selectedStudentId }
                        ?.name
                        .orEmpty()

                    DropdownField(
                        label = "Siswa",
                        selectedText = selectedStudentName,
                        placeholder = "Pilih siswa",
                        options = studentOptions,
                        onSelect = onStudentSelected
                    )

                    BillingTypeSelector(
                        selected = uiState.billingType,
                        onSelect = onBillingTypeChanged
                    )

                    OutlinedTextField(
                        value = uiState.feeAmountInput,
                        onValueChange = onFeeAmountChanged,
                        label = {
                            Text(
                                when (uiState.billingType) {
                                    BillingType.MONTHLY -> "Biaya Bulanan (Rp)"
                                    BillingType.PACKAGE -> "Harga Paket (Rp)"
                                    BillingType.PER_SESSION -> "Biaya per Sesi (Rp)"
                                }
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        supportingText = {
                            Text(
                                "Terbaca: " + CurrencyFormatter.formatRupiah(
                                    CurrencyFormatter.parseRupiah(uiState.feeAmountInput)
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    when (uiState.billingType) {
                        BillingType.MONTHLY -> {
                            OutlinedTextField(
                                value = uiState.dueDayInput,
                                onValueChange = onDueDayChanged,
                                label = { Text("Tanggal Jatuh Tempo (1–28)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        BillingType.PACKAGE -> {
                            OutlinedTextField(
                                value = uiState.packageSessionCountInput,
                                onValueChange = onPackageSessionCountChanged,
                                label = { Text("Jumlah Sesi Paket *") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        BillingType.PER_SESSION -> Unit
                    }
                }

                uiState.errorMessage?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !uiState.isSavingEnrollment && uiState.availableStudents.isNotEmpty()
            ) {
                if (uiState.isSavingEnrollment) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Daftarkan")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !uiState.isSavingEnrollment) {
                Text("Batal")
            }
        }
    )
}
