package com.ecolix.atschool.di

import com.ecolix.atschool.api.AuthService
import com.ecolix.atschool.data.*
import org.koin.dsl.module

val appModule = module {
    single { UserRepository() }
    single { StudentRepository() }
    single { EstablishmentRepository() }
    single { ClassRepository() }
    single { SubjectRepository() }
    single { EvaluationRepository() }
    single { GradeRepository() }
    single { AuthService(get()) }
}
