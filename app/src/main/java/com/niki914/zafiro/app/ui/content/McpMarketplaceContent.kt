package com.niki914.zafiro.app.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.niki914.uikit.dialog.ZafiroInputDialog
import com.niki914.uikit.infra.component.OptionRow
import com.niki914.uikit.infra.component.OptionSheet
import com.niki914.uikit.infra.component.SettingsGroupCard
import com.niki914.uikit.infra.component.SettingsListItem
import com.niki914.uikit.infra.nav.pageViewModel
import com.niki914.uikit.screen.zafiroScreenBottomPadding
import com.niki914.uikit.screen.zafiroScreenTopPadding
import com.niki914.uikit.textfield.ZafiroTextField
import com.niki914.zafiro.api.util.runCatchingCancellable
import com.niki914.zafiro.app.R
import com.niki914.zafiro.app.ui.PageChromeContribution
import com.niki914.zafiro.app.ui.RegisterPageChrome
import com.niki914.zafiro.app.ui.model.McpSettingsIntent
import com.niki914.zafiro.app.ui.nav.TopBarActionSpec
import com.niki914.zafiro.app.ui.model.McpSettingsViewModel
import com.niki914.zafiro.mcp.McpCatalogEntry
import com.niki914.zafiro.mcp.McpCore
import com.niki914.zafiro.repo.XRepo
import com.niki914.zafiro.settings.model.RuntimeMcpServer
import kotlinx.coroutines.launch

/**
 * Single home for MCP servers, Skills and Workspaces from the catalog: installed servers
 * (toggle / configure / import JSON) live above the browsable catalog, instead of two
 * separate screens (the old standalone "MCP Configuration" page is folded in here).
 */
@Composable
fun McpMarketplaceContent(
    onBack: () -> Unit,
    onOpenServerDetail: (serverName: String, serverIndex: Int, isCreating: Boolean) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val installedViewModel = pageViewModel<McpSettingsViewModel>()
    val installedState by installedViewModel.uiStateFlow.collectAsState()
    LaunchedEffect(Unit) { installedViewModel.sendIntent(McpSettingsIntent.Load) }

    val catalog = remember {
        runCatchingCancellable {
            context.assets.open("mcp_catalog.json").bufferedReader().use { it.readText() }
        }.getOrDefault("""{"entries":[]}""")
            .let { McpCore.parseCatalog(it).getOrDefault(emptyList()) }
    }
    var kindFilter by remember { mutableStateOf("All") }
    var query by rememberSaveable { mutableStateOf("") }
    val categories = remember(catalog) { listOf("All") + catalog.map { it.category }.distinct().sorted() }
    var category by remember { mutableStateOf("All") }
    val filtered = remember(catalog, kindFilter, category, query) {
        catalog.filter { entry ->
            (kindFilter == "All" || entry.kind.equals(kindFilter, ignoreCase = true)) &&
                (category == "All" || entry.category == category) &&
                (query.isBlank() || entry.name.contains(query, ignoreCase = true) ||
                    entry.category.contains(query, ignoreCase = true) ||
                    entry.notes.contains(query, ignoreCase = true))
        }
    }

    var showImport by rememberSaveable { mutableStateOf(false) }
    var importJson by rememberSaveable { mutableStateOf("") }
    var detailEntry by remember { mutableStateOf<McpCatalogEntry?>(null) }

    val createTitle = stringResource(R.string.mcp_editor_title_create)
    val latestOnOpenServerDetail by rememberUpdatedState(onOpenServerDetail)
    val pageChromeContribution = remember(createTitle) {
        PageChromeContribution(
            rightAction = TopBarActionSpec(
                icon = Icons.Default.Add,
                onClick = { latestOnOpenServerDetail(createTitle, -1, true) },
                contentDescription = "Add MCP server",
            ),
        )
    }
    RegisterPageChrome(pageChromeContribution)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = zafiroScreenTopPadding(8.dp),
            bottom = zafiroScreenBottomPadding(),
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (installedState.items.isNotEmpty()) {
            item(key = "installed") {
                SettingsGroupCard(
                    items = installedState.items,
                    title = stringResource(R.string.mcp_installed_section_title),
                    itemContent = { item, shape, standalone, index ->
                        SettingsListItem(
                            title = item.name,
                            summary = item.url.ifBlank { null },
                            shape = shape,
                            isStandaloneCard = standalone,
                            trailingContent = {
                                Switch(
                                    checked = item.enabled,
                                    onCheckedChange = { checked ->
                                        installedViewModel.sendIntent(
                                            McpSettingsIntent.ItemEnabledChanged(index, checked)
                                        )
                                    },
                                    enabled = !installedState.isSaving,
                                )
                            },
                            onClick = { onOpenServerDetail(item.name, index, false) },
                        )
                    },
                )
            }
        }

        item(key = "import") {
            SettingsGroupCard(items = listOf(Unit)) { _, shape, standalone, _ ->
                SettingsListItem(
                    title = stringResource(R.string.mcp_import_row_title),
                    summary = stringResource(R.string.mcp_import_row_summary),
                    shape = shape,
                    isStandaloneCard = standalone,
                    onClick = { showImport = true },
                )
            }
        }

        item(key = "search") {
            ZafiroTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = stringResource(R.string.mcp_marketplace_search_hint),
                singleLine = true,
                leadingContent = { Icon(Icons.Default.Search, contentDescription = null) },
            )
        }

        item(key = "kind-filter") {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("All", "MCP", "Skill", "Workspace")) { filter ->
                    FilterChip(
                        selected = kindFilter == filter,
                        onClick = { kindFilter = filter },
                        label = { Text(filter) },
                    )
                }
            }
        }

        item(key = "category-filter") {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { name ->
                    FilterChip(
                        selected = category == name,
                        onClick = { category = name },
                        label = { Text(name) },
                    )
                }
            }
        }

        items(filtered, key = { it.id }) { entry ->
            MarketplaceCard(entry = entry, onClick = { detailEntry = entry })
        }
    }

    McpCatalogEntryDetailSheet(
        entry = detailEntry,
        onDismiss = { detailEntry = null },
        onAddServer = { toAdd ->
            scope.launch {
                XRepo.mcp.save(
                    RuntimeMcpServer(
                        name = toAdd.name,
                        url = toAdd.url,
                        enabled = false,
                        headers = emptyMap(),
                        transport = toAdd.transport,
                        authMode = toAdd.auth,
                    )
                )
                installedViewModel.sendIntent(McpSettingsIntent.Load)
                detailEntry = null
            }
        },
    )

    ZafiroInputDialog(
        visible = showImport,
        onDismissRequest = { showImport = false },
        title = stringResource(R.string.mcp_import_row_title),
        hint = stringResource(R.string.mcp_import_row_summary),
        value = importJson,
        onValueChange = { importJson = it },
        placeholder = """{"mcpServers":{"Example":{"url":"https://example.com/mcp"}}}""",
        positiveButtonText = stringResource(R.string.mcp_import_confirm),
        positiveEnabled = importJson.trim().isNotBlank(),
        onPositiveClick = {
            McpCore.parseMcpServersJson(importJson).onSuccess { servers ->
                scope.launch {
                    servers.forEach { server ->
                        XRepo.mcp.save(
                            RuntimeMcpServer(
                                name = server.name,
                                url = server.url,
                                enabled = false,
                                headers = server.headers,
                                transport = server.transport,
                                authMode = server.auth,
                            )
                        )
                    }
                    showImport = false
                    importJson = ""
                    installedViewModel.sendIntent(McpSettingsIntent.Load)
                }
            }
        },
        negativeButtonText = stringResource(R.string.mcp_import_cancel),
        onNegativeClick = { showImport = false },
    )
}

@Composable
private fun MarketplaceCard(entry: McpCatalogEntry, onClick: () -> Unit) {
    SettingsGroupCard(
        items = listOf(entry),
        itemContent = { item, shape, standalone, _ ->
            SettingsListItem(
                title = item.name,
                summary = item.notes.ifBlank { item.category },
                shape = shape,
                isStandaloneCard = standalone,
                leadingContent = { Icon(kindIcon(item.kind), contentDescription = null) },
                trailingContent = {
                    Text(
                        text = buildString {
                            append(if (item.free) "Free" else "Paid")
                            if (item.needsKey) append(" · Key required")
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                onClick = onClick,
            )
        },
    )
}

@Composable
private fun kindIcon(kind: String): ImageVector = when (kind.lowercase()) {
    "skill" -> Icons.Default.AutoAwesome
    "workspace" -> Icons.Default.Groups
    else -> Icons.Default.Extension
}

@Composable
private fun McpCatalogEntryDetailSheet(
    entry: McpCatalogEntry?,
    onDismiss: () -> Unit,
    onAddServer: (McpCatalogEntry) -> Unit,
) {
    OptionSheet(
        visible = entry != null,
        onDismissRequest = onDismiss,
        title = entry?.name,
    ) { dismissThen ->
        val item = entry ?: return@OptionSheet
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (item.notes.isNotBlank()) {
                Text(item.notes, style = MaterialTheme.typography.bodyMedium)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailFact(Icons.Default.Extension, "${item.kind.replaceFirstChar { it.uppercase() }} · ${item.category}")
                DetailFact(Icons.Default.AttachMoney, if (item.free) "Free to use" else "Paid service")
                DetailFact(
                    Icons.Default.Verified,
                    if (item.verifiedOn.isNotBlank()) "Verified on ${item.verifiedOn}" else "Community-provided, not verified by Zafiro",
                )
                if (item.needsKey) DetailFact(Icons.Default.Key, "Requires an API key or sign-in")
                if (item.writes) DetailFact(Icons.Default.Extension, "Can make changes on your behalf (writes)")
                if (item.spendsMoney) DetailFact(Icons.Default.AttachMoney, "Can spend real money when used")
            }
            when (item.kind.lowercase()) {
                "mcp" -> if (item.url.isNotBlank()) {
                    OptionRow(
                        title = "Add to Zafiro",
                        onClick = { dismissThen { onAddServer(item) } },
                    )
                }
                else -> Text(
                    text = "Skills and workspace bundles from the catalog aren't installable from here yet. " +
                        "Manage them from Settings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DetailFact(icon: ImageVector, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}
