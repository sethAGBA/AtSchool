package com.ecolix.atschool.data

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class Student(
    val id: Long? = null,
    val tenantId: Int,
    val matricule: String,
    val nom: String,
    val prenom: String,
    val dateNaissance: LocalDate,
    val sexe: String
)

class StudentRepository {
    fun getAllStudents(tenantId: Int): List<Student> = transaction {
        Eleves.selectAll().where { Eleves.tenantId eq tenantId }
            .map { it.toStudent() }
    }

    fun getStudentById(id: Long, tenantId: Int): Student? = transaction {
        Eleves.selectAll().where { (Eleves.id eq id) and (Eleves.tenantId eq tenantId) }
            .map { it.toStudent() }
            .singleOrNull()
    }

    fun addStudent(student: Student): Long = transaction {
        Eleves.insert {
            it[tenantId] = student.tenantId
            it[matricule] = student.matricule
            it[nom] = student.nom
            it[prenom] = student.prenom
            it[dateNaissance] = student.dateNaissance
            it[sexe] = student.sexe
        } get Eleves.id
    }.value

    private fun ResultRow.toStudent() = Student(
        id = this[Eleves.id].value,
        tenantId = this[Eleves.tenantId].value,
        matricule = this[Eleves.matricule],
        nom = this[Eleves.nom],
        prenom = this[Eleves.prenom],
        dateNaissance = this[Eleves.dateNaissance],
        sexe = this[Eleves.sexe]
    )
}
