package com.niki914.zafiro.app.ui.content.mcp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.niki914.uikit.infra.component.settings.SettingsPageSpec
import com.niki914.uikit.infra.component.settings.SettingsRowAction
import com.niki914.uikit.infra.component.settings.SettingsRowSpec
import com.niki914.uikit.infra.component.settings.SettingsSectionLayout
import com.niki914.uikit.infra.component.settings.SettingsSectionSpec
import com.niki914.uikit.infra.component.settings.SettingsSpecPageContent
import com.niki914.uikit.dialog.ZafiroInputDialog
import com.niki914.uikit.infra.nav.pageViewModel
import com.niki914.zafiro.app.R
import com.niki914.zafiro.app.ui.PageChromeContribution
import com.niki914.zafiro.app.ui.RegisterPageChrome
import com.niki914.zafiro.app.ui.model.McpSettingsIntent
import com.niki914.zafiro.app.ui.model.McpSettingsUiState
import com.niki914.zafiro.app.ui.model.McpSettingsViewModel
import com.niki914.zafiro.app.ui.nav.TopBarActionSpec
import com.niki914.zafiro.mcp.McpCore
import com.niki914.zafiro.repo.XRepo
import com.niki914.zafiro.settings.model.RuntimeMcpServer
import kotlinx.coroutines.launch

private const val MCP_SERVER_ROW_ID_PREFIX = "mcp.server."
private const val MCP_IMPORT_ROW_ID = "mcp.import-json"

@Composable
fun McpSettingsContent(
    onOpenServerDetail: (serverName: String, serverIndex: Int, isCreating: Boolean) -> Unit,
) {
    val viewModel = pageViewModel<McpSettingsViewModel>()
    val uiState by viewModel.uiStateFlow.collectAsState()
    val scope = rememberCoroutineScope()
    var showImport by rememberSaveable { mutableStateOf(false) }
    var importJson by rememberSaveable { mutableStateOf("") }
    val createTitle = stringResource(R.string.mcp_editor_title_create)
    val latestOnOpenServerDetail by rememberUpdatedState(onOpenServerDetail)
    val pageChromeContribution = remember(createTitle) {
        PageChromeContribution(
            rightAction = TopBarActionSpec(
                icon = Icons.Default.Add,
                onClick = {
                    latestOnOpenServerDetail(createTitle, -1, true)
                },
                contentDescription = "Add Button",
            ),
        )
    }
    RegisterPageChrome(pageChromeContribution)

    LaunchedEffect(Unit) {
        viewModel.sendIntent(McpSettingsIntent.Load)
    }
    val pageDescription = when {
        uiState.isLoading || uiState.items.isNotEmpty() -> stringResource(R.string.mcp_page_description)
        else -> stringResource(R.string.mcp_page_empty_description)
    }
    val loadingText = stringResource(R.string.mcp_loading)
    val spec = mcpSettingsSpec(
        uiState = uiState,
        pageDescription = pageDescription,
        loadingText = loadingText,
    )

    SettingsSpecPageContent(spec = spec) { action ->
        when (action) {
            is SettingsRowAction.Click -> {
                if (action.id == MCP_IMPORT_ROW_ID) showImport = true
            }
            is SettingsRowAction.Navigate -> {
                val index = mcpServerIndexFromRowId(action.id) ?: return@SettingsSpecPageContent
                val item = uiState.items.getOrNull(index) ?: return@SettingsSpecPageContent
                onOpenServerDetail(item.name, index, false)
            }

            is SettingsRowAction.ToggleChanged -> {
                val index = mcpServerIndexFromRowId(action.id) ?: return@SettingsSpecPageContent
                viewModel.sendIntent(
                    McpSettingsIntent.ItemEnabledChanged(
                        index = index,
                        value = action.checked,
                    )
                )
            }

            else -> Unit
        }
    }

    ZafiroInputDialog(
        visible = showImport,
        onDismissRequest = { showImport = false },
        title = "Import mcpServers JSON",
        hint = "Paste Claude or Cursor mcpServers JSON",
        value = importJson,
        onValueChange = { importJson = it },
        placeholder = """{"mcpServers":{"Example":{"url":"https://example.com/mcp"}}}""",
        positiveButtonText = "Import",
        positiveEnabled = importJson.trim().isNotBlank(),
        onPositiveClick = {
            val parsed = McpCore.parseMcpServersJson(importJson)
            parsed.onSuccess { servers ->
                scope.launch {
                    servers.forEach { server ->
                        XRepo.mcp.save(RuntimeMcpServer(
                            name = server.name,
                            url = server.url,
                            enabled = false,
                            headers = server.headers,
                            transport = server.transport,
                            authMode = server.auth,
                        ))
                    }
                    showImport = false
                    importJson = ""
                    viewModel.sendIntent(McpSettingsIntent.Load)
                }
            }
        },
        negativeButtonText = "Cancel",
        onNegativeClick = { showImport = false },
    )
}

private fun mcpSettingsSpec(
    uiState: McpSettingsUiState,
    pageDescription: String,
    loadingText: String,
): SettingsPageSpec {
    val sections = mutableListOf(
        SettingsSectionSpec(
            layout = SettingsSectionLayout.GroupedCard,
            rows = listOf(
                SettingsRowSpec.Action(
                    id = MCP_IMPORT_ROW_ID,
                    title = "Import mcpServers JSON",
                    summary = "Paste Claude or Cursor MCP configuration",
                )
            ),
        )
    )
    when {
        uiState.isLoading -> listOf<SettingsSectionSpec>(
            SettingsSectionSpec(
                layout = SettingsSectionLayout.GroupedCard,
                rows = listOf(SettingsRowSpec.Message(title = loadingText)),
            )
        ).let(sections::addAll)

        uiState.items.isNotEmpty() -> listOf(
            SettingsSectionSpec(
                layout = SettingsSectionLayout.CardList,
                rows = uiState.items.mapIndexed { index, item ->
                    SettingsRowSpec.ToggleNavigation(
                        id = mcpServerRowId(index),
                        title = item.name,
                        checked = item.enabled,
                        enabled = !uiState.isSaving,
                    )
                }
            )
        ).let(sections::addAll)

        else -> emptyList()
    }

    return SettingsPageSpec(
        description = pageDescription,
        sections = sections,
    )
}

private fun mcpServerRowId(index: Int): String = "$MCP_SERVER_ROW_ID_PREFIX$index"

private fun mcpServerIndexFromRowId(id: String): Int? {
    if (!id.startsWith(MCP_SERVER_ROW_ID_PREFIX)) return null
    return id.removePrefix(MCP_SERVER_ROW_ID_PREFIX).toIntOrNull()
}
