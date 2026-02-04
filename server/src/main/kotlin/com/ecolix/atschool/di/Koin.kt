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
    single { EvaluationRepository() }
    single { GradeRepository() }
    single { SchoolYearRepository() }
    single { AcademicPeriodRepository() }
    single { CycleRepository() }
    single { LevelRepository() }
    single { SuperAdminRepository() }
    single { SuperAdminAdvancedRepository() }
    single { 
        AuthService(
            get(),
            get(named("jwtSecret")),
            get(named("jwtIssuer")),
            get(named("jwtAudience"))
        ) 
    }
}
