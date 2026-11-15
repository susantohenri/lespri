package edu.lespri.app.ui.student

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.lespri.app.domain.model.ClassType
import edu.lespri.app.domain.model.Student
import edu.lespri.app.domain.model.StudentStatus
import edu.lespri.app.ui.components.ActiveStatusChip
import edu.lespri.app.ui.components.BillingTypeChip
import edu.lespri.app.ui.components.CompactRow
import edu.lespri.app.ui.components.EmptyStateView
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.components.SessionStatusChip
import edu.lespri.app.ui.navigation.NavRoutes
import edu.lespri.app.ui.theme.StatusError
import edu.lespri.app.ui.theme.StatusWarning
import edu.lespri.app.util.CurrencyFormatter
import edu.lespri.app.util.DateTimeUtil

/**
 * Layar detail siswa (P05).
 * Menampilkan profil, ringkasan keuangan, kelas, sesi mendatang, dan catatan.
 */
@Composable
fun StudentDetailScreen(
    onBack: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onEditStudent: (String) -> Unit,
    onOpenClass: (String) -> Unit,
    onArchived: () -> Unit,
    viewModel: StudentDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }
    var showArchiveDialog by remember { mutableStateOf(false) }

    LespriScaffold(
        currentRoute = NavRoutes.Students.route,
        onNavigateToRoute = onNavigateToRoute,
        title = uiState.student?.name ?: "Detail Siswa",
        subtitle = "Profil siswa",
        onBack = onBack,
        actions = {
            val student = uiState.student
            if (student != null && student.status == StudentStatus.ACTIVE) {
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Aksi lainnya")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Siswa") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onEditStudent(student.id)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Nonaktifkan Siswa") },
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

            uiState.student == null -> {
                EmptyStateView(
                    title = "Siswa tidak ditemukan",
                    description = uiState.errorMessage ?: "Data siswa mungkin sudah tidak tersedia.",
                    icon = Icons.Default.Person,
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                val student = requireNotNull(uiState.student)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(2.dp)) }

                    item {
                        StudentHeaderCard(student = student)
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SummaryMetric(
                                label = "Tunggakan",
                                value = CurrencyFormatter.formatCompactRupiah(uiState.totalDebt),
                                accent = if (uiState.totalDebt > 0) StatusError else MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.weight(1f)
                            )
                            SummaryMetric(
                                label = "Saldo Paket",
                                value = "${uiState.totalRemainingPackageSessions} sesi",
                                accent = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            SummaryMetric(
                                label = "Kelas",
                                value = "${uiState.classes.size}",
                                accent = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Kelas yang Diikuti",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (uiState.classes.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Siswa belum terdaftar di kelas mana pun.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    TextButton(onClick = { onNavigateToRoute(NavRoutes.Classes.route) }) {
                                        Text("Buka Daftar Kelas")
                                    }
                                }
                            }
                        }
                    } else {
                        uiState.classes.forEach { classItem ->
                            item(key = classItem.enrollmentId) {
                                CompactRow(
                                    title = classItem.className,
                                    subtitle = listOfNotNull(
                                        if (classItem.classType == ClassType.PRIVATE) "Privat" else "Kelompok",
                                        classItem.subject,
                                        classItem.tutorName?.let { "Tutor: $it" }
                                    ).joinToString(" • "),
                                    extraInfo = buildString {
                                        append(
                                            if (classItem.remainingPackageSessions != null) {
                                                "Sisa paket: ${classItem.remainingPackageSessions} sesi"
                                            } else {
                                                "Biaya: ${CurrencyFormatter.formatRupiah(classItem.feeAmount)}"
                                            }
                                        )
                                        if (classItem.currentDebt > 0) {
                                            append(" • Tunggakan: ")
                                            append(CurrencyFormatter.formatRupiah(classItem.currentDebt))
                                        }
                                    },
                                    leadingContent = {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = classItem.className.take(1).uppercase(),
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    },
                                    trailingContent = {
                                        BillingTypeChip(billingType = classItem.billingType)
                                    },
                                    onClick = { onOpenClass(classItem.classId) }
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Sesi Mendatang",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (uiState.upcomingSessions.isEmpty()) {
                        item {
                            Text(
                                text = "Belum ada sesi terjadwal untuk kelas siswa ini.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        uiState.upcomingSessions.forEach { session ->
                            item(key = "session_${session.sessionId}") {
                                CompactRow(
                                    title = session.className,
                                    subtitle = DateTimeUtil.formatDisplayDayDate(session.sessionDate) +
                                        " • ${session.startTime}",
                                    trailingContent = {
                                        SessionStatusChip(status = session.status)
                                    }
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Catatan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                text = student.notes?.takeIf { it.isNotBlank() }
                                    ?: "Belum ada catatan untuk siswa ini.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (student.notes.isNullOrBlank()) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }

                    if (uiState.errorMessage != null) {
                        item {
                            Text(
                                text = uiState.errorMessage.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = StatusWarning
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }

    if (showArchiveDialog) {
        AlertDialog(
            onDismissRequest = { showArchiveDialog = false },
            title = { Text("Nonaktifkan siswa?") },
            text = {
                Text(
                    "Siswa tidak akan muncul di daftar aktif. Riwayat kelas, kehadiran, " +
                        "dan tagihan tetap tersimpan."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showArchiveDialog = false
                        viewModel.archiveStudent(onArchived)
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
}

@Composable
private fun StudentHeaderCard(student: Student) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = student.name.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    ActiveStatusChip(isActive = student.status == StudentStatus.ACTIVE)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = listOfNotNull(
                        student.guardianName?.takeIf { it.isNotBlank() }?.let { "Wali: $it" },
                        student.guardianPhone?.takeIf { it.isNotBlank() },
                        student.schoolGrade?.takeIf { it.isNotBlank() }?.let { "Kelas $it" }
                    ).joinToString(" • ").ifBlank { "Data kontak belum lengkap" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Bergabung ${DateTimeUtil.formatDisplayTimestamp(student.joinedAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun SummaryMetric(
    label: String,
    value: String,
    accent: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = accent
            )
        }
    }
}
