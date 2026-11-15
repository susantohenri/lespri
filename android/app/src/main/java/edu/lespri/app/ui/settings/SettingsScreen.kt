package edu.lespri.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.navigation.NavRoutes

@Composable
fun SettingsScreen(
    onNavigateToRoute: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LespriScaffold(
        currentRoute = NavRoutes.Settings.route,
        onNavigateToRoute = onNavigateToRoute,
        title = "Pengaturan",
        subtitle = "Konfigurasi aplikasi bimbel"
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            profileCard(viewModel, uiState)
            Spacer(modifier = Modifier.height(8.dp))
            adsCard(viewModel, uiState)
            messages(uiState)
            saveButton(viewModel, uiState)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun profileCard(viewModel: SettingsViewModel, uiState: SettingsUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Profil Bimbel",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = uiState.bimbelsName,
                onValueChange = viewModel::updateBimbelsName,
                label = { Text(text = "Nama Bimbel") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = uiState.ownerName,
                onValueChange = viewModel::updateOwnerName,
                label = { Text(text = "Nama Pemilik / Admin") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = uiState.whatsappNumber,
                onValueChange = viewModel::updateWhatsAppNumber,
                label = { Text(text = "Nomor WhatsApp (E.164, contoh +6281234567890)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                )
            )
        }
    }
}

@Composable
private fun adsCard(viewModel: SettingsViewModel, uiState: SettingsUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Iklan & Notifikasi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Switch(
                    checked = uiState.adsEnabled,
                    onCheckedChange = viewModel::toggleAdsEnabled,
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        checkedThumbColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Tampilkan Iklan Banner", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "Iklan AdMob akan ditampilkan di bagian bawah layar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = uiState.lowSessionThreshold,
                onValueChange = viewModel::updateLowSessionThreshold,
                label = { Text(text = "Threshold Saldo Paket Rendah (sesi)") },
                modifier = Modifier.width(200.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
            Text(
                text = "Peringatan akan muncul jika sisa sesi paket siswa ≤ nilai ini.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun messages(uiState: SettingsUiState) {
    uiState.errorMessage?.let { error ->
        Text(text = error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
    }
    uiState.successMessage?.let { msg ->
        Text(text = msg, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun saveButton(viewModel: SettingsViewModel, uiState: SettingsUiState) {
    Button(
        onClick = { viewModel.saveSettings() },
        enabled = !uiState.isLoading,
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
    ) {
        if (uiState.isLoading) {
            androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.size(16.dp))
        } else {
            Text(text = "Simpan Pengaturan")
        }
    }
}
