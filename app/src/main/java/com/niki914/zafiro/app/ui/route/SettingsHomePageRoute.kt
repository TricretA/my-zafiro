package com.niki914.zafiro.app.ui.route

import androidx.compose.runtime.Composable
import com.niki914.zafiro.app.ui.content.SettingsHomePageContent
import com.niki914.zafiro.app.ui.nav.SettingsDetailPage
import com.niki914.zafiro.app.ui.nav.ZafiroPage

@Composable
internal fun SettingsHomePageRoute(
    onPush: (ZafiroPage) -> Unit,
) {
    SettingsHomePageContent(
        onOpenGroup = { group -> onPush(SettingsDetailPage(group = group)) },
    )
}
