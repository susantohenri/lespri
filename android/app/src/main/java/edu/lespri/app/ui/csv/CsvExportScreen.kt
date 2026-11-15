package edu.lespri.app.ui.csv

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.navigation.NavRoutes

@Composable
fun CsvExportScreen(
    onNavigateToRoute: (String) -> Unit,
    viewModel: CsvExportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LespriScaffold(
        currentRoute = NavRoutes.CsvExport.route,
        onNavigateToRoute = onNavigateToRoute,
        title = "Ekspor Data",
        subtitle = "Simpan data sebagai berkas CSV"
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "Pilih Dataset",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                text = "Pilih data yang akan diekspor ke berkas CSV.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                items(items = uiState.availableDatasets) { dataset ->
                    val isSelected = uiState.selectedDataset == dataset
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { viewModel.selectDataset(dataset) })
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            Spacer(modifier = Modifier.height(20.dp))

            uiState.errorMessage?.let { error ->
                Text(text = error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
            uiState.successMessage?.let { msg ->
                Text(text = msg, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            uiState.exportedFileName?.let { fileName ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        androidx.compose.material.icons.filled.FileDownload()
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Berkas tersimpan", style = MaterialTheme.typography.labelSmall)
                            Text(text = fileName, style = MaterialTheme.typography.bodySmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { viewModel.exportDataset() },
                    enabled = !uiState.isLoading,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                ) {
                    if (uiState.isLoading) {
                        androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text(text = "Ekspor CSV")
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onNavigateToRoute(NavRoutes.Dashboard.route) },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Batal")
                }
            }
        }
    }
}
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.selectDataset(dataset) },
                            colors = androidx.compose.material3.RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = dataset.capitalize(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            )