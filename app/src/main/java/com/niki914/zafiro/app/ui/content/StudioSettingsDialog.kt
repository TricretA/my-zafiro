package com.niki914.zafiro.app.ui.content

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.niki914.zafiro.api.StudioConfig
import com.niki914.zafiro.repo.StudioSettings

/** Configure the Video Studio GitHub token (encrypted at rest), render repository, and default public output folder. */
@Composable
fun StudioSettingsDialog(context: Context, onDismiss: () -> Unit) {
    var config by remember { mutableStateOf(StudioSettings.read(context)) }
    var tokenVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Video Studio & GitHub") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    "Stored encrypted on this device and never sent into AI chat prompts. Give your GitHub fine-grained token Contents:write and Actions:write on your studio repo.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 10.dp),
                )
                OutlinedTextField(
                    value = config.githubToken,
                    onValueChange = { config = config.copy(githubToken = it) },
                    label = { Text("GitHub token") },
                    placeholder = { Text("github_pat_... or ghp_...") },
                    singleLine = true,
                    visualTransformation = if (tokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { tokenVisible = !tokenVisible }) {
                            Icon(
                                imageVector = if (tokenVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (tokenVisible) "Hide token" else "Show token",
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = config.repo,
                    onValueChange = { config = config.copy(repo = it) },
                    label = { Text("Render repository (owner/name)") },
                    placeholder = { Text(StudioConfig.DEFAULT_REPO) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                )
                OutlinedTextField(
                    value = config.outputFolder,
                    onValueChange = { config = config.copy(outputFolder = it) },
                    label = { Text("Default video output folder") },
                    placeholder = { Text(StudioConfig.DEFAULT_OUTPUT_FOLDER) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                )
                Text(
                    "Finished videos download here automatically (e.g. Download/Zafiro or Movies/Reels). You can also tell Zafiro any folder in chat anytime.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                StudioSettings.write(context, config)
                onDismiss()
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

internal fun studioSummary(context: Context): String {
    val cfg = StudioSettings.read(context)
    return if (cfg.githubToken.isNotBlank()) "Token set · ${cfg.outputFolder}" else "Token not set"
}
