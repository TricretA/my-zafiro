package com.niki914.zafiro.app.ui.content

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niki914.zafiro.app.R
import com.niki914.zafiro.repo.WorkspaceManager
import com.tricreta.zafiro.autonomy.workspace.SocialPlatform
import com.tricreta.zafiro.autonomy.workspace.SocialPlatforms
import com.tricreta.zafiro.autonomy.AutonomyGraph
import com.tricreta.zafiro.autonomy.workspace.WorkspaceDef
import kotlinx.coroutines.launch

private val DEPARTMENT_GROUPS = listOf(
    "Careers and income" to setOf("jobhunt"),
    "Content" to setOf("video-studio", "social"),
    "Communication" to setOf("email"),
    "Tools and upkeep" to setOf("toolsmith", "health"),
)

/** One card per department, grouped by purpose: its icon, a switch, and a bin. Nothing to read. */
@Composable
fun WorkspacesVisualContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var tick by remember { mutableStateOf(0) }
    val defs = remember(tick) { WorkspaceManager.defs(context) }
    val deleted = remember(tick) { WorkspaceManager.deletedIds(context) }
    val states by AutonomyGraph.require().repo.teamDao().observeWorkspaces().collectAsState(initial = emptyList())
    val on = states.filter { it.enabled }.map { it.id }.toSet()
    var confirm by remember { mutableStateOf<WorkspaceDef?>(null) }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp, top = topChromePadding(), bottom = 24.dp + bottomChromePadding()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("${on.size} of ${defs.size} departments on", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary) }
        val grouped = DEPARTMENT_GROUPS.map { (title, ids) -> title to defs.filter { it.id in ids } } +
            listOf("Other" to defs.filter { d -> DEPARTMENT_GROUPS.none { d.id in it.second } })
        grouped.filter { it.second.isNotEmpty() }.forEach { (title, list) ->
            item(key = "hdr-$title") {
                Text("$title · ${list.count { it.id in on }}/${list.size} on", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
            }
            items(list, key = { it.id }) { d ->
            val isOn = d.id in on
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (isOn) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
                Column {
                Row(Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PlatformIcon(d.platform, d.app)
                    Column(Modifier.weight(1f)) {
                        Text(d.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("${d.agents.size} agents · ${d.tasks.size} tasks", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { confirm = d }) { Icon(Icons.Default.Delete, contentDescription = "Delete ${d.name}") }
                    Switch(checked = isOn, onCheckedChange = { want ->
                        scope.launch {
                            val msg = runCatching { WorkspaceManager.setEnabled(context, d, want) }.getOrElse { "Could not switch ${d.name}: ${it.message}" }
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                if (d.id == SocialPlatforms.WORKSPACE_ID) SocialPlatformsPanel()
                }
            }
            }
        }
        if (deleted.isNotEmpty()) item {
            OutlinedButton(onClick = { WorkspaceManager.restoreDeleted(context); tick++ }, modifier = Modifier.fillMaxWidth().height(52.dp)) { Text("Restore ${deleted.size} deleted") }
        }
    }
    confirm?.let { d ->
        AlertDialog(
            onDismissRequest = { confirm = null },
            title = { Text("Delete ${d.name}?") },
            text = { Text("Its agents, tasks and history are removed. You can restore it later.") },
            confirmButton = { TextButton(onClick = { confirm = null; scope.launch { WorkspaceManager.delete(context, d); tick++ } }) { Text("Delete", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { confirm = null }) { Text("Cancel") } },
        )
    }
}

/** Platform grid of the Social Media Manager: a switch per platform (real app icon when installed), a helper for installed apps, and a daily-caps editor. */
@Composable
private fun SocialPlatformsPanel() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val catalog = remember { WorkspaceManager.socialCatalog(context) }
    var version by remember { mutableStateOf(0) }
    val enabled = remember(version) { WorkspaceManager.enabledPlatforms(context) }
    var expanded by remember { mutableStateOf(false) }
    var capsFor by remember { mutableStateOf<SocialPlatform?>(null) }
    Column(Modifier.padding(start = 12.dp, end = 12.dp, bottom = 8.dp)) {
        TextButton(onClick = { expanded = !expanded }) { Text(stringResource(R.string.social_platforms_summary, enabled.size, catalog.platforms.size)) }
        if (expanded) {
            Text(stringResource(R.string.social_platforms_hint), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedButton(onClick = {
                scope.launch {
                    val n = WorkspaceManager.enableInstalledPlatforms(context).size
                    version++
                    Toast.makeText(context, context.getString(R.string.social_installed_done, n), Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) { Text(stringResource(R.string.social_select_installed)) }
            catalog.platforms.chunked(3).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    row.forEach { p ->
                        Column(Modifier.weight(1f).padding(vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            PlatformIcon(p.id, p.app, 40.dp)
                            Text(p.name.substringBefore(" ("), style = MaterialTheme.typography.labelMedium, maxLines = 1)
                            if (p.optional) Text(stringResource(R.string.social_optional), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Switch(checked = p.id in enabled, onCheckedChange = { want ->
                                scope.launch {
                                    val msg = runCatching { WorkspaceManager.setPlatformEnabled(context, p.id, want) }.getOrElse { "Could not switch ${p.name}: ${it.message}" }
                                    version++
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            })
                            TextButton(onClick = { capsFor = p }) { Text(stringResource(R.string.social_caps_button), style = MaterialTheme.typography.labelSmall) }
                        }
                    }
                    repeat(3 - row.size) { Column(Modifier.weight(1f)) {} }
                }
            }
        }
    }
    capsFor?.let { p -> CapsDialog(p, onDismiss = { capsFor = null }, onSaved = { capsFor = null; version++ }) }
}

@Composable
private fun CapsDialog(p: SocialPlatform, onDismiss: () -> Unit, onSaved: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val current = remember(p.id) { SocialPlatforms.caps(p, WorkspaceManager.socialSettings(context)) }
    val values = remember(p.id) { mutableStateMapOf<String, String>().also { m -> current.forEach { (k, v) -> m[k] = v.toString() } } }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.social_caps_title, p.name)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(stringResource(R.string.social_caps_text), style = MaterialTheme.typography.bodySmall)
                current.keys.forEach { action ->
                    OutlinedTextField(
                        value = values[action].orEmpty(), onValueChange = { v -> values[action] = v.filter { it.isDigit() }.take(3) },
                        label = { Text(action) }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                scope.launch { WorkspaceManager.setPlatformCaps(context, p.id, current.keys.associateWith { values[it]?.toIntOrNull() ?: current.getValue(it) }); onSaved() }
            }) { Text(stringResource(R.string.social_caps_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.social_cancel)) } },
    )
}
