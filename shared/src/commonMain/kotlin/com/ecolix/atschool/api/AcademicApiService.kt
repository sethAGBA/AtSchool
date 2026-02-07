package com.ecolix.atschool.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AcademicApiService(private val client: HttpClient) {
    // Subjects
    suspend fun getAllSubjects(): Result<List<SubjectDto>> = runCatching {
        val resp = client.get("subjects")
        if (!resp.status.isSuccess()) throw Exception("Failed to load subjects: ${resp.status}")
        resp.body()
    }

    suspend fun createSubject(subject: SubjectDto): Result<Int> = runCatching {
        val resp = client.post("subjects") {
            setBody(subject)
            contentType(ContentType.Application.Json)
        }
        if (!resp.status.isSuccess()) throw Exception("Failed to create subject: ${resp.status}")
        resp.body()
    }

    suspend fun updateSubject(id: Int, subject: SubjectDto): Result<Unit> = runCatching {
        val resp = client.put("subjects/$id") {
            setBody(subject)
            contentType(ContentType.Application.Json)
        }
        if (!resp.status.isSuccess()) throw Exception("Failed to update subject: ${resp.status}")
        resp.body()
    }

    suspend fun deleteSubject(id: Int): Result<Unit> = runCatching {
        val resp = client.delete("subjects/$id")
        if (!resp.status.isSuccess()) throw Exception("Failed to delete subject: ${resp.status}")
        resp.body()
    }

    // Subject Categories
    suspend fun getAllCategories(): Result<List<SubjectCategoryDto>> = runCatching {
        val resp = client.get("subject-categories")
        if (!resp.status.isSuccess()) throw Exception("Failed to load categories: ${resp.status}")
        resp.body()
    }

    suspend fun createCategory(category: SubjectCategoryDto): Result<Int> = runCatching {
        val resp = client.post("subject-categories") {
            setBody(category)
            contentType(ContentType.Application.Json)
        }
        if (!resp.status.isSuccess()) throw Exception("Failed to create category: ${resp.status}")
        resp.body()
    }

    suspend fun updateCategory(id: Int, category: SubjectCategoryDto): Result<Unit> = runCatching {
        val resp = client.put("subject-categories/$id") {
            setBody(category)
            contentType(ContentType.Application.Json)
        }
        if (!resp.status.isSuccess()) throw Exception("Failed to update category: ${resp.status}")
        resp.body()
    }

    suspend fun deleteCategory(id: Int): Result<Unit> = runCatching {
        val resp = client.delete("subject-categories/$id")
        if (!resp.status.isSuccess()) throw Exception("Failed to delete category: ${resp.status}")
        resp.body()
    }

    suspend fun seedDefaultCategories(): Result<Unit> = runCatching {
        val resp = client.post("subject-categories/seed")
        if (!resp.status.isSuccess()) throw Exception("Failed to seed categories: ${resp.status}")
        resp.body()
    }

    // Class Subjects
    suspend fun getClassSubjects(classId: Int): Result<List<ClassSubjectAssignmentDto>> = runCatching {
        val resp = client.get("class-subjects/$classId")
        if (!resp.status.isSuccess()) throw Exception("Failed to load class subjects: ${resp.status}")
        resp.body()
    }

    suspend fun saveClassSubject(assignment: ClassSubjectAssignmentDto): Result<Int> = runCatching {
        val resp = client.post("class-subjects") {
            setBody(assignment)
            contentType(ContentType.Application.Json)
        }
        if (!resp.status.isSuccess()) throw Exception("Failed to save class subject: ${resp.status}")
        resp.body()
    }

    suspend fun toggleClassSubject(classId: Int, subjectId: Int): Result<Boolean> = runCatching {
        val resp = client.post("class-subjects/toggle") {
            setBody(mapOf("classId" to classId, "subjectId" to subjectId))
            contentType(ContentType.Application.Json)
        }
        if (!resp.status.isSuccess()) throw Exception("Failed to toggle class subject: ${resp.status}")
        val map: Map<String, Boolean> = resp.body()
        map["added"] ?: false
    }
}
