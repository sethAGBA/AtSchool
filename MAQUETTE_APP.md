


🧩 MAQUETTE DES FONCTIONNALITÉS Logiciel de Gestion d’École (Offline)
🎓 MODULES PRINCIPAUX
Gestion des élèves
·       • Enregistrement des élèves (fiche complète : nom, date de naissance, contacts, tuteur, etc.)
·       • Suivi des effectifs par classe, par sexe, par année
·       • Historique des inscriptions par élève
Gestion des inscriptions et réinscriptions
·       • Paiement des frais d’inscription (avec reçu)
·       • Suivi des réinscriptions par cycle scolaire
Gestion des notes et bulletins
·       • Saisie des notes par matière et par période (semestre, trimestre, etc.)
·       • Calcul automatique des moyennes, rangs et appréciations
·       • Génération automatique des bulletins de notes personnalisés
·       • Archivage des résultats par année scolaire
Gestion des emplois du temps
·       • Création des emplois du temps par classe et par enseignant
·       • Impression des plannings hebdomadaires
Gestion du personnel
·       • Fiches du personnel enseignant et administratif
·       • Attribution des cours et des classes
·       • Suivi des présences et absences du personnel
Suivi de la discipline
·       • Gestion des absences et retards des élèves
·       • Historique des sanctions et avertissements
Suivi des paiements
·       • Enregistrement des paiements des frais de scolarité
·       • Génération de reçus personnalisés
·       • Alerte en cas de solde impayé
·       • Rapport financier par classe ou par élève
🧾 MODULES COMPLÉMENTAIRES (optionnels)
·       • 📚 Gestion de la bibliothèque : emprunt, retour, inventaire de livres
·       • 🏫 Gestion du matériel scolaire : distribution, inventaire
·       • 📊 Rapports et statistiques automatiques : export PDF/Excel
·       • 🔒 Sécurité des données : accès par mot de passe, sauvegardes locales
💻 CARACTÉRISTIQUES TECHNIQUES
·       • Fonctionne sans Internet
·       • Compatible Windows (version de bureau)
·       • Interface conviviale en français
·       • Données stockées localement (base de données SQLite ou Access)
·       • Export possible des bulletins, listes, statistiques au format PDF ou Excel🧩 MAQUETTE DES FONCTIONNALITÉS Logiciel de Gestion d’École (Offline)
🎓 MODULES PRINCIPAUX
Gestion des élèves
·       • Enregistrement des élèves (fiche complète : nom, date de naissance, contacts, tuteur, etc.)
·       • Suivi des effectifs par classe, par sexe, par année
·       • Historique des inscriptions par élève
Gestion des inscriptions et réinscriptions
·       • Paiement des frais d’inscription (avec reçu)
·       • Suivi des réinscriptions par cycle scolaire
Gestion des notes et bulletins
·       • Saisie des notes par matière et par période (semestre, trimestre, etc.)
·       • Calcul automatique des moyennes, rangs et appréciations
·       • Génération automatique des bulletins de notes personnalisés
·       • Archivage des résultats par année scolaire
Gestion des emplois du temps
·       • Création des emplois du temps par classe et par enseignant
·       • Impression des plannings hebdomadaires
Gestion du personnel
·       • Fiches du personnel enseignant et administratif
·       • Attribution des cours et des classes
·       • Suivi des présences et absences du personnel
Suivi de la discipline
·       • Gestion des absences et retards des élèves
·       • Historique des sanctions et avertissements
Suivi des paiements
·       • Enregistrement des paiements des frais de scolarité
·       • Génération de reçus personnalisés
·       • Alerte en cas de solde impayé
·       • Rapport financier par classe ou par élève
🧾 MODULES COMPLÉMENTAIRES (optionnels)
·       • 📚 Gestion de la bibliothèque : emprunt, retour, inventaire de livres
·       • 🏫 Gestion du matériel scolaire : distribution, inventaire
·       • 📊 Rapports et statistiques automatiques : export PDF/Excel
·       • 🔒 Sécurité des données : accès par mot de passe, sauvegardes locales
💻 CARACTÉRISTIQUES TECHNIQUES
·       • Fonctionne sans Internet
·       • Compatible Windows (version de bureau)
·       • Interface conviviale en français
·       • Données stockées localement (base de données SQLite ou Access)
·       • Export possible des bulletins, listes, statistiques au format PDF ou Excel)



# 🚀 Architecture Kotlin Compose Multiplatform pour ÉcoliX

## ✅ Configuration Projet Compose Multiplatform

### Structure de Projet Complète

```
ecolix/
├── composeApp/                          # App principale (Desktop/Mobile/Web)
│   ├── src/
│   │   ├── commonMain/                  # Code partagé (70-80%)
│   │   │   ├── kotlin/com/ecolix/
│   │   │   │   ├── App.kt              # Point d'entrée UI commun
│   │   │   │   ├── di/                 # Dependency Injection (Koin)
│   │   │   │   ├── data/
│   │   │   │   │   ├── models/         # Data classes
│   │   │   │   │   ├── repository/     # Repository pattern
│   │   │   │   │   ├── local/          # SQLite local
│   │   │   │   │   └── remote/         # Ktor Client API
│   │   │   │   ├── domain/             # Business logic
│   │   │   │   │   ├── usecases/
│   │   │   │   │   └── entities/
│   │   │   │   ├── presentation/       # UI Layer
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── dashboard/
│   │   │   │   │   │   ├── eleves/
│   │   │   │   │   │   ├── notes/
│   │   │   │   │   │   ├── paiements/
│   │   │   │   │   │   └── ...
│   │   │   │   │   ├── components/     # Composables réutilisables
│   │   │   │   │   ├── navigation/
│   │   │   │   │   ├── theme/
│   │   │   │   │   └── viewmodels/
│   │   │   │   └── utils/
│   │   │   └── resources/              # Images, strings, etc.
│   │   │
│   │   ├── desktopMain/                # Code spécifique Desktop
│   │   │   └── kotlin/com/ecolix/
│   │   │       ├── Main.kt             # Point d'entrée Desktop
│   │   │       └── platform/           # Platform-specific code
│   │   │
│   │   ├── androidMain/                # Code spécifique Android
│   │   │   └── kotlin/com/ecolix/
│   │   │       └── MainActivity.kt
│   │   │
│   │   ├── iosMain/                    # Code spécifique iOS
│   │   │   └── kotlin/com/ecolix/
│   │   │
│   │   └── wasmJsMain/                 # Code spécifique Web
│   │       └── kotlin/com/ecolix/
│   │
│   └── build.gradle.kts
│
├── server/                              # Backend Ktor (optionnel)
│   └── src/main/kotlin/com/ecolix/api/
│
├── gradle/
├── gradle.properties
├── settings.gradle.kts
└── build.gradle.kts
```

---

## 📦 Configuration `build.gradle.kts`

### Fichier Root `build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.kotlin.compose).apply(false)
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.kotlinx.serialization).apply(false)
}
```

### `composeApp/build.gradle.kts`

```kotlin
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    // ========================================
    // DESKTOP (Priorité)
    // ========================================
    jvm("desktop") {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    // ========================================
    // ANDROID
    // ========================================
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    // ========================================
    // iOS
    // ========================================
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    // ========================================
    // WEB (WASM)
    // ========================================
    wasmJs {
        moduleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }
    
    // ========================================
    // SOURCES SETS
    // ========================================
    sourceSets {
        // Common (Code partagé)
        val commonMain by getting {
            dependencies {
                // Compose Multiplatform
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                
                // Navigation
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.screenModel)
                implementation(libs.voyager.transitions)
                
                // Ktor Client (API calls)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.auth)
                
                // Kotlinx
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                
                // Dependency Injection (Koin)
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                
                // Local Database
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.coroutines)
                
                // Image Loading
                implementation(libs.kamel.image)
                
                // Settings (Preferences)
                implementation(libs.multiplatform.settings)
            }
        }
        
        // Desktop
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                
                // Ktor Client Engine
                implementation(libs.ktor.client.cio)
                
                // SQLite Driver
                implementation(libs.sqldelight.sqlite.driver)
                
                // File Picker
                implementation(libs.filekit.compose)
                
                // PDF Generation
                implementation(libs.itext.pdf)
            }
        }
        
        // Android
        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                
                // Ktor Client Engine
                implementation(libs.ktor.client.okhttp)
                
                // SQLite Driver
                implementation(libs.sqldelight.android.driver)
                
                // Android specific
                implementation(libs.koin.android)
                implementation(libs.androidx.core.ktx)
            }
        }
        
        // iOS
        val iosMain by creating {
            dependencies {
                // Ktor Client Engine
                implementation(libs.ktor.client.darwin)
                
                // SQLite Driver
                implementation(libs.sqldelight.native.driver)
            }
        }
        
        // Web
        val wasmJsMain by getting {
            dependencies {
                // Ktor Client Engine
                implementation(libs.ktor.client.js)
            }
        }
    }
}

// ========================================
// CONFIGURATION ANDROID
// ========================================
android {
    namespace = "com.ecolix.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    
    defaultConfig {
        applicationId = "com.ecolix.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
    }
    
    buildFeatures {
        compose = true
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// ========================================
// CONFIGURATION DESKTOP
// ========================================
compose.desktop {
    application {
        mainClass = "com.ecolix.MainKt"
        
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            
            packageName = "ÉcoliX"
            packageVersion = "1.0.0"
            description = "Solution de gestion scolaire moderne"
            vendor = "ÉcoliX Team"
            
            // Windows
            windows {
                iconFile.set(project.file("src/desktopMain/resources/icon.ico"))
                menuGroup = "ÉcoliX"
                perUserInstall = true
            }
            
            // macOS
            macOS {
                iconFile.set(project.file("src/desktopMain/resources/icon.icns"))
                bundleID = "com.ecolix.app"
            }
            
            // Linux
            linux {
                iconFile.set(project.file("src/desktopMain/resources/icon.png"))
            }
        }
    }
}
```

---

## 📝 Fichier `libs.versions.toml`

```toml
[versions]
kotlin = "2.0.21"
compose = "1.7.1"
agp = "8.2.2"
android-compileSdk = "34"
android-minSdk = "24"
android-targetSdk = "34"

ktor = "2.3.12"
kotlinx-coroutines = "1.8.1"
kotlinx-serialization = "1.7.1"
kotlinx-datetime = "0.6.1"
voyager = "1.1.0-beta02"
koin = "3.5.6"
sqldelight = "2.0.2"
kamel = "0.9.5"
multiplatform-settings = "1.1.1"
filekit = "0.8.2"
itext = "5.5.13.3"

[libraries]
# Ktor
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-cio = { module = "io.ktor:ktor-client-cio", version.ref = "ktor" }
ktor-client-okhttp = { module = "io.ktor:ktor-client-okhttp", version.ref = "ktor" }
ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktor" }
ktor-client-js = { module = "io.ktor:ktor-client-js", version.ref = "ktor" }
ktor-client-content-negotiation = { module = "io.ktor:ktor-client-content-negotiation", version.ref = "ktor" }
ktor-serialization-kotlinx-json = { module = "io.ktor:ktor-serialization-kotlinx-json", version.ref = "ktor" }
ktor-client-logging = { module = "io.ktor:ktor-client-logging", version.ref = "ktor" }
ktor-client-auth = { module = "io.ktor:ktor-client-auth", version.ref = "ktor" }

# Kotlinx
kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "kotlinx-coroutines" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinx-serialization" }
kotlinx-datetime = { module = "org.jetbrains.kotlinx:kotlinx-datetime", version.ref = "kotlinx-datetime" }

# Navigation (Voyager)
voyager-navigator = { module = "cafe.adriel.voyager:voyager-navigator", version.ref = "voyager" }
voyager-screenModel = { module = "cafe.adriel.voyager:voyager-screenmodel", version.ref = "voyager" }
voyager-transitions = { module = "cafe.adriel.voyager:voyager-transitions", version.ref = "voyager" }

# DI (Koin)
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-compose = { module = "io.insert-koin:koin-compose", version.ref = "koin" }
koin-android = { module = "io.insert-koin:koin-android", version.ref = "koin" }

# SQLDelight
sqldelight-runtime = { module = "app.cash.sqldelight:runtime", version.ref = "sqldelight" }
sqldelight-coroutines = { module = "app.cash.sqldelight:coroutines-extensions", version.ref = "sqldelight" }
sqldelight-sqlite-driver = { module = "app.cash.sqldelight:sqlite-driver", version.ref = "sqldelight" }
sqldelight-android-driver = { module = "app.cash.sqldelight:android-driver", version.ref = "sqldelight" }
sqldelight-native-driver = { module = "app.cash.sqldelight:native-driver", version.ref = "sqldelight" }

# Image Loading
kamel-image = { module = "media.kamel:kamel-image", version.ref = "kamel" }

# Settings
multiplatform-settings = { module = "com.russhwolf:multiplatform-settings", version.ref = "multiplatform-settings" }

# File Picker (Desktop)
filekit-compose = { module = "io.github.vinceglb:filekit-compose", version.ref = "filekit" }

# PDF Generation (Desktop)
itext-pdf = { module = "com.itextpdf:itextpdf", version.ref = "itext" }

# Android
androidx-core-ktx = { module = "androidx.core:core-ktx", version = "1.13.1" }
androidx-activity-compose = { module = "androidx.activity:activity-compose", version = "1.9.2" }

[plugins]
kotlin-multiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
compose = { id = "org.jetbrains.compose", version.ref = "compose" }
compose-compiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
android-application = { id = "com.android.application", version.ref = "agp" }
kotlinx-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

---

## 🎨 Point d'Entrée Desktop

### `desktopMain/kotlin/com/ecolix/Main.kt`

```kotlin
package com.ecolix

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.ecolix.di.initKoin
import com.ecolix.presentation.App

fun main() = application {
    // Initialiser Koin
    initKoin()
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "ÉcoliX - Gestion Scolaire",
        state = rememberWindowState(
            size = DpSize(1400.dp, 900.dp)
        )
    ) {
        App()
    }
}
```

---

## 🎯 App Principal (Common)

### `commonMain/kotlin/com/ecolix/App.kt`

```kotlin
package com.ecolix

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.ecolix.presentation.screens.auth.LoginScreen
import com.ecolix.presentation.theme.EcolixTheme

@Composable
fun App() {
    EcolixTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Navigator(LoginScreen()) { navigator ->
                SlideTransition(navigator)
            }
        }
    }
}
```

---

## 🎨 Theme Material 3

### `commonMain/kotlin/com/ecolix/presentation/theme/Theme.kt`

```kotlin
package com.ecolix.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Couleurs ÉcoliX
private val BluePrimary = Color(0xFF2563EB)      // Bleu confiance
private val OrangeSecondary = Color(0xFFF97316)  // Orange énergie
private val GreenAccent = Color(0xFF10B981)      // Vert réussite

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = OrangeSecondary,
    tertiary = GreenAccent,
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1E293B),
    onSurface = Color(0xFF1E293B)
)

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    secondary = OrangeSecondary,
    tertiary = GreenAccent,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9)
)

@Composable
fun EcolixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

---

## 📊 Exemple Écran (Dashboard)

### `commonMain/kotlin/com/ecolix/presentation/screens/dashboard/DashboardScreen.kt`

```kotlin
package com.ecolix.presentation.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

class DashboardScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<DashboardScreenModel>()
        val state by screenModel.state.collectAsState()
        
        Scaffold(
            topBar = { DashboardTopBar() },
            bottomBar = { DashboardBottomNav() }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "Tableau de Bord",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                
                item {
                    StatsRow(state)
                }
                
                item {
                    AlertsCard(state.alerts)
                }
                
                item {
                    QuickActionsCard()
                }
            }
        }
    }
}

@Composable
private fun StatsRow(state: DashboardState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Élèves",
            value = state.totalEleves.toString(),
            icon = Icons.Default.School,
            color = MaterialTheme.colorScheme.primary
        )
        
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Présents",
            value = "${state.tauxPresence}%",
            icon = Icons.Default.CheckCircle,
            color = MaterialTheme.colorScheme.tertiary
        )
        
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Paiements",
            value = "${state.tauxPaiement}%",
            icon = Icons.Default.Payment,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = color
            )
        }
    }
}
```

---

**Voulez-vous que je continue avec :**
1. ScreenModel (ViewModel) pattern
2. Repository + API Client complet
3. SQLite local configuration
4. Navigation setup détaillé
5. Un module complet (ex: Gestion Élèves) ?
