# Repository Guidelines

## Project Structure & Module Organization
- `composeApp/`: Compose Multiplatform UI and platform-specific entry points. Shared UI code lives in `composeApp/src/commonMain`, with targets under `androidMain`, `iosMain`, `jvmMain`, and `webMain`.
- `shared/`: Kotlin Multiplatform shared logic in `shared/src/commonMain` with platform-specific subfolders as needed.
- `server/`: Ktor server in `server/src/main/kotlin` with tests in `server/src/test/kotlin`.
- `iosApp/`: Xcode project and SwiftUI entry points for iOS (`iosApp/iosApp`).
- Generated/build outputs appear under `build/`, `composeApp/build/`, and `shared/build/` and should not be edited manually.

## Build, Test, and Development Commands
- Android debug APK: `./gradlew :composeApp:assembleDebug`
- Desktop (JVM) app run: `./gradlew :composeApp:run`
- Server run: `./gradlew :server:run`
- Web (Wasm) dev run: `./gradlew :composeApp:wasmJsBrowserDevelopmentRun`
- Web (JS) dev run: `./gradlew :composeApp:jsBrowserDevelopmentRun`
- Run all tests (if configured): `./gradlew test`
- Module tests: `./gradlew :server:test`, `./gradlew :shared:test`, `./gradlew :composeApp:test`

## Coding Style & Naming Conventions
- Kotlin sources follow standard Kotlin style (4-space indentation, trailing commas where used). Match the surrounding file style.
- Package naming follows `com.example.atschool` (see `shared/src/commonMain/kotlin`).
- Keep platform-specific code in the appropriate `*Main` source set rather than conditional logic in common code.

## Testing Guidelines
- Server tests: `server/src/test/kotlin` using Ktor test host and JUnit.
- Shared/common tests: `shared/src/commonTest/kotlin` and `composeApp/src/commonTest/kotlin` using Kotlin test libraries.
- Name tests by feature or class under test (e.g., `ApplicationTest`, `SharedCommonTest`).

## Commit & Pull Request Guidelines
- No Git history or commit conventions are available in this repository. If you add commits, use clear, imperative messages (e.g., "Add web entry point for compose app").
- For PRs, include a concise description, linked issues if applicable, and screenshots or recordings for UI changes.

## Configuration Notes
- Local Android SDK paths are typically stored in `local.properties` and should not be committed if they contain user-specific paths.
- iOS configuration lives in `iosApp/Configuration/Config.xcconfig` and Xcode project files under `iosApp/iosApp.xcodeproj`.
