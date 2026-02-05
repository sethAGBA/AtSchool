package com.ecolix.presentation.screens.academic

import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ecolix.atschool.api.StructureApiService
import com.ecolix.atschool.api.SchoolYearDto
import com.ecolix.atschool.api.AcademicPeriodDto
import com.ecolix.atschool.api.AcademicSettingsDto
import com.ecolix.atschool.api.GradeLevelDto
import com.ecolix.data.models.*
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AcademicScreenModel(private val structureApiService: StructureApiService) : 
    StateScreenModel<AcademicUiState>(AcademicUiState()) {

    init {
        loadData()
        loadCalendarData()
        fetchAcademicSettings()
    }

    private fun fetchAcademicSettings() {
        screenModelScope.launch {
            structureApiService.getAcademicSettings()
                .onSuccess { settingsDto ->
                    structureApiService.getGradeLevels()
                        .onSuccess { gradeLevelDtos ->
                            mutableState.update { state ->
                                state.copy(
                                    settings = settingsDto.toUiModel(gradeLevelDtos)
                                )
                            }
                        }
                }
        }
    }

    fun updateSettings(settings: AcademicSettings) {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            val dto = settings.toDto(0) // tenantId handled by server
            structureApiService.updateAcademicSettings(dto)
                .onSuccess {
                    // Update grade levels too
                    val gradeDtos = settings.gradeScale.gradeLevels.map { it.toDto(0) }
                    structureApiService.updateGradeLevels(gradeDtos)
                        .onSuccess {
                            mutableState.update { it.copy(
                                isLoading = false,
                                successMessage = "Paramètres enregistrés avec succès"
                            ) }
                            fetchAcademicSettings()
                        }
                        .onFailure { error ->
                            mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                        }
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    private fun loadCalendarData() {
        screenModelScope.launch {
            val eventsResult = structureApiService.getAcademicEvents()
            val holidaysResult = structureApiService.getHolidays()

            val eventDtos = eventsResult.getOrDefault(emptyList())
            val holidayDtos = holidaysResult.getOrDefault(emptyList())

            if (eventDtos.isEmpty() && holidayDtos.isEmpty()) {
                seedDefaultCalendarData()
            } else {
                mutableState.update { it.copy(
                    events = eventDtos.map { dto -> dto.toUiModel() },
                    holidays = holidayDtos.map { dto -> dto.toUiModel() }
                ) }
            }
        }
    }

    private fun seedDefaultCalendarData() {
        screenModelScope.launch {
            val state = mutableState.value
            val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
            val activeYearId = state.selectedSchoolYearId ?: state.schoolYears.find { it.status == AcademicStatus.ACTIVE }?.id

            // 1. Seed Default Periods if missing in active year
            if (activeYearId != null && state.periods.isEmpty()) {
                val yearIdInt = activeYearId.toIntOrNull() ?: 0
                val defaultPeriods = listOf(
                    // Trimesters
                    Triple("1er Trimestre", "$currentYear-09-15", "$currentYear-12-20") to PeriodType.TRIMESTER,
                    Triple("2ème Trimestre", "${currentYear + 1}-01-05", "${currentYear + 1}-03-28") to PeriodType.TRIMESTER,
                    Triple("3ème Trimestre", "${currentYear + 1}-04-07", "${currentYear + 1}-06-30") to PeriodType.TRIMESTER,
                    // Semesters
                    Triple("1er Semestre", "$currentYear-09-15", "${currentYear + 1}-01-31") to PeriodType.SEMESTER,
                    Triple("2ème Semestre", "${currentYear + 1}-02-01", "${currentYear + 1}-06-30") to PeriodType.SEMESTER
                )

                defaultPeriods.forEachIndexed { index, (data, type) ->
                    val (name, start, end) = data
                    structureApiService.createAcademicPeriod(AcademicPeriodDto(
                        id = null,
                        tenantId = 0,
                        anneeScolaireId = yearIdInt,
                        nom = name,
                        numero = (index % 3) + 1,
                        dateDebut = start,
                        dateFin = end,
                        periodType = type.name,
                        status = "UPCOMING"
                    ))
                }
                
                // Refresh periods in state to generate events for them
                structureApiService.getPeriodsByYear(yearIdInt).onSuccess { dtos ->
                    mutableState.update { it.copy(periods = dtos.map { it.toUiModel() }) }
                }
            }

            // 2. Generate Events for existing periods (Evaluations & Bulletins)
            val currentState = mutableState.value
            currentState.periods.forEach { period ->
                // Evaluation Period (typically last 2 weeks of period)
                val evalDate = period.endDate // Simplified: use end date as reference
                structureApiService.createAcademicEvent(com.ecolix.atschool.api.AcademicEventDto(
                    title = "Évaluations - ${period.name}",
                    date = evalDate,
                    type = EventType.EXAM.name,
                    color = "#EF4444", // Red for exams
                    tenantId = 0
                ))

                // Report Card Distribution (typically 1 week after period end)
                structureApiService.createAcademicEvent(com.ecolix.atschool.api.AcademicEventDto(
                    title = "Remise des Bulletins - ${period.name}",
                    date = period.endDate, // simplified
                    type = EventType.DEADLINE.name,
                    color = "#10B981", // Green for results
                    tenantId = 0
                ))
            }

            // 3. General Events & Holidays
            val defaultEvents = listOf(
                Triple("Rentrée des Classes", "$currentYear-09-01", EventType.CEREMONY),
                Triple("Fête de Fin d'Année", "${currentYear + 1}-06-28", EventType.CEREMONY)
            )

            defaultEvents.forEach { (title, date, type) ->
                structureApiService.createAcademicEvent(com.ecolix.atschool.api.AcademicEventDto(
                    title = title,
                    date = date,
                    type = type.name,
                    color = "#3B82F6",
                    tenantId = 0
                ))
            }

            val defaultHolidays = listOf(
                Triple("Vacances de Noël", "$currentYear-12-20", "${currentYear + 1}-01-05"),
                Triple("Vacances de Pâques", "${currentYear + 1}-03-29", "${currentYear + 1}-04-06")
            )

            defaultHolidays.forEach { (name, start, end) ->
                structureApiService.createHoliday(com.ecolix.atschool.api.HolidayDto(
                    name = name,
                    startDate = start,
                    endDate = end,
                    type = HolidayType.SCHOOL_BREAK.name,
                    tenantId = 0
                ))
            }

            // Reload everything
            val updatedEvents = structureApiService.getAcademicEvents().getOrDefault(emptyList())
            val updatedHolidays = structureApiService.getHolidays().getOrDefault(emptyList())
            
            mutableState.update { it.copy(
                events = updatedEvents.map { it.toUiModel() },
                holidays = updatedHolidays.map { it.toUiModel() }
            ) }
        }
    }

    fun loadData() {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }
            
            structureApiService.getSchoolYears()
                .onSuccess { yearDtos ->
                    val uiYears = yearDtos.map { it.toUiModel() }
                    mutableState.update { state ->
                        state.copy(
                            schoolYears = uiYears,
                            isLoading = false
                        )
                    }
                    
                    // If there's an active year, load its periods
                    uiYears.find { it.status == AcademicStatus.ACTIVE }?.let { activeYear ->
                        mutableState.update { it.copy(selectedSchoolYearId = activeYear.id) }
                        loadPeriods(activeYear.id)
                    }
                    
                    updateStatistics()
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun loadPeriods(yearId: String) {
        val idInt = yearId.toIntOrNull() ?: return
        screenModelScope.launch {
            structureApiService.getPeriodsByYear(idInt)
                .onSuccess { periodDtos ->
                    mutableState.update { state ->
                        state.copy(
                            periods = periodDtos.map { it.toUiModel() }
                        )
                    }
                    updateStatistics()
                }
        }
    }

    fun onDarkModeChange(isDarkMode: Boolean) {
        val colors = if (isDarkMode) {
            DashboardColors.dark()
        } else {
            DashboardColors.light()
        }
        mutableState.update { it.copy(colors = colors) }
    }

    fun onViewModeChange(mode: AcademicViewMode) {
        mutableState.update { it.copy(viewMode = mode) }
    }

    fun onSearchQueryChange(query: String) {
        mutableState.update { it.copy(searchQuery = query) }
    }

    fun onSelectSchoolYear(yearId: String) {
        mutableState.update { it.copy(selectedSchoolYearId = yearId) }
        loadPeriods(yearId)
    }

    fun onSelectPeriod(periodId: String) {
        mutableState.update { it.copy(selectedPeriodId = periodId) }
    }

    fun createSchoolYear(name: String, startDate: String, endDate: String, types: List<PeriodType>, numPeriods: Int, periods: List<AcademicPeriodDto>? = null) {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            val dto = SchoolYearDto(
                id = null,
                tenantId = 0, // Server will override
                libelle = name,
                dateDebut = startDate,
                dateFin = endDate,
                status = "UPCOMING",
                numberOfPeriods = numPeriods,
                periodType = types.joinToString(","),
                isDefault = false,
                periods = periods
            )
            structureApiService.createSchoolYear(dto)
                .onSuccess { 
                    mutableState.update { it.copy(successMessage = "Année scolaire créée avec succès") }
                    loadData() 
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun updateSchoolYear(yearId: String, name: String, startDate: String, endDate: String, types: List<PeriodType>, numPeriods: Int, periods: List<AcademicPeriodDto>? = null) {
        val idInt = yearId.toIntOrNull() ?: return
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            val dto = SchoolYearDto(
                id = idInt,
                tenantId = 0,
                libelle = name,
                dateDebut = startDate,
                dateFin = endDate,
                status = "ACTIVE",
                numberOfPeriods = numPeriods,
                periodType = types.joinToString(","),
                isDefault = false,
                periods = periods
            )
            structureApiService.updateSchoolYear(idInt, dto)
                .onSuccess { 
                    mutableState.update { it.copy(successMessage = "Année scolaire modifiée") }
                    loadData() 
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun deleteSchoolYear(yearId: String) {
        val idInt = yearId.toIntOrNull() ?: return
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }
            structureApiService.deleteSchoolYear(idInt)
                .onSuccess { 
                    mutableState.update { it.copy(successMessage = "Année scolaire supprimée") }
                    loadData() 
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun setDefaultYear(yearId: String) {
        val idInt = yearId.toIntOrNull() ?: return
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }
            structureApiService.setDefaultSchoolYear(idInt)
                .onSuccess { 
                    mutableState.update { it.copy(successMessage = "Année définie par défaut") }
                    loadData() 
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun createAcademicPeriod(name: String, number: Int, startDate: String, endDate: String, type: PeriodType) {
        val yearId = mutableState.value.selectedSchoolYearId?.toIntOrNull() ?: return
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            val dto = AcademicPeriodDto(
                id = null,
                tenantId = 0,
                anneeScolaireId = yearId,
                nom = name,
                numero = number,
                dateDebut = startDate,
                dateFin = endDate,
                periodType = type.name,
                status = "UPCOMING"
            )
            structureApiService.createAcademicPeriod(dto)
                .onSuccess { 
                    mutableState.update { it.copy(successMessage = "Période créée avec succès") }
                    loadPeriods(yearId.toString())
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun updateAcademicPeriod(periodId: String, name: String, number: Int, startDate: String, endDate: String, type: PeriodType, evaluationDeadline: String? = null, reportCardDeadline: String? = null) {
        val idInt = periodId.toIntOrNull() ?: return
        val yearId = mutableState.value.selectedSchoolYearId ?: return
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            val dto = AcademicPeriodDto(
                id = idInt,
                tenantId = 0,
                anneeScolaireId = yearId.toInt(),
                nom = name,
                numero = number,
                dateDebut = startDate,
                dateFin = endDate,
                periodType = type.name,
                evaluationDeadline = evaluationDeadline,
                reportCardDeadline = reportCardDeadline,
                status = "ACTIVE"
            )
            structureApiService.updateAcademicPeriod(idInt, dto)
                .onSuccess { 
                    mutableState.update { it.copy(successMessage = "Période modifiée") }
                    loadPeriods(yearId)
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun updatePeriodDeadlines(periodId: String, evaluationDeadline: String?, reportCardDeadline: String?) {
        val period = mutableState.value.periods.find { it.id == periodId } ?: return
        updateAcademicPeriod(
            periodId = period.id,
            name = period.name,
            number = period.periodNumber,
            startDate = period.startDate,
            endDate = period.endDate,
            type = period.type,
            evaluationDeadline = evaluationDeadline,
            reportCardDeadline = reportCardDeadline
        )
    }

    fun deleteAcademicPeriod(periodId: String) {
        val idInt = periodId.toIntOrNull() ?: return
        val yearId = mutableState.value.selectedSchoolYearId ?: return
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }
            structureApiService.deleteAcademicPeriod(idInt)
                .onSuccess { 
                    mutableState.update { it.copy(successMessage = "Période supprimée") }
                    loadPeriods(yearId)
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun createAcademicEvent(title: String, description: String?, date: String, endDate: String?, type: EventType, color: Color) {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            val hexColor = color.toHex()
            val dto = com.ecolix.atschool.api.AcademicEventDto(
                title = title,
                description = description,
                date = date,
                endDate = endDate,
                type = type.name,
                color = hexColor,
                tenantId = 0
            )
            structureApiService.createAcademicEvent(dto)
                .onSuccess {
                    mutableState.update { it.copy(successMessage = "Événement créé") }
                    loadCalendarData()
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun updateAcademicEvent(id: String, title: String, description: String?, date: String, endDate: String?, type: EventType, color: Color) {
        val idInt = id.toIntOrNull() ?: return
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            val hexColor = color.toHex()
            val dto = com.ecolix.atschool.api.AcademicEventDto(
                id = idInt,
                title = title,
                description = description,
                date = date,
                endDate = endDate,
                type = type.name,
                color = hexColor,
                tenantId = 0
            )
            structureApiService.updateAcademicEvent(idInt, dto)
                .onSuccess {
                    mutableState.update { it.copy(successMessage = "Événement mis à jour") }
                    loadCalendarData()
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun deleteAcademicEvent(id: String) {
        val idInt = id.toIntOrNull() ?: return
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            structureApiService.deleteAcademicEvent(idInt)
                .onSuccess {
                    mutableState.update { it.copy(successMessage = "Événement supprimé") }
                    loadCalendarData()
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    // Holidays Operations
    fun createHoliday(name: String, startDate: String, endDate: String, type: HolidayType) {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            val dto = com.ecolix.atschool.api.HolidayDto(
                name = name,
                startDate = startDate,
                endDate = endDate,
                type = type.name,
                tenantId = 0
            )
            structureApiService.createHoliday(dto)
                .onSuccess {
                    mutableState.update { it.copy(successMessage = "Vacances créées") }
                    loadCalendarData()
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun updateHoliday(id: String, name: String, startDate: String, endDate: String, type: HolidayType) {
        val idInt = id.toIntOrNull() ?: return
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            val dto = com.ecolix.atschool.api.HolidayDto(
                id = idInt,
                name = name,
                startDate = startDate,
                endDate = endDate,
                type = type.name,
                tenantId = 0
            )
            structureApiService.updateHoliday(idInt, dto)
                .onSuccess {
                    mutableState.update { it.copy(successMessage = "Vacances mises à jour") }
                    loadCalendarData()
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun deleteHoliday(id: String) {
        val idInt = id.toIntOrNull() ?: return
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            structureApiService.deleteHoliday(idInt)
                .onSuccess {
                    mutableState.update { it.copy(successMessage = "Vacances supprimées") }
                    loadCalendarData()
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun clearError() {
        mutableState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccess() {
        mutableState.update { it.copy(successMessage = null) }
    }

    fun setPeriodStatus(periodId: String, status: AcademicStatus) {
        val idInt = periodId.toIntOrNull() ?: return
        val yearId = mutableState.value.selectedSchoolYearId ?: return
        val statusString = when (status) {
            AcademicStatus.ACTIVE -> "ACTIVE"
            AcademicStatus.COMPLETED -> "COMPLETED"
            AcademicStatus.UPCOMING -> "UPCOMING"
            else -> "UPCOMING"
        }
        
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }
            structureApiService.setAcademicPeriodStatus(idInt, statusString)
                .onSuccess {
                    mutableState.update { it.copy(successMessage = "Statut mis à jour") }
                    loadPeriods(yearId)
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun setSchoolYearStatus(yearId: String, status: AcademicStatus) {
        val idInt = yearId.toIntOrNull() ?: return
        val statusString = when (status) {
            AcademicStatus.ACTIVE -> "ACTIVE"
            AcademicStatus.COMPLETED -> "COMPLETED"
            AcademicStatus.UPCOMING -> "UPCOMING"
            else -> "UPCOMING"
        }
        
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }
            structureApiService.setSchoolYearStatus(idInt, statusString)
                .onSuccess {
                    mutableState.update { it.copy(successMessage = "Statut de l'année mis à jour") }
                    loadData()
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    private fun SchoolYearDto.toUiModel() = SchoolYear(
        id = this.id?.toString() ?: "",
        name = this.libelle,
        startDate = this.dateDebut,
        endDate = this.dateFin,
        status = when (this.status) {
            "ACTIVE" -> AcademicStatus.ACTIVE
            "COMPLETED" -> AcademicStatus.COMPLETED
            "UPCOMING" -> AcademicStatus.UPCOMING
            else -> AcademicStatus.UPCOMING
        },
        periodTypes = this.periodType.split(",").filter { it.isNotBlank() }.map { 
            try { PeriodType.valueOf(it) } catch (e: Exception) { PeriodType.TRIMESTER }
        },
        numberOfPeriods = this.numberOfPeriods,
        isDefault = this.isDefault,
        description = this.description
    )

    private fun AcademicPeriodDto.toUiModel() = AcademicPeriod(
        id = this.id?.toString() ?: "",
        schoolYearId = this.anneeScolaireId.toString(),
        name = this.nom,
        periodNumber = this.numero,
        startDate = this.dateDebut,
        endDate = this.dateFin,
        status = when (this.status) {
            "ACTIVE" -> AcademicStatus.ACTIVE
            "COMPLETED" -> AcademicStatus.COMPLETED
            "UPCOMING" -> AcademicStatus.UPCOMING
            else -> AcademicStatus.UPCOMING
        },
        type = try { PeriodType.valueOf(this.periodType) } catch (e: Exception) { PeriodType.TRIMESTER },
        evaluationDeadline = this.evaluationDeadline,
        reportCardDeadline = this.reportCardDeadline
    )

    private fun com.ecolix.atschool.api.AcademicEventDto.toUiModel() = AcademicEvent(
        id = this.id?.toString() ?: "",
        title = this.title,
        description = this.description,
        date = this.date,
        endDate = this.endDate,
        type = try { EventType.valueOf(this.type) } catch (e: Exception) { EventType.OTHER },
        color = parseHexColor(this.color)
    )

    private fun com.ecolix.atschool.api.HolidayDto.toUiModel() = Holiday(
        id = this.id?.toString() ?: "",
        name = this.name,
        startDate = this.startDate,
        endDate = this.endDate,
        type = try { HolidayType.valueOf(this.type) } catch (e: Exception) { HolidayType.OTHER }
    )

    private fun updateStatistics() {
        val state = mutableState.value
        val activeYear = state.schoolYears.find { it.status == AcademicStatus.ACTIVE }
        val currentPeriods = state.periods.filter { it.status == AcademicStatus.ACTIVE }
        
        val statistics = AcademicStatistics(
            totalSchoolYears = state.schoolYears.size,
            activeYear = activeYear,
            currentPeriods = currentPeriods,
            upcomingEvents = state.events.size,
            daysUntilNextPeriod = 0,
            completionRate = if (state.periods.isNotEmpty()) {
                (state.periods.count { it.status == AcademicStatus.COMPLETED }.toFloat() / state.periods.size) * 100f
            } else 0f
        )
        
        mutableState.update { it.copy(statistics = statistics) }
    }

    private fun AcademicSettingsDto.toUiModel(gradeLevels: List<com.ecolix.atschool.api.GradeLevelDto>) = AcademicSettings(
        defaultPeriodType = try { PeriodType.valueOf(this.defaultPeriodType) } catch (e: Exception) { PeriodType.TRIMESTER },
        gradeScale = GradeScale(
            minGrade = this.minGrade,
            maxGrade = this.maxGrade,
            passingGrade = this.passingGrade,
            gradeLevels = gradeLevels.map { it.toUiModel() }
        ),
        passingGrade = this.passingGrade,
        attendanceRequired = this.attendanceRequiredPercentage,
        allowMidPeriodTransfer = this.allowMidPeriodTransfer,
        autoPromoteStudents = this.autoPromoteStudents,
        decimalPrecision = this.decimalPrecision,
        showRankOnReportCard = this.showRankOnReportCard,
        showClassAverageOnReportCard = this.showClassAverageOnReportCard,
        absencesThresholdAlert = this.absencesThresholdAlert,
        matriculePrefix = this.matriculePrefix
    )

    private fun com.ecolix.atschool.api.GradeLevelDto.toUiModel() = GradeLevel(
        name = this.name,
        minValue = this.minValue,
        maxValue = this.maxValue,
        description = this.description ?: "",
        color = parseHexColor(this.color)
    )

    private fun AcademicSettings.toDto(tenantId: Int) = com.ecolix.atschool.api.AcademicSettingsDto(
        tenantId = tenantId,
        defaultPeriodType = this.defaultPeriodType.name,
        minGrade = this.gradeScale.minGrade,
        maxGrade = this.gradeScale.maxGrade,
        passingGrade = this.passingGrade,
        attendanceRequiredPercentage = this.attendanceRequired,
        allowMidPeriodTransfer = this.allowMidPeriodTransfer,
        autoPromoteStudents = this.autoPromoteStudents,
        decimalPrecision = this.decimalPrecision,
        showRankOnReportCard = this.showRankOnReportCard,
        showClassAverageOnReportCard = this.showClassAverageOnReportCard,
        absencesThresholdAlert = this.absencesThresholdAlert,
        matriculePrefix = this.matriculePrefix
    )

    private fun GradeLevel.toDto(tenantId: Int) = com.ecolix.atschool.api.GradeLevelDto(
        tenantId = tenantId,
        name = this.name,
        minValue = this.minValue,
        maxValue = this.maxValue,
        description = this.description,
        color = this.color.toHex()
    )

    private fun Color.toHex(): String {
        val argb = (value shr 32).toInt()
        val r = (argb shr 16) and 0xFF
        val g = (argb shr 8) and 0xFF
        val b = argb and 0xFF
        return "#${r.toString(16).padStart(2, '0')}${g.toString(16).padStart(2, '0')}${b.toString(16).padStart(2, '0')}".uppercase()
    }

    private fun parseHexColor(hexString: String): Color {
        return try {
            val hex = hexString.removePrefix("#")
            val argb = when (hex.length) {
                6 -> (0xFF shl 24) or hex.toInt(16)
                8 -> hex.toLong(16).toInt()
                else -> 0xFF3B82F6.toInt()
            }
            Color(argb)
        } catch (e: Exception) {
            Color(0xFF3B82F6)
        }
    }
}
