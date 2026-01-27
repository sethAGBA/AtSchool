package com.ecolix.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class ExportType {
    BULLETIN,
    CERTIFICATE,
    STUDENT_LIST,
    FINANCIAL_REPORT,
    ATTENDANCE_REPORT,
    PROCES_VERBAL,
    PAYMENT_RECEIPT,
    ID_CARD,
    TIMETABLE,
    TRANSCRIPT,
    SUCCESS_CERTIFICATE,
    INFO_SHEET,
    WITHDRAWAL_CERTIFICATE,
    HONOR_ROLL;

    fun toFrench(): String = when (this) {
        BULLETIN -> "Bulletin de Notes"
        CERTIFICATE -> "Certificat de Scolarité"
        STUDENT_LIST -> "Liste d'Élèves"
        FINANCIAL_REPORT -> "Rapport Financier"
        ATTENDANCE_REPORT -> "Rapport de Présence"
        PROCES_VERBAL -> "Procès Verbal (PV)"
        PAYMENT_RECEIPT -> "Reçu de Paiement"
        ID_CARD -> "Carte d'Identité"
        TIMETABLE -> "Emploi du Temps"
        TRANSCRIPT -> "Relevé de Notes"
        SUCCESS_CERTIFICATE -> "Attestation de Réussite"
        INFO_SHEET -> "Fiche de Renseignement"
        WITHDRAWAL_CERTIFICATE -> "Certificat de Radiation"
        HONOR_ROLL -> "Tableau d'Honneur"
    }

    fun getIcon(): ImageVector = when (this) {
        BULLETIN -> Icons.Default.Description
        CERTIFICATE -> Icons.Default.Verified
        STUDENT_LIST -> Icons.Default.Groups
        FINANCIAL_REPORT -> Icons.Default.AccountBalanceWallet
        ATTENDANCE_REPORT -> Icons.Default.EventAvailable
        PROCES_VERBAL -> Icons.Default.Gavel
        PAYMENT_RECEIPT -> Icons.Default.Receipt
        ID_CARD -> Icons.Default.Badge
        TIMETABLE -> Icons.Default.CalendarMonth
        TRANSCRIPT -> Icons.Default.HistoryEdu
        SUCCESS_CERTIFICATE -> Icons.Default.EmojiEvents
        INFO_SHEET -> Icons.Default.ContactPage
        WITHDRAWAL_CERTIFICATE -> Icons.Default.ExitToApp
        HONOR_ROLL -> Icons.Default.MilitaryTech
    }
}

enum class ExportFormat {
    PDF,
    EXCEL,
    CSV
}

enum class ExportStatus {
    PENDING,
    GENERATING,
    COMPLETED,
    FAILED;

    fun toFrench(): String = when (this) {
        PENDING -> "En attente"
        GENERATING -> "Génération..."
        COMPLETED -> "Terminé"
        FAILED -> "Échec"
    }
}

data class ExportJob(
    val id: String,
    val name: String,
    val type: ExportType,
    val format: ExportFormat,
    val status: ExportStatus,
    val generatedAt: String,
    val fileSize: String? = null
)

data class ExportTemplate(
    val id: String,
    val name: String,
    val description: String,
    val type: ExportType,
    val supportedFormats: List<ExportFormat> = listOf(ExportFormat.PDF, ExportFormat.EXCEL)
)

data class ExportUiState(
    val templates: List<ExportTemplate> = emptyList(),
    val recentExports: List<ExportJob> = emptyList(),
    val searchQuery: String = "",
    val isDarkMode: Boolean = false,
    val isLoading: Boolean = false
) {
    val colors: DashboardColors
        get() = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()
}
