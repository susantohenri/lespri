package edu.lespri.app.ui.backup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
fun BackupRestoreScreen(
    onNavigateToRoute: (String) -> Unit,
    viewModel: BackupViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    LespriScaffold(
        currentRoute = NavRoutes.BackupRestore.route,
        onNavigateToRoute = onNavigateToRoute,
        title = "Backup & Restore",
        subtitle = "Cadangkan dan pulihkan data aplikasi"
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            ActionsBar(viewModel = viewModel, uiState = uiState, context = context, scope = scope)
            Spacer(modifier = Modifier.height(16.dp))
            Messages(uiState = uiState)
            Spacer(modifier = Modifier.height(16.dp))
            FileList(viewModel = viewModel, uiState = uiState, scope = scope)
        }
    }
}

@Composable
private fun ActionsBar(
    viewModel: BackupViewModel,
    uiState: BackupUiState,
    context: android.content.Context,
    scope: androidx.compose.runtime.CoroutineScope
) {
    var launcher by remember { mutableStateOf<ActivityResultLauncher<Intent>?>(null) }
    LaunchedEffect(Unit) {
        launcher = rememberFileLauncher { uri ->
            if (uri != null) {
                scope.launch {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        val tempFile = java.io.File(context.cacheDir, "lespri_restore_${System.currentTimeMillis()}.gz")
                        tempFile.outputStream().use { fos -> stream.copyTo(fos) }
                        viewModel.restoreFromFile(tempFile.absolutePath)
                    }
                }
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { viewModel.exportBackup() },
            enabled = !uiState.isLoading,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
        ) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(imageVector = CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (uiState.isLoading) "Menyimpan..." else "Backup")
            }
        }
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/gzip"
                }
                launcher?.launch(intent)
            },
            shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
        ) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(imageVector = CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Restore")
            }
        }
    }
}

@Composable
private fun Messages(uiState: BackupUiState) {
    uiState.errorMessage?.let { error ->
        Text(text = error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
    }
    uiState.successMessage?.let { msg ->
        Text(text = msg, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun FileList(
    viewModel: BackupViewModel,
    uiState: BackupUiState,
    scope: androidx.compose.runtime.CoroutineScope
) {
    Text(
        text = "Berkas Backup Tersedia",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        items(items = uiState.backupFiles) { file ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = file.name, style = MaterialTheme.typography.bodyMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                        Text(
                            text = "${file.sizeBytes / 1024} KB",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { scope.launch { viewModel.restoreFromFile(file.path) } },
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Restore", style = MaterialTheme.typography.labelSmall)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(
                            onClick = { viewModel.deleteBackup(file.name) },
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    )

    if (uiState.backupFiles.isEmpty()) {
        Text(
            text = "Belum ada berkas backup.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun rememberFileLauncher(callback: (Uri?) -> Unit): ActivityResultLauncher<Intent> {
    val activity = LocalContext.current as Activity
    return remember { activity.registerForActivityResult(ActivityResultContracts.OpenDocument()) { callback(it) } }
}

