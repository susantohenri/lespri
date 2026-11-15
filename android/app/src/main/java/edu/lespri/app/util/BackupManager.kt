package edu.lespri.app.util

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Manajer backup & restore data aplikasi Lespri.
 *
 * Melakukan serialisasi lengkap seluruh tabel Room ke dalam payload JSON,
 * lalu mengompresinya dengan GZIP untuk menyimpan ruang penyimpanan.
 * Semua operasi berkas dilakukan di direktori file aplikasi
 * ([Context.getExternalFilesDir] atau fallback internal files dir) sehingga
 * tidak memerlukan izin penyimpanan luas.
 */
class BackupManager(
    private val context: Context
) {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    companion object {
        const val BACKUP_VERSION = 1

        /**
         * Membuat instance [BackupManager] dengan konteks aplikasi.
         * Digunakan oleh Hilt / Repository.
         */
        fun create(context: Context): BackupManager = BackupManager(context)
    }

    /** Direktori tempat berkas backup disimpan. */
    internal val backupDir: File by lazy {
        val ext = context.getExternalFilesDir(null)
        val dir = if (ext != null) File(ext, "backups") else File(context.filesDir, "backups")
        dir.mkdirs()
        dir
    }

    /**
     * Nama berkas backup dengan stempel waktu, contoh: `lespri_backup_20261115_073000.json.gz`.
     */
    fun backupFileName(): String {
        val ts = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US)
            .format(java.util.Date())
        return "lespri_backup_$ts.json.gz"
    }

    /**
     * Ekspor seluruh [payload] ke berkas GZIP-kompresi.
     * Mengembalikan [File] yang telah ditulis.
     */
    suspend fun exportBackup(payload: BackupPayload, fileName: String = backupFileName()): File {
        return withContext(Dispatchers.IO) {
            val target = File(backupDir, fileName)
            val jsonString = json.encodeToString(payload)
            val compressed = gzipCompress(jsonString.toByteArray(Charsets.UTF_8))
            target.outputStream().use { fos ->
                BufferedOutputStream(fos).use { bos ->
                    bos.write(compressed)
                }
            }
            target
        }
    }

    /**
     * Kompatibilitas dengan kode lama: ekspor berkas dari string atau map.
     */
    suspend fun exportToFile(payloadMap: Map<String, String?>, fileName: String = backupFileName()): File {
        return withContext(Dispatchers.IO) {
            val target = File(backupDir, fileName)
            val content = payloadMap["content"] ?: payloadMap["backup"] ?: ""
            target.outputStream().use { fos ->
                BufferedOutputStream(fos).use { bos ->
                    bos.write(content.toByteArray(Charsets.UTF_8))
                }
            }
            target
        }
    }

    /**
     * Impor berkas backup GZIP dan mengembalikan payload JSON string.
     */
    suspend fun importFromFile(file: File): String {
        return withContext(Dispatchers.IO) {
            file.inputStream().use { fis ->
                BufferedInputStream(fis).use { bis ->
                    // Cek apakah file berupa gzip atau plain text
                    try {
                        GZIPInputStream(bis).use { gzis ->
                            val bytes = gzis.readBytes()
                            String(bytes, charset = java.nio.charset.StandardCharsets.UTF_8)
                        }
                    } catch (e: Exception) {
                        file.readText(Charsets.UTF_8)
                    }
                }
            }
        }
    }

    /** Daftar berkas backup yang tersedia, diurut dari yang terbaru. */
    fun listBackups(): List<File> {
        return backupDir.listFiles()
            ?.filter { (it.extension == "gz" || it.extension == "json") && it.name.startsWith("lespri_backup_") }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }

    /** Hapus berkas backup tertentu. */
    fun deleteBackup(file: File): Boolean {
        return file.delete()
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    private fun gzipCompress(input: ByteArray): ByteArray {
        val baos = java.io.ByteArrayOutputStream()
        GZIPOutputStream(baos).use { gzos ->
            gzos.write(input)
        }
        return baos.toByteArray()
    }
}

/**
 * Payload standar backup JSON Lespri.
 */
@Serializable
data class BackupPayload(
    val version: Int = BackupManager.BACKUP_VERSION,
    val exportedAt: Long = System.currentTimeMillis(),
    val data: Map<String, List<Map<String, String>>>
)
