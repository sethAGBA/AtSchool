package com.ecolix.atschool.data

import org.jetbrains.exposed.sql.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class Establishment(
    val id: Int? = null,
    val tenantId: Int,
    val nom: String,
    val adresse: String? = null,
    val ville: String? = null
)

@Serializable
data class ClassEntity(
    val id: Int? = null,
    val tenantId: Int,
    val niveauId: Int? = null,
    val schoolLevelId: Int? = null,
    val code: String,
    val nom: String,
    val legacyLevel: String? = null,
    val mainTeacher: String? = null,
    val roomNumber: String? = null,
    val capacity: Int? = null,
    val description: String? = null
)

class EstablishmentRepository {
    fun getAll(tenantId: Int): List<Establishment> = transaction {
        Establishments.selectAll().where { Establishments.tenantId eq tenantId }
            .map { it.toEstablishment() }
    }

    private fun ResultRow.toEstablishment() = Establishment(
        id = this[Establishments.id].value,
        tenantId = this[Establishments.tenantId].value,
        nom = this[Establishments.nom],
        adresse = this[Establishments.adresse],
        ville = this[Establishments.ville]
    )
}

class ClassRepository {
    fun getAllByTenant(tenantId: Int): List<ClassEntity> = transaction {
        Classes.selectAll().where { Classes.tenantId eq tenantId }
            .map { it.toClass() }
    }

    fun getById(id: Int, tenantId: Int): ClassEntity? = transaction {
        Classes.selectAll().where { (Classes.id eq id) and (Classes.tenantId eq tenantId) }
            .map { it.toClass() }
            .singleOrNull()
    }

    fun create(entity: ClassEntity): Int = transaction {
        try {
            Classes.insertAndGetId {
                it[Classes.tenantId] = entity.tenantId
                it[Classes.niveauId] = entity.niveauId
                it[Classes.schoolLevelId] = entity.schoolLevelId
                it[Classes.code] = entity.code
                it[Classes.nom] = entity.nom
                it[Classes.legacyLevel] = entity.legacyLevel
                it[Classes.mainTeacher] = entity.mainTeacher
                it[Classes.roomNumber] = entity.roomNumber
                it[Classes.capacity] = entity.capacity
                it[Classes.description] = entity.description
            }.value
        } catch (e: Exception) {
            // Check if it's a duplicate key error
            if (e.message?.contains("unique", ignoreCase = true) == true || 
                e.message?.contains("duplicate", ignoreCase = true) == true) {
                throw IllegalArgumentException("Une classe avec le nom '${entity.nom}' existe déjà pour cet établissement.")
            }
            throw e
        }
    }

    fun update(id: Int, entity: ClassEntity): Boolean = transaction {
        Classes.update({ (Classes.id eq id) and (Classes.tenantId eq entity.tenantId) }) {
            it[Classes.niveauId] = entity.niveauId
            it[Classes.schoolLevelId] = entity.schoolLevelId
            it[Classes.code] = entity.code
            it[Classes.nom] = entity.nom
            it[Classes.legacyLevel] = entity.legacyLevel
            it[Classes.mainTeacher] = entity.mainTeacher
            it[Classes.roomNumber] = entity.roomNumber
            it[Classes.capacity] = entity.capacity
            it[Classes.description] = entity.description
        } > 0
    }

    fun delete(id: Int, tenantId: Int): Boolean = transaction {
        Classes.deleteWhere { (Classes.id eq id) and (Classes.tenantId eq tenantId) } > 0
    }

    fun syncLevelRename(schoolLevelId: Int, oldName: String, newName: String, tenantId: Int) = transaction {
        // Update the legacyLevel reference for all classes in this level
        Classes.update({ (Classes.schoolLevelId eq schoolLevelId) and (Classes.tenantId eq tenantId) }) {
            it[Classes.legacyLevel] = newName
        }
        
        // Update names for classes that exactly matched the old level name
        Classes.update({ 
            (Classes.schoolLevelId eq schoolLevelId) and 
            (Classes.tenantId eq tenantId) and 
            (Classes.nom eq oldName) 
        }) {
            it[Classes.nom] = newName
            it[Classes.code] = "CL-${newName.uppercase().replace(" ", "")}"
        }
    }

    private fun ResultRow.toClass() = ClassEntity(
        id = this[Classes.id].value,
        tenantId = this[Classes.tenantId].value,
        niveauId = this[Classes.niveauId]?.value,
        schoolLevelId = this[Classes.schoolLevelId]?.value,
        code = this[Classes.code],
        nom = this[Classes.nom],
        legacyLevel = this[Classes.legacyLevel],
        mainTeacher = this[Classes.mainTeacher],
        roomNumber = this[Classes.roomNumber],
        capacity = this[Classes.capacity],
        description = this[Classes.description]
    )
}

@Serializable
data class AcademicPeriodEntity(
    val id: Int? = null,
    val tenantId: Int,
    val anneeScolaireId: Int,
    val nom: String,
    val numero: Int,
    val dateDebut: String,
    val dateFin: String,
    val periodType: String = "TRIMESTER",
    val evaluationDeadline: String? = null,
    val reportCardDeadline: String? = null,
    val status: String = "UPCOMING"
)

@Serializable
data class SchoolYearEntity(
    val id: Int? = null,
    val tenantId: Int,
    val libelle: String,
    val dateDebut: String,
    val dateFin: String,
    val numberOfPeriods: Int = 3,
    val periodType: String = "TRIMESTER",
    val isDefault: Boolean = false,
    val status: String = "UPCOMING",
    val description: String? = null,
    val periods: List<AcademicPeriodEntity>? = null
)

class SchoolYearRepository {
    
    /**
     * Validates that school year dates are in correct order and within acceptable duration
     */
    private fun validateSchoolYearDates(start: String, end: String) {
        val s = try {
            kotlinx.datetime.LocalDate.parse(start)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid start date format: $start")
        }
        
        val e = try {
            kotlinx.datetime.LocalDate.parse(end)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid end date format: $end")
        }
        
        require(e > s) { "End date must be after start date" }
        
        val days = e.toEpochDays() - s.toEpochDays()
        require(days >= 90) { "School year duration must be at least 3 months (90 days)" }
        require(days <= 548) { "School year duration cannot exceed 18 months (548 days)" }
    }
    
    /**
     * Validates that periods are chronologically ordered, within year bounds, and non-overlapping
     */
    private fun validatePeriodSequence(
        periods: List<AcademicPeriodEntity>?,
        yearStart: String,
        yearEnd: String
    ) {
        if (periods.isNullOrEmpty()) return
        
        val yS = try {
            kotlinx.datetime.LocalDate.parse(yearStart)
        } catch (e: Exception) {
            return // Year dates already validated
        }
        
        val yE = try {
            kotlinx.datetime.LocalDate.parse(yearEnd)
        } catch (e: Exception) {
            return // Year dates already validated
        }
        
        // Group periods by type to validate sequences independently
        // This allows parallel tracks (e.g. Trimesters AND Semesters) to coexist
        val periodsByType = periods.groupBy { it.periodType }
        
        periodsByType.forEach { (_, typePeriods) ->
            val sorted = typePeriods.sortedBy { it.numero }
            
            sorted.forEach { period ->
                val pStart = try {
                    kotlinx.datetime.LocalDate.parse(period.dateDebut)
                } catch (e: Exception) {
                    throw IllegalArgumentException("Invalid period start date for ${period.nom}: ${period.dateDebut}")
                }
                
                val pEnd = try {
                    kotlinx.datetime.LocalDate.parse(period.dateFin)
                } catch (e: Exception) {
                    throw IllegalArgumentException("Invalid period end date for ${period.nom}: ${period.dateFin}")
                }
                
                require(pStart >= yS) { 
                    "Period '${period.nom}' starts before school year (${period.dateDebut} < $yearStart)" 
                }
                require(pEnd <= yE) { 
                    "Period '${period.nom}' ends after school year (${period.dateFin} > $yearEnd)" 
                }
                require(pEnd > pStart) { 
                    "Period '${period.nom}' has invalid date range (end must be after start)" 
                }
            }
            
            // Check for overlaps within the SAME period type
            for (i in 0 until sorted.size - 1) {
                val currentEnd = kotlinx.datetime.LocalDate.parse(sorted[i].dateFin)
                val nextStart = kotlinx.datetime.LocalDate.parse(sorted[i + 1].dateDebut)
                require(nextStart > currentEnd) { 
                    "Periods '${sorted[i].nom}' and '${sorted[i + 1].nom}' overlap or are not properly separated (${sorted[i].periodType})" 
                }
            }
        }
    }
    
    fun getAll(tenantId: Int): List<SchoolYearEntity> = transaction {
        AnneesScolaires.selectAll().where { AnneesScolaires.tenantId eq tenantId }
            .map { it.toSchoolYear() }
    }

    fun create(entity: SchoolYearEntity): Int = transaction {
        // Validate dates and periods before creating
        validateSchoolYearDates(entity.dateDebut, entity.dateFin)
        validatePeriodSequence(entity.periods, entity.dateDebut, entity.dateFin)
        
        val yearId = AnneesScolaires.insertAndGetId {
            it[tenantId] = entity.tenantId
            it[libelle] = entity.libelle
            it[dateDebut] = try { kotlinx.datetime.LocalDate.parse(entity.dateDebut) } catch (e: Exception) { kotlinx.datetime.LocalDate(2000, 1, 1) }
            it[dateFin] = try { kotlinx.datetime.LocalDate.parse(entity.dateFin) } catch (e: Exception) { kotlinx.datetime.LocalDate(2000, 1, 1) }
            it[numberOfPeriods] = entity.numberOfPeriods
            it[periodType] = entity.periodType
            it[isDefault] = entity.isDefault
            it[AnneesScolaires.status] = entity.status
            it[description] = entity.description
            it[description] = entity.description
        }.value
        
        // If this year is active, deactivate all others (set to COMPLETED if they were ACTIVE)
        if (entity.status == "ACTIVE") {
            AnneesScolaires.update({ (AnneesScolaires.tenantId eq entity.tenantId) and (AnneesScolaires.status eq "ACTIVE") and (AnneesScolaires.id neq yearId) }) {
                it[AnneesScolaires.status] = "COMPLETED"
            }
        }
        // Use provided periods or auto-generate
        if (!entity.periods.isNullOrEmpty()) {
            entity.periods.forEach { period ->
                AcademicPeriods.insert {
                    it[tenantId] = entity.tenantId
                    it[anneeScolaireId] = yearId
                    it[nom] = period.nom
                    it[numero] = period.numero
                    it[dateDebut] = try { kotlinx.datetime.LocalDate.parse(period.dateDebut) } catch (e: Exception) { kotlinx.datetime.LocalDate(2000, 1, 1) }
                    it[dateFin] = try { kotlinx.datetime.LocalDate.parse(period.dateFin) } catch (e: Exception) { kotlinx.datetime.LocalDate(2000, 1, 1) }
                    it[periodType] = period.periodType
                    it[AcademicPeriods.status] = period.status
                }
                // If this period is active, deactivate all others in the same year (set to COMPLETED if they were ACTIVE)
                if (period.status == "ACTIVE") {
                    AcademicPeriods.update({ (AcademicPeriods.anneeScolaireId eq yearId) and (AcademicPeriods.status eq "ACTIVE") and (AcademicPeriods.periodType eq period.periodType) }) {
                        it[AcademicPeriods.status] = "COMPLETED"
                    }
                    // Re-activate this one
                    AcademicPeriods.update({ (AcademicPeriods.anneeScolaireId eq yearId) and (AcademicPeriods.nom eq period.nom) and (AcademicPeriods.numero eq period.numero) }) {
                        it[AcademicPeriods.status] = "ACTIVE"
                    }
                }
            }
        } else {
            // Auto-generate periods based on types
            val start = try { kotlinx.datetime.LocalDate.parse(entity.dateDebut) } catch (e: Exception) { kotlinx.datetime.LocalDate(2000, 1, 1) }
            val end = try { kotlinx.datetime.LocalDate.parse(entity.dateFin) } catch (e: Exception) { kotlinx.datetime.LocalDate(2000, 1, 1) }
            
            val types = entity.periodType.split(",").filter { it.isNotBlank() }
            types.forEach { type ->
                val count = when(type) {
                    "TRIMESTER" -> 3
                    "SEMESTER" -> 2
                    else -> 0
                }
                
                if (count > 0) {
                    val totalDays = (end.toEpochDays() - start.toEpochDays()).toLong()
                    val daysPerPeriod = totalDays / count
                    
                    for (i in 1..count) {
                        val pStart = kotlinx.datetime.LocalDate.fromEpochDays((start.toEpochDays() + (i - 1) * daysPerPeriod).toInt())
                        val pEnd = if (i == count) end else kotlinx.datetime.LocalDate.fromEpochDays((start.toEpochDays() + i * daysPerPeriod - 1).toInt())
                        
                        val typeLabel = when(type) {
                            "TRIMESTER" -> "Trimestre"
                            "SEMESTER" -> "Semestre"
                            else -> type
                        }

                        AcademicPeriods.insert {
                            it[tenantId] = entity.tenantId
                            it[anneeScolaireId] = yearId
                            it[nom] = "$typeLabel $i"
                            it[numero] = i
                            it[dateDebut] = pStart
                            it[dateFin] = pEnd
                            it[periodType] = type
                            it[AcademicPeriods.status] = if (i == 1) "ACTIVE" else "UPCOMING"
                        }
                    }
                }
            }
        }
        
        yearId
    }

    fun update(id: Int, entity: SchoolYearEntity): Boolean = transaction {
        // Validate dates and periods before updating
        validateSchoolYearDates(entity.dateDebut, entity.dateFin)
        validatePeriodSequence(entity.periods, entity.dateDebut, entity.dateFin)
        
        val updated = AnneesScolaires.update({ (AnneesScolaires.id eq id) and (AnneesScolaires.tenantId eq entity.tenantId) }) {
            it[libelle] = entity.libelle
            it[dateDebut] = try { kotlinx.datetime.LocalDate.parse(entity.dateDebut) } catch (e: Exception) { kotlinx.datetime.LocalDate(2000, 1, 1) }
            it[dateFin] = try { kotlinx.datetime.LocalDate.parse(entity.dateFin) } catch (e: Exception) { kotlinx.datetime.LocalDate(2000, 1, 1) }
            it[numberOfPeriods] = entity.numberOfPeriods
            it[periodType] = entity.periodType
            it[isDefault] = entity.isDefault
            it[AnneesScolaires.status] = entity.status
            it[description] = entity.description
            it[description] = entity.description
        } > 0
        
        // If update was successful and status is ACTIVE, deactivate others
        if (updated && entity.status == "ACTIVE") {
             AnneesScolaires.update({ (AnneesScolaires.tenantId eq entity.tenantId) and (AnneesScolaires.status eq "ACTIVE") and (AnneesScolaires.id neq id) }) {
                it[AnneesScolaires.status] = "COMPLETED"
            }
        }
        
        // If periods are provided, update them
        if (updated && !entity.periods.isNullOrEmpty()) {
            // Delete existing periods
            AcademicPeriods.deleteWhere { 
                (anneeScolaireId eq id) and (tenantId eq entity.tenantId) 
            }
            
            // Insert new periods
            entity.periods.forEach { period ->
                AcademicPeriods.insert {
                    it[tenantId] = entity.tenantId
                    it[anneeScolaireId] = id
                    it[nom] = period.nom
                    it[numero] = period.numero
                    it[dateDebut] = try { kotlinx.datetime.LocalDate.parse(period.dateDebut) } catch (e: Exception) { kotlinx.datetime.LocalDate(2000, 1, 1) }
                    it[dateFin] = try { kotlinx.datetime.LocalDate.parse(period.dateFin) } catch (e: Exception) { kotlinx.datetime.LocalDate(2000, 1, 1) }
                    it[periodType] = period.periodType
                    it[AcademicPeriods.status] = period.status
                }
            }
            
            // Ensure only one is active after replacement if any was set to active
            // Ensure only one is active PER TYPE after replacement if any was set to active
            val activePeriods = AcademicPeriods.selectAll()
                 .where { (AcademicPeriods.anneeScolaireId eq id) and (AcademicPeriods.status eq "ACTIVE") }
                 .map { it[AcademicPeriods.id].value to it[AcademicPeriods.periodType] }

            // Group by type and if multiple active, keep one?
            // Actually, if we just trust the input, we might not need this closure.
            // But if we want to enforce "Single Active per Type", we should iterate unique types.
            // For now, let's remove the aggressive enforcement that assumed only global active period.
            // If the input data has duplicates, we might want to fix it, but let's assume input is valid for now
            // or we could enforce it by type if needed.
            // Simplified: Removing the global "deactivate others" block.

        }
        
        updated
    }

    fun delete(id: Int, tenantId: Int): Boolean = transaction {
        AnneesScolaires.deleteWhere { (AnneesScolaires.id eq id) and (AnneesScolaires.tenantId eq tenantId) } > 0
    }

    fun setAsDefault(id: Int, tenantId: Int) = transaction {
        // Remove default from all other years for this tenant
        AnneesScolaires.update({ AnneesScolaires.tenantId eq tenantId }) {
            it[isDefault] = false
        }
        // Set this one as default
        AnneesScolaires.update({ (AnneesScolaires.id eq id) and (AnneesScolaires.tenantId eq tenantId) }) {
            it[AnneesScolaires.isDefault] = true
        }
    }

    fun setStatus(id: Int, status: String, tenantId: Int) = transaction {
        if (status == "ACTIVE") {
            // Transition previously ACTIVE to COMPLETED
            AnneesScolaires.update({ (AnneesScolaires.tenantId eq tenantId) and (AnneesScolaires.status eq "ACTIVE") and (AnneesScolaires.id neq id) }) {
                it[AnneesScolaires.status] = "COMPLETED"
            }
        }
        
        AnneesScolaires.update({ (AnneesScolaires.id eq id) and (AnneesScolaires.tenantId eq tenantId) }) {
            it[AnneesScolaires.status] = status
        }
    }

    private fun ResultRow.toSchoolYear() = SchoolYearEntity(
        id = this[AnneesScolaires.id].value,
        tenantId = this[AnneesScolaires.tenantId].value,
        libelle = this[AnneesScolaires.libelle],
        dateDebut = this[AnneesScolaires.dateDebut].toString(),
        dateFin = this[AnneesScolaires.dateFin].toString(),
        numberOfPeriods = this[AnneesScolaires.numberOfPeriods],
        periodType = this[AnneesScolaires.periodType],
        isDefault = this[AnneesScolaires.isDefault],
        status = this[AnneesScolaires.status],
        description = this[AnneesScolaires.description]
    )
}



class AcademicPeriodRepository {
    fun getByYear(anneeScolaireId: Int, tenantId: Int): List<AcademicPeriodEntity> = transaction {
        AcademicPeriods.selectAll().where { (AcademicPeriods.anneeScolaireId eq anneeScolaireId) and (AcademicPeriods.tenantId eq tenantId) }
            .map { it.toPeriod() }
    }

    fun create(entity: AcademicPeriodEntity): Int = transaction {
        val newId = AcademicPeriods.insertAndGetId {
            it[tenantId] = entity.tenantId
            it[anneeScolaireId] = entity.anneeScolaireId
            it[nom] = entity.nom
            it[numero] = entity.numero
            it[dateDebut] = kotlinx.datetime.LocalDate.parse(entity.dateDebut)
            it[dateFin] = kotlinx.datetime.LocalDate.parse(entity.dateFin)
            it[periodType] = entity.periodType
            it[evaluationDeadline] = entity.evaluationDeadline?.let { d -> kotlinx.datetime.LocalDate.parse(d) }
            it[reportCardDeadline] = entity.reportCardDeadline?.let { d -> kotlinx.datetime.LocalDate.parse(d) }
            it[AcademicPeriods.status] = entity.status
        }.value
        
        if (entity.status == "ACTIVE") {
            // Logic: transition previously ACTIVE one of SAME TYPE to COMPLETED
            AcademicPeriods.update({ (AcademicPeriods.anneeScolaireId eq entity.anneeScolaireId) and (AcademicPeriods.id neq newId) and (AcademicPeriods.status eq "ACTIVE") and (AcademicPeriods.periodType eq entity.periodType) }) {
                it[AcademicPeriods.status] = "COMPLETED"
            }
        }
        newId
    }

    fun update(id: Int, entity: AcademicPeriodEntity): Boolean = transaction {
        val updated = AcademicPeriods.update({ (AcademicPeriods.id eq id) and (AcademicPeriods.tenantId eq entity.tenantId) }) {
            it[nom] = entity.nom
            it[numero] = entity.numero
            it[dateDebut] = kotlinx.datetime.LocalDate.parse(entity.dateDebut)
            it[dateFin] = kotlinx.datetime.LocalDate.parse(entity.dateFin)
            it[periodType] = entity.periodType
            it[evaluationDeadline] = entity.evaluationDeadline?.let { d -> kotlinx.datetime.LocalDate.parse(d) }
            it[reportCardDeadline] = entity.reportCardDeadline?.let { d -> kotlinx.datetime.LocalDate.parse(d) }
            it[AcademicPeriods.status] = entity.status
        } > 0
        
        if (updated && entity.status == "ACTIVE") {
            val period = AcademicPeriods.selectAll().where { AcademicPeriods.id eq id }.limit(1).firstOrNull()
            val yearId = period?.get(AcademicPeriods.anneeScolaireId)?.value
            if (yearId != null) {
                AcademicPeriods.update({ (AcademicPeriods.anneeScolaireId eq yearId) and (AcademicPeriods.id neq id) and (AcademicPeriods.status eq "ACTIVE") and (AcademicPeriods.periodType eq entity.periodType) }) {
                    it[AcademicPeriods.status] = "COMPLETED"
                }
            }
        }
        updated
    }
    
    fun setStatus(id: Int, status: String, tenantId: Int): Boolean = transaction {
        println("DEBUG: setStatus called for id=$id, status=$status, tenantId=$tenantId")
        val period = AcademicPeriods.selectAll().where { (AcademicPeriods.id eq id) and (AcademicPeriods.tenantId eq tenantId) }
            .limit(1)
            .firstOrNull() 
            
        if (period == null) {
            println("DEBUG: Period not found for id=$id and tenantId=$tenantId")
            return@transaction false
        }
            
        val yearId = period[AcademicPeriods.anneeScolaireId].value
        println("DEBUG: Found period. YearId=$yearId, Current Type=${period[AcademicPeriods.periodType]}")
        
        if (status == "ACTIVE") {
            val pType = period[AcademicPeriods.periodType]
            println("DEBUG: Activating period. Deactivating others of type $pType in year $yearId")
            // Transition previously ACTIVE of SAME TYPE to COMPLETED
            val deactivatedCount = AcademicPeriods.update({ (AcademicPeriods.anneeScolaireId eq yearId) and (AcademicPeriods.status eq "ACTIVE") and (AcademicPeriods.periodType eq pType) }) {
                it[AcademicPeriods.status] = "COMPLETED"
            }
            println("DEBUG: Deactivated $deactivatedCount periods")
        }
        
        // Update this one
        val updatedCount = AcademicPeriods.update({ AcademicPeriods.id eq id }) {
            it[AcademicPeriods.status] = status
        }
        println("DEBUG: Updated target period status. affected rows=$updatedCount")
        updatedCount > 0
    }

    fun delete(id: Int, tenantId: Int): Boolean = transaction {
        AcademicPeriods.deleteWhere { (AcademicPeriods.id eq id) and (AcademicPeriods.tenantId eq tenantId) } > 0
    }

    private fun ResultRow.toPeriod() = AcademicPeriodEntity(
        id = this[AcademicPeriods.id].value,
        tenantId = this[AcademicPeriods.tenantId].value,
        anneeScolaireId = this[AcademicPeriods.anneeScolaireId].value,
        nom = this[AcademicPeriods.nom],
        numero = this[AcademicPeriods.numero],
        dateDebut = this[AcademicPeriods.dateDebut].toString(),
        dateFin = this[AcademicPeriods.dateFin].toString(),
        periodType = this[AcademicPeriods.periodType],
        evaluationDeadline = this[AcademicPeriods.evaluationDeadline]?.toString(),
        reportCardDeadline = this[AcademicPeriods.reportCardDeadline]?.toString(),
        status = this[AcademicPeriods.status]
    )
}

@Serializable
data class CycleEntity(
    val id: Int? = null,
    val tenantId: Int,
    val nom: String
)

class CycleRepository {
    fun getAll(tenantId: Int): List<CycleEntity> = transaction {
        Cycles.selectAll().where { Cycles.tenantId eq tenantId }
            .map { it.toCycle() }
    }

    private fun ResultRow.toCycle() = CycleEntity(
        id = this[Cycles.id].value,
        tenantId = this[Cycles.tenantId].value,
        nom = this[Cycles.nom]
    )
}

@Serializable
data class LevelEntity(
    val id: Int? = null,
    val cycleId: Int,
    val nom: String
)


class LevelRepository {
    fun getByCycle(cycleId: Int): List<LevelEntity> = transaction {
        Niveaux.selectAll().where { Niveaux.cycleId eq cycleId }
            .map { it.toLevel() }
    }

    private fun ResultRow.toLevel() = LevelEntity(
        id = this[Niveaux.id].value,
        cycleId = this[Niveaux.cycleId].value,
        nom = this[Niveaux.nom]
    )
}

@Serializable
data class SchoolCycleEntity(
    val id: Int? = null,
    val tenantId: Int,
    val name: String,
    val sortOrder: Int
)

class SchoolCycleRepository {
    fun getAll(tenantId: Int): List<SchoolCycleEntity> = transaction {
        SchoolCycles.selectAll()
            .where { SchoolCycles.tenantId eq tenantId }
            .orderBy(SchoolCycles.sortOrder to SortOrder.ASC) // Corrected: Using SortOrder.ASC
            .map { it.toSchoolCycle() }
    }
    
    fun getById(id: Int, tenantId: Int): SchoolCycleEntity? = transaction {
        SchoolCycles.selectAll()
            .where { (SchoolCycles.id eq id) and (SchoolCycles.tenantId eq tenantId) }
            .map { it.toSchoolCycle() }
            .singleOrNull()
    }

    fun create(entity: SchoolCycleEntity): Int = transaction {
        SchoolCycles.insertAndGetId {
            it[tenantId] = entity.tenantId
            it[name] = entity.name
            it[sortOrder] = entity.sortOrder
        }.value
    }

    fun update(id: Int, entity: SchoolCycleEntity): Boolean = transaction {
        SchoolCycles.update({ (SchoolCycles.id eq id) and (SchoolCycles.tenantId eq entity.tenantId) }) {
            it[name] = entity.name
            it[sortOrder] = entity.sortOrder
        } > 0
    }

    fun delete(id: Int, tenantId: Int): Boolean = transaction {
        SchoolCycles.deleteWhere { (SchoolCycles.id eq id) and (SchoolCycles.tenantId eq tenantId) } > 0
    }

    private fun ResultRow.toSchoolCycle() = SchoolCycleEntity(
        id = this[SchoolCycles.id].value,
        tenantId = this[SchoolCycles.tenantId].value,
        name = this[SchoolCycles.name],
        sortOrder = this[SchoolCycles.sortOrder]
    )
}

@Serializable
data class SchoolLevelEntity(
    val id: Int? = null,
    val tenantId: Int,
    val cycleId: Int,
    val name: String,
    val sortOrder: Int,
    val standardCapacity: Int? = null
)

class SchoolLevelRepository(private val classRepo: ClassRepository) {
    fun getAll(tenantId: Int): List<SchoolLevelEntity> = transaction {
        SchoolLevels.selectAll()
            .where { SchoolLevels.tenantId eq tenantId }
            .orderBy(SchoolLevels.sortOrder to SortOrder.ASC) // Corrected
            .map { it.toSchoolLevel() }
    }

    fun getByCycle(cycleId: Int, tenantId: Int): List<SchoolLevelEntity> = transaction {
        SchoolLevels.selectAll()
            .where { (SchoolLevels.cycleId eq cycleId) and (SchoolLevels.tenantId eq tenantId) }
            .orderBy(SchoolLevels.sortOrder to SortOrder.ASC) // Corrected
            .map { it.toSchoolLevel() }
    }
    
    fun getById(id: Int, tenantId: Int): SchoolLevelEntity? = transaction {
        SchoolLevels.selectAll()
            .where { (SchoolLevels.id eq id) and (SchoolLevels.tenantId eq tenantId) }
            .map { it.toSchoolLevel() }
            .singleOrNull()
    }

    fun create(entity: SchoolLevelEntity): Int = transaction {
        val levelId = SchoolLevels.insertAndGetId {
            it[tenantId] = entity.tenantId
            it[cycleId] = entity.cycleId
            it[name] = entity.name
            it[sortOrder] = entity.sortOrder
            it[standardCapacity] = entity.standardCapacity
        }.value

        // Automatically create a matching classroom, but skip if already exists
        try {
            classRepo.create(
                ClassEntity(
                    tenantId = entity.tenantId,
                    schoolLevelId = levelId,
                    nom = entity.name,
                    code = "CL-${entity.name.uppercase().replace(" ", "")}",
                    legacyLevel = entity.name
                )
            )
        } catch (e: IllegalArgumentException) {
            // Skip if class with this name already exists
            if (e.message?.contains("existe déjà") == true) {
                // Silently skip - this is fine during auto-creation
            } else {
                throw e
            }
        }

        levelId
    }

    fun update(id: Int, entity: SchoolLevelEntity): Boolean = transaction {
        val oldLevel = SchoolLevels.selectAll().where { (SchoolLevels.id eq id) and (SchoolLevels.tenantId eq entity.tenantId) }.singleOrNull()
        val oldName = oldLevel?.get(SchoolLevels.name)

        val updated = SchoolLevels.update({ (SchoolLevels.id eq id) and (SchoolLevels.tenantId eq entity.tenantId) }) {
            it[cycleId] = entity.cycleId
            it[name] = entity.name
            it[sortOrder] = entity.sortOrder
            it[standardCapacity] = entity.standardCapacity
        } > 0

        if (updated && oldName != null && oldName != entity.name) {
            classRepo.syncLevelRename(id, oldName, entity.name, entity.tenantId)
        }
        updated
    }

    fun delete(id: Int, tenantId: Int): Boolean = transaction {
        SchoolLevels.deleteWhere { (SchoolLevels.id eq id) and (SchoolLevels.tenantId eq tenantId) } > 0
    }

    private fun ResultRow.toSchoolLevel() = SchoolLevelEntity(
        id = this[SchoolLevels.id].value,
        tenantId = this[SchoolLevels.tenantId].value,
        cycleId = this[SchoolLevels.cycleId].value,
        name = this[SchoolLevels.name],
        sortOrder = this[SchoolLevels.sortOrder],
        standardCapacity = this[SchoolLevels.standardCapacity]
    )
}

class StructureSeedingRepository(
    private val cycleRepo: SchoolCycleRepository,
    private val levelRepo: SchoolLevelRepository
) {
    fun seedDefaultStructure(tenantId: Int, schoolLevel: String) = transaction {
        // Only seed if no cycles exist
        if (cycleRepo.getAll(tenantId).isNotEmpty()) return@transaction

        when {
            schoolLevel.contains("Maternelle", ignoreCase = true) -> {
                seedMaternelle(tenantId)
            }
            schoolLevel.contains("Primaire", ignoreCase = true) -> {
                seedPrimaire(tenantId)
            }
            schoolLevel.contains("Collège", ignoreCase = true) -> {
                seedCollege(tenantId)
            }
            schoolLevel.contains("Lycée", ignoreCase = true) -> {
                seedLycee(tenantId)
            }
            schoolLevel.contains("Complexe", ignoreCase = true) -> {
                seedMaternelle(tenantId)
                seedPrimaire(tenantId)
                seedCollege(tenantId)
                seedLycee(tenantId)
            }
        }
    }

    private fun seedMaternelle(tenantId: Int) {
        val cycleId = cycleRepo.create(SchoolCycleEntity(tenantId = tenantId, name = "Maternelle", sortOrder = 1))
        levelRepo.create(SchoolLevelEntity(tenantId = tenantId, cycleId = cycleId, name = "Petite Section (PS)", sortOrder = 1))
        levelRepo.create(SchoolLevelEntity(tenantId = tenantId, cycleId = cycleId, name = "Moyenne Section (MS)", sortOrder = 2))
        levelRepo.create(SchoolLevelEntity(tenantId = tenantId, cycleId = cycleId, name = "Grande Section (GS)", sortOrder = 3))
    }

    private fun seedPrimaire(tenantId: Int) {
        val cycleId = cycleRepo.create(SchoolCycleEntity(tenantId = tenantId, name = "Primaire", sortOrder = 2))
        levelRepo.create(SchoolLevelEntity(tenantId = tenantId, cycleId = cycleId, name = "Cours Initial (CI)", sortOrder = 1))
        levelRepo.create(SchoolLevelEntity(tenantId = tenantId, cycleId = cycleId, name = "Cours Préparatoire 1 (CP1)", sortOrder = 2))
        levelRepo.create(SchoolLevelEntity(tenantId = tenantId, cycleId = cycleId, name = "Cours Préparatoire 2 (CP2)", sortOrder = 3))
        levelRepo.create(SchoolLevelEntity(tenantId = tenantId, cycleId = cycleId, name = "Cours Élémentaire 1 (CE1)", sortOrder = 4))
        levelRepo.create(SchoolLevelEntity(tenantId = tenantId, cycleId = cycleId, name = "Cours Élémentaire 2 (CE2)", sortOrder = 5))
        levelRepo.create(SchoolLevelEntity(tenantId = tenantId, cycleId = cycleId, name = "Cours Moyen 1 (CM1)", sortOrder = 6))
        levelRepo.create(SchoolLevelEntity(tenantId = tenantId, cycleId = cycleId, name = "Cours Moyen 2 (CM2)", sortOrder = 7))
    }

    private fun seedCollege(tenantId: Int) {
        val cycleId = cycleRepo.create(SchoolCycleEntity(tenantId = tenantId, name = "Collège", sortOrder = 3))
        levelRepo.create(SchoolLevelEntity(tenantId = tenantId, cycleId = cycleId, name = "6ème", sortOrder = 1))
        levelRepo.create(SchoolLevelEntity(tenantId = tenantId, cycleId = cycleId, name = "5ème", sortOrder = 2))
        levelRepo.create(SchoolLevelEntity(tenantId = tenantId, cycleId = cycleId, name = "4ème", sortOrder = 3))
        levelRepo.create(SchoolLevelEntity(tenantId = tenantId, cycleId = cycleId, name = "3ème", sortOrder = 4))
    }

    private fun seedLycee(tenantId: Int) {
        val cycleId = cycleRepo.create(SchoolCycleEntity(tenantId = tenantId, name = "Lycée", sortOrder = 4))
        levelRepo.create(SchoolLevelEntity(tenantId = tenantId, cycleId = cycleId, name = "Seconde", sortOrder = 1))
        levelRepo.create(SchoolLevelEntity(tenantId = tenantId, cycleId = cycleId, name = "Première", sortOrder = 2))
        levelRepo.create(SchoolLevelEntity(tenantId = tenantId, cycleId = cycleId, name = "Terminale", sortOrder = 3))
    }
}
