package com.davealone69.githubboss.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.davealone69.githubboss.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data object Authenticating : AuthState()
    data class Authenticated(val user: GitHubUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

data class CodeGenResult(
    val files: List<GeneratedFile>,
    val source: String,
    val error: String? = null
)

class GitHubViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)
    private val gitHubRepo = GitHubRepository()
    private val geminiService = GeminiCodeService()
    private val tipsStore = CodingTipsStore(application)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _reposState = MutableStateFlow<ApiResult<List<GitHubRepo>>>(ApiResult.Success(emptyList()))
    val reposState: StateFlow<ApiResult<List<GitHubRepo>>> = _reposState.asStateFlow()

    private val _issuesState = MutableStateFlow<ApiResult<List<GitHubIssue>>>(ApiResult.Success(emptyList()))
    val issuesState: StateFlow<ApiResult<List<GitHubIssue>>> = _issuesState.asStateFlow()

    private val _workflowRunsState = MutableStateFlow<ApiResult<List<GitHubWorkflowRun>>>(ApiResult.Success(emptyList()))
    val workflowRunsState: StateFlow<ApiResult<List<GitHubWorkflowRun>>> = _workflowRunsState.asStateFlow()

    private val _notificationsState = MutableStateFlow<ApiResult<List<GitHubNotification>>>(ApiResult.Success(emptyList()))
    val notificationsState: StateFlow<ApiResult<List<GitHubNotification>>> = _notificationsState.asStateFlow()

    private val _searchState = MutableStateFlow<ApiResult<List<GitHubRepo>>>(ApiResult.Success(emptyList()))
    val searchState: StateFlow<ApiResult<List<GitHubRepo>>> = _searchState.asStateFlow()

    private val _selectedRepo = MutableStateFlow<GitHubRepo?>(null)
    val selectedRepo: StateFlow<GitHubRepo?> = _selectedRepo.asStateFlow()

    private val _repoCreationState = MutableStateFlow<ApiResult<GitHubRepo>?>(null)
    val repoCreationState: StateFlow<ApiResult<GitHubRepo>?> = _repoCreationState.asStateFlow()

    private val _codeGenState = MutableStateFlow<ApiResult<CodeGenResult>?>(null)
    val codeGenState: StateFlow<ApiResult<CodeGenResult>?> = _codeGenState.asStateFlow()

    private val _hasGeminiKey = MutableStateFlow(tokenManager.hasGeminiKey())
    val hasGeminiKey: StateFlow<Boolean> = _hasGeminiKey.asStateFlow()

    private val _helpState = MutableStateFlow<HelpCoach.HelpAnswer?>(null)
    val helpState: StateFlow<HelpCoach.HelpAnswer?> = _helpState.asStateFlow()

    private val _tipsState = MutableStateFlow<List<CodingTipsStore.Tip>>(emptyList())
    val tipsState: StateFlow<List<CodingTipsStore.Tip>> = _tipsState.asStateFlow()

    init {
        checkSavedToken()
        refreshTips()
    }

    fun checkSavedToken() {
        val savedToken = tokenManager.getToken()
        if (!savedToken.isNullOrBlank()) {
            loginWithToken(savedToken)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun loginWithToken(patToken: String) {
        val cleanToken = patToken.trim()
        if (cleanToken.isBlank()) {
            _authState.value = AuthState.Error("Please enter a valid GitHub Personal Access Token")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Authenticating
            when (val result = gitHubRepo.getAuthenticatedUser(cleanToken)) {
                is ApiResult.Success -> {
                    tokenManager.saveToken(cleanToken)
                    _authState.value = AuthState.Authenticated(result.data)
                    refreshData()
                }
                is ApiResult.Error -> {
                    _authState.value = AuthState.Error(result.message)
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun logout() {
        tokenManager.clearToken()
        _authState.value = AuthState.Unauthenticated
        _reposState.value = ApiResult.Success(emptyList())
        _issuesState.value = ApiResult.Success(emptyList())
        _workflowRunsState.value = ApiResult.Success(emptyList())
        _notificationsState.value = ApiResult.Success(emptyList())
        _searchState.value = ApiResult.Success(emptyList())
        _selectedRepo.value = null
    }

    fun saveGeminiKey(key: String) {
        tokenManager.saveGeminiKey(key)
        _hasGeminiKey.value = tokenManager.hasGeminiKey()
    }

    fun clearGeminiKey() {
        tokenManager.clearGeminiKey()
        _hasGeminiKey.value = false
    }

    fun refreshData() {
        val token = tokenManager.getToken() ?: return
        viewModelScope.launch {
            _reposState.value = ApiResult.Loading
            val reposRes = gitHubRepo.getUserRepos(token)
            _reposState.value = reposRes

            if (reposRes is ApiResult.Success && reposRes.data.isNotEmpty() && _selectedRepo.value == null) {
                _selectedRepo.value = reposRes.data.first()
            }

            _issuesState.value = ApiResult.Loading
            _issuesState.value = gitHubRepo.getUserIssues(token)

            _notificationsState.value = ApiResult.Loading
            _notificationsState.value = gitHubRepo.getNotifications(token)

            _selectedRepo.value?.let { fetchWorkflowRuns(it) }
        }
    }

    fun selectRepo(repo: GitHubRepo) {
        _selectedRepo.value = repo
        fetchWorkflowRuns(repo)
    }

    fun fetchWorkflowRuns(repo: GitHubRepo) {
        val token = tokenManager.getToken() ?: return
        viewModelScope.launch {
            _workflowRunsState.value = ApiResult.Loading
            val owner = repo.owner?.login ?: repo.fullName.substringBefore("/")
            _workflowRunsState.value = gitHubRepo.getRepoWorkflowRuns(token, owner, repo.name)
        }
    }

    fun createRepositoryOnGitHub(name: String, description: String?, isPrivate: Boolean) {
        val token = tokenManager.getToken() ?: return
        viewModelScope.launch {
            _repoCreationState.value = ApiResult.Loading
            val result = gitHubRepo.createRepository(token, name, description, isPrivate)
            _repoCreationState.value = result
            if (result is ApiResult.Success) refreshData()
        }
    }

    fun clearRepoCreationState() {
        _repoCreationState.value = null
    }

    fun starSelectedRepo() {
        val token = tokenManager.getToken() ?: return
        val repo = _selectedRepo.value ?: return
        val owner = repo.owner?.login ?: repo.fullName.substringBefore("/")
        viewModelScope.launch {
            gitHubRepo.starRepo(token, owner, repo.name)
        }
    }

    fun unstarSelectedRepo() {
        val token = tokenManager.getToken() ?: return
        val repo = _selectedRepo.value ?: return
        val owner = repo.owner?.login ?: repo.fullName.substringBefore("/")
        viewModelScope.launch {
            gitHubRepo.unstarRepo(token, owner, repo.name)
        }
    }

    fun searchRepos(query: String) {
        val token = tokenManager.getToken() ?: return
        viewModelScope.launch {
            _searchState.value = ApiResult.Loading
            _searchState.value = gitHubRepo.searchRepositories(token, query)
        }
    }

    fun refreshTips() {
        _tipsState.value = tipsStore.all()
    }

    fun addCodingTip(title: String, body: String, tags: String = "") {
        if (title.isBlank() || body.isBlank()) return
        tipsStore.add(title, body, tags)
        refreshTips()
    }

    fun removeCodingTip(id: String) {
        tipsStore.remove(id)
        refreshTips()
    }

    /** Offline help Q&A; appends matching saved coding tips. */
    fun askHelp(question: String) {
        val base = HelpCoach.answer(question)
        val matched = tipsStore.search(question).take(5)
        val tipBlock = if (matched.isNotEmpty()) {
            buildString {
                appendLine()
                appendLine()
                appendLine("From your coding tips:")
                matched.forEach { t ->
                    appendLine("• ${t.title}: ${t.body}")
                }
            }
        } else {
            val allCtx = tipsStore.asContextBlock(3)
            if (allCtx.isNotBlank()) "\n\n$allCtx" else ""
        }
        _helpState.value = base.copy(body = base.body + tipBlock)
    }

    fun generateTermuxCommands(topic: String) {
        val snippets = TermuxCommandGenerator.generate(topic)
        val tipExtra = tipsStore.search(topic).take(3).joinToString("\n") {
            "# tip: ${it.title} — ${it.body}"
        }
        val body = buildString {
            appendLine("Tap a command to copy. Paste into Termux.")
            appendLine()
            append(TermuxCommandGenerator.toShellFile(snippets))
            if (tipExtra.isNotBlank()) {
                appendLine()
                appendLine("# --- your tips ---")
                appendLine(tipExtra)
            }
        }
        _helpState.value = HelpCoach.HelpAnswer(
            title = "Termux: $topic",
            body = body,
            termuxCommands = snippets
        )
    }

    private fun mapParsedToGenerated(
        parsed: List<KotlinCodeMaker.KotlinFile>,
        packageName: String
    ): List<GeneratedFile> {
        return parsed.map { file ->
            val isDoc = file.name.endsWith(".md") || file.path.startsWith("docs/") ||
                file.path.startsWith("termux/") || file.name.endsWith(".sh")
            val path = when {
                file.path.startsWith("docs/") || file.path.startsWith("termux/") -> file.path
                isDoc && file.name.endsWith(".sh") -> "termux/${file.name}"
                isDoc -> if (file.path.startsWith("docs/")) file.path else "docs/${file.name}"
                else -> "app/src/main/java/${packageName.replace('.', '/')}/${file.path}"
            }
            GeneratedFile(
                path = path,
                category = when {
                    path.startsWith("termux/") || file.name.endsWith(".sh") -> "Config"
                    isDoc -> "Docs"
                    else -> "Kotlin"
                },
                content = file.content
            )
        }
    }

    private fun templateBundle(
        prompt: String,
        includeRoom: Boolean,
        includeHilt: Boolean,
        includeWorkflows: Boolean,
        includeGeminiStub: Boolean,
        minSdk: String,
        packageName: String,
        fullAppGuide: Boolean
    ): List<GeneratedFile> {
        val project = generateProjectFiles(
            prompt, includeRoom, includeHilt, includeWorkflows, includeGeminiStub, minSdk, packageName
        )
        return if (fullAppGuide) {
            listOf(
                AppGuideGenerator.generateGuide(
                    prompt, includeRoom, includeHilt, includeWorkflows, packageName
                )
            ) + project
        } else {
            project
        }
    }

    fun generateCode(
        prompt: String,
        useGemini: Boolean,
        includeRoom: Boolean,
        includeHilt: Boolean,
        includeWorkflows: Boolean,
        fullAppGuide: Boolean = false,
        includeGeminiStub: Boolean = false,
        minSdk: String = "24",
        packageName: String = "com.example"
    ) {
        viewModelScope.launch {
            _codeGenState.value = ApiResult.Loading

            val mode = if (fullAppGuide) {
                GeminiCodeService.Mode.FULL_APP_GUIDE
            } else {
                GeminiCodeService.Mode.CODE
            }

            val geminiKey = tokenManager.getGeminiKey()
            if (useGemini && !geminiKey.isNullOrBlank()) {
                when (val res = geminiService.generateKotlinCode(geminiKey, prompt, packageName, mode)) {
                    is ApiResult.Success -> {
                        val kotlinFiles = GeminiCodeService.parseGeminiOutput(res.data)
                        val generated = mapParsedToGenerated(kotlinFiles, packageName)
                        val scaffold = generateProjectFiles(
                            prompt, includeRoom, includeHilt, includeWorkflows, includeGeminiStub, minSdk, packageName
                        ).filter { it.category != "Kotlin" }
                        val hasGuide = generated.any { it.path.contains("APP_GUIDE") || it.category == "Docs" }
                        val files = if (fullAppGuide && !hasGuide) {
                            listOf(
                                AppGuideGenerator.generateGuide(
                                    prompt, includeRoom, includeHilt, includeWorkflows, packageName
                                )
                            ) + generated + scaffold
                        } else {
                            generated + scaffold
                        }
                        _codeGenState.value = ApiResult.Success(
                            CodeGenResult(files = files, source = "gemini")
                        )
                        return@launch
                    }
                    is ApiResult.Error -> {
                        val template = templateBundle(
                            prompt, includeRoom, includeHilt, includeWorkflows,
                            includeGeminiStub, minSdk, packageName, fullAppGuide
                        )
                        _codeGenState.value = ApiResult.Success(
                            CodeGenResult(
                                files = template,
                                source = "template",
                                error = "Gemini failed: ${res.message}. Used free template instead."
                            )
                        )
                        return@launch
                    }
                    is ApiResult.Loading -> {}
                }
            }

            val template = templateBundle(
                prompt, includeRoom, includeHilt, includeWorkflows,
                includeGeminiStub, minSdk, packageName, fullAppGuide
            )
            _codeGenState.value = ApiResult.Success(
                CodeGenResult(files = template, source = "template")
            )
        }
    }

    fun clearCodeGenState() {
        _codeGenState.value = null
    }

    fun getSavedToken(): String? = tokenManager.getToken()
}
