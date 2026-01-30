package com.ecolix.di

import com.ecolix.presentation.screens.auth.LoginScreenModel
import com.ecolix.presentation.screens.eleves.StudentsScreenModel
import org.koin.dsl.module

val presentationModule = module {
    factory { LoginScreenModel(get()) }
    factory { StudentsScreenModel(get()) }
}
