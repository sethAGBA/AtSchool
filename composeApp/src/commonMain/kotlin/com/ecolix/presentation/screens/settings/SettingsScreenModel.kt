package com.ecolix.presentation.screens.settings

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ecolix.atschool.api.EstablishmentSettingsDto
import com.ecolix.atschool.api.SettingsApiService
import com.ecolix.atschool.api.UploadApiService
import com.ecolix.data.services.SettingsDataCache
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val isUploading: Boolean = false,
    
    // Identité
    val schoolName: String = "",
    val schoolCode: String = "",
    val schoolSlogan: String = "",
    val schoolLevel: String = "Primaire",
    val logoUrl: String? = null,
    val republicLogoUrl: String? = null,
    val localLogoBytes: ByteArray? = null,
    val localRepublicLogoBytes: ByteArray? = null,
    
    // Tutelle
    val ministry: String = "",
    val republicName: String = "",
    val republicMotto: String = "",
    val educationDirection: String = "",
    val inspection: String = "",
    
    // Direction
    val genCivility: String = "M.",
    val genDirector: String = "",
    val matCivility: String = "Mme",
    val matDirector: String = "",
    val priCivility: String = "M.",
    val priDirector: String = "",
    val colCivility: String = "M.",
    val colDirector: String = "",
    val lycCivility: String = "M.",
    val lycDirector: String = "",
    val uniCivility: String = "Pr",
    val uniDirector: String = "",
    val supCivility: String = "Dr",
    val supDirector: String = "",
    
    // Contact
    val phone: String = "",
    val email: String = "",
    val website: String = "",
    val bp: String = "",
    val address: String = "",
    
    // Configuration
    val pdfFooter: String = "",
    val useTrimesters: Boolean = true,
    val useSemesters: Boolean = false,
    
    // Système
    val autoBackup: Boolean = true,
    val backupFrequency: String = "Quotidienne",
    val retentionDays: Float = 30f,
    
    // Academic
    val academicYear: String = "2024-2025"
) {
    fun toDto(tenantId: Int) = EstablishmentSettingsDto(
        tenantId = tenantId,
        schoolName = schoolName,
        schoolCode = schoolCode,
        schoolSlogan = schoolSlogan.ifEmpty { null },
        schoolLevel = schoolLevel,
        logoUrl = logoUrl,
        republicLogoUrl = republicLogoUrl,
        ministry = ministry.ifEmpty { null },
        republicName = republicName.ifEmpty { null },
        republicMotto = republicMotto.ifEmpty { null },
        educationDirection = educationDirection.ifEmpty { null },
        inspection = inspection.ifEmpty { null },
        genCivility = genCivility,
        genDirector = genDirector.ifEmpty { null },
        matCivility = matCivility,
        matDirector = matDirector.ifEmpty { null },
        priCivility = priCivility,
        priDirector = priDirector.ifEmpty { null },
        colCivility = colCivility,
        colDirector = colDirector.ifEmpty { null },
        lycCivility = lycCivility,
        lycDirector = lycDirector.ifEmpty { null },
        uniCivility = uniCivility,
        uniDirector = uniDirector.ifEmpty { null },
        supCivility = supCivility,
        supDirector = supDirector.ifEmpty { null },
        phone = phone.ifEmpty { null },
        email = email.ifEmpty { null },
        website = website.ifEmpty { null },
        bp = bp.ifEmpty { null },
        address = address.ifEmpty { null },
        pdfFooter = pdfFooter.ifEmpty { null },
        useTrimesters = useTrimesters,
        useSemesters = useSemesters,
        autoBackup = autoBackup,
        backupFrequency = backupFrequency,
        retentionDays = retentionDays.toInt()
    )
}

class SettingsScreenModel(
    private val settingsApiService: SettingsApiService,
    private val uploadApiService: UploadApiService,
    private val settingsCache: SettingsDataCache? = null
) : StateScreenModel<SettingsUiState>(SettingsUiState()) {
    
    init {
        loadSettings()
    }
    
    fun loadSettings() {
        mutableState.update { it.copy(isLoading = true, error = null) }
        screenModelScope.launch {
            // Vérifier le cache d'abord
            val cacheKey = SettingsDataCache.KEY_SETTINGS
            val cachedSettings = settingsCache?.get(cacheKey)
            
            if (cachedSettings != null) {
                updateStateFromDto(cachedSettings)
                return@launch
            }
            
            // Sinon, charger depuis l'API
            settingsApiService.getSettings()
                .onSuccess { dto ->
                    // Mettre en cache
                    settingsCache?.put(cacheKey, dto)
                    updateStateFromDto(dto)
                }
                .onFailure { error ->
                    val cleanError = when {
                        error.message?.contains("http") == true -> "Erreur de connexion au serveur"
                        error.message?.contains("NoTransformationFoundException") == true -> "Erreur de format de données"
                        else -> error.message ?: "Une erreur inconnue est survenue"
                    }
                    mutableState.update { it.copy(isLoading = false, error = cleanError) }
                }
        }
    }

    private fun updateStateFromDto(dto: EstablishmentSettingsDto) {
        mutableState.update { state ->
            state.copy(
                isLoading = false,
                schoolName = dto.schoolName,
                schoolCode = dto.schoolCode,
                schoolSlogan = dto.schoolSlogan ?: "",
                schoolLevel = dto.schoolLevel,
                logoUrl = dto.logoUrl,
                republicLogoUrl = dto.republicLogoUrl,
                ministry = dto.ministry ?: "",
                republicName = dto.republicName ?: "",
                republicMotto = dto.republicMotto ?: "",
                educationDirection = dto.educationDirection ?: "",
                inspection = dto.inspection ?: "",
                genCivility = dto.genCivility,
                genDirector = dto.genDirector ?: "",
                matCivility = dto.matCivility,
                matDirector = dto.matDirector ?: "",
                priCivility = dto.priCivility,
                priDirector = dto.priDirector ?: "",
                colCivility = dto.colCivility,
                colDirector = dto.colDirector ?: "",
                lycCivility = dto.lycCivility,
                lycDirector = dto.lycDirector ?: "",
                uniCivility = dto.uniCivility,
                uniDirector = dto.uniDirector ?: "",
                supCivility = dto.supCivility,
                supDirector = dto.supDirector ?: "",
                phone = dto.phone ?: "",
                email = dto.email ?: "",
                website = dto.website ?: "",
                bp = dto.bp ?: "",
                address = dto.address ?: "",
                pdfFooter = dto.pdfFooter ?: "",
                useTrimesters = dto.useTrimesters,
                useSemesters = dto.useSemesters,
                autoBackup = dto.autoBackup,
                backupFrequency = dto.backupFrequency,
                retentionDays = dto.retentionDays.toFloat(),
                localLogoBytes = null,
                localRepublicLogoBytes = null
            )
        }
    }
    
    fun saveSettings() {
        val currentState = mutableState.value
        mutableState.update { it.copy(isLoading = true, error = null, saveSuccess = false) }
        
        screenModelScope.launch {
            // Note: tenantId is overwritten by server from JWT, using 0 as placeholder
            val dto = currentState.toDto(tenantId = 0)
            
            settingsApiService.updateSettings(dto)
                .onSuccess {
                    // Update cache with new values
                    settingsCache?.put(SettingsDataCache.KEY_SETTINGS, dto)
                    
                    mutableState.update { it.copy(
                        isLoading = false, 
                        saveSuccess = true
                        // Note: Removed clearing of local bytes to prevent disappearance during remote load
                    ) }
                }
                .onFailure { error ->
                    val cleanError = when {
                        error.message?.contains("http") == true -> "Erreur de connexion au serveur"
                        error.message?.contains("NoTransformationFoundException") == true -> "Erreur de format de données"
                        else -> error.message ?: "Une erreur lors de la sauvegarde est survenue"
                    }
                    mutableState.update { it.copy(isLoading = false, error = cleanError) }
                }
        }
    }
    
    // Update methods for each field
    fun updateSchoolName(value: String) = mutableState.update { it.copy(schoolName = value) }
    fun updateSchoolCode(value: String) = mutableState.update { it.copy(schoolCode = value) }
    fun updateSchoolSlogan(value: String) = mutableState.update { it.copy(schoolSlogan = value) }
    fun updateSchoolLevel(value: String) = mutableState.update { it.copy(schoolLevel = value) }
    fun updateMinistry(value: String) = mutableState.update { it.copy(ministry = value) }
    fun updateRepublicName(value: String) = mutableState.update { it.copy(republicName = value) }
    fun updateRepublicMotto(value: String) = mutableState.update { it.copy(republicMotto = value) }
    fun updateEducationDirection(value: String) = mutableState.update { it.copy(educationDirection = value) }
    fun updateInspection(value: String) = mutableState.update { it.copy(inspection = value) }
    
    fun updateGenCivility(value: String) = mutableState.update { it.copy(genCivility = value) }
    fun updateGenDirector(value: String) = mutableState.update { it.copy(genDirector = value) }
    fun updateMatCivility(value: String) = mutableState.update { it.copy(matCivility = value) }
    fun updateMatDirector(value: String) = mutableState.update { it.copy(matDirector = value) }
    fun updatePriCivility(value: String) = mutableState.update { it.copy(priCivility = value) }
    fun updatePriDirector(value: String) = mutableState.update { it.copy(priDirector = value) }
    fun updateColCivility(value: String) = mutableState.update { it.copy(colCivility = value) }
    fun updateColDirector(value: String) = mutableState.update { it.copy(colDirector = value) }
    fun updateLycCivility(value: String) = mutableState.update { it.copy(lycCivility = value) }
    fun updateLycDirector(value: String) = mutableState.update { it.copy(lycDirector = value) }
    fun updateUniCivility(value: String) = mutableState.update { it.copy(uniCivility = value) }
    fun updateUniDirector(value: String) = mutableState.update { it.copy(uniDirector = value) }
    fun updateSupCivility(value: String) = mutableState.update { it.copy(supCivility = value) }
    fun updateSupDirector(value: String) = mutableState.update { it.copy(supDirector = value) }
    
    fun updatePhone(value: String) = mutableState.update { it.copy(phone = value) }
    fun updateEmail(value: String) = mutableState.update { it.copy(email = value) }
    fun updateWebsite(value: String) = mutableState.update { it.copy(website = value) }
    fun updateBp(value: String) = mutableState.update { it.copy(bp = value) }
    fun updateAddress(value: String) = mutableState.update { it.copy(address = value) }
    fun updatePdfFooter(value: String) = mutableState.update { it.copy(pdfFooter = value) }
    
    fun updateUseTrimesters(value: Boolean) = mutableState.update { it.copy(useTrimesters = value) }
    fun updateUseSemesters(value: Boolean) = mutableState.update { it.copy(useSemesters = value) }
    fun updateAutoBackup(value: Boolean) = mutableState.update { it.copy(autoBackup = value) }
    fun updateBackupFrequency(value: String) = mutableState.update { it.copy(backupFrequency = value) }
    fun updateRetentionDays(value: Float) = mutableState.update { it.copy(retentionDays = value) }
    
    fun clearSaveSuccess() = mutableState.update { it.copy(saveSuccess = false) }

    fun pickAndUploadLogo(isRepublic: Boolean = false) {
        screenModelScope.launch {
            val fileData = com.ecolix.utils.FilePicker.pickFile() ?: return@launch
            
            mutableState.update { state: SettingsUiState ->
                if (isRepublic) {
                    state.copy(localRepublicLogoBytes = fileData.bytes, isUploading = true, error = null)
                } else {
                    state.copy(localLogoBytes = fileData.bytes, isUploading = true, error = null)
                }
            }
            
            uploadApiService.uploadFile(fileData.name, fileData.bytes)
                .onSuccess { url: String ->
                    mutableState.update { state: SettingsUiState ->
                        if (isRepublic) {
                            state.copy(isUploading = false, republicLogoUrl = url)
                        } else {
                            state.copy(isUploading = false, logoUrl = url)
                        }
                    }
                }
                .onFailure { error: Throwable ->
                    mutableState.update { state: SettingsUiState -> 
                        state.copy(isUploading = false, error = "Échec de l'upload: ${error.message}")
                    }
                }
        }
    }
}
