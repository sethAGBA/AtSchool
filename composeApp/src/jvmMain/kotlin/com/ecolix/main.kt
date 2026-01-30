package com.ecolix

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

import com.ecolix.atschool.di.initKoin
import com.ecolix.di.presentationModule

fun main() {
    initKoin {
        modules(presentationModule)
    }
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "AtSchool",
        ) {
            App()
        }
    }
}