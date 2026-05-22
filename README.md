# Norton 360 Dashboard - AI-First Intern Assignment

## Project Overview
This project is an implementation of the **Security Health Dashboard (Option A)** for the Norton AI-First Internship. The application simulates a security scan of an Android device, displaying an overall health score and specific security category results (OS Version, App Threats, Wi-Fi Safety, and Password Strength) using a modern, reactive UI built with Jetpack Compose.

**Key Features:**
- **Incremental Scan Simulation**: Results appear one-by-one to mimic a real-world scanning process.
- **Dynamic Gauge UI**: A responsive button element that acts as both a progress indicator and the primary scan trigger.
- **State-Driven Design**: The UI responds fluidly to Idle, Scanning, Completed, and Error states.
- **Modern Android Stack**: Built with Kotlin, Jetpack Compose, Coroutines, Flow, and Hilt DI.

## Setup Instructions
To build and run the project locally:
1.  **Clone the repository**: `git clone https://github.com/Jindrasko/norton-aifirst-intern-jindrich-spacek.git`
2.  **Open in Android Studio**: Use the latest version of Android Studio (Ladybug or newer recommended).
3.  **Sync Gradle**: Allow the project to download dependencies (Hilt, Compose Material 3, Turbine, etc.).
4.  **Run the App**: Select a device/emulator (API 29+) and click the **Run** button.
5.  **Run Tests**: Execute `./gradlew test` or run the tests in `app/src/test` via the IDE to verify logic.

## Screenshots of the running app

<img width="570" height="1143" alt="Idle State" src="https://github.com/user-attachments/assets/70042b27-5623-4de7-9a3d-4b16e414d737" />
<img width="574" height="1149" alt="Scanning State" src="https://github.com/user-attachments/assets/ab7ffca5-0865-4280-aeaf-a1af19e6d664" />
<img width="567" height="1142" alt="Completed State" src="https://github.com/user-attachments/assets/4e4fd916-4a6e-456b-b2f4-74bc8cf7b479" />

---

## AI Interaction Log

In this project, I used a multi-agent AI strategy. I utilized a separate AI agent (GPT-4.1) to help me "engineer" detailed, structured prompts which I then fed into the Android Studio AI (Gemini) to execute the code changes. This allowed for very specific architectural requirements to be met with minimal trial and error.

### 1. Data Layer & Repository Foundation
**Prompt:**
> "Act as a Senior Android Engineer. I am building a 'Security Health Dashboard' feature for an Android app using Kotlin and Jetpack Compose. I need you to generate the Data Layer and Mock Repository for this feature... Create an enum class called SecurityStatus with three states: SAFE, WARNING, and CRITICAL... Create a data class SecurityCategory... Create an interface SecurityRepository with a single function: fun performSecurityScan(): Flow<ScanState>... Mock implementation: Use Kotlin Coroutines (delay) to emit ScanState.Scanning updates (e.g., 0, 20, 50, 80, 100) with a slight delay... categories list MUST contain exactly these 4 checks: OS Version, App Threats, Wi-Fi Safety, Password Strength."

**Result & Commentary:**
The AI correctly implemented the Repository pattern and modeled the data cleanly. I initially used this simple progress emission, but I later changed it so categories appeared incrementally for better scan progress visualization, leading to a follow-up refinement prompt.

### 2. UI Button Element & Gauge Logic
**Prompt:**
> "Generate production-quality Jetpack Compose UI code... Screen Layout: SecurityDashboardScreen that fills the whole display... Score Gauge Area (Top ~33%)... circular gauge (ring) with: a background track (light gray) and a progress arc that fills based on scan progress (0–100)... Inside the gauge: During scanning: show 'Scanning...' and progress number (0–100) %... When completed: show overallScore."

**Result & Commentary:**
The AI created `SecurityScoreGauge.kt` using `Canvas`. It implemented a `rememberInfiniteTransition` for a spinning animation while scanning.
**Refinement:** I had to provide a subsequent prompt to scale the gauge using `BoxWithConstraints` because it was initially too small. I also moved the primary action button *inside* the gauge to act as the "Start Scan" trigger. Initially, the button for the scan start was a lot smaller than the gauge and was an oval shape, so in a later prompt, I made it circular to fit the same style as the gauge.

### 3. Realistic Incremental Scan Simulation
**Prompt:**
> "I want a more realistic scan simulation: While scanning is running, the repository should emit incremental category results so the grid cards get filled one-by-one in sequence (like a real scan)... category #1 result arrives early (e.g., at 25%), #2 at 50%, #3 at 75%, #4 near the end (100%)... UI should show partial results immediately."

**Result & Commentary:**
This required the AI to update the `ScanState` model and the `MockSecurityRepositoryImpl`. The AI successfully managed the merging of 'Idle' state placeholders with 'Completed' results in the UI grid. Importantly, the `ScanState` model had to be changed so it can contain a list of categories even when scanning and not only in the completed state.

### 4. Dependency Injection & Unit Testing
**Prompt:**
> "Remove ViewModel factory; implement Hilt DI... Add Hilt Gradle setup... Create an Application class @HiltAndroidApp... Annotate MainActivity with @AndroidEntryPoint... Provide the repository binding via a Hilt module... Create 4 meaningful unit tests total: 2 for the ViewModel, 2 for the data models... Use JUnit 5, kotlinx-coroutines-test, and Turbine."

**Result & Commentary:**
The agent initially implemented a `ViewModelProvider.Factory` to handle ViewModel creation. However, in a later prompt, I demanded the use of Dependency Injection using Hilt as a better solution, which the AI then implemented perfectly. For testing, it used `mock()` from Mockito to simulate repository behavior.

### 5. Visual Bug Fixing & Final Polish
**Prompt:**
> "There is a problem with cards after the scan, where shadow is inside cards like on the image... cards need to fit 2 lines of description text... include mock bottom panel as shown on image."

**Result & Commentary:**
The shadow issue was caused by semi-transparent status colors allowing the card shadow to bleed through. The AI suggested using `compositeOver(Color.White)` to make the container opaque, which fixed the visual bug immediately.
**Final Result:** The app now matches the visual design and behavioral requirements perfectly.

**Overall AI Interaction Note:**
The AI Agent often had problems with the correct sizes of components, texts, and paddings. It also frequently introduced new hex colors instead of strictly using the colors defined in the theme resources. I often fixed these manually to ensure the UI matched my design exactly.

---

## AI Code Review Summary

I requested a "candid" code review from the AI acting as a Senior Engineer.

### What the AI suggested:
Code Review: Security Health Dashboard
High-Level Summary
- Strengths: Clean separation of concerns with Hilt, robust state modeling using Sealed Interfaces, and excellent use of modern Compose APIs like BoxWithConstraints for responsive design.
- Key Risks: Lack of protection against concurrent coroutine execution (multiple scans), missing accessibility semantics for custom UI components, and hardcoded "magic numbers" for progress and layouts.
- Overall: The architecture is solid and follows "Modern Android Development" (MAD) best practices, but requires a few "polish" changes to be truly production-ready.
A) Architecture & State Management
- Concurrent Scans (Important): viewModel.startScan() does not check if a scan is already in progress. In a real-world scenario, or even with rapid clicks, this could launch multiple concurrent Flows.
   - Fix: Add a guard clause: if (_uiState.value is SecurityUiState.Scanning) return@launch.
- State Redundancy (Nice-to-have): idleCategories is defined in the Composable. While remembered, it’s technically "data" that would better reside in a Domain layer or as a const in the ViewModel to ensure the UI remains a pure "view" of the state.
B) Coroutines / Flow Correctness
- Error Handling (Important): The Flow collection in the ViewModel lacks a .catch {} block. If the repository ever throws an exception (e.g., simulated "Device Busy"), the app scope might stay in an inconsistent state or crash.
   - Fix: Map exceptions to a SecurityUiState.Error state.
- Dispatcher Injection (Nice-to-have): The ViewModel uses viewModelScope (Default/Main). For production, you should inject a CoroutineDispatcher to make the ViewModel 100% testable without setMain.
C) Compose UI Quality
- Magic Numbers (Important): Heights like 100.dp in SecurityCategoryTile and 0.33f in the screen layout are hardcoded.
   - Fix: Move these to a DashboardDimensions object or use Modifier.weight() for the grid to ensure it takes up exactly the remaining space regardless of the gauge size.
- Hardcoded Colors (Nice-to-have): While Norton Yellow is defined in the theme, status colors like Color(0xFF4CAF50) appear in the Gauge logic.
   - Fix: Centralize all brand colors in app/src/main/java/cz/jindrichspacekv/norton360_dashboard/ui/theme/Color.kt.
D) Accessibility & UX Polish
- Gauge Semantics (Important): The Gauge is a Button but uses a custom Canvas. TalkBack will see a button but won't inherently know it represents a progress bar or a specific score.
   - Fix: Use Modifier.semantics { progressBarRangeInfo = ProgressBarRangeInfo(current, range) } on the Gauge.
- Contrast (Nice-to-have): The Primary Yellow (#FEEC2A) with White text is a common Norton branding challenge. Ensure onPrimary is strictly Black (#000000) for WCAG compliance.
E) Code Style & Maintainability
- Naming (Nice-to-have): isIdle in SecurityCategoryTile is slightly misleading since it's also true during the "Scanning" phase for that specific card.
   - Fix: Rename to isResultsVisible or isAnalyzed.
- String Resources (Important): "Scan Now", "Overall Score", etc., are hardcoded strings.
   - Fix: Move to res/strings.xml to support localization (essential for a global brand like Norton).
F) DI / Hilt
- Repository Binding (Correct): Using @Binds in an abstract AppModule is the correct, performant way to handle interface-to-implementation mapping.
- Scope (Correct): SingletonComponent for the Mock Repository is appropriate for a global state simulation.
G) Testing
- Determinism (Important): The ViewModel test uses flowOf, which is deterministic. However, if the MockRepository used actual delay(), the tests would need advanceTimeBy() to be efficient.
- Missing Edge Case (Important): There is no test for the "Re-scan" scenario (calling startScan twice).

### What I changed:
- **Implemented Scan Guard**: Added a guard clause in `SecurityDashboardViewModel` to ignore requests if state is already `Scanning`.
- **Error Handling**: Added `SecurityUiState.Error` and implemented a `.catch` block in the ViewModel. Added an error UI with a **Retry** button.
- **Localization**: Moved 100% of UI strings to `strings.xml`.

---

## Reflection

### What did I learn?
- **Prompt Engineering as a Skill**: I learned that the quality of AI output is directly proportional to the structural detail of the prompt. Using a "meta-agent" to write prompts for the "coding agent" is a highly effective workflow.

### What would I do differently?
- **Theme Centralization**: I initially hardcoded some brand colors in local Composables; next time, I would strictly define all brand tokens in `Theme.kt` first to avoid search-and-replace later.
- **Comprehensive Initial Prompt**: I would specify from the very beginning that the agent should implement error handling, use string resources, and strictly use defined colors instead of introducing new hex colors. I would also use AI agent rules to specify these practices upfront.



Video Demo: https://drive.google.com/file/d/149LQYb8kiWIRKOMtRYTG7Yq5Q54NhUep/view?usp=sharing 
