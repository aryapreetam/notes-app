<div align="center">
  <img src="composeApp/src/commonMain/composeResources/drawable/my_notes_icon.png" alt="Notes App Icon" width="120" height="120" />

  <h1>Notes App</h1>
</div>

---

## ✨ Brief Description

<strong>Notes App</strong> is a personal note-taking application built with Compose Multiplatform. Effortlessly create,
edit, and organize your notes—each note includes a title, body (HTML supported), and created date. Fast, beautiful, and
works on Android, iOS, and Desktop(Only for development purposes).

---

## 🎥 Demo

- 📹 [Demo Video](#) <!-- REPLACE WITH ACTUAL DEMO LINK -->
- 🖼️ [Screenshots](#) <!-- REPLACE WITH ACTUAL SCREENSHOT LINKS -->

---

## 🗂️ Project Structure & Architecture

### Directory Structure (Key parts)

```text
composeApp/src/commonMain/kotlin/org/notesapp/
├── data/               // DB, models, repository impls
├── domain/             // UseCases for business logic
├── presentation/       // Composables, navigation, UI state, screens
├── di/                 // Koin modules
├── theme/              // Material3 expressive themes
├── utils/              // Utilities/helpers (date, platform)
```

### Architecture Diagram

```mermaid
```mermaid
flowchart TD
    %% style and curve improvements
    %%{init: {'flowchart': {'curve': 'monotoneY'}} }%%

    %%--- LAYER: Platforms ---
    subgraph Platforms
      direction LR
      ANDROID("Android")
      IOS("iOS")
      DESKTOP("Desktop")
    end

    %%--- LAYER: Compose UI ---
    UI["Compose Multiplatform UI"]:::ui
    NAV["NavHost/<br/>Type-safe Sealed Screens"]:::ui

    %%--- LAYER: ViewModels ---
    VM1["NotesListViewModel"]:::vm
    VM2["CreateNoteViewModel"]:::vm

    %%--- LAYER: Feature Screens/Interops ---
    PDF["PDF Screen/<br/>Viewer"] 
    HTML["WebView HTML Renderer<br/>+ JS Bridge"]

    %%--- LAYER: UseCases ---
    UC1["CreateNoteUseCase"]:::service
    UC2["DeleteNoteUseCase"]:::service
    UC3["GetNotesUseCase"]:::service
    UC4["ValidateHtmlUseCase"]:::service

    %%--- LAYER: Repository+Database ---
    REPO["NotesRepository"]:::data
    DB["SQLDelight Database + Driver"]:::data

    %%--- LAYER: DI and Logging ---
    KOIN["Koin DI"]:::service
    KER["Kermit<br/>Logging"]:::service

    %%--- Platforms to Compose UI ---
    ANDROID-->|"Compose UI"|UI
    IOS-->|"Compose UI"|UI
    DESKTOP-->|"Compose UI"|UI

    %%--- Compose UI connections ---
    UI-->|"Navigation events"|NAV
    UI-->|"Triggers"|VM1
    UI-->|"Triggers"|VM2
    UI-->|"PDF Open"|PDF
    UI-->|"HTML View"|HTML

    %%--- ViewModels connections ---
    VM1-->|"State/Actions"|UC3
    VM1-->|"State/Actions"|UC2
    VM2-->|"State/Actions"|UC1
    VM2-->|"Validation"|UC4

    %%--- UseCases to Repo ---
    UC1-->|"Repo"|REPO
    UC2-->|"Repo"|REPO
    UC3-->|"Repo"|REPO

    %%--- Repo to DB ---
    REPO-->|"SQL"|DB

    %%--- Dependency Injection (dotted) ---
    KOIN-.->VM1
    KOIN-.->VM2
    KOIN-.->UC1
    KOIN-.->UC2
    KOIN-.->UC3
    KOIN-.->UC4
    KOIN-.->REPO

    %%--- Logging connections ---
    KER-->|"Logs"|VM1
    KER-->|"Logs"|VM2
    KER-->|"Logs"|REPO
    KER-->|"Logs"|UI

    %%--- Interop connections (HTML/PDF) ---
    HTML-->|"onClick events"|VM1
    PDF-->|"View Callback"|UI

    %%--- Invisible links for alignment ---
    VM1 ~~~ VM2
    UC2 ~~~ UC3 ~~~ UC1 ~~~ UC4

    %%--- Node Groups by class ---
    classDef service fill:#ECEFFF,stroke:#a9a9c3,color:#000,font-weight:bold;
    classDef data fill:#F6F7F8,stroke:#bbb,color:#232,font-weight:bold;
    classDef vm fill:#DCF5E6,stroke:#3B7C5D,color:#036,font-weight:bold;
    classDef ui fill:#FFF8DC,stroke:#CA8727,color:#652,font-weight:bold;
    
    %%--- Testing/E2E Layer ---
    subgraph Testing [Testing/E2E]
      direction LR
      TEST1["Unit/<br/>Integration"]
      TEST2["UI Compose Tests"]
      TEST3["Maestro E2E"]
    end
    TEST1-->|"Covers"|UC1
    TEST2-->|"Covers"|UI
    TEST3-->|"Covers"|ANDROID
    TEST3-->|"Covers"|IOS

    %%--- Visually group KOIN/KER on left ---
    KOIN ~~~ KER

    %%--- Classes ---
    UI:::ui
    NAV:::ui
    VM1:::vm
    VM2:::vm
    REPO:::data
    DB:::data
    KOIN:::service
    KER:::service
    UC1:::service
    UC2:::service
    UC3:::service
    UC4:::service
```

---

## 🏛️ Architecture Explanation

- <strong>Multiplatform MVVM:</strong> Dumb/reactive UI with ViewModels driving all state and business logic,
  independently testable.
- <strong>Navigation:</strong> Type-safe sealed class screens, custom NavHost composable.
- <strong>Dependency Injection:</strong> Koin modules (per layer: db, repo, usecase, viewmodel) for scalable, testable
  design.
- <strong>Data:</strong> SQLDelight for strongly-typed, multiplatform SQLite with platform-specific drivers.
- <strong>UI:</strong> Material3 Expressive with custom themes/components. Pure Compose with state as single source of
  truth.
- <strong>HTML & PDF:</strong> Rich-text body using WebView widgets and PDF Viewer component.
- <strong>Testing:</strong> Unit, UI Compose, and Maestro E2E (planned, see TODO).
- <strong>Logging:</strong> Kermit logger everywhere, no PII.
- <strong>Platform abstraction:</strong> actual/expect code for database drivers, date formatting, and native
  integrations.

---

## 🚀 Installation & Usage

### Prerequisites

- JDK 17+
- Android Studio OR Xcode (for iOS)
- [KDoctor](https://github.com/Kotlin/kdoctor) for KMP environment check
- Android SDK path set in `local.properties`

### Android

- Open the project in Android Studio
- Run on device/emulator using run configuration
- Build APK: `./gradlew :composeApp:assembleDebug` (APK in `composeApp/build/outputs/apk/debug/composeApp-debug.apk`)
- Run UI tests: `./gradlew :composeApp:connectedDebugAndroidTest`

### Desktop

- Run app: `./gradlew :composeApp:run`
- Hot reload: `./gradlew :composeApp:hotRunJvm`
- Run desktop UI tests: `./gradlew :composeApp:jvmTest`

### iOS

- Open `iosApp/iosApp.xcproject` in Xcode, run standard configuration
- Or use the [KMM Plugin for Android Studio](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)
- Run iOS UI tests: `./gradlew :composeApp:iosSimulatorArm64Test`

---

## 🧪 Running Tests

- <strong>Unit tests (domain, repository, utils):</strong>
    - `./gradlew :composeApp:commonTest`
- <strong>UI Compose tests:</strong>
    - Android: `./gradlew :composeApp:connectedDebugAndroidTest`
    - Desktop: `./gradlew :composeApp:jvmTest`
- <strong>End-to-End (Maestro):</strong>
    - (Setup Maestro CLI, then run E2E scripts—see `docs/implementation_plan.md` for planned flows)

---

## 📚 Libraries Used

- <strong>UI:</strong> JetBrains Compose Multiplatform, Material3 Expressive
- <strong>Dependency Injection:</strong> Koin (core, compose, viewmodel)
- <strong>Database:</strong> SQLDelight (core, coroutines, multiplatform drivers)
- <strong>Testing:</strong> Kotlinx Test, Compose UI Test, Maestro (E2E planned), Mockk, Turbine
- <strong>Platform Integration:</strong> Compose WebView Multiplatform, Bouquet (PDF), kotlinx.datetime
- <strong>Logging:</strong> Kermit

---

## ⬇️ How HTML Clicks and PDF Are Handled

- <strong>HTML Body/Clicks:</strong> Notes can include HTML body, displayed in a Compose WebView. Clicks inside HTML (
  links, buttons) are handled with a custom JS Bridge (see `NoteCard.kt`), which sends events from the WebView back to
  the ViewModel/UI for further actions. Only allow-listed tags are accepted.
- <strong>PDF Viewing:</strong> Notes, or screens, can open PDF links in a dedicated PDF Viewer screen/component.
  Currently, Android PDF UX may be improved—see TODOs below.

---

## 📝 To Be Done

1. Comprehensive unit, integration (UI), and Maestro end-to-end tests
2. Enhanced form and field validations (especially for HTML/body)
3. Cross-platform PDF renderer improvements (especially on Android—smoother navigation/UI)
4. Accessible and localized error messages (validation and network/database)
5. iOS/desktop performance optimizations for large notes
6. Documentation expansion, including demo links/screenshots and E2E test setup guide

---

Happy note-taking! 🚀
