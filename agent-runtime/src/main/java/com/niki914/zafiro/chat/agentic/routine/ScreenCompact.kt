package com.niki914.zafiro.chat.agentic.routine

import com.niki914.zafiro.api.screen.NodeInfo
import com.niki914.zafiro.api.screen.ScreenSnapshot
import com.niki914.zafiro.api.screen.SemanticType

/**
 * Token-lean view of a screen for the model.
 *
 * The verbose tree (bounds, position words, container nesting, a fresh random version on every read) costs several thousand
 * tokens per step and is re-sent on every later step. This keeps only what the agent can act on or read, one short line each:
 *
 *     app:com.substack.app 1080x2400
 *     [12]btn "Post" tap
 *     [13]in "What's on your mind" edit
 *     [14]txt "Hello world"
 *
 * The number in brackets is the node index: pass it as `token` (the current screen version is implied).
 * Ideas adopted from Artemis (observation = indexed element list) and agent-browser (interactive-only, short refs).
 */
object ScreenCompact {
    const val MAX_LINES = 110
    const val MAX_TEXT = 60

    private val APP = Regex("""(?m)^app:\s*(\S+)""")

    fun format(s: ScreenSnapshot): String {
        val lines = lines(s)
        val shown = lines.take(MAX_LINES)
        val pkg = APP.find(s.yaml)?.groupValues?.get(1).orEmpty()
        val sb = StringBuilder()
        sb.append("app:").append(pkg.ifEmpty { "?" }).append(' ').append(s.width).append('x').append(s.height).append('\n')
        shown.forEach { sb.append(it.text).append('\n') }
        if (lines.size > MAX_LINES) sb.append("... ").append(lines.size - MAX_LINES).append(" more nodes hidden; use operation=search to find them\n")
        if (lines.isEmpty()) sb.append("(no readable or tappable elements)\n")
        return sb.toString().trimEnd()
    }

    /** What changes when the screen really changes. Excludes the node indices and the per-read version, which differ on every read. */
    fun signature(s: ScreenSnapshot): String = lines(s).joinToString("\n") { it.body }

    class Line(val index: Int, val body: String) {
        val text: String get() = "[$index]$body"
    }

    fun lines(s: ScreenSnapshot): List<Line> {
        val out = ArrayList<Line>()
        val consumed = HashSet<Int>()
        val flat = flatten(s.nodes)
        for (n in flat) {
            if (n.index in consumed) continue
            val interactive = n.isClickable || n.isLongClickable || n.isEditable || n.isScrollable
            var label = clean(n.text)
            var desc = clean(n.contentDesc)
            var derived = false
            if (label.isEmpty() && desc.isEmpty() && n.isClickable) {
                val child = descendants(n).firstOrNull { clean(it.text).isNotEmpty() || clean(it.contentDesc).isNotEmpty() }
                if (child != null) {
                    label = clean(child.text).ifEmpty { clean(child.contentDesc) }
                    derived = true
                    descendants(n).filter { clean(it.text) == label || clean(it.contentDesc) == label }.forEach { consumed += it.index }
                }
            }
            if (label.isEmpty() && desc.isEmpty() && !interactive && !n.isChecked) continue
            if (desc == label) desc = ""
            val sb = StringBuilder()
            sb.append(code(n.semanticType))
            if (label.isNotEmpty()) sb.append(' ').append(if (derived) "~" else "").append('"').append(label).append('"')
            if (desc.isNotEmpty()) sb.append(" h:\"").append(desc).append('"')
            if (n.isClickable) sb.append(" tap")
            if (n.isLongClickable) sb.append(" hold")
            if (n.isEditable) sb.append(" edit")
            if (n.isScrollable) sb.append(" scroll")
            if (n.isChecked) sb.append(" on")
            out += Line(n.index, sb.toString())
        }
        // Identical rows ("Like", "Like", ...) are all kept: each has its own index and can be the target.
        return out
    }

    private fun clean(raw: String): String {
        val t = raw.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').trim().replace(Regex("\\s{2,}"), " ")
        return if (t.length > MAX_TEXT) t.take(MAX_TEXT) + "…" else t
    }

    private fun code(t: SemanticType): String = when (t) {
        SemanticType.BUTTON -> "btn"
        SemanticType.INPUT -> "in"
        SemanticType.TEXT -> "txt"
        SemanticType.IMAGE -> "img"
        SemanticType.LIST -> "list"
        SemanticType.LIST_ITEM -> "item"
        SemanticType.SWITCH -> "sw"
        SemanticType.CHECKBOX -> "chk"
        SemanticType.TAB -> "tab"
        SemanticType.CHIP -> "chip"
        SemanticType.TOOLBAR -> "bar"
        SemanticType.DIALOG -> "dlg"
        SemanticType.CONTAINER -> "box"
    }

    private fun flatten(nodes: List<NodeInfo>): List<NodeInfo> = nodes.flatMap { listOf(it) + flatten(it.children) }
    private fun descendants(n: NodeInfo): List<NodeInfo> = flatten(n.children)
}
