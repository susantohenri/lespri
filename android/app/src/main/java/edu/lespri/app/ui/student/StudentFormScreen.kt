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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.lespri.app.domain.model.BillingType
import edu.lespri.app.domain.model.ClassType
import edu.lespri.app.ui.components.BillingTypeSelector
import edu.lespri.app.ui.components.DropdownField
import edu.lespri.app.ui.components.DropdownOption
import edu.lespri.app.ui.components.FormSection
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.navigation.NavRoutes
import edu.lespri.app.util.CurrencyFormatter

/**
 * Layar form tambah/edit siswa (P04).
 * Termasuk opsi mendaftarkan siswa ke kelas sekaligus dengan skema pembayaran.
 */
@Composable
fun StudentFormScreen(
    onBack: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onSaved: () -> Unit,
    viewModel: StudentFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.savedSuccess) {
        if (uiState.savedSuccess) onSaved()
    }

    LespriScaffold(
        currentRoute = NavRoutes.Students.route,
        onNavigateToRoute = onNavigateToRoute,
        title = if (uiState.isEditing) "Edit Siswa" else "Tambah Siswa",
        subtitle = "Data master siswa",
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
                FormSection(title = "Data Siswa") {
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = viewModel::onNameChanged,
                        label = { Text("Nama Siswa *") },
                        placeholder = { Text("misal: Ahmad Rizki") },
                        singleLine = true,
                        isError = uiState.name.isBlank() && uiState.errorMessage != null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.schoolGrade,
                        onValueChange = viewModel::onSchoolGradeChanged,
                        label = { Text("Jenjang / Kelas Sekolah") },
                        placeholder = { Text("misal: SMP 8A") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                FormSection(title = "Kontak Wali") {
                    OutlinedTextField(
                        value = uiState.guardianName,
                        onValueChange = viewModel::onGuardianNameChanged,
                        label = { Text("Nama Orang Tua / Wali") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.guardianPhone,
                        onValueChange = viewModel::onGuardianPhoneChanged,
                        label = { Text("Nomor WhatsApp / Telepon") },
                        placeholder = { Text("misal: 081234567890") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                FormSection(title = "Kelas (Opsional)") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Daftarkan ke kelas sekarang",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Buat enrollment dan tagihan awal sekaligus.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.enrollToClass,
                            onCheckedChange = viewModel::onEnrollToClassChanged
                        )
                    }

                    if (uiState.enrollToClass) {
                        if (uiState.availableClasses.isEmpty()) {
                            Text(
                                text = "Belum ada kelas aktif yang bisa dipilih. Buat kelas terlebih dahulu.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(onClick = { onNavigateToRoute(NavRoutes.Classes.route) }) {
                                Text("Buka Daftar Kelas")
                            }
                        } else {
                            val classOptions = uiState.availableClasses.map { classGroup ->
                                DropdownOption(
                                    id = classGroup.id,
                                    label = "${classGroup.name} • " +
                                        if (classGroup.classType == ClassType.PRIVATE) "Privat" else "Kelompok"
                                )
                            }
                            val selectedClassName = uiState.availableClasses
                                .firstOrNull { it.id == uiState.selectedClassId }
                                ?.name
                                .orEmpty()

                            DropdownField(
                                label = "Kelas",
                                selectedText = selectedClassName,
                                placeholder = "Pilih kelas",
                                options = classOptions,
                                onSelect = viewModel::onClassSelected
                            )

                            BillingTypeSelector(
                                selected = uiState.billingType,
                                onSelect = viewModel::onBillingTypeChanged
                            )

                            OutlinedTextField(
                                value = uiState.feeAmountInput,
                                onValueChange = viewModel::onFeeAmountChanged,
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
                                        onValueChange = viewModel::onDueDayChanged,
                                        label = { Text("Tanggal Jatuh Tempo (1–28)") },
                                        placeholder = { Text("misal: 10") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                BillingType.PACKAGE -> {
                                    OutlinedTextField(
                                        value = uiState.packageSessionCountInput,
                                        onValueChange = viewModel::onPackageSessionCountChanged,
                                        label = { Text("Jumlah Sesi Paket *") },
                                        placeholder = { Text("misal: 8") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                BillingType.PER_SESSION -> Unit
                            }
                        }
                    }
                }
            }

            item {
                FormSection(title = "Status & Catatan") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Siswa Aktif",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Nonaktifkan tanpa menghapus riwayat.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.isActive,
                            onCheckedChange = viewModel::onActiveChanged
                        )
                    }

                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = viewModel::onNotesChanged,
                        label = { Text("Catatan Internal") },
                        placeholder = { Text("misal: butuh pendampingan matematika dasar") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
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
                            text = if (uiState.isEditing) "Simpan Perubahan" else "Simpan Siswa",
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
