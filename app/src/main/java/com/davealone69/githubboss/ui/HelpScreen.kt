package com.davealone69.githubboss.ui

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HelpScreen(viewModel: GitHubViewModel) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val help by viewModel.helpState.collectAsStateWithLifecycle()
    val tips by viewModel.tipsState.collectAsStateWithLifecycle()

    var question by remember { mutableStateOf("") }
    var termuxTopic by remember { mutableStateOf("build apk adb") }
    var tipTitle by remember { mutableStateOf("") }
    var tipBody by remember { mutableStateOf("") }
    var tipTags by remember { mutableStateOf("") }

    LazyColumn(
        Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Help, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Help coach",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        "Ask about this app, GitHub, Builder, or Termux. Saved tips are appended when relevant.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = question,
                        onValueChange = { question = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. How do I log in? What scopes?") },
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "How do I log in?",
                            "PAT scopes",
                            "How does Builder work?",
                            "Free Gemini key",
                            "Termux install APK",
                            "Gradle build"
                        ).forEach { tip ->
                            SuggestionChip(
                                onClick = {
                                    question = tip
                                    viewModel.askHelp(tip)
                                },
                                label = { Text(tip, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.askHelp(question) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = question.isNotBlank(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Ask")
                    }
                }
            }
        }

        item {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Terminal, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Termux commands", fontWeight = FontWeight.Bold)
                    }
                    OutlinedTextField(
                        value = termuxTopic,
                        onValueChange = { termuxTopic = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("build apk, git clone, ssh, adb...") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("build apk", "git clone", "adb install", "ssh key", "pkg update").forEach { t ->
                            SuggestionChip(
                                onClick = {
                                    termuxTopic = t
                                    viewModel.generateTermuxCommands(t)
                                },
                                label = { Text(t, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                    Button(
                        onClick = { viewModel.generateTermuxCommands(termuxTopic) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Generate Termux commands")
                    }
                }
            }
        }

        // Coding tips memory
        item {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Coding tips (local memory)", fontWeight = FontWeight.Bold)
                    }
                    Text(
                        "Save fixes & Termux tricks. Not ML training — your notes, reused in Help.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = tipTitle,
                        onValueChange = { tipTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Title") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = tipBody,
                        onValueChange = { tipBody = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Tip / fix") },
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = tipTags,
                        onValueChange = { tipTags = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Tags (optional)") },
                        placeholder = { Text("gradle, adb, pat") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Button(
                        onClick = {
                            viewModel.addCodingTip(tipTitle, tipBody, tipTags)
                            tipTitle = ""
                            tipBody = ""
                            tipTags = ""
                            Toast.makeText(context, "Tip saved", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = tipTitle.isNotBlank() && tipBody.isNotBlank(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save tip")
                    }
                }
            }
        }

        items(tips, key = { it.id }) { tip ->
            Card(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(tip.title, fontWeight = FontWeight.SemiBold)
                        Text(tip.body, style = MaterialTheme.typography.bodySmall)
                        if (tip.tags.isNotBlank()) {
                            Text(
                                tip.tags,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.removeCodingTip(tip.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete tip")
                    }
                }
            }
        }

        val answer = help
        if (answer != null) {
            item {
                Text(answer.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            item {
                SelectionContainer {
                    Text(answer.body, style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (answer.termuxCommands.isNotEmpty()) {
                item {
                    Text(
                        "Termux — tap to copy",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(answer.termuxCommands) { snip ->
                    Card(
                        onClick = {
                            clipboard.setText(AnnotatedString(snip.command))
                            Toast.makeText(context, "Copied: ${snip.label}", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(snip.label, style = MaterialTheme.typography.labelMedium)
                                Text(
                                    snip.command,
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                        }
                    }
                }
                item {
                    TextButton(
                        onClick = {
                            val all = answer.termuxCommands.joinToString("\n") {
                                "# ${it.label}\n${it.command}\n"
                            }
                            clipboard.setText(AnnotatedString(all))
                            Toast.makeText(context, "All commands copied", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Copy all as script")
                    }
                }
            }
        }
    }
}
