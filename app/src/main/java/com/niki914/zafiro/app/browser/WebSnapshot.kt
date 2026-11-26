package com.niki914.zafiro.app.browser

import com.niki914.zafiro.api.screen.NodeInfo
import com.niki914.zafiro.api.screen.Rect
import com.niki914.zafiro.api.screen.SemanticType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull

/** What the page script reports about the document, with nodes already in device pixels. */
data class WebPage(
    val url: String,
    val title: String,
    val viewportCssWidth: Int,
    val viewportCssHeight: Int,
    val scrollY: Int,
    val scrollHeight: Int,
    val truncated: Boolean,
    val dialog: String,
    val nodes: List<NodeInfo>,
)

/** Pure parsing of the JSON that `zbrowser.js` returns. No Android types, so it runs in plain JUnit. */
object WebSnapshotParser {
    const val MAX_NODES = 300

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    /** `evaluateJavascript` hands back a JSON literal; a string result arrives quoted and escaped. */
    fun unwrapJsResult(raw: String?): String? {
        val text = raw?.trim().orEmpty()
        if (text.isEmpty() || text == "null" || text == "undefined") return null
        if (!text.startsWith("\"")) return text
        return try {
            (json.parseToJsonElement(text) as? JsonPrimitive)?.contentOrNull
        } catch (_: Exception) {
            null
        }
    }

    fun typeOf(kind: String): SemanticType = when (kind) {
        "btn" -> SemanticType.BUTTON
        "link" -> SemanticType.LINK
        "in" -> SemanticType.INPUT
        "h" -> SemanticType.HEADING
        "img" -> SemanticType.IMAGE
        "item" -> SemanticType.LIST_ITEM
        "list" -> SemanticType.LIST
        "sw" -> SemanticType.SWITCH
        "chk" -> SemanticType.CHECKBOX
        "tab" -> SemanticType.TAB
        "txt" -> SemanticType.TEXT
        else -> SemanticType.CONTAINER
    }

    /**
     * @param deviceWidthPx width of the WebView in device pixels; css px are scaled by deviceWidthPx / viewport width,
     * so coordinates in the snapshot are the same ones `x`/`y` taps accept.
     */
    fun parse(raw: String, deviceWidthPx: Int): WebPage? {
        val root = try {
            json.parseToJsonElement(raw) as? JsonObject
        } catch (_: Exception) {
            null
        } ?: return null
        val vw = root.int("vw")
        val vh = root.int("vh")
        val scale = if (vw > 0 && deviceWidthPx > 0) deviceWidthPx.toDouble() / vw else 1.0
        val nodes = ArrayList<NodeInfo>()
        val seen = HashSet<String>()
        for (element in (root["n"] as? JsonArray).orEmpty()) {
            val n = element as? JsonObject ?: continue
            val r = n.rect() ?: continue
            if (r[2] - r[0] <= 0 || r[3] - r[1] <= 0) continue
            if (vw > 0 && vh > 0 && (r[0] >= vw || r[2] <= 0 || r[1] >= vh || r[3] <= 0)) continue
            val type = typeOf(n.str("k"))
            val text = n.str("t")
            val desc = n.str("d")
            val interactive = n.flag("c") || n.flag("e") || n.flag("s")
            if (text.isEmpty() && desc.isEmpty() && !interactive) continue
            // The same line twice in a row at the same spot is a duplicate; identical rows elsewhere stay.
            val key = "${n.str("k")}|$text|$desc|${r[0]},${r[1]},${r[2]},${r[3]}"
            if (!seen.add(key)) continue
            if (nodes.size >= MAX_NODES) break
            nodes += NodeInfo(
                index = nodes.size + 1,
                semanticType = type,
                text = text,
                contentDesc = desc,
                bounds = Rect(
                    (r[0] * scale).toInt(),
                    (r[1] * scale).toInt(),
                    (r[2] * scale).toInt(),
                    (r[3] * scale).toInt(),
                ),
                isClickable = n.flag("c"),
                isLongClickable = false,
                isEditable = n.flag("e"),
                isScrollable = n.flag("s"),
                isChecked = n.flag("x"),
                children = emptyList(),
            )
        }
        return WebPage(
            url = root.str("url"),
            title = root.str("title"),
            viewportCssWidth = vw,
            viewportCssHeight = vh,
            scrollY = root.int("sy"),
            scrollHeight = root.int("sh"),
            truncated = (root["truncated"] as? JsonPrimitive)?.contentOrNull == "true",
            dialog = root.str("dialog"),
            nodes = nodes,
        )
    }

    /** `{"ok":true}` or `{"ok":false,"code":"NODE_STALE"}`; null when the script did not answer. */
    fun parseAction(raw: String?): ActionAnswer? {
        val text = unwrapJsResult(raw) ?: return null
        val o = try {
            json.parseToJsonElement(text) as? JsonObject
        } catch (_: Exception) {
            null
        } ?: return null
        val ok = (o["ok"] as? JsonPrimitive)?.contentOrNull == "true"
        return ActionAnswer(ok, o.str("code").ifEmpty { null })
    }

    private fun JsonObject.str(key: String): String = (this[key] as? JsonPrimitive)?.contentOrNull.orEmpty()

    private fun JsonObject.int(key: String): Int {
        val p = this[key] as? JsonPrimitive ?: return 0
        return p.intOrNull ?: p.doubleOrNull?.toInt() ?: 0
    }

    private fun JsonObject.flag(key: String): Boolean = int(key) != 0

    private fun JsonObject.rect(): IntArray? {
        val a = this["r"] as? JsonArray ?: return null
        if (a.size != 4) return null
        val out = IntArray(4)
        for (i in 0 until 4) {
            val p = a[i] as? JsonPrimitive ?: return null
            out[i] = p.intOrNull ?: p.doubleOrNull?.toInt() ?: return null
        }
        return out
    }
}

data class ActionAnswer(val ok: Boolean, val code: String?)

/** The `yaml` of a browser snapshot: header lines the compact view reads plus every node with full text (for `format:"full"`). */
object WebYaml {
    fun build(page: WebPage, screenId: String, widthPx: Int, heightPx: Int, version: String): String {
        val host = hostOf(page.url).ifEmpty { "blank" }
        val sb = StringBuilder()
        sb.append("app: ").append(host).append('\n')
        sb.append("screen: ").append(screenId).append('\n')
        sb.append("version: \"").append(version).append("\"\n")
        sb.append("size: ").append(widthPx).append('x').append(heightPx).append('\n')
        sb.append("url: ").append(page.url).append('\n')
        sb.append("page: ").append(pageLine(page)).append('\n')
        if (page.dialog.isNotEmpty()) sb.append("dialog: ").append(page.dialog.replace('\n', ' ')).append('\n')
        for (n in page.nodes) {
            sb.append('[').append(n.index).append("] ").append(n.semanticType.name.lowercase())
            if (n.text.isNotEmpty()) sb.append(" text=\"").append(n.text.replace('\n', ' ')).append('"')
            if (n.contentDesc.isNotEmpty()) sb.append(" desc=\"").append(n.contentDesc.replace('\n', ' ')).append('"')
            sb.append(" bounds=").append(n.bounds.left).append(',').append(n.bounds.top).append(',').append(n.bounds.right).append(',').append(n.bounds.bottom)
            if (n.isClickable) sb.append(" clickable")
            if (n.isEditable) sb.append(" editable")
            if (n.isScrollable) sb.append(" scrollable")
            if (n.isChecked) sb.append(" checked")
            sb.append('\n')
        }
        return sb.toString()
    }

    fun pageLine(page: WebPage): String {
        val vh = page.viewportCssHeight
        val more = if (page.scrollHeight > page.scrollY + vh + 8) " (more below)" else ""
        val scroll = if (page.scrollHeight > vh + 8) " | scroll ${page.scrollY}/${page.scrollHeight}$more" else ""
        val cut = if (page.truncated) " | list cut at ${WebSnapshotParser.MAX_NODES} nodes" else ""
        return page.title.ifEmpty { "(no title)" } + scroll + cut
    }

    fun hostOf(url: String): String {
        val afterScheme = url.substringAfter("://", "")
        if (afterScheme.isEmpty()) return ""
        return afterScheme.substringBefore('/').substringBefore('?').substringBefore('#').substringAfter('@')
    }
}
