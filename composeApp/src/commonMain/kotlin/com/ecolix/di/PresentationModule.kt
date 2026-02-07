package com.ecolix.di

import com.ecolix.presentation.screens.auth.LoginScreenModel
import com.ecolix.presentation.screens.eleves.StudentsScreenModel
import com.ecolix.presentation.screens.dashboard.DashboardScreenModel
import com.ecolix.presentation.screens.superadmin.SuperAdminScreenModel
import com.ecolix.presentation.screens.settings.SettingsScreenModel
import com.ecolix.presentation.screens.academic.AcademicScreenModel
import com.ecolix.atschool.api.AcademicApiService
import com.ecolix.atschool.api.StructureApiService
import com.ecolix.atschool.api.StaffApiService
import com.ecolix.domain.services.BulletinCacheService
import com.ecolix.domain.services.BulletinGenerationQueue
import com.ecolix.data.services.BulletinCacheServiceImpl
import com.ecolix.data.services.BulletinGenerationQueueImpl
import com.ecolix.data.services.StudentDataCache
import com.ecolix.data.services.ClassroomDataCache
import com.ecolix.data.services.StructureDataCache
import com.ecolix.data.services.StaffDataCache
import com.ecolix.data.services.SettingsDataCache
import com.ecolix.data.services.DashboardDataCache
import com.ecolix.data.services.AcademicDataCache
import com.ecolix.data.services.SubjectDataCache
import org.koin.dsl.module

val presentationModule = module {
    factory { LoginScreenModel(get()) }
    factory { StudentsScreenModel(get(), get(), get(), get(), get(), get(), get()) }
    factory { DashboardScreenModel(get(), get()) }
    factory { SuperAdminScreenModel(get()) }
    single { SettingsScreenModel(get(), get(), get()) }
    factory { AcademicScreenModel(get(), get()) }
    factory { com.ecolix.presentation.screens.staff.StaffScreenModel(get(), get()) }
    factory { com.ecolix.presentation.screens.subjects.SubjectsScreenModel(get(), get(), get(), get(), get(), get()) }
    factory { com.ecolix.presentation.screens.categories.CategoriesScreenModel(get()) }
    factory { com.ecolix.presentation.screens.notes.GradesScreenModel(get(), get(), get()) }
    
    // Bulletin Cache Service
    single<BulletinCacheService> { 
        BulletinCacheServiceImpl(
            maxBulletinCacheSize = 100,
            maxTemplateCacheSize = 10,
            bulletinTtlMinutes = 60,
            templateTtlMinutes = 1440 // 24h
        )
    }
    
    // Bulletin Generation Queue
    single<BulletinGenerationQueue> {
        BulletinGenerationQueueImpl(
            pdfService = get(),
            getReportCard = { bulletinId ->
                // TODO: Implémenter la récupération du ReportCard depuis le repository
                // Pour l'instant, retourne un bulletin factice
                throw NotImplementedError("getReportCard not implemented yet")
            }
        )
    }
    
    single<com.ecolix.domain.services.PdfExportService> { com.ecolix.data.services.PdfExportServiceImpl() }
    
    // Data Cache Services
    single { StudentDataCache() }
    single { ClassroomDataCache() }
    single<StructureDataCache<Any>> { StructureDataCache() }
    single { StaffDataCache() }
    single { SettingsDataCache() }
    single { DashboardDataCache() }
    single { AcademicDataCache() }
    single { SubjectDataCache() }
}

