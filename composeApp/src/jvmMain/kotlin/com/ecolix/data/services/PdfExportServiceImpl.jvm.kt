package com.ecolix.data.services

import com.ecolix.data.models.ReportCard
import com.ecolix.domain.services.PdfExportService
import com.ecolix.data.services.templates.HtmlBulletinTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Implémentation JVM du service d'export PDF
 * Utilise openhtmltopdf pour convertir HTML → PDF
 */
actual class PdfExportServiceImpl actual constructor() : PdfExportService {
    
    actual override suspend fun generateReportCardPdf(reportCard: ReportCard): ByteArray = withContext(Dispatchers.IO) {
        try {
            // Générer le HTML du bulletin
            val html = HtmlBulletinTemplate.generateHtml(reportCard)
            
            println("📄 Generating PDF for student: ${reportCard.studentName}")
            println("   HTML generated: ${html.length} characters")
            
            // Convertir HTML → PDF avec openhtmltopdf
            val outputStream = ByteArrayOutputStream()
            
            com.openhtmltopdf.pdfboxout.PdfRendererBuilder().apply {
                useFastMode()
                withHtmlContent(html, null)
                toStream(outputStream)
                run()
            }
            
            val pdfBytes = outputStream.toByteArray()
            println("✅ PDF generated successfully: ${pdfBytes.size} bytes")
            
            pdfBytes
            
        } catch (e: Exception) {
            println("❌ PDF generation failed: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
    
    actual override suspend fun exportToFile(pdfData: ByteArray, fileName: String, destinationPath: String?): Result<String> = withContext(Dispatchers.IO) {
        try {
            val sanitizedFileName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
            val fullFileName = "$sanitizedFileName.pdf"
            
            // Utiliser le chemin fourni ou le dossier par défaut
            val targetDir = if (destinationPath != null) {
                File(destinationPath)
            } else {
                // Sauvegarder dans le dossier Documents de l'utilisateur
                val userHome = System.getProperty("user.home")
                File(userHome, "Documents/AtSchool/Bulletins")
            }
            
            // Créer le dossier s'il n'existe pas
            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }
            
            val outputFile = File(targetDir, fullFileName)
            
            // Écrire le PDF
            outputFile.writeBytes(pdfData)
            println("💾 PDF saved to: ${outputFile.absolutePath}")
            println("   File size: ${pdfData.size} bytes")
            
            Result.success(outputFile.absolutePath)
            
        } catch (e: Exception) {
            println("❌ File export failed: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
