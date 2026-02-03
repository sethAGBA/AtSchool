package com.ecolix.di

import com.ecolix.presentation.screens.auth.LoginScreenModel
import com.ecolix.presentation.screens.eleves.StudentsScreenModel
import com.ecolix.presentation.screens.dashboard.DashboardScreenModel
import com.ecolix.presentation.screens.superadmin.SuperAdminScreenModel
import com.ecolix.presentation.screens.settings.SettingsScreenModel
import org.koin.dsl.module

val presentationModule = module {
    factory { LoginScreenModel(get()) }
    factory { StudentsScreenModel(get()) }
    factory { DashboardScreenModel(get()) }
    factory { SuperAdminScreenModel(get()) }
    single { SettingsScreenModel(get(), get()) }
}
