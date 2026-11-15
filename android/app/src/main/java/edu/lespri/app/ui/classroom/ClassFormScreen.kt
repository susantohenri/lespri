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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.lespri.app.domain.model.ClassType
import edu.lespri.app.ui.components.DropdownField
import edu.lespri.app.ui.components.DropdownOption
import edu.lespri.app.ui.components.FormSection
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.navigation.NavRoutes

/**
 * Layar form tambah/edit kelas (P07).
 * Anggota kelas dan jadwal dikelola dari layar detail kelas / milestone jadwal.
 */
@Composable
fun ClassFormScreen(
    onBack: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onSaved: () -> Unit,
    viewModel: ClassFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.savedSuccess) {
        if (uiState.savedSuccess) onSaved()
    }

    LespriScaffold(
        currentRoute = NavRoutes.Classes.route,
        onNavigateToRoute = onNavigateToRoute,
        title = if (uiState.isEditing) "Edit Kelas" else "Tambah Kelas",
        subtitle = "Data master kelas",
        showBottomBar = false,
        onBack = onBack
    ) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@LespriScaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            item {
                FormSection(title = "Data Kelas") {
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = viewModel::onNameChanged,
                        label = { Text("Nama Kelas *") },
                        placeholder = { Text("misal: Matematika SMP 8A") },
                        singleLine = true,
                        isError = uiState.name.isBlank() && uiState.errorMessage != null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column {
                        Text(
                            text = "Jenis Kelas",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ClassType.entries.forEach { classType ->
                                FilterChip(
                                    selected = uiState.classType == classType,
                                    onClick = { viewModel.onClassTypeChanged(classType) },
                                    label = {
                                        Text(
                                            if (classType == ClassType.PRIVATE) "Privat" else "Kelompok"
                                        )
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uiState.subject,
                        onValueChange = viewModel::onSubjectChanged,
                        label = { Text("Mata Pelajaran") },
                        placeholder = { Text("misal: Matematika") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.locationNote,
                        onValueChange = viewModel::onLocationNoteChanged,
                        label = { Text("Lokasi / Catatan Tempat") },
                        placeholder = { Text("misal: Ruang 2, rumah Bu Rina") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                FormSection(title = "Tutor Penanggung Jawab") {
                    val tutorOptions = buildList {
                        add(DropdownOption(id = "", label = "Tanpa Tutor"))
                        uiState.tutors.forEach { tutor ->
                            add(DropdownOption(id = tutor.id, label = tutor.name))
                        }
                    }
                    val selectedTutorName = uiState.tutors
                        .firstOrNull { it.id == uiState.tutorId }
                        ?.name
                        .orEmpty()

                    DropdownField(
                        label = "Tutor Utama",
                        selectedText = selectedTutorName,
                        placeholder = "Tanpa Tutor",
                        options = tutorOptions,
                        onSelect = { tutorId ->
                            viewModel.onTutorSelected(tutorId.takeIf { it.isNotBlank() })
                        }
                    )

                    if (uiState.tutors.isEmpty()) {
                        Text(
                            text = "Belum ada tutor aktif. Kelas tetap bisa disimpan sebagai draft, " +
                                "tetapi tentukan tutor sebelum membuat jadwal.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = { onNavigateToRoute(NavRoutes.Tutors.route) }) {
                            Text("Buka Daftar Tutor")
                        }
                    }
                }
            }

            item {
                FormSection(title = "Status") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Kelas Aktif",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Arsipkan kelas tanpa menghapus riwayat sesi dan transaksi.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.isActive,
                            onCheckedChange = viewModel::onActiveChanged
                        )
                    }
                }
            }

            if (uiState.errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = uiState.errorMessage.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = viewModel::save,
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
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
                            text = if (uiState.isEditing) "Simpan Perubahan" else "Simpan Kelas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
