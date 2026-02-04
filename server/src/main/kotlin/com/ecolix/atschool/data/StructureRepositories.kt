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
    val niveauId: Int,
    val code: String,
    val nom: String
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

    private fun ResultRow.toClass() = ClassEntity(
        id = this[Classes.id].value,
        tenantId = this[Classes.tenantId].value,
        niveauId = this[Classes.niveauId].value,
        code = this[Classes.code],
        nom = this[Classes.nom]
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
    val isActif: Boolean = true
)

@Serializable
data class SchoolYearEntity(
    val id: Int? = null,
    val tenantId: Int,
    val libelle: String,
    val dateDebut: String,
    val dateFin: String,
    val isActif: Boolean = true,
    val numberOfPeriods: Int = 3,
    val periodType: String = "TRIMESTER",
    val isDefault: Boolean = false,
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
            it[isActif] = entity.isActif
            it[numberOfPeriods] = entity.numberOfPeriods
            it[periodType] = entity.periodType
            it[isDefault] = entity.isDefault
            it[description] = entity.description
        }.value

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
                    it[isActif] = true
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
                            it[isActif] = true
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
            it[isActif] = entity.isActif
            it[numberOfPeriods] = entity.numberOfPeriods
            it[periodType] = entity.periodType
            it[isDefault] = entity.isDefault
            it[description] = entity.description
        } > 0
        
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
                    it[isActif] = period.isActif
                }
            }
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
            it[isDefault] = true
        }
    }

    private fun ResultRow.toSchoolYear() = SchoolYearEntity(
        id = this[AnneesScolaires.id].value,
        tenantId = this[AnneesScolaires.tenantId].value,
        libelle = this[AnneesScolaires.libelle],
        dateDebut = this[AnneesScolaires.dateDebut].toString(),
        dateFin = this[AnneesScolaires.dateFin].toString(),
        isActif = this[AnneesScolaires.isActif],
        numberOfPeriods = this[AnneesScolaires.numberOfPeriods],
        periodType = this[AnneesScolaires.periodType],
        isDefault = this[AnneesScolaires.isDefault],
        description = this[AnneesScolaires.description]
    )
}



class AcademicPeriodRepository {
    fun getByYear(anneeScolaireId: Int, tenantId: Int): List<AcademicPeriodEntity> = transaction {
        AcademicPeriods.selectAll().where { (AcademicPeriods.anneeScolaireId eq anneeScolaireId) and (AcademicPeriods.tenantId eq tenantId) }
            .map { it.toPeriod() }
    }

    fun create(entity: AcademicPeriodEntity): Int = transaction {
        AcademicPeriods.insertAndGetId {
            it[tenantId] = entity.tenantId
            it[anneeScolaireId] = entity.anneeScolaireId
            it[nom] = entity.nom
            it[numero] = entity.numero
            it[dateDebut] = kotlinx.datetime.LocalDate.parse(entity.dateDebut)
            it[dateFin] = kotlinx.datetime.LocalDate.parse(entity.dateFin)
            it[periodType] = entity.periodType
            it[evaluationDeadline] = entity.evaluationDeadline?.let { d -> kotlinx.datetime.LocalDate.parse(d) }
            it[reportCardDeadline] = entity.reportCardDeadline?.let { d -> kotlinx.datetime.LocalDate.parse(d) }
            it[isActif] = entity.isActif
        }.value
    }

    fun update(id: Int, entity: AcademicPeriodEntity): Boolean = transaction {
        AcademicPeriods.update({ (AcademicPeriods.id eq id) and (AcademicPeriods.tenantId eq entity.tenantId) }) {
            it[nom] = entity.nom
            it[numero] = entity.numero
            it[dateDebut] = kotlinx.datetime.LocalDate.parse(entity.dateDebut)
            it[dateFin] = kotlinx.datetime.LocalDate.parse(entity.dateFin)
            it[periodType] = entity.periodType
            it[evaluationDeadline] = entity.evaluationDeadline?.let { d -> kotlinx.datetime.LocalDate.parse(d) }
            it[reportCardDeadline] = entity.reportCardDeadline?.let { d -> kotlinx.datetime.LocalDate.parse(d) }
            it[isActif] = entity.isActif
        } > 0
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
        isActif = this[AcademicPeriods.isActif]
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
