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
