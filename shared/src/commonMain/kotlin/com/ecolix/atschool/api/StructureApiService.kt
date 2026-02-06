package com.ecolix.atschool.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class StructureApiService(private val client: HttpClient) {
    
    suspend fun getSchoolYears(): Result<List<SchoolYearDto>> = runCatching {
        val response = client.get("school-years")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la récupération des années scolaires (${response.status.value})")
        }
        response.body()
    }

    suspend fun createSchoolYear(year: SchoolYearDto): Result<Int> = runCatching {
        val response = client.post("school-years") {
            contentType(ContentType.Application.Json)
            setBody(year)
        }
        if (response.status != HttpStatusCode.Created) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la création de l'année scolaire (${response.status.value})")
        }
        response.body()
    }

    suspend fun updateSchoolYear(id: Int, year: SchoolYearDto): Result<Unit> = runCatching {
        val response = client.put("school-years/$id") {
            contentType(ContentType.Application.Json)
            setBody(year)
        }
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la mise à jour de l'année scolaire (${response.status.value})")
        }
    }

    suspend fun deleteSchoolYear(id: Int): Result<Unit> = runCatching {
        val response = client.delete("school-years/$id")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la suppression de l'année scolaire (${response.status.value})")
        }
    }

    suspend fun setDefaultSchoolYear(id: Int): Result<Unit> = runCatching {
        val response = client.post("school-years/$id/set-default")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la définition de l'année par défaut (${response.status.value})")
        }
    }

    suspend fun setSchoolYearStatus(id: Int, status: String): Result<Unit> = runCatching {
        val response = client.post("school-years/$id/set-status") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("status" to status))
        }
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la mise à jour du statut de l'année scolaire (${response.status.value})")
        }
    }

    suspend fun getPeriodsByYear(yearId: Int): Result<List<AcademicPeriodDto>> = runCatching {
        val response = client.get("school-years/$yearId/periods")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la récupération des périodes (${response.status.value})")
        }
        response.body()
    }

    suspend fun createAcademicPeriod(period: AcademicPeriodDto): Result<Int> = runCatching {
        val response = client.post("academic-periods") {
            contentType(ContentType.Application.Json)
            setBody(period)
        }
        if (response.status != HttpStatusCode.Created) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la création de la période (${response.status.value})")
        }
        response.body()
    }

    suspend fun updateAcademicPeriod(id: Int, period: AcademicPeriodDto): Result<Unit> = runCatching {
        val response = client.put("academic-periods/$id") {
            contentType(ContentType.Application.Json)
            setBody(period)
        }
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la mise à jour de la période (${response.status.value})")
        }
    }

    suspend fun deleteAcademicPeriod(id: Int): Result<Unit> = runCatching {
        val response = client.delete("academic-periods/$id")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la suppression de la période (${response.status.value})")
        }
    }

    suspend fun setAcademicPeriodStatus(id: Int, status: String): Result<Unit> = runCatching {
        val response = client.post("academic-periods/$id/set-status") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("status" to status))
        }
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la mise à jour du statut de la période (${response.status.value})")
        }
    }

    suspend fun getCycles(): Result<List<CycleDto>> = runCatching {
        val response = client.get("cycles")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la récupération des cycles (${response.status.value})")
        }
        response.body()
    }

    suspend fun getLevelsByCycle(cycleId: Int): Result<List<LevelDto>> = runCatching {
        val response = client.get("cycles/$cycleId/levels")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la récupération des niveaux (${response.status.value})")
        }
        response.body()
    }

    suspend fun getClasses(): Result<List<ClassDto>> = runCatching {
        val response = client.get("classes")
        if (response.status != HttpStatusCode.OK) {
             val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la récupération des classes (${response.status.value})")
        }
        response.body()
    }

    suspend fun createClass(classroom: ClassDto): Result<Int> = runCatching {
        val response = client.post("classes") {
            contentType(ContentType.Application.Json)
            setBody(classroom)
        }
        if (response.status != HttpStatusCode.Created) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la création de la classe (${response.status.value})")
        }
        response.body()
    }

    suspend fun updateClass(id: Int, classroom: ClassDto): Result<Unit> = runCatching {
        val response = client.put("classes/$id") {
            contentType(ContentType.Application.Json)
            setBody(classroom)
        }
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la mise à jour de la classe (${response.status.value})")
        }
    }

    suspend fun deleteClass(id: Int): Result<Unit> = runCatching {
        val response = client.delete("classes/$id")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la suppression de la classe (${response.status.value})")
        }
    }

    // Academic Events
    suspend fun getAcademicEvents(): Result<List<AcademicEventDto>> = runCatching {
        val response = client.get("academic-events")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la récupération des événements (${response.status.value})")
        }
        response.body()
    }

    suspend fun createAcademicEvent(event: AcademicEventDto): Result<Int> = runCatching {
        val response = client.post("academic-events") {
            contentType(ContentType.Application.Json)
            setBody(event)
        }
        if (response.status != HttpStatusCode.Created) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la création de l'événement (${response.status.value})")
        }
        response.body()
    }

    suspend fun updateAcademicEvent(id: Int, event: AcademicEventDto): Result<Unit> = runCatching {
        val response = client.put("academic-events/$id") {
            contentType(ContentType.Application.Json)
            setBody(event)
        }
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la mise à jour de l'événement (${response.status.value})")
        }
    }

    suspend fun deleteAcademicEvent(id: Int): Result<Unit> = runCatching {
        val response = client.delete("academic-events/$id")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la suppression de l'événement (${response.status.value})")
        }
    }

    // Holidays
    suspend fun getHolidays(): Result<List<HolidayDto>> = runCatching {
        val response = client.get("holidays")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la récupération des vacances (${response.status.value})")
        }
        response.body()
    }

    suspend fun createHoliday(holiday: HolidayDto): Result<Int> = runCatching {
        val response = client.post("holidays") {
            contentType(ContentType.Application.Json)
            setBody(holiday)
        }
        if (response.status != HttpStatusCode.Created) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la création des vacances (${response.status.value})")
        }
        response.body()
    }

    suspend fun updateHoliday(id: Int, holiday: HolidayDto): Result<Unit> = runCatching {
        val response = client.put("holidays/$id") {
            contentType(ContentType.Application.Json)
            setBody(holiday)
        }
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la mise à jour des vacances (${response.status.value})")
        }
    }

    suspend fun deleteHoliday(id: Int): Result<Unit> = runCatching {
        val response = client.delete("holidays/$id")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la suppression des vacances (${response.status.value})")
        }
    }

    // Academic Settings
    suspend fun getAcademicSettings(): Result<AcademicSettingsDto> = runCatching {
        val response = client.get("settings")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la récupération des paramètres (${response.status.value})")
        }
        response.body()
    }

    suspend fun updateAcademicSettings(settings: AcademicSettingsDto): Result<Unit> = runCatching {
        val response = client.post("settings") {
            contentType(ContentType.Application.Json)
            setBody(settings)
        }
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la mise à jour des paramètres (${response.status.value})")
        }
    }

    // Grade Levels
    suspend fun getGradeLevels(): Result<List<GradeLevelDto>> = runCatching {
        val response = client.get("grade-levels")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la récupération des échelons (${response.status.value})")
        }
        response.body()
    }

    suspend fun updateGradeLevels(levels: List<GradeLevelDto>): Result<Unit> = runCatching {
        val response = client.post("grade-levels") {
            contentType(ContentType.Application.Json)
            setBody(levels)
        }
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la mise à jour des échelons (${response.status.value})")
        }
    }

    // School Structure (Cycles & Levels)
    suspend fun getSchoolCycles(): Result<List<SchoolCycleDto>> = runCatching {
        val response = client.get("school-cycles")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la récupération des cycles scolaires (${response.status.value})")
        }
         response.body()
    }

    suspend fun createSchoolCycle(cycle: SchoolCycleDto): Result<Int> = runCatching {
        val response = client.post("school-cycles") {
            contentType(ContentType.Application.Json)
            setBody(cycle)
        }
        if (response.status != HttpStatusCode.Created) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la création du cycle (${response.status.value})")
        }
        response.body()
    }

    suspend fun updateSchoolCycle(id: Int, cycle: SchoolCycleDto): Result<Unit> = runCatching {
        val response = client.put("school-cycles/$id") {
            contentType(ContentType.Application.Json)
            setBody(cycle)
        }
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la mise à jour du cycle (${response.status.value})")
        }
    }

    suspend fun deleteSchoolCycle(id: Int): Result<Unit> = runCatching {
        val response = client.delete("school-cycles/$id")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
             throw Exception(errorBody?.get("error") ?: "Erreur lors de la suppression du cycle (${response.status.value})")
        }
    }

    suspend fun getSchoolLevelsByCycle(cycleId: Int): Result<List<SchoolLevelDto>> = runCatching {
        val response = client.get("school-cycles/$cycleId/levels")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la récupération des niveaux (${response.status.value})")
        }
        response.body()
    }
    
    suspend fun getAllSchoolLevels(): Result<List<SchoolLevelDto>> = runCatching {
        val response = client.get("school-levels")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la récupération des niveaux (${response.status.value})")
        }
        response.body()
    }

    suspend fun createSchoolLevel(level: SchoolLevelDto): Result<Int> = runCatching {
        val response = client.post("school-levels") {
            contentType(ContentType.Application.Json)
            setBody(level)
        }
        if (response.status != HttpStatusCode.Created) {
             val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la création du niveau (${response.status.value})")
        }
        response.body()
    }

    suspend fun updateSchoolLevel(id: Int, level: SchoolLevelDto): Result<Unit> = runCatching {
        val response = client.put("school-levels/$id") {
            contentType(ContentType.Application.Json)
             setBody(level)
        }
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
             throw Exception(errorBody?.get("error") ?: "Erreur lors de la mise à jour du niveau (${response.status.value})")
        }
    }

    suspend fun deleteSchoolLevel(id: Int): Result<Unit> = runCatching {
        val response = client.delete("school-levels/$id")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la suppression du niveau (${response.status.value})")
        }
    }

    suspend fun seedDefaultStructure(): Result<Unit> = runCatching {
        val response = client.post("structure/seed-defaults")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = try { response.body<Map<String, String>>() } catch (e: Exception) { null }
            throw Exception(errorBody?.get("error") ?: "Erreur lors de la génération de la structure (${response.status.value})")
        }
    }
}
