package edu.lespri.app.domain.repository

import edu.lespri.app.domain.model.ImportSummary

/**
 * Kontrak repositori untuk portabilitas data (ekspor/impor CSV dan backup/restore JSON).
 */
interface BackupRepository {
    suspend fun exportJsonBackup(): Result<String>
    suspend fun restoreJsonBackup(jsonPayload: String): Result<Unit>
    suspend fun exportCsvDataset(datasetName: String): Result<String>
    suspend fun importCsvData(rawCsv: String, columnMapping: Map<String, String>): Result<ImportSummary>
}
