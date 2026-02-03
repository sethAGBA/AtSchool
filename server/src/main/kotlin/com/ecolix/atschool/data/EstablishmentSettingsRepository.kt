package com.ecolix.atschool.data

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class EstablishmentSettingsDto(
    val id: Int? = null,
    val tenantId: Int,
    
    // Identité
    val schoolName: String,
    val schoolCode: String,
    val schoolSlogan: String? = null,
    val schoolLevel: String = "Primaire",
    val logoUrl: String? = null,
    val republicLogoUrl: String? = null,
    
    // Tutelle
    val ministry: String? = null,
    val republicName: String? = null,
    val republicMotto: String? = null,
    val educationDirection: String? = null,
    val inspection: String? = null,
    
    // Direction
    val genCivility: String = "M.",
    val genDirector: String? = null,
    val matCivility: String = "Mme",
    val matDirector: String? = null,
    val priCivility: String = "M.",
    val priDirector: String? = null,
    val colCivility: String = "M.",
    val colDirector: String? = null,
    val lycCivility: String = "M.",
    val lycDirector: String? = null,
    val uniCivility: String = "Pr",
    val uniDirector: String? = null,
    val supCivility: String = "Dr",
    val supDirector: String? = null,
    
    // Contact
    val phone: String? = null,
    val email: String? = null,
    val website: String? = null,
    val bp: String? = null,
    val address: String? = null,
    
    // Configuration
    val pdfFooter: String? = null,
    val useTrimesters: Boolean = true,
    val useSemesters: Boolean = false,
    
    // Système
    val autoBackup: Boolean = true,
    val backupFrequency: String = "Quotidienne",
    val retentionDays: Int = 30,
    
    val updatedAt: String? = null
)

class EstablishmentSettingsRepository {
    
    fun getSettings(tenantId: Int): EstablishmentSettingsDto? = transaction {
        EstablishmentSettings.selectAll()
            .where { EstablishmentSettings.tenantId eq tenantId }
            .singleOrNull()
            ?.toDto()
    }
    
    fun createSettings(dto: EstablishmentSettingsDto): Int = transaction {
        EstablishmentSettings.insertAndGetId {
            it[tenantId] = dto.tenantId
            it[schoolName] = dto.schoolName
            it[schoolCode] = dto.schoolCode
            it[schoolSlogan] = dto.schoolSlogan
            it[schoolLevel] = dto.schoolLevel
            it[logoUrl] = dto.logoUrl
            it[republicLogoUrl] = dto.republicLogoUrl
            
            it[ministry] = dto.ministry
            it[republicName] = dto.republicName
            it[republicMotto] = dto.republicMotto
            it[educationDirection] = dto.educationDirection
            it[inspection] = dto.inspection
            
            it[genCivility] = dto.genCivility
            it[genDirector] = dto.genDirector
            it[matCivility] = dto.matCivility
            it[matDirector] = dto.matDirector
            it[priCivility] = dto.priCivility
            it[priDirector] = dto.priDirector
            it[colCivility] = dto.colCivility
            it[colDirector] = dto.colDirector
            it[lycCivility] = dto.lycCivility
            it[lycDirector] = dto.lycDirector
            it[uniCivility] = dto.uniCivility
            it[uniDirector] = dto.uniDirector
            it[supCivility] = dto.supCivility
            it[supDirector] = dto.supDirector
            
            it[phone] = dto.phone
            it[email] = dto.email
            it[website] = dto.website
            it[bp] = dto.bp
            it[address] = dto.address
            
            it[pdfFooter] = dto.pdfFooter
            it[useTrimesters] = dto.useTrimesters
            it[useSemesters] = dto.useSemesters
            
            it[autoBackup] = dto.autoBackup
            it[backupFrequency] = dto.backupFrequency
            it[retentionDays] = dto.retentionDays
            
            it[updatedAt] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        }.value
    }
    
    fun updateSettings(tenantId: Int, dto: EstablishmentSettingsDto): Boolean = transaction {
        EstablishmentSettings.update({ EstablishmentSettings.tenantId eq tenantId }) {
            it[schoolName] = dto.schoolName
            it[schoolCode] = dto.schoolCode
            it[schoolSlogan] = dto.schoolSlogan
            it[schoolLevel] = dto.schoolLevel
            it[logoUrl] = dto.logoUrl
            it[republicLogoUrl] = dto.republicLogoUrl
            
            it[ministry] = dto.ministry
            it[republicName] = dto.republicName
            it[republicMotto] = dto.republicMotto
            it[educationDirection] = dto.educationDirection
            it[inspection] = dto.inspection
            
            it[genCivility] = dto.genCivility
            it[genDirector] = dto.genDirector
            it[matCivility] = dto.matCivility
            it[matDirector] = dto.matDirector
            it[priCivility] = dto.priCivility
            it[priDirector] = dto.priDirector
            it[colCivility] = dto.colCivility
            it[colDirector] = dto.colDirector
            it[lycCivility] = dto.lycCivility
            it[lycDirector] = dto.lycDirector
            it[uniCivility] = dto.uniCivility
            it[uniDirector] = dto.uniDirector
            it[supCivility] = dto.supCivility
            it[supDirector] = dto.supDirector
            
            it[phone] = dto.phone
            it[email] = dto.email
            it[website] = dto.website
            it[bp] = dto.bp
            it[address] = dto.address
            
            it[pdfFooter] = dto.pdfFooter
            it[useTrimesters] = dto.useTrimesters
            it[useSemesters] = dto.useSemesters
            
            it[autoBackup] = dto.autoBackup
            it[backupFrequency] = dto.backupFrequency
            it[retentionDays] = dto.retentionDays
            
            it[updatedAt] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        } > 0
    }
    
    private fun ResultRow.toDto() = EstablishmentSettingsDto(
        id = this[EstablishmentSettings.id].value,
        tenantId = this[EstablishmentSettings.tenantId].value,
        
        schoolName = this[EstablishmentSettings.schoolName],
        schoolCode = this[EstablishmentSettings.schoolCode],
        schoolSlogan = this[EstablishmentSettings.schoolSlogan],
        schoolLevel = this[EstablishmentSettings.schoolLevel],
        logoUrl = this[EstablishmentSettings.logoUrl],
        republicLogoUrl = this[EstablishmentSettings.republicLogoUrl],
        
        ministry = this[EstablishmentSettings.ministry],
        republicName = this[EstablishmentSettings.republicName],
        republicMotto = this[EstablishmentSettings.republicMotto],
        educationDirection = this[EstablishmentSettings.educationDirection],
        inspection = this[EstablishmentSettings.inspection],
        
        genCivility = this[EstablishmentSettings.genCivility],
        genDirector = this[EstablishmentSettings.genDirector],
        matCivility = this[EstablishmentSettings.matCivility],
        matDirector = this[EstablishmentSettings.matDirector],
        priCivility = this[EstablishmentSettings.priCivility],
        priDirector = this[EstablishmentSettings.priDirector],
        colCivility = this[EstablishmentSettings.colCivility],
        colDirector = this[EstablishmentSettings.colDirector],
        lycCivility = this[EstablishmentSettings.lycCivility],
        lycDirector = this[EstablishmentSettings.lycDirector],
        uniCivility = this[EstablishmentSettings.uniCivility],
        uniDirector = this[EstablishmentSettings.uniDirector],
        supCivility = this[EstablishmentSettings.supCivility],
        supDirector = this[EstablishmentSettings.supDirector],
        
        phone = this[EstablishmentSettings.phone],
        email = this[EstablishmentSettings.email],
        website = this[EstablishmentSettings.website],
        bp = this[EstablishmentSettings.bp],
        address = this[EstablishmentSettings.address],
        
        pdfFooter = this[EstablishmentSettings.pdfFooter],
        useTrimesters = this[EstablishmentSettings.useTrimesters],
        useSemesters = this[EstablishmentSettings.useSemesters],
        
        autoBackup = this[EstablishmentSettings.autoBackup],
        backupFrequency = this[EstablishmentSettings.backupFrequency],
        retentionDays = this[EstablishmentSettings.retentionDays],
        
        updatedAt = this[EstablishmentSettings.updatedAt].toString()
    )
}
