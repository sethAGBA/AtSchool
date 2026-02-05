package com.ecolix.atschool.data

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

@Serializable
data class Subject(
    val id: Int? = null,
    val tenantId: Int,
    val nom: String,
    val code: String
)

@Serializable
data class Evaluation(
    val id: Long? = null,
    val tenantId: Int,
    val classeId: Int,
    val matiereId: Int,
    val type: String,
    val date: LocalDate,
    val coefficient: Double
)

@Serializable
data class Grade(
    val id: Long? = null,
    val evaluationId: Long,
    val eleveId: Long,
    val valeur: Double,
    val appreciation: String? = null
)

class SubjectRepository {
    fun getAll(tenantId: Int): List<Subject> = transaction {
        Matieres.selectAll().where { Matieres.tenantId eq tenantId }
            .map { it.toSubject() }
    }

    private fun ResultRow.toSubject() = Subject(
        id = this[Matieres.id].value,
        tenantId = this[Matieres.tenantId].value,
        nom = this[Matieres.nom],
        code = this[Matieres.code]
    )
}

class EvaluationRepository {
    fun getByClass(classeId: Int, tenantId: Int): List<Evaluation> = transaction {
        Evaluations.selectAll().where { (Evaluations.classeId eq classeId) and (Evaluations.tenantId eq tenantId) }
            .map { it.toEvaluation() }
    }

    private fun ResultRow.toEvaluation() = Evaluation(
        id = this[Evaluations.id].value,
        tenantId = this[Evaluations.tenantId].value,
        classeId = this[Evaluations.classeId].value,
        matiereId = this[Evaluations.matiereId].value,
        type = this[Evaluations.type],
        date = this[Evaluations.date],
        coefficient = this[Evaluations.coefficient]
    )
}

class GradeRepository {
    fun getByEvaluation(evaluationId: Long): List<Grade> = transaction {
        Notes.selectAll().where { Notes.evaluationId eq evaluationId }
            .map { it.toGrade() }
    }

    private fun ResultRow.toGrade() = Grade(
        id = this[Notes.id].value,
        evaluationId = this[Notes.evaluationId].value,
        eleveId = this[Notes.eleveId].value,
        valeur = this[Notes.valeur],
        appreciation = this[Notes.appreciation]
    )
}
@Serializable
data class AcademicEventEntity(
    val id: Int? = null,
    val tenantId: Int,
    val title: String,
    val description: String? = null,
    val date: String,
    val endDate: String? = null,
    val type: String,
    val color: String
)

@Serializable
data class HolidayEntity(
    val id: Int? = null,
    val tenantId: Int,
    val name: String,
    val startDate: String,
    val endDate: String,
    val type: String
)

class AcademicEventRepository {
    fun getAll(tenantId: Int): List<AcademicEventEntity> = transaction {
        AcademicEvents.selectAll().where { AcademicEvents.tenantId eq tenantId }
            .map { it.toEvent() }
    }

    fun create(entity: AcademicEventEntity): Int = transaction {
        AcademicEvents.insertAndGetId {
            it[tenantId] = entity.tenantId
            it[title] = entity.title
            it[description] = entity.description
            it[date] = LocalDate.parse(entity.date)
            it[endDate] = entity.endDate?.let { d -> LocalDate.parse(d) }
            it[type] = entity.type
            it[color] = entity.color
        }.value
    }

    fun update(id: Int, entity: AcademicEventEntity): Boolean = transaction {
        AcademicEvents.update({ (AcademicEvents.id eq id) and (AcademicEvents.tenantId eq entity.tenantId) }) {
            it[title] = entity.title
            it[description] = entity.description
            it[date] = LocalDate.parse(entity.date)
            it[endDate] = entity.endDate?.let { d -> LocalDate.parse(d) }
            it[type] = entity.type
            it[color] = entity.color
        } > 0
    }

    fun delete(id: Int, tenantId: Int): Boolean = transaction {
        AcademicEvents.deleteWhere { (AcademicEvents.id eq id) and (AcademicEvents.tenantId eq tenantId) } > 0
    }

    private fun ResultRow.toEvent() = AcademicEventEntity(
        id = this[AcademicEvents.id].value,
        tenantId = this[AcademicEvents.tenantId].value,
        title = this[AcademicEvents.title],
        description = this[AcademicEvents.description],
        date = this[AcademicEvents.date].toString(),
        endDate = this[AcademicEvents.endDate]?.toString(),
        type = this[AcademicEvents.type],
        color = this[AcademicEvents.color]
    )
}

class HolidayRepository {
    fun getAll(tenantId: Int): List<HolidayEntity> = transaction {
        Holidays.selectAll().where { Holidays.tenantId eq tenantId }
            .map { it.toHoliday() }
    }

    fun create(entity: HolidayEntity): Int = transaction {
        Holidays.insertAndGetId {
            it[tenantId] = entity.tenantId
            it[name] = entity.name
            it[startDate] = LocalDate.parse(entity.startDate)
            it[endDate] = LocalDate.parse(entity.endDate)
            it[type] = entity.type
        }.value
    }

    fun update(id: Int, entity: HolidayEntity): Boolean = transaction {
        Holidays.update({ (Holidays.id eq id) and (Holidays.tenantId eq entity.tenantId) }) {
            it[name] = entity.name
            it[startDate] = LocalDate.parse(entity.startDate)
            it[endDate] = LocalDate.parse(entity.endDate)
            it[type] = entity.type
        } > 0
    }

    fun delete(id: Int, tenantId: Int): Boolean = transaction {
        Holidays.deleteWhere { (Holidays.id eq id) and (Holidays.tenantId eq tenantId) } > 0
    }

    private fun ResultRow.toHoliday() = HolidayEntity(
        id = this[Holidays.id].value,
        tenantId = this[Holidays.tenantId].value,
        name = this[Holidays.name],
        startDate = this[Holidays.startDate].toString(),
        endDate = this[Holidays.endDate].toString(),
        type = this[Holidays.type]
    )
}
