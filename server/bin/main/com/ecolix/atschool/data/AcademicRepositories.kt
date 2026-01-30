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
