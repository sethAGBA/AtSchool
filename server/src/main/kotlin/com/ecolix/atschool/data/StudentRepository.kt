package com.ecolix.atschool.data

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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
    val sexe: String,
    val classeId: Int? = null,
    val classeNom: String? = null,
    val dateInscription: LocalDate? = null,
    val lieuNaissance: String? = null,
    val adresse: String? = null,
    val telephone: String? = null,
    val email: String? = null,
    val contactUrgence: String? = null,
    val nomTuteur: String? = null,
    val contactTuteur: String? = null,
    val infoMedicale: String? = null,
    val groupeSanguin: String? = null,
    val remarques: String? = null,
    val nationalite: String? = null,
    val photoUrl: String? = null,
    val deleted: Boolean = false
)

class StudentRepository {
    fun getAllStudents(tenantId: Int, anneeScolaireId: Int? = null): List<Student> = transaction {
        val query = (Eleves leftJoin Inscriptions leftJoin Classes)
            .selectAll().where { Eleves.tenantId eq tenantId }
        
        anneeScolaireId?.let { 
            query.andWhere { Inscriptions.anneeScolaireId eq it }
        } ?: run {
            // If no year specified, optionally pick the active one or just the latest?
            // For now, let's keep it simple and join what's there
        }

        query.map { it.toStudent() }
    }

    fun getStudentById(id: Long, tenantId: Int): Student? = transaction {
        Eleves.selectAll().where { (Eleves.id eq id) and (Eleves.tenantId eq tenantId) }
            .map { it.toStudent() }
            .singleOrNull()
    }

    fun addStudent(student: Student): Long = transaction {
        val eleveId = Eleves.insertAndGetId {
            it[tenantId] = student.tenantId
            it[matricule] = student.matricule
            it[nom] = student.nom
            it[prenom] = student.prenom
            it[dateNaissance] = student.dateNaissance
            it[sexe] = student.sexe
            it[lieuNaissance] = student.lieuNaissance
            it[adresse] = student.adresse
            it[telephone] = student.telephone
            it[email] = student.email
            it[contactUrgence] = student.contactUrgence
            it[nomTuteur] = student.nomTuteur
            it[contactTuteur] = student.contactTuteur
            it[infoMedicale] = student.infoMedicale
            it[groupeSanguin] = student.groupeSanguin
            it[remarques] = student.remarques
            it[nationalite] = student.nationalite
            it[photoUrl] = student.photoUrl
        }.value

        // If classroom is provided, create an inscription for the active school year
        student.classeId?.let { cId ->
            val activeYearId = AnneesScolaires.selectAll()
                .where { (AnneesScolaires.tenantId eq student.tenantId) and (AnneesScolaires.status eq "ACTIVE") }
                .map { it[AnneesScolaires.id].value }
                .singleOrNull() ?: AnneesScolaires.selectAll()
                    .where { (AnneesScolaires.tenantId eq student.tenantId) and (AnneesScolaires.isDefault eq true) }
                    .map { it[AnneesScolaires.id].value }
                    .singleOrNull()

            activeYearId?.let { yId ->
                Inscriptions.insert {
                    it[Inscriptions.eleveId] = eleveId
                    it[Inscriptions.classeId] = cId
                    it[Inscriptions.anneeScolaireId] = yId
                    it[Inscriptions.dateInscription] = student.dateInscription ?: kotlinx.datetime.Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
                    it[Inscriptions.statut] = "ACTIF"
                }
            }
        }
        eleveId
    }

    fun updateStudent(student: Student, tenantId: Int) = transaction {
        Eleves.update({ (Eleves.id eq student.id!!) and (Eleves.tenantId eq tenantId) }) {
            it[matricule] = student.matricule
            it[nom] = student.nom
            it[prenom] = student.prenom
            it[dateNaissance] = student.dateNaissance
            it[sexe] = student.sexe
            it[lieuNaissance] = student.lieuNaissance
            it[adresse] = student.adresse
            it[telephone] = student.telephone
            it[email] = student.email
            it[contactUrgence] = student.contactUrgence
            it[nomTuteur] = student.nomTuteur
            it[contactTuteur] = student.contactTuteur
            it[infoMedicale] = student.infoMedicale
            it[groupeSanguin] = student.groupeSanguin
            it[remarques] = student.remarques
            it[nationalite] = student.nationalite
            it[photoUrl] = student.photoUrl
        }

        // Update inscription if classeId is provided
        student.classeId?.let { cId ->
            val activeYearId = AnneesScolaires.selectAll()
                .where { (AnneesScolaires.tenantId eq tenantId) and (AnneesScolaires.status eq "ACTIVE") }
                .map { it[AnneesScolaires.id].value }
                .singleOrNull()

            activeYearId?.let { yId ->
                val existing = Inscriptions.selectAll()
                    .where { (Inscriptions.eleveId eq student.id!!) and (Inscriptions.anneeScolaireId eq yId) }
                    .singleOrNull()

                if (existing != null) {
                    Inscriptions.update({ (Inscriptions.eleveId eq student.id!!) and (Inscriptions.anneeScolaireId eq yId) }) {
                        it[Inscriptions.classeId] = cId
                    }
                } else {
                    Inscriptions.insert {
                        it[Inscriptions.eleveId] = student.id!!
                        it[Inscriptions.classeId] = cId
                        it[Inscriptions.anneeScolaireId] = yId
                        it[Inscriptions.dateInscription] = student.dateInscription ?: kotlinx.datetime.Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
                        it[Inscriptions.statut] = "ACTIF"
                    }
                }
            }
        }
    }

    fun deleteStudent(id: Long, tenantId: Int) = transaction {
        Eleves.update({ (Eleves.id eq id) and (Eleves.tenantId eq tenantId) }) {
            it[deleted] = true
        }
    }

    fun restoreStudent(id: Long, tenantId: Int) = transaction {
        Eleves.update({ (Eleves.id eq id) and (Eleves.tenantId eq tenantId) }) {
            it[deleted] = false
        }
    }

    fun deleteStudentPermanently(id: Long, tenantId: Int) = transaction {
        Eleves.deleteWhere { (Eleves.id eq id) and (Eleves.tenantId eq tenantId) }
    }

    fun generateNextMatricule(tenantId: Int): String = transaction {
        val prefix = AcademicSettings.selectAll().where { AcademicSettings.tenantId eq tenantId }
            .map { it[AcademicSettings.matriculePrefix] }
            .singleOrNull() ?: "MAT"

        val regex = "^$prefix-(\\d+)$".toRegex()
        val maxNumber = Eleves.selectAll()
            .where { (Eleves.tenantId eq tenantId) and (Eleves.matricule like "$prefix-%") }
            .mapNotNull { 
                val match = regex.find(it[Eleves.matricule])
                match?.groupValues?.get(1)?.toIntOrNull()
            }
            .maxOrNull() ?: 0

        val nextNumber = maxNumber + 1
        "$prefix-${nextNumber.toString().padStart(5, '0')}"
    }

    fun transferStudents(studentIds: List<Long>, newClassroomId: Int, tenantId: Int): Int = transaction {
        println("DEBUG [StudentRepository] Transfer operation started for ${studentIds.size} students to class $newClassroomId")
        // Verify the target classroom exists and belongs to the tenant
        val targetClass = Classes.selectAll()
            .where { (Classes.id eq newClassroomId) and (Classes.tenantId eq tenantId) }
            .singleOrNull() 
        
        if (targetClass == null) {
            println("DEBUG [StudentRepository] Target class $newClassroomId not found or invalid tenant")
            return@transaction 0
        }

        // Get the active school year
        val activeYearId = AnneesScolaires.selectAll()
            .where { (AnneesScolaires.tenantId eq tenantId) and (AnneesScolaires.status eq "ACTIVE") }
            .map { it[AnneesScolaires.id].value }
            .singleOrNull() ?: AnneesScolaires.selectAll()
                .where { (AnneesScolaires.tenantId eq tenantId) and (AnneesScolaires.isDefault eq true) }
                .map { it[AnneesScolaires.id].value }
                .singleOrNull()

        if (activeYearId == null) {
            println("DEBUG [StudentRepository] No active or default school year found")
            return@transaction 0
        }
        println("DEBUG [StudentRepository] Active year ID: $activeYearId")

        // Update inscriptions for all students
        var updatedCount = 0
        studentIds.forEach { studentId ->
            // Check if student belongs to the tenant
            val studentExists = Eleves.selectAll()
                .where { (Eleves.id eq studentId) and (Eleves.tenantId eq tenantId) }
                .count() > 0

            if (studentExists) {
                // Update existing inscription or create a new one
                val existingInscription = Inscriptions.selectAll()
                    .where { 
                        (Inscriptions.eleveId eq studentId) and 
                        (Inscriptions.anneeScolaireId eq activeYearId) 
                    }
                    .singleOrNull()

                if (existingInscription != null) {
                    println("DEBUG [StudentRepository] Updating existing inscription for student $studentId")
                    Inscriptions.update({ 
                        (Inscriptions.eleveId eq studentId) and 
                        (Inscriptions.anneeScolaireId eq activeYearId) 
                    }) {
                        it[classeId] = newClassroomId
                    }
                    updatedCount++
                } else {
                    println("DEBUG [StudentRepository] Creating new inscription for student $studentId")
                    // Create new inscription
                    Inscriptions.insert {
                        it[Inscriptions.eleveId] = studentId
                        it[Inscriptions.classeId] = newClassroomId
                        it[Inscriptions.anneeScolaireId] = activeYearId
                        it[Inscriptions.dateInscription] = kotlinx.datetime.Clock.System.now()
                            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
                        it[Inscriptions.statut] = "ACTIF"
                    }
                    updatedCount++
                }
            } else {
                println("DEBUG [StudentRepository] Student $studentId not found or invalid tenant")
            }
        }
        println("DEBUG [StudentRepository] Transfer completed. Updated $updatedCount records")
        updatedCount
    }

    private fun ResultRow.toStudent() = Student(
        id = this[Eleves.id].value,
        tenantId = this[Eleves.tenantId].value,
        matricule = this[Eleves.matricule],
        nom = this[Eleves.nom],
        prenom = this[Eleves.prenom],
        dateNaissance = this[Eleves.dateNaissance],
        sexe = this[Eleves.sexe],
        classeId = this.getOrNull(Inscriptions.classeId)?.value,
        classeNom = this.getOrNull(Classes.nom),
        dateInscription = this.getOrNull(Inscriptions.dateInscription),
        lieuNaissance = this[Eleves.lieuNaissance],
        adresse = this[Eleves.adresse],
        telephone = this[Eleves.telephone],
        email = this[Eleves.email],
        contactUrgence = this[Eleves.contactUrgence],
        nomTuteur = this[Eleves.nomTuteur],
        contactTuteur = this[Eleves.contactTuteur],
        infoMedicale = this[Eleves.infoMedicale],
        groupeSanguin = this[Eleves.groupeSanguin],
        remarques = this[Eleves.remarques],
        nationalite = this[Eleves.nationalite],
        photoUrl = this[Eleves.photoUrl],
        deleted = this[Eleves.deleted]
    )
}
