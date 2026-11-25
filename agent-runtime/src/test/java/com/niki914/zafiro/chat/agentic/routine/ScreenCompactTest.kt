package com.niki914.zafiro.chat.agentic.routine

import com.niki914.zafiro.api.screen.NodeInfo
import com.niki914.zafiro.api.screen.Rect
import com.niki914.zafiro.api.screen.ScreenId
import com.niki914.zafiro.api.screen.ScreenSnapshot
import com.niki914.zafiro.api.screen.SemanticType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// 保护：模型每步都会收到屏幕文本；它必须短、保留可点击/可读的节点、且「是否变化」的判断不能被每次随机的 version 污染。
class ScreenCompactTest {
    private fun node(
        index: Int,
        text: String = "",
        type: SemanticType = SemanticType.TEXT,
        clickable: Boolean = false,
        editable: Boolean = false,
        children: List<NodeInfo> = emptyList(),
        desc: String = "",
    ) = NodeInfo(index, type, text, desc, Rect(0, 0, 10, 10), clickable, false, editable, false, false, children)

    private fun snap(version: String, nodes: List<NodeInfo>, yaml: String = "app: com.x\nversion: \"$version\"") =
        ScreenSnapshot(ScreenId("phone"), version, 1080, 2400, null, nodes, yaml)

    @Test
    fun keepsTappableAndReadableNodesAndDropsEmptyShells() {
        val tree = listOf(
            node(0, children = listOf(
                node(1, "Post", SemanticType.BUTTON, clickable = true),
                node(2, "Hello world"),
                node(3, children = listOf(node(4))),
                node(5, "What's on your mind", SemanticType.INPUT, editable = true),
            )),
        )
        val out = ScreenCompact.format(snap("a1", tree))
        assertTrue(out.startsWith("app:com.x 1080x2400"))
        assertTrue(out.contains("[1]btn \"Post\" tap"))
        assertTrue(out.contains("[2]txt \"Hello world\""))
        assertTrue(out.contains("[5]in \"What's on your mind\" edit"))
        assertFalse(out.contains("[0]"))
        assertFalse(out.contains("[3]"))
        assertFalse(out.contains("[4]"))
    }

    @Test
    fun clickableContainerTakesItsChildsLabelInsteadOfTwoLines() {
        val row = node(1, type = SemanticType.CONTAINER, clickable = true, children = listOf(node(2, "Notifications")))
        val out = ScreenCompact.format(snap("a1", listOf(row)))
        assertTrue(out.contains("[1]box ~\"Notifications\" tap"))
        assertFalse(out.contains("[2]"))
    }

    @Test
    fun signatureIgnoresVersionAndIndicesButSeesRealChanges() {
        val a = snap("v1", listOf(node(1, "Post", SemanticType.BUTTON, clickable = true)))
        val sameContentNewVersion = snap("v2", listOf(node(7, "Post", SemanticType.BUTTON, clickable = true)))
        val changed = snap("v3", listOf(node(1, "Posted", SemanticType.BUTTON, clickable = true)))
        assertEquals(ScreenCompact.signature(a), ScreenCompact.signature(sameContentNewVersion))
        assertNotEquals(ScreenCompact.signature(a), ScreenCompact.signature(changed))
    }

    @Test
    fun capsLongScreensAndLongText() {
        val many = (1..200).map { node(it, "item $it ${"x".repeat(100)}") }
        val out = ScreenCompact.format(snap("a", many))
        assertTrue(out.contains("more nodes hidden"))
        assertTrue(out.lines().size <= ScreenCompact.MAX_LINES + 3)
        assertTrue(out.lines().all { it.length < 140 })
    }

    @Test
    fun compactIsMuchSmallerThanTheVerboseTreeForTheSameScreen() {
        val many = (1..60).map { node(it, "Row $it", SemanticType.BUTTON, clickable = true) }
        val compact = ScreenCompact.format(snap("a", many))
        val verbose = many.joinToString("\n") {
            "  - {i: ${it.index}, t: button, b: [0,${it.index * 40},1080,${it.index * 40 + 39}], pos: top-left, txt: \"${it.text}\", tap: true}"
        }
        assertTrue(compact.length < verbose.length * 0.7)
    }
}
