package edu.lespri.app.ui.classroom

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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.School
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
import edu.lespri.app.domain.model.ClassType
import edu.lespri.app.ui.components.ClassTypeChip
import edu.lespri.app.ui.components.CompactRow
import edu.lespri.app.ui.components.EmptyStateView
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.navigation.NavRoutes
import edu.lespri.app.util.DateTimeUtil

/**
 * Layar daftar kelas (P06).
 */
@Composable
fun ClassListScreen(
    onNavigateToRoute: (String) -> Unit,
    onAddClass: () -> Unit,
    onOpenClass: (String) -> Unit,
    viewModel: ClassListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LespriScaffold(
        currentRoute = NavRoutes.Classes.route,
        onNavigateToRoute = onNavigateToRoute,
        title = "Kelas",
        subtitle = "Kelas privat & kelompok",
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClass) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Kelas")
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
                placeholder = { Text("Cari kelas, mapel, atau tutor") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.typeFilter == null,
                    onClick = { viewModel.onTypeFilterChanged(null) },
                    label = { Text("Semua") }
                )
                ClassType.entries.forEach { classType ->
                    FilterChip(
                        selected = uiState.typeFilter == classType,
                        onClick = { viewModel.onTypeFilterChanged(classType) },
                        label = {
                            Text(if (classType == ClassType.PRIVATE) "Privat" else "Kelompok")
                        }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.statusFilter == "ACTIVE",
                    onClick = { viewModel.onStatusFilterChanged("ACTIVE") },
                    label = { Text("Aktif") }
                )
                FilterChip(
                    selected = uiState.statusFilter == "ARCHIVED",
                    onClick = { viewModel.onStatusFilterChanged("ARCHIVED") },
                    label = { Text("Nonaktif") }
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
                        title = "Gagal memuat kelas",
                        description = uiState.errorMessage.orEmpty(),
                        icon = Icons.Default.School
                    )
                }

                uiState.classes.isEmpty() -> {
                    EmptyStateView(
                        title = if (uiState.searchQuery.isBlank()) "Belum ada kelas" else "Kelas tidak ditemukan",
                        description = if (uiState.searchQuery.isBlank()) {
                            "Buat kelas privat atau kelompok, lalu tambahkan siswa dan tutor."
                        } else {
                            "Coba kata kunci lain atau ubah filter."
                        },
                        icon = Icons.Default.School,
                        actionLabel = if (uiState.searchQuery.isBlank() && uiState.statusFilter == "ACTIVE") {
                            "Tambah Kelas"
                        } else {
                            null
                        },
                        onActionClick = if (uiState.searchQuery.isBlank() && uiState.statusFilter == "ACTIVE") {
                            onAddClass
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
                        items(uiState.classes, key = { it.id }) { classItem ->
                            CompactRow(
                                title = classItem.name,
                                subtitle = listOfNotNull(
                                    classItem.subject?.takeIf { it.isNotBlank() },
                                    classItem.tutorName?.takeIf { it.isNotBlank() }
                                        ?.let { "Tutor: $it" }
                                ).joinToString(" • ").ifBlank { "Mapel & tutor belum diatur" },
                                extraInfo = buildString {
                                    append("${classItem.enrolledStudentsCount} siswa")
                                    if (classItem.nextSessionDate != null) {
                                        append(" • Jadwal berikutnya ")
                                        append(DateTimeUtil.formatShortDayDate(classItem.nextSessionDate))
                                        append(" ${classItem.nextSessionTime.orEmpty()}")
                                    } else {
                                        append(" • Belum ada jadwal")
                                    }
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
                                            text = classItem.name.take(1).uppercase(),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                trailingContent = {
                                    ClassTypeChip(classType = classItem.classType)
                                },
                                onClick = { onOpenClass(classItem.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
