package com.ecolix.domain.services

import com.ecolix.data.models.ReportCard

/**
 * Service d'export de bulletins en format PDF
 * Interface suivant l'architecture MVVM - layer Domain
 */
interface PdfExportService {
    /**
     * Génère un PDF pour un bulletin donné
     * @param reportCard Le bulletin à exporter
     * @return ByteArray contenant les données du PDF
     */
    suspend fun generateReportCardPdf(reportCard: ReportCard): ByteArray
    
    /**
     * Exporte le PDF vers un fichier
     * @param pdfData Les données du PDF
     * @param fileName Le nom du fichier (sans extension)
     * @param destinationPath Chemin optionnel du dossier de destination. Si null, utilise le dossier par défaut
     * @return Result contenant le chemin du fichier ou une erreur
     */
    suspend fun exportToFile(pdfData: ByteArray, fileName: String, destinationPath: String? = null): Result<String>
}
