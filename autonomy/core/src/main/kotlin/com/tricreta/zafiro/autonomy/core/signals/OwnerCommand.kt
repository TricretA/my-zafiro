package com.tricreta.zafiro.autonomy.core.signals

/** What the owner asked for in a message. Anything that is not an exact command is [Free]. */
sealed interface OwnerCommand {
    data object Status : OwnerCommand
    data class Pause(val dept: String) : OwnerCommand
    data class Resume(val dept: String) : OwnerCommand
    data class Priority(val text: String) : OwnerCommand
    data object StopAll : OwnerCommand
    data class Approve(val code: String) : OwnerCommand
    data class Reject(val code: String) : OwnerCommand
    data class Free(val text: String) : OwnerCommand
}

/**
 * The fixed grammar for messages from the owner. Matching is case-insensitive on the first word; an optional leading
 * "zafiro" and trailing punctuation are ignored. A command with a missing or malformed argument is NOT guessed at:
 * it falls back to [OwnerCommand.Free], which only ever becomes a board item for the team to read.
 *
 *   STATUS | STOP | STOP ALL | PAUSE <dept> | RESUME <dept> | PRIORITY <text> | YES <code> | NO <code> | anything else
 */
object OwnerCommandParser {
    private val CODE = Regex("[A-Za-z0-9]{1,12}")

    fun parse(raw: String): OwnerCommand {
        var text = raw.trim().replace(Regex("\\s+"), " ")
        if (text.isEmpty()) return OwnerCommand.Free("")
        val lead = Regex("^zafiro[,:]?\\s+", RegexOption.IGNORE_CASE)
        text = lead.replace(text, "")
        val firstEnd = text.indexOf(' ').let { if (it < 0) text.length else it }
        val word = text.substring(0, firstEnd).trimEnd('.', '!', ',', ':', ';').uppercase()
        val rest = text.substring(firstEnd).trim().trimEnd('.', '!', '?')
        return when (word) {
            "STATUS" -> if (rest.isEmpty()) OwnerCommand.Status else OwnerCommand.Free(text)
            "STOP" -> if (rest.isEmpty() || rest.equals("all", true) || rest.equals("everything", true)) OwnerCommand.StopAll else OwnerCommand.Free(text)
            "PAUSE" -> when {
                rest.equals("all", true) -> OwnerCommand.StopAll
                rest.isNotEmpty() && rest.length <= MAX_DEPT -> OwnerCommand.Pause(rest)
                else -> OwnerCommand.Free(text)
            }
            "RESUME" -> if (rest.isNotEmpty() && rest.length <= MAX_DEPT) OwnerCommand.Resume(rest) else OwnerCommand.Free(text)
            "PRIORITY" -> if (rest.isNotEmpty()) OwnerCommand.Priority(rest) else OwnerCommand.Free(text)
            "YES" -> if (CODE.matches(rest)) OwnerCommand.Approve(rest.uppercase()) else OwnerCommand.Free(text)
            "NO" -> if (CODE.matches(rest)) OwnerCommand.Reject(rest.uppercase()) else OwnerCommand.Free(text)
            else -> OwnerCommand.Free(text)
        }
    }

    private const val MAX_DEPT = 40
}

/** Maps what the owner typed ("substack", "Video Studio", "linkedin team") onto a workspace id. */
object DeptMatcher {
    data class Dept(val id: String, val name: String)

    fun resolve(query: String, depts: List<Dept>): Dept? {
        val q = norm(query).removeSuffix(" team").removeSuffix(" department").removeSuffix(" dept").trim()
        if (q.isEmpty()) return null
        depts.firstOrNull { norm(it.id) == q || norm(it.name) == q }?.let { return it }
        val hits = depts.filter { norm(it.id).startsWith(q) || norm(it.name).startsWith(q) }
        return hits.singleOrNull()
    }

    /** Every department mentioned by id or name inside a free text (whole words only), in the order of [depts]. */
    fun mentioned(text: String, depts: List<Dept>): List<Dept> {
        val t = " " + norm(text) + " "
        return depts.filter { d -> listOf(norm(d.id), norm(d.name)).any { it.length >= 2 && t.contains(" $it ") } }
    }

    private fun norm(s: String) = s.lowercase().replace(Regex("[^a-z0-9]+"), " ").trim()
}
