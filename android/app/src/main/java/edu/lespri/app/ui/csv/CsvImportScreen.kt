package edu.lespri.app.ui.csv

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.navigation.NavRoutes
import kotlinx.coroutines.launch

@Composable
fun CsvImportScreen(
    onNavigateToRoute: (String) -> Unit,
    viewModel: CsvImportViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    LespriScaffold(
        currentRoute = NavRoutes.CsvImport.route,
        onNavigateToRoute = onNavigateToRoute,
        title = "Impor CSV",
        subtitle = "Muat data siswa dari berkas CSV"
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            if (uiState.csvResult == null) {
                ImportFilePicker(viewModel = viewModel, context = context, scope = scope)
            } else {
                MappingPreview(uiState = uiState, viewModel = viewModel, onNavigateToRoute = onNavigateToRoute)
            }
@Composable
private fun ImportFilePicker(
    viewModel: CsvImportViewModel,
    context: android.content.Context,
    scope: androidx.compose.runtime.CoroutineScope
) {
    var launcher by remember { mutableStateOf<ActivityResultLauncher<Intent>?>(null) }
    LaunchedEffect(Unit) {
        launcher = rememberFileLauncher { uri ->
            if (uri != null) {
                scope.launch {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        viewModel.loadCsv(stream.bufferedReader().readText())
                    }
                }
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.filled.CloudDownload,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Pilih Berkas CSV", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Pilih berkas .csv yang berisi data siswa. Baris pertama harus berupa header kolom.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "text/csv"
                    }
                    launcher?.launch(intent)
                },
@Composable
private fun MappingPreview(
    uiState: CsvImportUiState,
    viewModel: CsvImportViewModel,
    onNavigateToRoute: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Petakan Kolom CSV", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Text(text = "Cocokkan setiap kolom CSV dengan field data siswa.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        LazyColumn(modifier = Modifier.weight(1f), items(items = uiState.csvResult!!.headers)) { header ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(text = header, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.width(120.dp))
                Spacer(modifier = Modifier.width(8.dp))
                var selectedField by remember { mutableStateOf(uiState.headerMapping[header] ?: "") }
                androidx.compose.material3.ExposedDropdownMenuBox(expanded = false, onExpandedChange = { }) {
                    androidx.compose.material3.ExposedDropdownMenu(modifier = Modifier.fillMaxWidth()) {
                        val fields = listOf("", "id", "name", "guardianName", "guardianPhone", "schoolGrade", "notes", "status")
                        items(fields) { field ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(text = if (field.isBlank()) "(lewati)" else field) },
                                onClick = {
                                    selectedField = field
                                    viewModel.updateMapping(header, if (field.isBlank()) null else field)
                                }
                            )
                        }
                    }
                    TextField(
                        value = selectedField,
                        onValueChange = { selectedField = it; viewModel.updateMapping(header, if (it.isBlank()) null else it) },
                        label = { Text(text = "Field domain") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        singleLine = true
                    )
                }
            }
        }

        if (uiState.previewRows.isNotEmpty()) {
            Text(text = "Preview (${uiState.previewRows.size} baris)", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            LazyColumn(modifier = Modifier.height(200.dp), items(items = uiState.previewRows.take(5))) { row ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text(text = row.values.joinToString(", "), style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                }
            }
        }

        uiState.errorMessage?.let { error ->
            Text(text = error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = { viewModel.confirmImport() }, enabled = uiState.headerMapping.values.contains("name"), shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)) {
                Text(text = "Simpan Impor")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onNavigateToRoute(NavRoutes.Dashboard.route) }, shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)) {
                Text(text = "Batal")
            }
        }
    }
}

@Composable
private fun rememberFileLauncher(callback: (Uri?) -> Unit): ActivityResultLauncher<Intent> {
    val activity = LocalContext.current as Activity
    return remember { activity.registerForActivityResult(ActivityResultContracts.OpenDocument()) { callback(it) } }
}
