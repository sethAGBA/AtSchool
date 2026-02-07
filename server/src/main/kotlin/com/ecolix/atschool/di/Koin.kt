package com.ecolix.atschool.di

import com.ecolix.atschool.api.AuthService
import com.ecolix.atschool.data.*
import org.koin.dsl.module
import org.koin.core.qualifier.named

val appModule = module {
    single { UserRepository() }
    single { StudentRepository() }
    single { DashboardRepository() }
    single { EstablishmentRepository() }
    single { ClassRepository() }
    single { SubjectRepository() }
    single { ClassSubjectRepository() }
    single { EvaluationRepository() }
    single { GradeRepository() }
    single { CategoryRepository() }
    single { SchoolYearRepository() }
    single { AcademicPeriodRepository() }
    single { CycleRepository() }
    single { LevelRepository() }
    single { AcademicEventRepository() }
    single { SchoolCycleRepository() }
    single { SchoolLevelRepository(get()) }
    single { HolidayRepository() }
    single { SuperAdminAdvancedRepository() }
    single { AcademicSettingsRepository() }
    single { GradeLevelRepository() }
    single { EstablishmentSettingsRepository() }
    single { StaffRepository() }
    single { StructureSeedingRepository(get(), get()) }
    single { 
        AuthService(
            get(),
            get(named("jwtSecret")),
            get(named("jwtIssuer")),
            get(named("jwtAudience"))
        ) 
    }
}
