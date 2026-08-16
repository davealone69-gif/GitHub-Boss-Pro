package com.davealone69.githubboss.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.davealone69.githubboss.data.*

enum class SystemTab(val label: String) { Home("Home"), Repos("Repos"), Issues("Issues"), Actions("Actions") }

@Composable
fun GitHubBossApp(vm: GitHubViewModel) {
    val auth by vm.authState.collectAsState()
    when (auth) {
        AuthState.Unauthenticated -> LoginScreen(vm::loginWithToken)
        AuthState.Authenticating -> Loading("Connecting to GitHub…")
        is AuthState.Error -> LoginScreen(vm::loginWithToken, (auth as AuthState.Error).message)
        is AuthState.Authenticated -> BossShell(vm)
    }
}

@Composable
private fun LoginScreen(onLogin: (String) -> Unit, error: String? = null) {
    var token by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp), Arrangement.Center, Alignment.CenterHorizontally) {
        Text("GitHub Boss Pro", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("GitHub command center", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(token, { token = it }, Modifier.fillMaxWidth(), label = { Text("Personal Access Token") }, singleLine = true, visualTransformation = PasswordVisualTransformation())
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp)) }
        Button({ onLogin(token) }, Modifier.fillMaxWidth().padding(top = 8.dp), enabled = token.isNotBlank()) { Text("Connect") }
    }
}

@Composable
private fun BossShell(vm: GitHubViewModel) {
    var tab by remember { mutableStateOf(SystemTab.Home) }
    val repos by vm.reposState.collectAsState(); val issues by vm.issuesState.collectAsState(); val runs by vm.workflowRunsState.collectAsState(); val selected by vm.selectedRepo.collectAsState()
    Scaffold(topBar = { TopAppBar({ Text("GitHub Boss") }, actions = { TextButton(vm::refreshData) { Text("Refresh") }; TextButton(vm::logout) { Text("Sign out") } }) }, bottomBar = { NavigationBar { SystemTab.entries.forEach { t -> NavigationBarItem(tab == t, { tab = t }, icon = { Text(t.label.first().toString()) }, label = { Text(t.label) }) } } }) { p ->
        when (tab) {
            SystemTab.Home -> HomeScreen(p, repos, issues, runs, selected)
            SystemTab.Repos -> RepoScreen(p, repos, selected, vm::selectRepo)
            SystemTab.Issues -> IssueScreen(p, issues)
            SystemTab.Actions -> RunScreen(p, runs, selected?.fullName)
        }
    }
}

@Composable private fun HomeScreen(p: PaddingValues, repos: ApiResult<List<GitHubRepo>>, issues: ApiResult<List<GitHubIssue>>, runs: ApiResult<List<GitHubWorkflowRun>>, selected: GitHubRepo?) {
    LazyColumn(Modifier.fillMaxSize().padding(p), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Dashboard", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        item { Stat("Repositories", count(repos), "Your repositories") }; item { Stat("Open issues", count(issues), "Issues visible to you") }; item { Stat("Workflow runs", count(runs), selected?.fullName ?: "Select a repository") }
    }
}

@Composable private fun RepoScreen(p: PaddingValues, result: ApiResult<List<GitHubRepo>>, selected: GitHubRepo?, select: (GitHubRepo) -> Unit) = ResultList(p, result, "No repositories") { r -> Card({ select(r) }, Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text(r.name, fontWeight = FontWeight.Bold); Text(r.description ?: "No description"); Text("★ ${r.stargazersCount}  •  Issues ${r.openIssuesCount}  •  ${r.language ?: "Unknown"}"); if (r.fullName == selected?.fullName) Text("SELECTED", style = MaterialTheme.typography.labelSmall) } } }

@Composable private fun IssueScreen(p: PaddingValues, result: ApiResult<List<GitHubIssue>>) = ResultList(p, result, "No issues") { i -> Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("#${i.number}  ${i.title}", fontWeight = FontWeight.Bold); Text("${i.state.uppercase()} • ${i.comments} comments") } } }

@Composable private fun RunScreen(p: PaddingValues, result: ApiResult<List<GitHubWorkflowRun>>, repo: String?) { Column(Modifier.fillMaxSize().padding(p)) { Text("Actions", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp)); Text(repo ?: "Select a repository", modifier = Modifier.padding(horizontal = 16.dp)); ResultList(PaddingValues(), result, "No workflow runs") { r -> Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text(r.name ?: "Workflow", fontWeight = FontWeight.Bold); Text("${r.status ?: "unknown"} • ${r.conclusion ?: "pending"} • #${r.runNumber}") } } } } }

@Composable private fun <T> ResultList(p: PaddingValues, result: ApiResult<List<T>>, empty: String, content: @Composable (T) -> Unit) { when (result) { ApiResult.Loading -> Box(Modifier.fillMaxSize().padding(p), Alignment.Center) { CircularProgressIndicator() }; is ApiResult.Error -> Box(Modifier.fillMaxSize().padding(p), Alignment.Center) { Text(result.message, color = MaterialTheme.colorScheme.error) }; is ApiResult.Success -> if (result.data.isEmpty()) Box(Modifier.fillMaxSize().padding(p), Alignment.Center) { Text(empty) } else LazyColumn(Modifier.fillMaxSize().padding(p), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { items(result.data) { content(it) } } } }

@Composable private fun Stat(title: String, value: Int, subtitle: String) { Card(Modifier.fillMaxWidth()) { Row(Modifier.padding(18.dp), Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, style = MaterialTheme.typography.bodySmall) }; Text(value.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) } } }

private fun <T> count(r: ApiResult<List<T>>) = (r as? ApiResult.Success)?.data?.size ?: 0
@Composable private fun Loading(message: String) { Box(Modifier.fillMaxSize(), Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { CircularProgressIndicator(); Text(message, Modifier.padding(12.dp)) } } }
