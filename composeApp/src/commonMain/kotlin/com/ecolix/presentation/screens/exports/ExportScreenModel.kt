package com.ecolix.presentation.screens.exports

import com.ecolix.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExportScreenModel {
    private val _state = MutableStateFlow(ExportUiState())
    val state: StateFlow<ExportUiState> = _state.asStateFlow()

    init {
        loadMockTemplates()
        loadMockRecentExports()
    }

    fun onDarkModeChange(isDarkMode: Boolean) {
        _state.value = _state.value.copy(isDarkMode = isDarkMode)
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    private fun loadMockTemplates() {
        val templates = listOf(
            ExportTemplate(
                id = "1",
                name = "Bulletin de Notes (Standard)",
                description = "Modèle complet avec moyennes, rangs et appréciations par matière.",
                type = ExportType.BULLETIN
            ),
            ExportTemplate(
                id = "2",
                name = "PV de Délibération",
                description = "Procès verbal de synthèse de classe trié par ordre de mérite.",
                type = ExportType.PROCES_VERBAL
            ),
            ExportTemplate(
                id = "3",
                name = "Certificat de Scolarité",
                description = "Attestation officielle de fréquentation pour l'élève sélectionné.",
                type = ExportType.CERTIFICATE
            ),
            ExportTemplate(
                id = "4",
                name = "Liste d'Émargement",
                description = "Liste pour le suivi des présences ou des signatures d'examen.",
                type = ExportType.STUDENT_LIST,
                supportedFormats = listOf(ExportFormat.PDF, ExportFormat.EXCEL, ExportFormat.CSV)
            ),
            ExportTemplate(
                id = "5",
                name = "Carte d'Identité Scolaire",
                description = "Génération de cartes d'identité avec photo et QR code.",
                type = ExportType.ID_CARD
            ),
            ExportTemplate(
                id = "6",
                name = "Reçu de Paiement",
                description = "Générer un duplicata de reçu pour une transaction spécifique.",
                type = ExportType.PAYMENT_RECEIPT
            ),
            ExportTemplate(
                id = "7",
                name = "Emploi du Temps Classique",
                description = "Planning hebdomadaire des cours par classe ou par enseignant.",
                type = ExportType.TIMETABLE
            ),
            ExportTemplate(
                id = "8",
                name = "Bilan Financier",
                description = "Rapport détaillé des encaissements et décaissements.",
                type = ExportType.FINANCIAL_REPORT
            ),
            ExportTemplate(
                id = "9",
                name = "Relevé de Notes (Annuel)",
                description = "Synthèse cumulative de toutes les notes obtenues durant l'année.",
                type = ExportType.TRANSCRIPT
            ),
            ExportTemplate(
                id = "10",
                name = "Attestation de Réussite",
                description = "Document confirmant le passage en classe supérieure.",
                type = ExportType.SUCCESS_CERTIFICATE
            ),
            ExportTemplate(
                id = "11",
                name = "Fiche de Renseignement Élève",
                description = "Dossier synthétique contenant les infos personnelles et médicales.",
                type = ExportType.INFO_SHEET
            ),
            ExportTemplate(
                id = "12",
                name = "Certificat de Radiation",
                description = "Document officiel attestant qu'un élève a quitté l'établissement.",
                type = ExportType.WITHDRAWAL_CERTIFICATE
            ),
            ExportTemplate(
                id = "13",
                name = "Tableau d'Honneur",
                description = "Diplôme honorifique pour les élèves ayant atteint l'excellence.",
                type = ExportType.HONOR_ROLL
            )
        )
        _state.value = _state.value.copy(templates = templates)
    }

    private fun loadMockRecentExports() {
        val recent = listOf(
            ExportJob(
                id = "j1",
                name = "Bulletins_6eme_A_T1.pdf",
                type = ExportType.BULLETIN,
                format = ExportFormat.PDF,
                status = ExportStatus.COMPLETED,
                generatedAt = "26/01/2026 14:20",
                fileSize = "2.4 MB"
            ),
            ExportJob(
                id = "j2",
                name = "Certificat_KOUASSI_Jean.pdf",
                type = ExportType.CERTIFICATE,
                format = ExportFormat.PDF,
                status = ExportStatus.COMPLETED,
                generatedAt = "26/01/2026 09:15",
                fileSize = "150 KB"
            ),
            ExportJob(
                id = "j3",
                name = "Liste_Eleves_Total_2026.xlsx",
                type = ExportType.STUDENT_LIST,
                format = ExportFormat.EXCEL,
                status = ExportStatus.COMPLETED,
                generatedAt = "25/01/2026 16:45",
                fileSize = "85 KB"
            ),
            ExportJob(
                id = "j4",
                name = "Bilan_Janvier_Intermediaire.pdf",
                type = ExportType.FINANCIAL_REPORT,
                format = ExportFormat.PDF,
                status = ExportStatus.FAILED,
                generatedAt = "24/01/2026 11:30"
            ),
            ExportJob(
                id = "j5",
                name = "Fiche_Presence_G1_S4.pdf",
                type = ExportType.ATTENDANCE_REPORT,
                format = ExportFormat.PDF,
                status = ExportStatus.GENERATING,
                generatedAt = "À l'instant"
            )
        )
        _state.value = _state.value.copy(recentExports = recent)
    }
}
