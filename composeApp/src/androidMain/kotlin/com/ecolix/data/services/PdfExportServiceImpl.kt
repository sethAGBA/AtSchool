package com.ecolix.data.services

import android.content.Context
import android.os.Environment
import com.ecolix.data.models.ReportCard
import com.ecolix.domain.services.PdfExportService
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Android implementation of [PdfExportService].
 */
actual class PdfExportServiceImpl actual constructor() : PdfExportService {

    actual override suspend fun generateReportCardPdf(reportCard: ReportCard): ByteArray {
        // For now, we will return an empty byte array.
        // We will implement this later.
        return ByteArray(0)
    }

    actual override suspend fun exportToFile(pdfData: ByteArray, fileName: String, destinationPath: String?): Result<String> {
        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val destination = destinationPath?.let { File(it) } ?: downloadsDir
            if (!destination.exists()) {
                destination.mkdirs()
            }
            val file = File(destination, fileName)
            FileOutputStream(file).use { fos ->
                fos.write(pdfData)
            }
            Result.success(file.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
