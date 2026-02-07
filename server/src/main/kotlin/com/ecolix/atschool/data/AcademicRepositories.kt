package com.ecolix.atschool.data

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import org.slf4j.LoggerFactory

import com.ecolix.atschool.api.SubjectDto
import com.ecolix.atschool.api.SubjectCategoryDto

typealias Category = SubjectCategoryDto
typealias Subject = SubjectDto

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

    fun create(subject: Subject): Int = transaction {
        Matieres.insertAndGetId {
            it[tenantId] = subject.tenantId!!
            it[nom] = subject.nom
            it[code] = subject.code
            it[categoryId] = subject.categoryId
            it[defaultCoefficient] = subject.defaultCoefficient
            it[weeklyHours] = subject.weeklyHours
            it[description] = subject.description
            it[colorHex] = subject.colorHex
        }.value
    }

    fun update(id: Int, subject: Subject): Boolean = transaction {
        Matieres.update({ (Matieres.id eq id) and (Matieres.tenantId eq subject.tenantId!!) }) {
            it[nom] = subject.nom
            it[code] = subject.code
            it[categoryId] = subject.categoryId
            it[defaultCoefficient] = subject.defaultCoefficient
            it[weeklyHours] = subject.weeklyHours
            it[description] = subject.description
            it[colorHex] = subject.colorHex
        } > 0
    }

    fun delete(id: Int, tenantId: Int): Boolean = transaction {
        Matieres.deleteWhere { (Matieres.id eq id) and (Matieres.tenantId eq tenantId) } > 0
    }

    private fun ResultRow.toSubject() = Subject(
        id = this[Matieres.id].value,
        tenantId = this[Matieres.tenantId].value,
        nom = this[Matieres.nom],
        code = this[Matieres.code],
        categoryId = this[Matieres.categoryId]?.value,
        defaultCoefficient = this[Matieres.defaultCoefficient],
        weeklyHours = this[Matieres.weeklyHours],
        description = this[Matieres.description],
        colorHex = this[Matieres.colorHex]
    )
}

class CategoryRepository {
    fun getAll(tenantId: Int): List<Category> = transaction {
        println("CategoryRepository: Fetching categories for tenant $tenantId")
        val results = SubjectCategories.selectAll().where { SubjectCategories.tenantId eq tenantId }
            .orderBy(SubjectCategories.sortOrder to SortOrder.ASC)
            .map { it.toCategory() }
        println("CategoryRepository: Found ${results.size} categories for tenant $tenantId")
        results
    }

    fun create(category: Category): Int = transaction {
        println("CategoryRepository: Creating category '${category.name}' for tenant ${category.tenantId}")
        val id = SubjectCategories.insertAndGetId {
            it[tenantId] = category.tenantId!!
            it[name] = category.name
            it[description] = category.description
            it[colorHex] = category.colorHex
            it[sortOrder] = category.sortOrder
        }.value
        println("CategoryRepository: Created category '${category.name}' with ID $id")
        id
    }

    fun update(id: Int, category: Category): Boolean = transaction {
        SubjectCategories.update({ (SubjectCategories.id eq id) and (SubjectCategories.tenantId eq category.tenantId!!) }) {
            it[name] = category.name
            it[description] = category.description
            it[colorHex] = category.colorHex
            it[sortOrder] = category.sortOrder
        } > 0
    }

    fun delete(id: Int, tenantId: Int): Boolean = transaction {
        // Optionially set category_id to null in Matieres table is handled by onDelete = SET_NULL in Models.kt
        SubjectCategories.deleteWhere { (SubjectCategories.id eq id) and (SubjectCategories.tenantId eq tenantId) } > 0
    }

    fun seedDefaultCategories(tenantId: Int) = transaction {
        println("CategoryRepository: Seeding default categories for tenant $tenantId")
        
        val existing = getAll(tenantId)
        if (existing.isNotEmpty()) {
            println("CategoryRepository: Seeding aborted: ${existing.size} categories already exist for tenant $tenantId")
            return@transaction
        }
        
        println("CategoryRepository: No categories found for tenant $tenantId. Creating defaults...")

        val defaultCategories = listOf(
            Category(name = "Scientifique", description = "Mathématiques, Physique, Chimie, SVT", colorHex = "#4CAF50", sortOrder = 1, tenantId = tenantId),
            Category(name = "Littéraire", description = "Français, Philosophie, Littérature", colorHex = "#2196F3", sortOrder = 2, tenantId = tenantId),
            Category(name = "Langues", description = "Anglais, Espagnol, Arabe, etc.", colorHex = "#F44336", sortOrder = 3, tenantId = tenantId),
            Category(name = "Artistique", description = "Arts Plastiques, Musique", colorHex = "#9C27B0", sortOrder = 4, tenantId = tenantId),
            Category(name = "Sportive", description = "Éducation Physique et Sportive", colorHex = "#FF9800", sortOrder = 5, tenantId = tenantId),
            Category(name = "Civique & Sociale", description = "Histoire, Géographie, ECM", colorHex = "#795548", sortOrder = 6, tenantId = tenantId)
        )

        defaultCategories.forEach { create(it) }
        println("CategoryRepository: Seeding completed successfully for tenant $tenantId")
    }

    private fun ResultRow.toCategory() = Category(
        id = this[SubjectCategories.id].value,
        tenantId = this[SubjectCategories.tenantId].value,
        name = this[SubjectCategories.name],
        description = this[SubjectCategories.description],
        colorHex = this[SubjectCategories.colorHex],
        sortOrder = this[SubjectCategories.sortOrder]
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

@Serializable
data class ClassSubjectAssignment(
    val id: Int? = null,
    val tenantId: Int? = null,
    val classeId: Int,
    val matiereId: Int,
    val professeurId: Int? = null,
    val coefficient: Float? = null,
    val weeklyHours: Int? = null
)

class ClassSubjectRepository {
    fun getAllByClass(tenantId: Int, classId: Int): List<ClassSubjectAssignment> = transaction {
        ClassSubjects.selectAll().where { (ClassSubjects.tenantId eq tenantId) and (ClassSubjects.classeId eq classId) }
            .map { it.toAssignment() }
    }

    fun upsert(assignment: ClassSubjectAssignment): Int = transaction {
        val existing = ClassSubjects.selectAll().where { 
            (ClassSubjects.tenantId eq assignment.tenantId!!) and 
            (ClassSubjects.classeId eq assignment.classeId) and 
            (ClassSubjects.matiereId eq assignment.matiereId) 
        }.singleOrNull()

        if (existing != null) {
            val id = existing[ClassSubjects.id].value
            ClassSubjects.update({ ClassSubjects.id eq id }) {
                it[ClassSubjects.professeurId] = assignment.professeurId
                it[ClassSubjects.coefficient] = assignment.coefficient
                it[ClassSubjects.weeklyHours] = assignment.weeklyHours
            }
            id
        } else {
            ClassSubjects.insertAndGetId {
                it[ClassSubjects.tenantId] = assignment.tenantId!!
                it[ClassSubjects.classeId] = assignment.classeId
                it[ClassSubjects.matiereId] = assignment.matiereId
                it[ClassSubjects.professeurId] = assignment.professeurId
                it[ClassSubjects.coefficient] = assignment.coefficient
                it[ClassSubjects.weeklyHours] = assignment.weeklyHours
            }.value
        }
    }

    fun delete(id: Int, tenantId: Int): Boolean = transaction {
        ClassSubjects.deleteWhere { (ClassSubjects.id eq id) and (ClassSubjects.tenantId eq tenantId) } > 0
    }

    fun toggle(tenantId: Int, classId: Int, subjectId: Int): Boolean = transaction {
        val existing = ClassSubjects.selectAll().where { 
            (ClassSubjects.tenantId eq tenantId) and 
            (ClassSubjects.classeId eq classId) and 
            (ClassSubjects.matiereId eq subjectId) 
        }.singleOrNull()

        if (existing != null) {
            ClassSubjects.deleteWhere { ClassSubjects.id eq existing[ClassSubjects.id] }
            false // Removed
        } else {
            ClassSubjects.insertAndGetId {
                it[ClassSubjects.tenantId] = tenantId
                it[ClassSubjects.classeId] = classId
                it[ClassSubjects.matiereId] = subjectId
            }
            true // Added
        }
    }

    private fun ResultRow.toAssignment() = ClassSubjectAssignment(
        id = this[ClassSubjects.id].value,
        tenantId = this[ClassSubjects.tenantId].value,
        classeId = this[ClassSubjects.classeId].value,
        matiereId = this[ClassSubjects.matiereId].value,
        professeurId = this[ClassSubjects.professeurId]?.value,
        coefficient = this[ClassSubjects.coefficient],
        weeklyHours = this[ClassSubjects.weeklyHours]
    )
}
