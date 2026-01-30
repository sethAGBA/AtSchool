```
ecolix/
├── ecolix-backend/ # Backend Ktor
│ └── src/main/kotlin/com/ecolix/api/
├── ecolix-desktop/ # Desktop Compose
│ └── src/main/kotlin/com/ecolix/desktop/
├── ecolix-web/ # Web Compose
│ └── src/main/kotlin/com/ecolix/web/
├── ecolix-mobile/ # Mobile Compose
│ └── src/main/kotlin/com/ecolix/mobile/
├── ecolix-shared/ # Code commun
│ └── src/commonMain/kotlin/com/ecolix/shared/
└── README.md
```

## ✅ Configuration Projet Compose Multiplatform
### Structure de Projet Complète
```
ecolix/
├── composeApp/ │ ├── src/
│ │ ├── commonMain/ │ │ │ ├── kotlin/com/ecolix/
│ │ │ │ ├── App.kt │ │ │ │ ├── di/ │ │ │ │ ├── data/
# App principale (Desktop/Mobile/Web)
# Code partagé (70-80%)
# Point d'entrée UI commun
# Dependency Injection (Koin)
│ │ │ │ │ ├── models/ # Data classes
│ │ │ │ │ ├── repository/ # Repository pattern
│ │ │ │ │ ├── local/ # SQLite local
│ │ │ │ │ └── remote/ # Ktor Client API
│ │ │ │ ├── domain/ # Business logic
│ │ │ │ │ ├── usecases/
│ │ │ │ │ └── entities/
│ │ │ │ ├── presentation/ # UI Layer
│ │ │ │ │ ├── screens/
│ │ │ │ │ │ ├── dashboard/
│ │ │ │ │ │ ├── eleves/
│ │ │ │ │ │ ├── notes/
│ │ │ │ │ │ ├── paiements/
│ │ │ │ │ │ └──
...
│ │ │ │ │ ├── components/ # Composables réutilisables
│ │ │ │ │ ├── navigation/
│ │ │ │ │ ├── theme/
│ │ │ │ │ └── viewmodels/
│ │ │ │ └── utils/
│ │ │ └── resources/ # Images, strings, etc.
│ │ │
│ │ ├── desktopMain/ # Code spécifique Desktop
│ │ │ └── kotlin/com/ecolix/
│ │ │ ├── Main.kt # Point d'entrée Desktop
│ │ │ └── platform/ # Platform-specific code
│ │ │
│ │ ├── androidMain/ # Code spécifique Android
│ │ │ └── kotlin/com/ecolix/
│ │ │ └── MainActivity.kt
│ │ │
│ │ ├── iosMain/ │ │ │ └── kotlin/com/ecolix/
│ │ │
│ │ └── wasmJsMain/ │ │ └── kotlin/com/ecolix/
│ │
│ └── build.gradle.kts
│
├── server/ │ └── src/main/kotlin/com/ecolix/api/
│
├── gradle/
├── gradle.properties
├── settings.gradle.kts
└── build.gradle.kts
```