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
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE).nullable()
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

object EstablishmentSettings : IntIdTable("establishment_settings") {
    val tenantId = reference("tenant_id", Tenants, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    
    // Identité
    val schoolName = varchar("school_name", 200)
    val schoolCode = varchar("school_code", 50)
    val schoolSlogan = text("school_slogan").nullable()
    val schoolLevel = varchar("school_level", 50).default("Primaire")
    val logoUrl = text("logo_url").nullable()
    val republicLogoUrl = text("republic_logo_url").nullable()
    
    // Tutelle
    val ministry = text("ministry").nullable()
    val republicName = varchar("republic_name", 100).nullable()
    val republicMotto = varchar("republic_motto", 200).nullable()
    val educationDirection = text("education_direction").nullable()
    val inspection = text("inspection").nullable()
    
    // Direction
    val genCivility = varchar("gen_civility", 10).default("M.")
    val genDirector = varchar("gen_director", 100).nullable()
    val matCivility = varchar("mat_civility", 10).default("Mme")
    val matDirector = varchar("mat_director", 100).nullable()
    val priCivility = varchar("pri_civility", 10).default("M.")
    val priDirector = varchar("pri_director", 100).nullable()
    val colCivility = varchar("col_civility", 10).default("M.")
    val colDirector = varchar("col_director", 100).nullable()
    val lycCivility = varchar("lyc_civility", 10).default("M.")
    val lycDirector = varchar("lyc_director", 100).nullable()
    val uniCivility = varchar("uni_civility", 10).default("Pr")
    val uniDirector = varchar("uni_director", 100).nullable()
    val supCivility = varchar("sup_civility", 10).default("Dr")
    val supDirector = varchar("sup_director", 100).nullable()
    
    // Contact
    val phone = varchar("phone", 50).nullable()
    val email = varchar("email", 100).nullable()
    val website = varchar("website", 100).nullable()
    val bp = varchar("bp", 100).nullable()
    val address = text("address").nullable()
    
    // Configuration
    val pdfFooter = text("pdf_footer").nullable()
    val useTrimesters = bool("use_trimesters").default(true)
    val useSemesters = bool("use_semesters").default(false)
    
    // Système
    val autoBackup = bool("auto_backup").default(true)
    val backupFrequency = varchar("backup_frequency", 20).default("Quotidienne")
    val retentionDays = integer("retention_days").default(30)
    
    val updatedAt = datetime("updated_at")
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
    val numberOfPeriods = integer("number_of_periods").default(3)
    val periodType = varchar("period_type", 255).default("TRIMESTER") // Comma-separated: TRIMESTER, SEMESTER, etc.
    val isDefault = bool("is_default").default(false)
    val description = text("description").nullable()
}

object AcademicPeriods : IntIdTable("academic_periods") {
    val tenantId = reference("tenant_id", Tenants, onDelete = ReferenceOption.CASCADE)
    val anneeScolaireId = reference("annee_scolaire_id", AnneesScolaires, onDelete = ReferenceOption.CASCADE)
    val nom = varchar("nom", 100)
    val numero = integer("numero")
    val dateDebut = date("date_debut")
    val dateFin = date("date_fin")
    val evaluationDeadline = date("evaluation_deadline").nullable()
    val reportCardDeadline = date("report_card_deadline").nullable()
    val periodType = varchar("period_type", 20).default("TRIMESTER")
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
