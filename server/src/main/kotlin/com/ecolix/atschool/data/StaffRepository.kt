package com.ecolix.atschool.data

import com.ecolix.atschool.models.Staff
import com.ecolix.atschool.models.StaffRole
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class StaffRepository {
    fun getAllStaff(tenantId: Int): List<Staff> = transaction {
        StaffTable.selectAll()
            .where { (StaffTable.tenantId eq tenantId) and (StaffTable.isDeleted eq false) }
            .orderBy(StaffTable.lastName to SortOrder.ASC, StaffTable.firstName to SortOrder.ASC)
            .map { it.toStaff() }
    }

    fun getStaffById(id: String, tenantId: Int): Staff? = transaction {
        StaffTable.selectAll().where { (StaffTable.id eq id.toInt()) and (StaffTable.tenantId eq tenantId) }
            .map { it.toStaff() }
            .singleOrNull()
    }

    fun addStaff(staff: Staff, tenantId: Int): Int = transaction {
        StaffTable.insertAndGetId {
            it[StaffTable.tenantId] = tenantId
            it[firstName] = staff.firstName
            it[lastName] = staff.lastName
            it[role] = staff.role.name
            it[department] = staff.department
            it[email] = staff.email
            it[phone] = staff.phone
            it[joinDate] = staff.joinDate
            it[status] = staff.status
            it[photoUrl] = staff.photoUrl
            it[matricule] = staff.matricule
            it[address] = staff.address
            it[gender] = staff.gender
            it[specialty] = staff.specialty
            it[assignedClasses] = staff.assignedClasses.joinToString(",")
            it[isDeleted] = staff.isDeleted
            
            it[qualifications] = staff.qualifications
            it[birthDate] = staff.birthDate
            it[birthPlace] = staff.birthPlace
            it[nationality] = staff.nationality
            it[idNumber] = staff.idNumber
            it[socialSecurityNumber] = staff.socialSecurityNumber
            it[maritalStatus] = staff.maritalStatus
            it[numberOfChildren] = staff.numberOfChildren
            it[region] = staff.region
            it[highestDegree] = staff.highestDegree
            it[experienceYears] = staff.experienceYears
            it[previousInstitution] = staff.previousInstitution
            it[contractType] = staff.contractType
            it[baseSalary] = staff.baseSalary
            it[weeklyHours] = staff.weeklyHours
            it[supervisor] = staff.supervisor
            it[retirementDate] = staff.retirementDate
        }.value
    }

    fun updateStaff(staff: Staff, tenantId: Int) = transaction {
        StaffTable.update({ (StaffTable.id eq staff.id.toInt()) and (StaffTable.tenantId eq tenantId) }) {
            it[firstName] = staff.firstName
            it[lastName] = staff.lastName
            it[role] = staff.role.name
            it[department] = staff.department
            it[email] = staff.email
            it[phone] = staff.phone
            it[joinDate] = staff.joinDate
            it[status] = staff.status
            it[photoUrl] = staff.photoUrl
            it[matricule] = staff.matricule
            it[address] = staff.address
            it[gender] = staff.gender
            it[specialty] = staff.specialty
            it[assignedClasses] = staff.assignedClasses.joinToString(",")
            it[isDeleted] = staff.isDeleted
            
            it[qualifications] = staff.qualifications
            it[birthDate] = staff.birthDate
            it[birthPlace] = staff.birthPlace
            it[nationality] = staff.nationality
            it[idNumber] = staff.idNumber
            it[socialSecurityNumber] = staff.socialSecurityNumber
            it[maritalStatus] = staff.maritalStatus
            it[numberOfChildren] = staff.numberOfChildren
            it[region] = staff.region
            it[highestDegree] = staff.highestDegree
            it[experienceYears] = staff.experienceYears
            it[previousInstitution] = staff.previousInstitution
            it[contractType] = staff.contractType
            it[baseSalary] = staff.baseSalary
            it[weeklyHours] = staff.weeklyHours
            it[supervisor] = staff.supervisor
            it[retirementDate] = staff.retirementDate
        }
    }

    fun deleteStaff(id: String, tenantId: Int) = transaction {
        StaffTable.update({ (StaffTable.id eq id.toInt()) and (StaffTable.tenantId eq tenantId) }) {
            it[isDeleted] = true
        }
    }

    fun deleteStaffBatch(ids: List<String>, tenantId: Int) = transaction {
        StaffTable.update({ (StaffTable.id inList ids.map { it.toInt() }) and (StaffTable.tenantId eq tenantId) }) {
            it[isDeleted] = true
        }
    }

    fun getDeletedStaff(tenantId: Int): List<Staff> = transaction {
        StaffTable.selectAll()
            .where { (StaffTable.tenantId eq tenantId) and (StaffTable.isDeleted eq true) }
            .orderBy(StaffTable.lastName to SortOrder.ASC, StaffTable.firstName to SortOrder.ASC)
            .map { it.toStaff() }
    }

    fun restoreStaff(id: String, tenantId: Int) = transaction {
        StaffTable.update({ (StaffTable.id eq id.toInt()) and (StaffTable.tenantId eq tenantId) }) {
            it[isDeleted] = false
        }
    }

    fun restoreStaffBatch(ids: List<String>, tenantId: Int) = transaction {
        StaffTable.update({ (StaffTable.id inList ids.map { it.toInt() }) and (StaffTable.tenantId eq tenantId) }) {
            it[isDeleted] = false
        }
    }

    fun permanentDeleteStaff(id: String, tenantId: Int) = transaction {
        StaffTable.deleteWhere { (StaffTable.id eq id.toInt()) and (StaffTable.tenantId eq tenantId) }
    }

    fun updateStaffStatusBatch(ids: List<String>, status: String, tenantId: Int) = transaction {
        StaffTable.update({ (StaffTable.id inList ids.map { it.toInt() }) and (StaffTable.tenantId eq tenantId) }) {
            it[StaffTable.status] = status
        }
    }

    private fun ResultRow.toStaff() = Staff(
        id = this[StaffTable.id].value.toString(),
        firstName = this[StaffTable.firstName],
        lastName = this[StaffTable.lastName],
        role = StaffRole.valueOf(this[StaffTable.role]),
        department = this[StaffTable.department],
        email = this[StaffTable.email],
        phone = this[StaffTable.phone],
        joinDate = this[StaffTable.joinDate],
        status = this[StaffTable.status],
        photoUrl = this[StaffTable.photoUrl],
        matricule = this[StaffTable.matricule],
        address = this[StaffTable.address],
        gender = this[StaffTable.gender],
        specialty = this[StaffTable.specialty],
        assignedClasses = this[StaffTable.assignedClasses].let { if (it.isEmpty()) emptyList() else it.split(",") },
        isDeleted = this[StaffTable.isDeleted],
        
        qualifications = this[StaffTable.qualifications],
        birthDate = this[StaffTable.birthDate],
        birthPlace = this[StaffTable.birthPlace],
        nationality = this[StaffTable.nationality],
        idNumber = this[StaffTable.idNumber],
        socialSecurityNumber = this[StaffTable.socialSecurityNumber],
        maritalStatus = this[StaffTable.maritalStatus],
        numberOfChildren = this[StaffTable.numberOfChildren],
        region = this[StaffTable.region],
        highestDegree = this[StaffTable.highestDegree],
        experienceYears = this[StaffTable.experienceYears],
        previousInstitution = this[StaffTable.previousInstitution],
        contractType = this[StaffTable.contractType],
        baseSalary = this[StaffTable.baseSalary],
        weeklyHours = this[StaffTable.weeklyHours],
        supervisor = this[StaffTable.supervisor],
        retirementDate = this[StaffTable.retirementDate]
    )
}
