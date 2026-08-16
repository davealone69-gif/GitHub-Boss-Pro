package com.davealone69.githubboss

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.davealone69.githubboss.ui.GitHubBossApp
import com.davealone69.githubboss.ui.GitHubViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel = GitHubViewModel(application)
        setContent { MaterialTheme { GitHubBossApp(viewModel) } }
    }
}
