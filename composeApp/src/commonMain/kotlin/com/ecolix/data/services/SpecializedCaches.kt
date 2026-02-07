package com.ecolix.data.services

import com.ecolix.atschool.api.StudentResponse
import com.ecolix.domain.services.DataCacheService
import com.ecolix.domain.services.CachePolicy

/**
 * Cache spécialisé pour les données d'élèves
 * TTL: 5 minutes (données fréquemment modifiées)
 */
class StudentDataCache : DataCacheServiceImpl<List<StudentResponse>>(
    CachePolicy(
        maxSize = 500,
        ttlMinutes = 5,
        name = "StudentCache"
    )
) {
    companion object {
        const val KEY_ALL_STUDENTS = "all_students"
        
        fun keyForYear(year: String) = "students_year_$year"
        fun keyForClass(classId: String) = "students_class_$classId"
    }
}

/**
 * Cache spécialisé pour les données de classes
 * TTL: 10 minutes (données moins volatiles)
 */
class ClassroomDataCache : DataCacheServiceImpl<List<com.ecolix.atschool.api.ClassDto>>(
    CachePolicy(
        maxSize = 100,
        ttlMinutes = 10,
        name = "ClassroomCache"
    )
) {
    companion object {
        const val KEY_ALL_CLASSES = "all_classes"
        
        fun keyForYear(year: String) = "classes_year_$year"
        fun keyForLevel(levelId: Int) = "classes_level_$levelId"
    }
}

/**
 * Cache spécialisé pour la structure scolaire (cycles et niveaux)
 * TTL: 30 minutes (données rarement modifiées)
 */
class StructureDataCache<T> : DataCacheServiceImpl<List<T>>(
    CachePolicy(
        maxSize = 50,
        ttlMinutes = 30,
        name = "StructureCache"
    )
) {
    companion object {
        const val KEY_ALL_CYCLES = "all_cycles"
        const val KEY_ALL_LEVELS = "all_levels"
        const val KEY_ALL_YEARS = "all_years"
        
        fun keyForCycle(cycleId: Int) = "levels_cycle_$cycleId"
    }
}

/**
 * Cache spécialisé pour les données du personnel
 * TTL: 10 minutes (données modifiées occasionnellement)
 */
class StaffDataCache : DataCacheServiceImpl<List<com.ecolix.atschool.models.Staff>>(
    CachePolicy(
        maxSize = 200,
        ttlMinutes = 10,
        name = "StaffCache"
    )
) {
    companion object {
        const val KEY_ALL_STAFF = "all_staff"
        
        fun keyForRole(role: String) = "staff_role_$role"
        fun keyForDepartment(dept: String) = "staff_dept_$dept"
    }
}

/**
 * Cache spécialisé pour les paramètres de l'établissement
 * TTL: 60 minutes (données très rarement modifiées)
 */
class SettingsDataCache : DataCacheServiceImpl<com.ecolix.atschool.api.EstablishmentSettingsDto>(
    CachePolicy(
        maxSize = 10,
        ttlMinutes = 60,
        name = "SettingsCache"
    )
) {
    companion object {
        const val KEY_SETTINGS = "establishment_settings"
    }
}

/**
 * Cache spécialisé pour les statistiques du tableau de bord
 * TTL: 5 minutes (données agrégées, rafraîchissement périodique suffisant)
 */
class DashboardDataCache : DataCacheServiceImpl<com.ecolix.atschool.api.DashboardStatsResponse>(
    CachePolicy(
        maxSize = 10,
        ttlMinutes = 5,
        name = "DashboardCache"
    )
) {
    companion object {
        const val KEY_STATS = "dashboard_stats"
    }
}

/**
 * Cache spécialisé pour les données académiques (Années, Périodes, Événements, Paramètres)
 * TTL: 15 minutes
 */
class AcademicDataCache : DataCacheServiceImpl<Any>(
    CachePolicy(
        maxSize = 100,
        ttlMinutes = 15,
        name = "AcademicCache"
    )
) {
    companion object {
        const val KEY_SETTINGS = "academic_settings"
        const val KEY_GRADE_LEVELS = "grade_levels"
        const val KEY_SCHOOL_YEARS = "school_years"
        const val KEY_ALL_EVENTS = "all_events"
        const val KEY_ALL_HOLIDAYS = "all_holidays"
        
        fun keyForPeriods(yearId: Int) = "periods_year_$yearId"
    }
}

/**
 * Cache spécialisé pour les matières et catégories
 * TTL: 15 minutes (données structurelles modifiées occasionnellement)
 */
class SubjectDataCache : DataCacheServiceImpl<Any>(
    CachePolicy(
        maxSize = 100,
        ttlMinutes = 15,
        name = "SubjectCache"
    )
) {
    companion object {
        const val KEY_ALL_SUBJECTS = "all_subjects"
        const val KEY_ALL_CATEGORIES = "all_categories"
        
        fun keyForClassAssignments(classId: Int) = "assignments_class_$classId"
    }
}
