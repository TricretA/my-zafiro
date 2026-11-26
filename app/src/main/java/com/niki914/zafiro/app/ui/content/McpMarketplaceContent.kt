package com.niki914.zafiro.app.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.niki914.uikit.infra.component.SettingsGroupCard
import com.niki914.uikit.infra.component.SettingsListItem
import com.niki914.zafiro.app.R
import com.niki914.zafiro.mcp.McpCatalogEntry
import com.niki914.zafiro.mcp.McpCore
import com.niki914.zafiro.repo.XRepo
import com.niki914.zafiro.settings.model.RuntimeMcpServer
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun McpMarketplaceContent(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val catalog = remember {
        runCatching {
            context.assets.open("mcp_catalog.json").bufferedReader().use { it.readText() }
        }.getOrDefault("""{"entries":[]}""")
            .let { McpCore.parseCatalog(it).getOrDefault(emptyList()) }
    }
    var kindFilter by remember { mutableStateOf("All") }
    var query by remember { mutableStateOf("") }
    val categories = remember(catalog) { listOf("All") + catalog.map { it.category }.distinct().sorted() }
    var category by remember { mutableStateOf("All") }
    val filtered = catalog.filter { entry ->
        (kindFilter == "All" || entry.kind.equals(kindFilter, ignoreCase = true)) &&
            (category == "All" || entry.category == category) &&
            (query.isBlank() || entry.name.contains(query, ignoreCase = true) ||
                entry.category.contains(query, ignoreCase = true) ||
                entry.notes.contains(query, ignoreCase = true))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("All", "MCP", "Skill", "Workspace").forEach { filter ->
                FilterChip(
                    selected = kindFilter == filter,
                    onClick = { kindFilter = filter },
                    label = { Text(filter) },
                )
            }
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            categories.take(12).forEach { name ->
                FilterChip(
                    selected = category == name,
                    onClick = { category = name },
                    label = { Text(name) },
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(filtered, key = { it.id }) { entry ->
                MarketplaceCard(
                    entry = entry,
                    onInstall = {
                        when (entry.kind) {
                            "mcp" -> if (entry.url.isNotBlank()) scope.launch {
                                XRepo.mcp.save(RuntimeMcpServer(
                                    name = entry.name,
                                    url = entry.url,
                                    enabled = false,
                                    headers = emptyMap(),
                                    transport = entry.transport,
                                    authMode = entry.auth,
                                ))
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun MarketplaceCard(entry: McpCatalogEntry, onInstall: () -> Unit) {
    SettingsGroupCard(
        items = listOf(entry),
        itemContent = { item, shape, standalone, _ ->
            SettingsListItem(
                title = item.name,
                summary = item.notes.ifBlank { item.category },
                shape = shape,
                isStandaloneCard = standalone,
                trailingContent = {
                    Text(
                        text = buildString {
                            append(item.kind.uppercase())
                            append(" · ")
                            append(if (item.free) "FREE" else "PAID")
                            append(if (item.verifiedOn.isNotBlank()) " · VERIFIED" else " · COMMUNITY")
                            if (item.needsKey) append(" · NEEDS KEY")
                            if (item.writes) append(" · WRITES")
                            if (item.spendsMoney) append(" · CAN SPEND")
                        },
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                onClick = onInstall,
            )
        },
    )
}
