package com.davealone69.githubboss.data

/**
 * Offline help coach for GitHub-Boss-Pro, GitHub, Android, and Termux.
 * Answers common questions without a network/API key.
 * Gemini can upgrade answers when a free key is present.
 */
object HelpCoach {

    data class HelpAnswer(
        val title: String,
        val body: String,
        val termuxCommands: List<TermuxSnippet> = emptyList()
    )

    data class TermuxSnippet(
        val label: String,
        val command: String
    )

    fun answer(question: String): HelpAnswer {
        val q = question.trim().lowercase()
        if (q.isBlank()) {
            return HelpAnswer(
                title = "Ask anything",
                body = """
Examples:
- How do I log in?
- What PAT scopes do I need?
- How do I build an APK in Termux?
- How do I install the debug APK?
- How does the Builder work?
- How do I get a free Gemini key?
                """.trimIndent()
            )
        }

        return when {
            q.contains("login") || q.contains("pat") || q.contains("token") || q.contains("sign in") ->
                HelpAnswer(
                    title = "GitHub login (PAT)",
                    body = """
1. Open GitHub → Settings → Developer settings → Personal access tokens.
2. Create a token (classic) with scopes: repo, workflow, read:user, notifications.
3. Paste it on the login screen in GitHub-Boss-Pro.
4. Token is stored only on this device (not in git).

If login fails with 401: token is wrong or missing scopes.
If 403: rate limit or org SSO — authorize the token for the org.
                    """.trimIndent(),
                    termuxCommands = listOf(
                        TermuxSnippet("Open token page (browser)", "termux-open-url https://github.com/settings/tokens")
                    )
                )

            q.contains("scope") ->
                HelpAnswer(
                    title = "Required PAT scopes",
                    body = """
Minimum scopes for this app:
- repo — list/create repos, issues
- workflow — Actions / workflow runs
- read:user — your profile
- notifications — notification list

Fine-grained tokens: grant access to the repos you need plus the same permissions.
                    """.trimIndent()
                )

            q.contains("builder") || q.contains("generate") || q.contains("kotlin") || q.contains("code maker") ->
                HelpAnswer(
                    title = "Builder (Text → Kotlin)",
                    body = """
Builder tab turns a text prompt into Kotlin/Compose files.

Modes:
- Full app guide — architecture, steps, Termux tips, starter code
- Code only — screen + ViewModel + stubs

Offline template always works. Optional free Gemini key makes smarter output.
Copy files from the explorer into your project or paste into Termux editors.
                    """.trimIndent()
                )

            q.contains("gemini") || q.contains("api key") || q.contains("llm") || q.contains("ai") ->
                HelpAnswer(
                    title = "Free Gemini key",
                    body = """
1. Open https://aistudio.google.com/apikey
2. Create an API key (free tier).
3. In Builder → API key → paste → Save.

No key needed for template generation or this offline help.
If Gemini fails (quota/rate limit), the app falls back to the free template.
                    """.trimIndent(),
                    termuxCommands = listOf(
                        TermuxSnippet("Open AI Studio", "termux-open-url https://aistudio.google.com/apikey")
                    )
                )

            q.contains("termux") || q.contains("adb") || q.contains("install") || q.contains("apk") ->
                HelpAnswer(
                    title = "Termux + APK tips",
                    body = """
Typical flow on phone:
1. Build debug APK with Gradle (in project folder).
2. Install with adb (USB or wireless debugging).
3. Watch logs with logcat.

Enable wireless debugging on Android (Developer options) if you have no cable.
Keep keystores and tokens out of git.
                    """.trimIndent(),
                    termuxCommands = listOf(
                        TermuxSnippet("Build debug APK", "./gradlew assembleDebug"),
                        TermuxSnippet("Install APK", "adb install -r app/build/outputs/apk/debug/*.apk"),
                        TermuxSnippet("Logcat (filter)", "adb logcat | grep -i github"),
                        TermuxSnippet("List devices", "adb devices"),
                        TermuxSnippet("Wireless pair (example)", "adb pair 192.168.1.10:XXXXX")
                    )
                )

            q.contains("build") || q.contains("gradle") || q.contains("wrapper") ->
                HelpAnswer(
                    title = "Gradle build",
                    body = """
From the project root:

./gradlew assembleDebug
./gradlew bundleRelease

If gradlew fails because wrapper jar is missing, restore it once (see docs/WRAPPER.md).
CI on GitHub Actions can also build APK/AAB for this repo.
                    """.trimIndent(),
                    termuxCommands = listOf(
                        TermuxSnippet("Debug APK", "./gradlew assembleDebug"),
                        TermuxSnippet("Release bundle", "./gradlew bundleRelease"),
                        TermuxSnippet("Clean", "./gradlew clean")
                    )
                )

            q.contains("notification") || q.contains("issue") || q.contains("workflow") || q.contains("action") ->
                HelpAnswer(
                    title = "GitHub features in the app",
                    body = """
Currently in the UI:
- Login + repo list + refresh + logout
- Builder (code + full app guide)
- Help (this coach) + Termux snippets

Already in the API layer (ready to wire to UI later):
- Issues, notifications, workflow runs, search, create repo, star/unstar

Ask for a specific screen if you want it next.
                    """.trimIndent()
                )

            q.contains("secret") || q.contains("security") || q.contains("keystore") ->
                HelpAnswer(
                    title = "Secrets & signing",
                    body = """
Never commit:
- PAT / Gemini keys
- keystore files (.jks / .keystore)
- passwords

Local: put KEYSTORE_* in a gitignored gradle.properties.
CI: use GitHub Actions secrets (see docs/SIGNING.md).
                    """.trimIndent()
                )

            q.contains("help") || q.contains("what can") || q.contains("how do i use") ->
                HelpAnswer(
                    title = "What this app does",
                    body = """
GitHub-Boss-Pro is a phone-first tool for GitHub + Termux.

Tabs:
- Repos — sign in, browse your repositories
- Builder — text → Kotlin + optional full app guide
- Help — ask questions; get Termux commands you can copy

Describe what you want to do in plain language here.
                    """.trimIndent()
                )

            else -> HelpAnswer(
                title = "Help",
                body = """
I matched a general answer for:
"$question"

Try more specific prompts:
- login / PAT / scopes
- builder / generate Kotlin
- gemini / free API key
- termux / adb / install apk
- gradle / build
- secrets / keystore

Or enable Gemini in Builder for a free custom answer to any question.
                """.trimIndent(),
                termuxCommands = listOf(
                    TermuxSnippet("Project status (git)", "git status"),
                    TermuxSnippet("Build debug", "./gradlew assembleDebug")
                )
            )
        }
    }

    fun geminiHelpSystemPrompt(): String = """
You are the in-app help coach for GitHub-Boss-Pro, a phone-first Android app for GitHub + Termux.

Answer the user's question clearly and briefly.
If Termux/shell commands help, list them as:

### FILE: termux/commands.sh
```bash
# label: description
command here
```

Also you may output:

### FILE: docs/HELP_ANSWER.md
```markdown
(your answer)
```

No fluff. Prefer copy-paste ready commands. No secrets.
    """.trimIndent()
}
