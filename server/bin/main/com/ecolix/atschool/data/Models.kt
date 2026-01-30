package com.ecolix.atschool.data

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Tenants : IntIdTable("tenants") {
    val name = varchar("name", 100)
    val domain = varchar("domain", 100).uniqueIndex()
    val code = varchar("code", 10).uniqueIndex()
    val createdAt = date("created_at")
}

object Establishments : IntIdTable("establishments") {
    val tenantId = reference("tenant_id", Tenants, onDelete = ReferenceOption.CASCADE)
    val nom = varchar("nom", 200)
    val adresse = text("adresse").nullable()
    val codePostale = varchar("code_postale", 20).nullable()
    val ville = varchar("ville", 100).nullable()
}

object Users : LongIdTable("users") {
    val tenantId = reference("tenant_id", Tenants, onDelete = ReferenceOption.CASCADE)
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 50) // ADMIN, TEACHER, STAFF, PARENT
    val nom = varchar("nom", 100).nullable()
    val prenom = varchar("prenom", 100).nullable()
    val isMfaEnabled = bool("is_mfa_enabled").default(false)
}

object AnneesScolaires : IntIdTable("annees_scolaires") {
    val tenantId = reference("tenant_id", Tenants, onDelete = ReferenceOption.CASCADE)
    val libelle = varchar("libelle", 50)
    val dateDebut = date("date_debut")
    val dateFin = date("date_fin")
    val isActif = bool("is_actif").default(true)
}

object Cycles : IntIdTable("cycles") {
    val tenantId = reference("tenant_id", Tenants, onDelete = ReferenceOption.CASCADE)
    val nom = varchar("nom", 100) // Primaire, Collège, Lycée
}

object Niveaux : IntIdTable("niveaux") {
    val cycleId = reference("cycle_id", Cycles, onDelete = ReferenceOption.CASCADE)
    val nom = varchar("nom", 100)
}

object Classes : IntIdTable("classes") {
    val tenantId = reference("tenant_id", Tenants, onDelete = ReferenceOption.CASCADE)
    val niveauId = reference("niveau_id", Niveaux, onDelete = ReferenceOption.CASCADE)
    val code = varchar("code", 20)
    val nom = varchar("nom", 100)
}

object Eleves : LongIdTable("eleves") {
    val tenantId = reference("tenant_id", Tenants, onDelete = ReferenceOption.CASCADE)
    val matricule = varchar("matricule", 20).uniqueIndex()
    val nom = varchar("nom", 100)
    val prenom = varchar("prenom", 100)
    val dateNaissance = date("date_naissance")
    val sexe = varchar("sexe", 1)
}

object Inscriptions : LongIdTable("inscriptions") {
    val eleveId = reference("eleve_id", Eleves, onDelete = ReferenceOption.CASCADE)
    val classeId = reference("classe_id", Classes, onDelete = ReferenceOption.CASCADE)
    val anneeScolaireId = reference("annee_scolaire_id", AnneesScolaires, onDelete = ReferenceOption.CASCADE)
    val dateInscription = date("date_inscription")
    val statut = varchar("statut", 20).default("ACTIF")
}

object Matieres : IntIdTable("matieres") {
    val tenantId = reference("tenant_id", Tenants, onDelete = ReferenceOption.CASCADE)
    val nom = varchar("nom", 100)
    val code = varchar("code", 20)
}

object Evaluations : LongIdTable("evaluations") {
    val tenantId = reference("tenant_id", Tenants, onDelete = ReferenceOption.CASCADE)
    val classeId = reference("classe_id", Classes, onDelete = ReferenceOption.CASCADE)
    val matiereId = reference("matiere_id", Matieres, onDelete = ReferenceOption.CASCADE)
    val type = varchar("type", 50) // DEVOIR, EXAMEN, PARTICIPATION
    val date = date("date_evaluation")
    val coefficient = double("coefficient").default(1.0)
}

object Notes : LongIdTable("notes") {
    val evaluationId = reference("evaluation_id", Evaluations, onDelete = ReferenceOption.CASCADE)
    val eleveId = reference("eleve_id", Eleves, onDelete = ReferenceOption.CASCADE)
    val valeur = double("valeur")
    val appreciation = text("appreciation").nullable()
}

object Paiements : LongIdTable("paiements") {
    val tenantId = reference("tenant_id", Tenants, onDelete = ReferenceOption.CASCADE)
    val eleveId = reference("eleve_id", Eleves, onDelete = ReferenceOption.CASCADE)
    val montant = double("montant")
    val datePaiement = datetime("date_paiement")
    val motif = varchar("motif", 255)
    val modePaiement = varchar("mode_paiement", 50) // ESPECES, CHEQUE, TRANSFERT
}

object AuditLogs : LongIdTable("audit_logs") {
    val tenantId = reference("tenant_id", Tenants, onDelete = ReferenceOption.CASCADE)
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val action = varchar("action", 255)
    val timestamp = datetime("timestamp")
    val details = text("details").nullable()
    val ipAddress = varchar("ip_address", 45).nullable()
}
