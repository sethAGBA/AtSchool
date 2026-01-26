package com.ecolix.data.services

import com.ecolix.data.models.ReportCard
import com.ecolix.domain.services.PdfExportService

/**
 * Implémentation platform-specific du service d'export PDF
 * Utilise expect/actual pour supporter JVM, Android, iOS
 */
expect class PdfExportServiceImpl() : PdfExportService {
    override suspend fun generateReportCardPdf(reportCard: ReportCard): ByteArray
    override suspend fun exportToFile(pdfData: ByteArray, fileName: String, destinationPath: String?): Result<String>
}
