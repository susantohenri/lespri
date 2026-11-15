package edu.lespri.app.util

import android.content.Context
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Manajer backup & restore data aplikasi Lespri.
 *
 * Melakukan serialisasi lengkap seluruh tabel Room ke dalam payload JSON,
 * lalu mengompresinya dengan GZIP untuk menyimpan ruang penyimpanan.
 * Semua operasi berkas dilakukan di direktori file aplikasi
 * ([Context.getExternalFilesDir] atau fallback internal files dir) sehingga
 * tidak memerlukan izin penyimpanan luas.
 */
class BackupManager @constructor internal constructor(
    private val context: Context
) {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    /** Versi format payload backup. Naikkan jika struktur data berubah. */
    internal const val BACKUP_VERSION = 1

    /** Direktori tempat berkas backup disimpan. */
    internal val backupDir: File by lazy {
        val ext = context.externalFilesDir(null)
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
    suspend fun exportToFile(payload: Map<String, Any?>, fileName: String = backupFileName()): File {
        return withContext(Dispatchers.IO) {
            val target = File(backupDir, fileName)
            val jsonString = json.encodeToString(payload)
            val compressed = gzipCompress(jsonString.toByteArray())
            target.outputStream().use { fos ->
                BufferedOutputStream(fos).use { bos ->
                    bos.write(compressed)
                }
            }
            target
        }
    }

    /**
     * Impor berkas backup GZIZ dan mengembalikan payload sebagai [Map<String, Any?>].
     */
    suspend fun importFromFile(file: File): Map<String, Any?> {
        return withContext(Dispatchers.IO) {
            file.inputStream().use { fis ->
                BufferedInputStream(fis).use { bis ->
                    GZIPInputStream(bis).use { gzis ->
                        val bytes = gzis.readBytes()
                        val jsonString = String(bytes, charset = java.nio.charset.StandardCharsets.UTF_8)
                        json.decodeFromString<Map<String, Any?>>(jsonString)
                    }
                }
            }
        }
    }

    /** Daftar berkas backup yang tersedia, diurut dari yang terbaru. */
    fun listBackups(): List<File> {
        return backupDir.listFiles()
            ?.filter { it.extension == "gz" && it.name.startsWith("lespri_backup_") }
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

    companion object {
        /**
         * Membuat instance [BackupManager] dengan konteks aplikasi.
         * Digunakan oleh Hilt / Repository.
         */
        fun create(context: Context): BackupManager = BackupManager(context)
    }
}

/**
 * Payload standar backup JSON Lespri.
 *
 * Struktur:
 * {
 *   "version": 1,
 *   "exportedAt": <epoch millis>,
 *   "data": {
 *     "students": [...],
 *     "tutors": [...],
 *     ...
 *   }
 * }
 */
data class BackupPayload(
    val version: Int = BackupManager.BACKUP_VERSION,
    val exportedAt: Long = System.currentTimeMillis(),
    val data: Map<String, List<Map<String, Any?>>>
)
