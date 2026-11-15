package edu.lespri.app.ui.student

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.lespri.app.domain.model.StudentStatus
import edu.lespri.app.ui.components.ActiveStatusChip
import edu.lespri.app.ui.components.CompactRow
import edu.lespri.app.ui.components.EmptyStateView
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.navigation.NavRoutes
import edu.lespri.app.util.CurrencyFormatter

/**
 * Layar daftar siswa (P03).
 * Menampilkan pencarian, filter status/tunggakan, dan daftar siswa padat.
 */
@Composable
fun StudentListScreen(
    onNavigateToRoute: (String) -> Unit,
    onAddStudent: () -> Unit,
    onOpenStudent: (String) -> Unit,
    viewModel: StudentListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LespriScaffold(
        currentRoute = NavRoutes.Students.route,
        onNavigateToRoute = onNavigateToRoute,
        title = "Siswa",
        subtitle = "Daftar siswa bimbel",
        floatingActionButton = {
            FloatingActionButton(onClick = onAddStudent) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Siswa")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onQueryChanged,
                placeholder = { Text("Cari nama, wali, atau nomor telepon") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = uiState.statusFilter == StudentStatus.ACTIVE,
                    onClick = { viewModel.onStatusFilterChanged(StudentStatus.ACTIVE) },
                    label = { Text("Aktif") }
                )
                FilterChip(
                    selected = uiState.statusFilter == StudentStatus.ARCHIVED,
                    onClick = { viewModel.onStatusFilterChanged(StudentStatus.ARCHIVED) },
                    label = { Text("Nonaktif") }
                )
                FilterChip(
                    selected = uiState.onlyWithDebt,
                    onClick = { viewModel.onOnlyWithDebtChanged(!uiState.onlyWithDebt) },
                    label = { Text("Ada Tunggakan") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.errorMessage != null -> {
                    EmptyStateView(
                        title = "Gagal memuat siswa",
                        description = uiState.errorMessage.orEmpty(),
                        icon = Icons.Default.Person
                    )
                }

                uiState.students.isEmpty() -> {
                    EmptyStateView(
                        title = if (uiState.searchQuery.isBlank()) "Belum ada siswa" else "Siswa tidak ditemukan",
                        description = if (uiState.searchQuery.isBlank()) {
                            "Tambahkan siswa pertama untuk mulai mengelola kelas dan pembayaran."
                        } else {
                            "Coba kata kunci lain atau ubah filter status."
                        },
                        icon = Icons.Default.Person,
                        actionLabel = if (uiState.searchQuery.isBlank() && uiState.statusFilter == StudentStatus.ACTIVE) {
                            "Tambah Siswa"
                        } else {
                            null
                        },
                        onActionClick = if (uiState.searchQuery.isBlank() && uiState.statusFilter == StudentStatus.ACTIVE) {
                            onAddStudent
                        } else {
                            null
                        }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 88.dp)
                    ) {
                        items(uiState.students, key = { it.id }) { student ->
                            CompactRow(
                                title = student.name,
                                subtitle = listOfNotNull(
                                    student.guardianPhone?.takeIf { it.isNotBlank() },
                                    student.schoolGrade?.takeIf { it.isNotBlank() }?.let { "Kelas $it" }
                                ).joinToString(" • ").ifBlank { "Kontak wali belum diisi" },
                                extraInfo = buildString {
                                    append("${student.activeClassesCount} kelas aktif")
                                    append(" • ")
                                    append(
                                        if (student.totalDebt > 0) {
                                            "${CurrencyFormatter.formatRupiah(student.totalDebt)} tunggakan"
                                        } else {
                                            "Tidak ada tunggakan"
                                        }
                                    )
                                },
                                leadingContent = {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = student.name.take(1).uppercase(),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                trailingContent = {
                                    ActiveStatusChip(isActive = student.status == StudentStatus.ACTIVE)
                                },
                                onClick = { onOpenStudent(student.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
