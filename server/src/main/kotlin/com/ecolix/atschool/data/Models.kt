package com.ecolix.atschool.data

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Tenants : IntIdTable("tenants") {
    val name = varchar("name", 100)
    val domain = varchar("domain", 100).uniqueIndex()
    val code = varchar("code", 10).uniqueIndex()
    val contactEmail = varchar("contact_email", 100).nullable()
    val contactPhone = varchar("contact_phone", 50).nullable()
    val address = text("address").nullable()
    val subscriptionExpiresAt = date("subscription_expires_at").nullable()
    val createdAt = date("created_at")
    val isActive = bool("is_active").default(true)
}

object Announcements : IntIdTable("announcements") {
    val content = text("content")
    val targetRole = varchar("target_role", 20).nullable() // null = everyone
    val expiresAt = date("expires_at").nullable()
    val createdAt = date("created_at")
    val isActive = bool("is_active").default(true)
}

object AuditLogs : LongIdTable("audit_logs") {
    val tenantId = reference("tenant_id", Tenants, onDelete = ReferenceOption.CASCADE).nullable()
    val actorEmail = varchar("actor_email", 100)
    val action = varchar("action", 100)
    val details = text("details").nullable()
    val timestamp = datetime("timestamp")
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

object SubscriptionPlans : IntIdTable("subscription_plans") {
    val name = varchar("name", 100)
    val price = double("price")
    val currency = varchar("currency", 4).default("FCFA")
    val description = text("description")
    val isPopular = bool("is_popular").default(false)
    val createdAt = datetime("created_at")
}

object SubscriptionPayments : LongIdTable("subscription_payments") {
    val tenantId = reference("tenant_id", Tenants, onDelete = ReferenceOption.CASCADE)
    val amount = double("amount")
    val currency = varchar("currency", 4).default("FCFA")
    val paymentDate = datetime("payment_date")
    val paymentMethod = varchar("payment_method", 50) // CARD, TRANSFER, CASH, CHECK
    val status = varchar("status", 20).default("PENDING") // PENDING, PAID, FAILED, REFUNDED
    val invoiceNumber = varchar("invoice_number", 50).nullable()
    val notes = text("notes").nullable()
    val createdAt = datetime("created_at")
}

object Notifications : LongIdTable("notifications") {
    val tenantId = reference("tenant_id", Tenants, onDelete = ReferenceOption.CASCADE).nullable()
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE).nullable()
    val title = varchar("title", 200)
    val message = text("message")
    val type = varchar("type", 50).default("INFO") // INFO, WARNING, ALERT, SUBSCRIPTION_EXPIRY
    val priority = varchar("priority", 20).default("NORMAL") // LOW, NORMAL, HIGH, URGENT
    val isRead = bool("is_read").default(false)
    val createdAt = datetime("created_at")
    val expiresAt = datetime("expires_at").nullable()
}

object SupportTickets : LongIdTable("support_tickets") {
    val tenantId = reference("tenant_id", Tenants, onDelete = ReferenceOption.CASCADE)
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val subject = varchar("subject", 200)
    val description = text("description")
    val status = varchar("status", 20).default("OPEN") // OPEN, IN_PROGRESS, RESOLVED, CLOSED
    val priority = varchar("priority", 20).default("NORMAL") // LOW, NORMAL, HIGH, URGENT
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    val resolvedAt = datetime("resolved_at").nullable()
    val assignedTo = reference("assigned_to", Users, onDelete = ReferenceOption.SET_NULL).nullable()
}

object AdminPermissions : LongIdTable("admin_permissions") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val permission = varchar("permission", 100)
    val grantedAt = datetime("granted_at")
    val grantedBy = reference("granted_by", Users, onDelete = ReferenceOption.CASCADE)
}

@Serializable
data class GlobalStatsResponse(
    val totalSchools: Int,
    val totalStudents: Int,
    val totalRevenue: Double
)
