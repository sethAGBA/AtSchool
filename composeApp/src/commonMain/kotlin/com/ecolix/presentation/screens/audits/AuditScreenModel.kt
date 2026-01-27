package com.ecolix.presentation.screens.audits

import com.ecolix.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AuditScreenModel {
    private val _state = MutableStateFlow(AuditUiState())
    val state: StateFlow<AuditUiState> = _state.asStateFlow()

    init {
        loadMockLogs()
    }

    fun onDarkModeChange(isDarkMode: Boolean) {
        _state.value = _state.value.copy(isDarkMode = isDarkMode)
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun onModuleFilterChange(module: String?) {
        _state.value = _state.value.copy(selectedModule = module)
    }

    fun onSeverityFilterChange(severity: LogSeverity?) {
        _state.value = _state.value.copy(selectedSeverity = severity)
    }

    private fun loadMockLogs() {
        val mockLogs = listOf(
            AuditLog(
                id = "1",
                userId = "admin-1",
                userName = "Jean Dupont",
                userRole = "Directeur",
                action = "Modification des notes - Terminale S",
                module = "Notes & Bulletins",
                timestamp = "27/01/2026 10:15",
                severity = LogSeverity.WARNING,
                ipAddress = "192.168.1.15",
                details = "Note de l'élève Binta Diallo modifiée de 14.5 à 15.5 en Mathématiques."
            ),
            AuditLog(
                id = "2",
                userId = "sec-1",
                userName = "Marie Koffi",
                userRole = "Secrétaire",
                action = "Nouvelle inscription",
                module = "Eleves & Classes",
                timestamp = "27/01/2026 09:45",
                severity = LogSeverity.INFO,
                ipAddress = "192.168.1.20",
                details = "Élève 'Issa Traoré' ajouté à la classe 6ème A."
            ),
            AuditLog(
                id = "3",
                userId = "sys",
                userName = "Système",
                userRole = "Automate",
                action = "Échec de connexion répétée",
                module = "Sécurité",
                timestamp = "27/01/2026 08:30",
                severity = LogSeverity.SECURITY,
                ipAddress = "45.12.8.99",
                details = "5 tentatives de connexion infructueuses pour l'utilisateur 'm.diop'."
            ),
            AuditLog(
                id = "4",
                userId = "dir-2",
                userName = "Alain Sarr",
                userRole = "Proviseur",
                action = "Validation budget maintenance",
                module = "Finance & Materiel",
                timestamp = "26/01/2026 16:20",
                severity = LogSeverity.INFO,
                ipAddress = "192.168.1.10",
                details = "Budget de 350 000 FCFA validé pour 'Réparation Toiture Bloc B'."
            ),
            AuditLog(
                id = "5",
                userId = "sys",
                userName = "Système",
                userRole = "Automate",
                action = "Sauvegarde terminée",
                module = "Système",
                timestamp = "26/01/2026 23:00",
                severity = LogSeverity.INFO,
                details = "Sauvegarde hebdomadaire de la base de données terminée avec succès."
            ),
            AuditLog(
                id = "6",
                userId = "admin-login",
                userName = "Admin",
                userRole = "SuperAdmin",
                action = "Suppression utilisateur",
                module = "Utilisateurs",
                timestamp = "26/01/2026 14:10",
                severity = LogSeverity.CRITICAL,
                ipAddress = "192.168.1.5",
                details = "Compte utilisateur 'test_user' supprimé définitivement."
            )
        )

        _state.value = _state.value.copy(logs = mockLogs)
    }
}
