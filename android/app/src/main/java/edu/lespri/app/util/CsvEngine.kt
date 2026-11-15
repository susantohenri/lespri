package edu.lespri.app.util

/**
 * RFC 4180 Compliant CSV Parser & Generator.
 *
 * Aturan RFC 4180 yang diimplementasi:
 * - Pemisah kolom: koma (,)
 * - Pemisah baris: CRLF (\r\n) pada output; input toleransi \n, \r\n, dan \r
 * - Kolom yang mengandung koma, tanda petik, atau baris baru harus diapit tanda petik ganda
 * - Tanda petik di dalam field di-quote di-escape dengan menggandakan ("")
 * - Baris pertama dianggap sebagai header
 *
 * Parser dirancang ramah streaming: hasilnya [CsvResult] berisi daftar header dan
 * baris data sebagai [Map<String, String>] yang siap dipetakan ke field domain.
 */
object CsvEngine {

    private const val DELIMITER = ','
    private const val QUOTE = '"'

    /**
     * Hasil parsing CSV: daftar nama kolom (header) dan baris data sebagai pasangan nama-nilai.
     */
    data class CsvResult(
        val headers: List<String>,
        val rows: List<Map<String, String>>
    )

    /**
     * Kesalahan saat parsing atau generate CSV.
     */
    sealed class CsvError(message: String? = null) : Exception(message) {
        object EmptyInput : CsvError("Input CSV kosong")
        data class MissingHeaders(val found: List<String>) : CsvError("Header tidak ditemukan")
        data class RowLengthMismatch(val rowNumber: Int, val expected: Int, val actual: Int) : CsvError("Panjang baris tidak sesuai")
        data class ParseError(override val message: String) : CsvError(message)
    }

    /**
     * Parsing teks CSV ke [CsvResult].
     * Baris pertama dianggap header. Baris kosong diabaikan.
     */
    fun parse(csvText: String): Result<CsvResult> = runCatching {
        if (csvText.isBlank()) throw CsvError.EmptyInput

        val lines = splitRecords(csvText)
        if (lines.isEmpty()) throw CsvError.EmptyInput

        val headerLine = lines.first()
        val headers = parseFields(headerLine)
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (headers.isEmpty()) {
            throw CsvError.MissingHeaders(found = parseFields(headerLine).map { it.trim() })
        }

        val rows = mutableListOf<Map<String, String>>()
        for (i in 1 until lines.size) {
            val line = lines[i]
            if (line.isBlank()) continue
            val fields = parseFields(line)
            val row = headers.indices.associate { idx ->
                headers[idx] to (fields.getOrNull(idx) ?: "")
            }
            rows += row
        }

        CsvResult(headers, rows)
    }

    /**
     * Generate teks CSV dari [headers] dan [rows].
     * Setiap field yang mengandung delimiter, petik, atau baris baru akan di-quote otomatis.
     */
    fun generate(headers: List<String>, rows: List<Map<String, String>>): String {
        val sb = StringBuilder()
        // Header
        sb.append(headers.joinToString(",") { escapeField(it) })
        sb.append("\r\n")
        // Data rows
        for (row in rows) {
            val values = headers.map { header -> escapeField(row[header] ?: "") }
            sb.append(values.joinToString(","))
            sb.append("\r\n")
        }
        return sb.toString()
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    private fun splitRecords(text: String): List<String> {
        // Normalisasi \r\n dan \r ke \n, lalu split
        return text.replace("\r\n", "\n").replace("\r", "\n").split("\n")
    }

    /**
     * Parse satu baris CSV ke daftar field, menghandle quoted fields sesuai RFC 4180.
     */
    private fun parseFields(line: String): List<String> {
        val fields = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                inQuotes -> {
                    if (c == QUOTE) {
                        if (i + 1 < line.length && line[i + 1] == QUOTE) {
                            // Petik terganda "" adalah escape
                            sb.append(QUOTE)
                            i += 2
                        } else {
                            // Akhir quoted field
                            inQuotes = false
                            i++
                        }
                    } else {
                        sb.append(c)
                        i++
                    }
                }
                c == QUOTE -> {
                    inQuotes = true
                    i++
                }
                c == DELIMITER -> {
                    fields += sb.toString()
                    sb.setLength(0)
                    i++
                }
                else -> {
                    sb.append(c)
                    i++
                }
            }
        }
        fields += sb.toString()
        return fields
    }

    private fun escapeField(value: String): String {
        if (value.contains(DELIMITER) || value.contains(QUOTE) || value.contains("\n") || value.contains("\r")) {
            return "\"${value.replace("\"", "\"\"")}\""
        }
        return value
    }
}
