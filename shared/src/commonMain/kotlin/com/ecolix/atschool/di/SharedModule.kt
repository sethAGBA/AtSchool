package com.ecolix.atschool.di

import com.ecolix.atschool.api.*
import org.koin.dsl.module

val sharedModule = module {
    single { createHttpClient() }
    single { AuthApiService(get()) }
    single { StudentApiService(get()) }
    single { DashboardApiService(get()) }
    single { SuperAdminApiService(get()) }
    single { SettingsApiService(get()) }
    single { UploadApiService(get()) }
    single { StructureApiService(get()) }
    single { StaffApiService(get()) }
}
