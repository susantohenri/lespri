package edu.lespri.app.ui.tutor

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
import androidx.compose.material.icons.filled.Badge
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
import edu.lespri.app.domain.model.TutorStatus
import edu.lespri.app.ui.components.ActiveStatusChip
import edu.lespri.app.ui.components.CompactRow
import edu.lespri.app.ui.components.EmptyStateView
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.navigation.NavRoutes

/**
 * Layar daftar tutor (P09).
 */
@Composable
fun TutorListScreen(
    onNavigateToRoute: (String) -> Unit,
    onAddTutor: () -> Unit,
    onEditTutor: (String) -> Unit,
    viewModel: TutorListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LespriScaffold(
        currentRoute = NavRoutes.Tutors.route,
        onNavigateToRoute = onNavigateToRoute,
        title = "Tutor",
        subtitle = "Pengajar bimbel",
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTutor) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Tutor")
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
                placeholder = { Text("Cari nama, telepon, atau keahlian") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.statusFilter == TutorStatus.ACTIVE,
                    onClick = { viewModel.onStatusFilterChanged(TutorStatus.ACTIVE) },
                    label = { Text("Aktif") }
                )
                FilterChip(
                    selected = uiState.statusFilter == TutorStatus.ARCHIVED,
                    onClick = { viewModel.onStatusFilterChanged(TutorStatus.ARCHIVED) },
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
                        title = "Gagal memuat tutor",
                        description = uiState.errorMessage.orEmpty(),
                        icon = Icons.Default.Badge
                    )
                }

                uiState.tutors.isEmpty() -> {
                    EmptyStateView(
                        title = if (uiState.searchQuery.isBlank()) "Belum ada tutor" else "Tutor tidak ditemukan",
                        description = if (uiState.searchQuery.isBlank()) {
                            "Tambahkan tutor agar dapat ditugaskan ke kelas."
                        } else {
                            "Coba kata kunci lain atau ubah filter status."
                        },
                        icon = Icons.Default.Badge,
                        actionLabel = if (uiState.searchQuery.isBlank() && uiState.statusFilter == TutorStatus.ACTIVE) {
                            "Tambah Tutor"
                        } else {
                            null
                        },
                        onActionClick = if (uiState.searchQuery.isBlank() && uiState.statusFilter == TutorStatus.ACTIVE) {
                            onAddTutor
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
                        items(uiState.tutors, key = { it.id }) { tutor ->
                            CompactRow(
                                title = tutor.name,
                                subtitle = listOfNotNull(
                                    tutor.phone?.takeIf { it.isNotBlank() },
                                    tutor.expertise?.takeIf { it.isNotBlank() }
                                ).joinToString(" • ").ifBlank { "Kontak/keahlian belum diisi" },
                                extraInfo = "${tutor.activeClassesCount} kelas aktif",
                                leadingContent = {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = tutor.name.take(1).uppercase(),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                },
                                trailingContent = {
                                    ActiveStatusChip(isActive = tutor.status == TutorStatus.ACTIVE)
                                },
                                onClick = { onEditTutor(tutor.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
