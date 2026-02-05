package com.ecolix.presentation.screens.academic

import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ecolix.atschool.api.StructureApiService
import com.ecolix.atschool.api.SchoolYearDto
import com.ecolix.atschool.api.AcademicPeriodDto
import com.ecolix.data.models.*
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AcademicScreenModel(private val structureApiService: StructureApiService) : 
    StateScreenModel<AcademicUiState>(AcademicUiState()) {

    init {
        loadData()
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

    fun updateAcademicPeriod(periodId: String, name: String, number: Int, startDate: String, endDate: String, type: PeriodType) {
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
}
