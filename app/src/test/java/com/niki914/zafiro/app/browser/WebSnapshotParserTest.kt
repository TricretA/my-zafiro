package com.niki914.zafiro.app.browser

import com.niki914.zafiro.accessibility.table.DisplayNodeTable
import com.niki914.zafiro.api.screen.SemanticToken
import com.niki914.zafiro.api.screen.SemanticType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// 保护：页面脚本返回的 JSON 变成模型看到的节点；坏输入、屏外/零尺寸节点、重复、超量、坐标缩放出错会让点击落空或撑爆 token。
class WebSnapshotParserTest {
    private fun doc(nodes: String, vw: Int = 360, vh: Int = 640) =
        """{"url":"https://a.com/p","title":"T","vw":$vw,"vh":$vh,"sx":0,"sy":0,"sw":$vw,"sh":2000,"truncated":false,"n":[$nodes]}"""

    private fun n(k: String, t: String, r: String, extra: String = "") =
        """{"k":"$k","t":"$t","d":"","r":$r${if (extra.isEmpty()) "" else ",$extra"}}"""

    @Test
    fun unwrapsQuotedJsStringAndRejectsNullResults() {
        assertEquals("""{"ok":true}""", WebSnapshotParser.unwrapJsResult(""""{\"ok\":true}""""))
        assertNull(WebSnapshotParser.unwrapJsResult("null"))
        assertNull(WebSnapshotParser.unwrapJsResult(null))
        assertNull(WebSnapshotParser.unwrapJsResult(""))
    }

    @Test
    fun scalesCssPixelsToDevicePixelsByViewportWidth() {
        val page = WebSnapshotParser.parse(doc(n("btn", "Go", "[10,20,110,60]", """"c":1""")), deviceWidthPx = 720)!!
        val b = page.nodes.single().bounds
        assertEquals(listOf(20, 40, 220, 120), listOf(b.left, b.top, b.right, b.bottom))
        assertTrue(page.nodes.single().isClickable)
    }

    @Test
    fun mapsKindsToSemanticTypes() {
        val nodes = listOf("link", "h", "in", "chk", "sw", "img", "item").mapIndexed { i, k ->
            n(k, "x$i", "[0,${i * 20},100,${i * 20 + 10}]", """"c":1""")
        }.joinToString(",")
        val types = WebSnapshotParser.parse(doc(nodes), 360)!!.nodes.map { it.semanticType }
        assertEquals(
            listOf(SemanticType.LINK, SemanticType.HEADING, SemanticType.INPUT, SemanticType.CHECKBOX, SemanticType.SWITCH, SemanticType.IMAGE, SemanticType.LIST_ITEM),
            types,
        )
    }

    @Test
    fun dropsOffscreenZeroSizeEmptyAndDuplicateNodes() {
        val nodes = listOf(
            n("txt", "kept", "[0,0,100,20]"),
            n("txt", "kept", "[0,0,100,20]"),
            n("txt", "below", "[0,700,100,720]"),
            n("txt", "flat", "[5,5,5,30]"),
            n("txt", "", "[0,40,100,60]"),
            n("txt", "same text elsewhere", "[0,100,100,120]"),
            n("txt", "same text elsewhere", "[0,200,100,220]"),
        ).joinToString(",")
        val out = WebSnapshotParser.parse(doc(nodes), 360)!!.nodes
        assertEquals(listOf("kept", "same text elsewhere", "same text elsewhere"), out.map { it.text })
        assertEquals(listOf(1, 2, 3), out.map { it.index })
    }

    @Test
    fun capsNodeCount() {
        val nodes = (0 until 400).joinToString(",") { n("txt", "row$it", "[0,${it % 600},100,${it % 600 + 10}]") }
        assertEquals(WebSnapshotParser.MAX_NODES, WebSnapshotParser.parse(doc(nodes), 360)!!.nodes.size)
    }

    @Test
    fun malformedDocumentIsNull() {
        assertNull(WebSnapshotParser.parse("not json", 360))
        assertNull(WebSnapshotParser.parse("[]", 360))
    }

    @Test
    fun actionAnswerCarriesFailureCode() {
        val a = WebSnapshotParser.parseAction(""""{\"ok\":false,\"code\":\"NODE_STALE\"}"""")
        assertNotNull(a)
        assertFalse(a!!.ok)
        assertEquals("NODE_STALE", a.code)
        assertNull(WebSnapshotParser.parseAction("null"))
    }

    // 旧版本的 token 在新快照后必须失效，否则会点到别的元素。
    @Test
    fun tokenFromAnOlderSnapshotIsStale() {
        val table = DisplayNodeTable()
        val nodes = WebSnapshotParser.parse(doc(n("btn", "Go", "[0,0,50,20]", """"c":1""")), 360)!!.nodes
        val v1 = table.mintVersion().also { table.remember(it, nodes) }
        assertNotNull(table.resolve(SemanticToken(v1, 1)))
        val v2 = table.mintVersion().also { table.remember(it, nodes) }
        assertNull(table.resolve(SemanticToken(v1, 1)))
        assertNotNull(table.resolve(SemanticToken(v2, 1)))
    }

    @Test
    fun yamlHeaderNamesTheHostAndScrollState() {
        val page = WebSnapshotParser.parse(doc(n("txt", "hi", "[0,0,100,20]")), 360)!!
        val yaml = WebYaml.build(page, "web-1", 360, 640, "ab")
        assertTrue(yaml.startsWith("app: a.com\n"))
        assertTrue(yaml.contains("page: T | scroll 0/2000 (more below)"))
    }
}
