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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.niki914.zafiro.repo.WorkspaceManager
import com.tricreta.zafiro.autonomy.AutonomyGraph
import com.tricreta.zafiro.autonomy.data.ActionLogEntity
import com.tricreta.zafiro.autonomy.data.BoardItemEntity
import com.tricreta.zafiro.autonomy.workspace.SocialAccount
import com.tricreta.zafiro.autonomy.workspace.SocialBrief
import com.tricreta.zafiro.autonomy.workspace.SocialPlatform
import com.tricreta.zafiro.autonomy.workspace.SocialPlatforms
import com.tricreta.zafiro.autonomy.workspace.WorkspaceDef
import kotlinx.coroutines.launch

private val DEPARTMENT_GROUPS = listOf(
    "Careers and income" to setOf("jobhunt"),
    "Content" to setOf("video-studio", "social"),
    "Communication" to setOf("email"),
    "Tools and upkeep" to setOf("toolsmith", "health"),
)

/** Department overview. Social expands into an explicit control room instead of hiding intent behind app switches. */
@Composable
fun WorkspacesVisualContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var tick by remember { mutableStateOf(0) }
    val defs = remember(tick) { WorkspaceManager.defs(context) }
    val deleted = remember(tick) { WorkspaceManager.deletedIds(context) }
    val states by AutonomyGraph.require().repo.teamDao().observeWorkspaces().collectAsState(initial = emptyList())
    val on = states.filter { it.enabled }.map { it.id }.toSet()

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp, top = topChromePadding(), bottom = 24.dp + bottomChromePadding()),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item { Text("${on.size} of ${defs.size} departments on", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary) }
        val grouped = DEPARTMENT_GROUPS.map { (title, ids) -> title to defs.filter { it.id in ids } } +
            listOf("Other" to defs.filter { d -> DEPARTMENT_GROUPS.none { d.id in it.second } })
        grouped.filter { it.second.isNotEmpty() }.forEach { (title, list) ->
            item(key = "hdr-$title") {
                Text("$title · ${list.count { it.id in on }}/${list.size} on", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
            }
            items(list, key = { it.id }) { def ->
                if (def.id == SocialPlatforms.WORKSPACE_ID) {
                    SocialWorkspaceCard(def, def.id in on)
                } else {
                    WorkspaceCard(def, def.id in on) { wanted ->
                        scope.launch {
                            val message = runCatching { WorkspaceManager.setEnabled(context, def, wanted) }.getOrElse { "Could not switch ${def.name}: ${it.message}" }
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        if (deleted.isNotEmpty()) item {
            OutlinedButton(onClick = { WorkspaceManager.restoreDeleted(context); tick++ }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text("Restore ${deleted.size} removed workspace${if (deleted.size == 1) "" else "s"}")
            }
        }
    }
}

@Composable
private fun WorkspaceCard(def: WorkspaceDef, isOn: Boolean, onEnabled: (Boolean) -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (isOn) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
    ) {
        Row(Modifier.padding(14.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            WorkspaceIcon(def.platform, def.app)
            Column(Modifier.weight(1f)) {
                Text(def.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(def.summary.ifBlank { "${def.agents.size} agents · ${def.tasks.size} tasks" }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Switch(checked = isOn, onCheckedChange = onEnabled)
        }
    }
}

@Composable
private fun SocialWorkspaceCard(def: WorkspaceDef, isOn: Boolean) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var version by remember { mutableStateOf(0) }
    var expanded by remember { mutableStateOf(true) }
    var editBrief by remember { mutableStateOf(false) }
    var editAccount by remember { mutableStateOf<SocialPlatform?>(null) }
    var editCaps by remember { mutableStateOf<SocialPlatform?>(null) }
    var requestVideo by remember { mutableStateOf(false) }
    val catalog = remember { WorkspaceManager.socialCatalog(context) }
    val settings = remember(version) { WorkspaceManager.socialSettings(context) }
    val issue = remember(version) { WorkspaceManager.socialConfigurationIssue(context) }
    val enabled = remember(version) { WorkspaceManager.enabledPlatforms(context) }
    val requests by produceState(emptyList<BoardItemEntity>(), version) { value = WorkspaceManager.socialVideoRequests(context) }
    val activity by produceState(emptyList<ActionLogEntity>(), version) { value = AutonomyGraph.require().repo.teamDao().actions(SocialPlatforms.WORKSPACE_ID, 5) }

    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (isOn) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SocialWorkspaceIcon()
                Column(Modifier.weight(1f)) {
                    Text(def.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        if (isOn) "Running ${settings.scheduledTaskKeys.size} owner-selected task${if (settings.scheduledTaskKeys.size == 1) "" else "s"}" else issue ?: "Ready to arm",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                TextButton(onClick = { expanded = !expanded }) { Text(if (expanded) "Hide" else "Open") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { editBrief = true }, modifier = Modifier.weight(1f)) { Text(if (settings.brief.isComplete()) "Edit brief" else "Set up brief") }
                Button(
                    onClick = {
                        scope.launch {
                            val message = WorkspaceManager.setEnabled(context, def, !isOn)
                            version++
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.weight(1f),
                ) { Text(if (isOn) "Pause" else "Review & arm") }
            }
            if (expanded) {
                BriefSummary(settings.brief)
                Text("Accounts", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("An installed app is not account permission. Select an account, state what it is for, and choose the actions it may take.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            val selected = WorkspaceManager.enableInstalledPlatforms(context).size
                            version++
                            Toast.makeText(context, "$selected installed platforms selected. Configure each account before arming.", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Select installed platforms") }
                catalog.platforms.forEach { platform ->
                    SocialAccountRow(
                        platform = platform,
                        enabled = platform.id in enabled,
                        account = settings.accounts[platform.id],
                        onEnabled = { wanted ->
                            scope.launch {
                                val message = WorkspaceManager.setPlatformEnabled(context, platform.id, wanted)
                                version++
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        },
                        onConfigure = { editAccount = platform },
                        onCaps = { editCaps = platform },
                    )
                }
                Text("Work plan", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp))
                Text("Only selected tasks run after you arm the workspace. Open the plan before turning it on.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                def.tasks.forEach { task ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = task.key in settings.scheduledTaskKeys,
                            onCheckedChange = { wanted -> scope.launch { WorkspaceManager.setSocialTaskScheduled(context, task.key, wanted); version++ } },
                        )
                        Column(Modifier.weight(1f)) {
                            Text(task.name, style = MaterialTheme.typography.bodyMedium)
                            Text(taskSchedule(task.trigger, task.time, task.intervalMinutes), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Text("Video Studio", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp))
                Text("Send a complete production brief to Video Studio. It creates and verifies the media; Social decides whether to publish it.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedButton(onClick = { requestVideo = true }, modifier = Modifier.fillMaxWidth()) { Text("Request a video") }
                if (requests.isEmpty()) Text("No video requests yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                requests.take(3).forEach { request ->
                    Text("${request.title.removePrefix("Social video: ")} · ${request.status}", style = MaterialTheme.typography.bodyMedium)
                    if (request.result.isNotBlank()) Text(request.result, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                if (activity.isNotEmpty()) {
                    Text("Recent activity", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp))
                    activity.forEach { action -> Text("${action.platform}: ${action.action} · ${action.status}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
        }
    }
    if (editBrief) SocialBriefDialog(settings.brief, onDismiss = { editBrief = false }) { brief ->
        scope.launch { WorkspaceManager.saveSocialBrief(context, brief); version++; editBrief = false }
    }
    editAccount?.let { platform -> SocialAccountDialog(platform, settings.accounts[platform.id], onDismiss = { editAccount = null }) { account ->
        scope.launch { WorkspaceManager.saveSocialAccount(context, platform.id, account); version++; editAccount = null }
    } }
    editCaps?.let { platform -> CapsDialog(platform, onDismiss = { editCaps = null }, onSaved = { editCaps = null; version++ }) }
    if (requestVideo) VideoRequestDialog(onDismiss = { requestVideo = false }) { title, brief ->
        scope.launch {
            val message = WorkspaceManager.requestSocialVideo(context, title, brief)
            version++
            requestVideo = false
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
private fun BriefSummary(brief: SocialBrief) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f))) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(if (brief.brandName.isBlank()) "Business brief not complete" else brief.brandName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(if (brief.mission.isBlank()) "Add what this operation should achieve." else brief.mission, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (brief.audience.isNotBlank()) Text("For: ${brief.audience}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SocialAccountRow(platform: SocialPlatform, enabled: Boolean, account: SocialAccount?, onEnabled: (Boolean) -> Unit, onConfigure: () -> Unit, onCaps: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        PlatformIcon(platform.id, platform.app, 42.dp)
        Column(Modifier.weight(1f)) {
            Text(platform.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            val status = when {
                !enabled -> "Not selected"
                account?.isReady() == true -> "${account.handle} · ${account.allowedActions.joinToString()}"
                account?.accessState == "LOGIN_REQUIRED" -> "Login needed"
                else -> "Needs account setup"
            }
            Text(status, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Switch(checked = enabled, onCheckedChange = onEnabled)
        if (enabled) Column(horizontalAlignment = Alignment.End) {
            TextButton(onClick = onConfigure) { Text("Account") }
            TextButton(onClick = onCaps) { Text("Caps") }
        }
    }
}

@Composable
private fun SocialBriefDialog(current: SocialBrief, onDismiss: () -> Unit, onSave: (SocialBrief) -> Unit) {
    var brand by remember(current) { mutableStateOf(current.brandName) }
    var mission by remember(current) { mutableStateOf(current.mission) }
    var audience by remember(current) { mutableStateOf(current.audience) }
    var voice by remember(current) { mutableStateOf(current.voice) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Social Media Manager brief") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("This is the instruction every social agent receives. Keep it concrete.", style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(brand, { brand = it }, label = { Text("Brand or account name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(mission, { mission = it }, label = { Text("Goal: what should these accounts achieve?") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                OutlinedTextField(audience, { audience = it }, label = { Text("Audience, country and language") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                OutlinedTextField(voice, { voice = it }, label = { Text("Voice, topics and boundaries (optional)") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            }
        },
        confirmButton = { TextButton(onClick = { onSave(SocialBrief(brand.trim(), mission.trim(), audience.trim(), voice.trim())) }) { Text("Save brief") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun SocialAccountDialog(platform: SocialPlatform, current: SocialAccount?, onDismiss: () -> Unit, onSave: (SocialAccount) -> Unit) {
    var handle by remember(platform.id) { mutableStateOf(current?.handle.orEmpty()) }
    var purpose by remember(platform.id) { mutableStateOf(current?.purpose.orEmpty()) }
    var access by remember(platform.id) { mutableStateOf(current?.accessState ?: "NOT_CREATED") }
    var actions by remember(platform.id) { mutableStateOf(current?.allowedActions.orEmpty()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${platform.name} account") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Zafiro uses the signed-in account on this phone. It never stores a password.", style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(handle, { handle = it }, label = { Text("Handle or page name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(purpose, { purpose = it }, label = { Text("What this account is for") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                Text("Access: ${accessLabel(access)}", style = MaterialTheme.typography.bodyMedium)
                OutlinedButton(onClick = { access = nextAccess(access) }, modifier = Modifier.fillMaxWidth()) { Text("Change access status") }
                Text("Allowed work", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                SocialPlatforms.CONFIGURABLE_ACTIONS.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        row.forEach { action ->
                            FilterChip(
                                selected = action in actions,
                                onClick = { actions = if (action in actions) actions - action else actions + action },
                                label = { Text(action.replaceFirstChar { it.uppercase() }) },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onSave(SocialAccount(handle.trim(), purpose.trim(), access, actions)) }) { Text("Save account") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun VideoRequestDialog(onDismiss: () -> Unit, onSend: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var brief by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Request a video") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Video Studio receives this as a production order with your Social Media Manager brief and selected platforms.", style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(title, { title = it }, label = { Text("Video title or campaign") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(brief, { brief = it }, label = { Text("Hook, message, call to action, format and deadline") }, modifier = Modifier.fillMaxWidth(), minLines = 4)
            }
        },
        confirmButton = { TextButton(onClick = { onSend(title, brief) }, enabled = title.isNotBlank() && brief.isNotBlank()) { Text("Send to Video Studio") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun CapsDialog(platform: SocialPlatform, onDismiss: () -> Unit, onSaved: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val current = remember(platform.id) { SocialPlatforms.caps(platform, WorkspaceManager.socialSettings(context)) }
    val values = remember(platform.id) { mutableStateMapOf<String, String>().also { values -> current.forEach { (action, cap) -> values[action] = cap.toString() } } }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${platform.name} daily caps") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("These are ceilings, not targets. An action not allowed for this account remains at zero.", style = MaterialTheme.typography.bodySmall)
                current.keys.forEach { action ->
                    OutlinedTextField(
                        value = values[action].orEmpty(),
                        onValueChange = { value -> values[action] = value.filter { it.isDigit() }.take(3) },
                        label = { Text(action) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                scope.launch {
                    WorkspaceManager.setPlatformCaps(context, platform.id, current.keys.associateWith { values[it]?.toIntOrNull() ?: current.getValue(it) })
                    onSaved()
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun SocialWorkspaceIcon() {
    Row(horizontalArrangement = Arrangement.spacedBy((-8).dp), verticalAlignment = Alignment.CenterVertically) {
        PlatformIcon("facebook", "com.facebook.katana", 30.dp)
        PlatformIcon("instagram", "com.instagram.android", 30.dp)
        PlatformIcon("tiktok", "com.zhiliaoapp.musically", 30.dp)
    }
}

@Composable
private fun WorkspaceIcon(platform: String, app: String) {
    if (platform == SocialPlatforms.WORKSPACE_ID) SocialWorkspaceIcon() else PlatformIcon(platform, app)
}

private fun taskSchedule(trigger: String, time: String, interval: Long): String = when (trigger) {
    "INTERVAL" -> "Every $interval minutes"
    "WEEKLY" -> "Weekly at $time"
    "DAILY" -> "Daily at $time"
    else -> trigger.lowercase().replaceFirstChar { it.uppercase() }
}

private fun accessLabel(access: String): String = when (access) {
    "VERIFIED" -> "Verified: this signed-in account may be used"
    "LOGIN_REQUIRED" -> "Login needed"
    else -> "Account not created yet"
}

private fun nextAccess(access: String): String = when (access) {
    "NOT_CREATED" -> "LOGIN_REQUIRED"
    "LOGIN_REQUIRED" -> "VERIFIED"
    else -> "NOT_CREATED"
}
