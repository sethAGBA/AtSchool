package com.ecolix.presentation.screens.communication

import com.ecolix.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CommunicationScreenModel {
    private val _state = MutableStateFlow(CommunicationUiState())
    val state: StateFlow<CommunicationUiState> = _state.asStateFlow()

    init {
        loadMockMessages()
        loadMockTemplates()
        loadMockReminderRules()
    }

    fun onDarkModeChange(isDarkMode: Boolean) {
        _state.value = _state.value.copy(isDarkMode = isDarkMode)
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun onChannelFilterChange(channel: CommunicationType?) {
        _state.value = _state.value.copy(selectedChannel = channel)
    }

    fun onStatusFilterChange(status: MessageStatus?) {
        _state.value = _state.value.copy(selectedStatus = status)
    }

    fun onTabChange(tabIndex: Int) {
        _state.value = _state.value.copy(selectedTab = tabIndex)
    }

    private fun loadMockMessages() {
        val mockMessages = listOf(
            SchoolMessage(
                id = "1",
                title = "Réunion Parents-Professeurs",
                content = "Nous vous invitons à la réunion trimestrielle ce samedi à 10h au bloc administratif.",
                sender = "Direction",
                recipients = "Tous les parents",
                channel = CommunicationType.ANNOUNCEMENT,
                status = MessageStatus.SENT,
                timestamp = "27/01/2026 08:30"
            ),
            SchoolMessage(
                id = "2",
                title = "Alerte Retard - Issa Traoré",
                content = "Votre enfant Issa Traoré est arrivé en retard ce matin à 08:45.",
                sender = "Surveillance",
                recipients = "Parent de Issa Traoré",
                channel = CommunicationType.SMS,
                status = MessageStatus.SENT,
                timestamp = "27/01/2026 09:00"
            ),
            SchoolMessage(
                id = "3",
                title = "Congés de Février",
                content = "Les congés de mi-trimestre débuteront le vendredi 13 février après les cours.",
                sender = "Administration",
                recipients = "Élèves et Personnel",
                channel = CommunicationType.EMAIL,
                status = MessageStatus.SCHEDULED,
                timestamp = "01/02/2026 08:00"
            ),
            SchoolMessage(
                id = "4",
                title = "Rappel Frais de Scolarité",
                content = "SVP régulariser les frais du 2ème trimestre avant le 31 janvier.",
                sender = "Comptabilité",
                recipients = "Parents (Impayés)",
                channel = CommunicationType.SMS,
                status = MessageStatus.SENT,
                timestamp = "26/01/2026 15:45"
            ),
            SchoolMessage(
                id = "5",
                title = "Invitation Gala de Bienfaisance",
                content = "Brouillon de l'invitation pour le gala annuel de l'école.",
                sender = "Association Parents",
                recipients = "Liste Partenaires",
                channel = CommunicationType.EMAIL,
                status = MessageStatus.DRAFT,
                timestamp = "25/01/2026 11:20"
            ),
            SchoolMessage(
                id = "6",
                title = "Urgent: Incident Électrique",
                content = "Coupure de courant prévue demain matin pour travaux de maintenance.",
                sender = "Maintenance",
                recipients = "Tout le personnel",
                channel = CommunicationType.ANNOUNCEMENT,
                status = MessageStatus.SENT,
                timestamp = "27/01/2026 14:10"
            ),
            SchoolMessage(
                id = "7",
                title = "Résultat Examen",
                content = "Félicitations ! Les résultats du BEPC sont disponibles sur l'espace élève.",
                sender = "Scolarité",
                recipients = "Élèves 3ème",
                channel = CommunicationType.PUSH,
                status = MessageStatus.SENT,
                timestamp = "27/01/2026 16:30"
            ),
            SchoolMessage(
                id = "8",
                title = "Notification de Retard (Auto)",
                content = "Cher parent, Issa Traoré est arrivé en retard de 20 minutes aujourd'hui.",
                sender = "Système",
                recipients = "Parent de Issa Traoré",
                channel = CommunicationType.SMS,
                status = MessageStatus.SENT,
                timestamp = "27/01/2026 08:35",
                isAutomated = true
            ),
            SchoolMessage(
                id = "9",
                title = "Rappel Solde (Programmé)",
                content = "Cher parent, n'oubliez pas de payer le solde de scolarité avant le 5 février.",
                sender = "Système",
                recipients = "Parents avec Arriérés",
                channel = CommunicationType.EMAIL,
                status = MessageStatus.SCHEDULED,
                timestamp = "27/01/2026 10:00",
                scheduledAt = "02/02/2026 09:00",
                isAutomated = true
            )
        )

        _state.value = _state.value.copy(messages = mockMessages)
    }

    private fun loadMockTemplates() {
        val mockTemplates = listOf(
            CommunicationTemplate(
                id = "t1",
                name = "Avis de retard",
                content = "Cher(e) {parent_name}, nous vous informons que votre enfant {student_name} est arrivé en retard à {school_name} ce jour à {time}.",
                type = CommunicationType.SMS,
                variables = listOf("{parent_name}", "{student_name}", "{school_name}", "{time}")
            ),
            CommunicationTemplate(
                id = "t2",
                name = "Rappel de paiement",
                content = "M./Mme {parent_name}, un solde de {balance} reste impayé pour {student_name}. Merci de régulariser avant le {due_date}.",
                type = CommunicationType.SMS,
                variables = listOf("{parent_name}", "{balance}", "{student_name}", "{due_date}")
            ),
            CommunicationTemplate(
                id = "t3",
                name = "Convocation réunion",
                content = "Invitation à la réunion de la classe {classroom} le {date} à {time}. Objet: {event_name}.",
                type = CommunicationType.EMAIL,
                variables = listOf("{classroom}", "{date}", "{time}", "{event_name}")
            ),
            CommunicationTemplate(
                id = "t4",
                name = "Alerte absence",
                content = "Absence injustifiée de {student_name} constatée ce {date}. Merci de contacter la vie scolaire.",
                type = CommunicationType.PUSH,
                variables = listOf("{student_name}", "{date}")
            )
        )
        _state.value = _state.value.copy(templates = mockTemplates)
    }

    private fun loadMockReminderRules() {
        val rules = listOf(
            ReminderRule(
                id = "r1",
                name = "Alerte Retard Immédiat",
                trigger = "Enregistrement d'un retard > 10 min",
                channel = CommunicationType.SMS,
                templateId = "t1"
            ),
            ReminderRule(
                id = "r2",
                name = "Rappel Mensuel Impayés",
                trigger = "Solde > 0 le 25 du mois",
                channel = CommunicationType.EMAIL,
                templateId = "t2"
            ),
            ReminderRule(
                id = "r3",
                name = "Notification Absences",
                trigger = "Élève absent au cours de 8h",
                channel = CommunicationType.SMS,
                templateId = "t4"
            ),
            ReminderRule(
                id = "r4",
                name = "Anniversaires Élèves",
                trigger = "Jour d'anniversaire",
                channel = CommunicationType.PUSH,
                isActive = false
            )
        )
        _state.value = _state.value.copy(reminderRules = rules)
    }
}
