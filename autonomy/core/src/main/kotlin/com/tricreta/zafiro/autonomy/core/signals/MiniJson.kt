package com.tricreta.zafiro.autonomy.core.signals

/**
 * A tiny JSON reader/writer so the signals store and config stay pure JVM (no org.json, which is Android-only).
 * Values are Map<String, Any?>, List<Any?>, String, Long, Double, Boolean or null. Input that does not parse yields null.
 */
object MiniJson {
    fun parse(text: String): Any? = try {
        val p = Parser(text)
        val v = p.value()
        p.skipWs()
        if (p.i != text.length) null else v
    } catch (e: IllegalArgumentException) {
        null
    } catch (e: IndexOutOfBoundsException) {
        null
    }

    @Suppress("UNCHECKED_CAST")
    fun parseObject(text: String): Map<String, Any?>? = parse(text) as? Map<String, Any?>

    fun write(v: Any?): String = StringBuilder().also { write(it, v) }.toString()

    private fun write(sb: StringBuilder, v: Any?) {
        when (v) {
            null -> sb.append("null")
            is String -> quote(sb, v)
            is Boolean -> sb.append(v)
            is Int, is Long -> sb.append(v.toString())
            is Double -> sb.append(if (v % 1.0 == 0.0 && kotlin.math.abs(v) < 1e15) v.toLong().toString() else v.toString())
            is Map<*, *> -> {
                sb.append('{')
                var first = true
                for ((k, x) in v) {
                    if (!first) sb.append(',')
                    first = false
                    quote(sb, k.toString()); sb.append(':'); write(sb, x)
                }
                sb.append('}')
            }
            is Iterable<*> -> {
                sb.append('[')
                var first = true
                for (x in v) {
                    if (!first) sb.append(',')
                    first = false
                    write(sb, x)
                }
                sb.append(']')
            }
            else -> quote(sb, v.toString())
        }
    }

    private fun quote(sb: StringBuilder, s: String) {
        sb.append('"')
        for (c in s) {
            when {
                c == '"' -> sb.append("\\\"")
                c == '\\' -> sb.append("\\\\")
                c == '\n' -> sb.append("\\n")
                c == '\r' -> sb.append("\\r")
                c == '\t' -> sb.append("\\t")
                c < ' ' -> sb.append("\\u").append(c.code.toString(16).padStart(4, '0'))
                else -> sb.append(c)
            }
        }
        sb.append('"')
    }

    private class Parser(val s: String) {
        var i = 0

        fun skipWs() { while (i < s.length && s[i].isWhitespace()) i++ }

        fun value(): Any? {
            skipWs()
            require(i < s.length) { "eof" }
            return when (val c = s[i]) {
                '{' -> obj()
                '[' -> arr()
                '"' -> str()
                't' -> lit("true", true)
                'f' -> lit("false", false)
                'n' -> lit("null", null)
                else -> if (c == '-' || c.isDigit()) num() else throw IllegalArgumentException("bad char")
            }
        }

        private fun lit(word: String, v: Any?): Any? {
            require(s.startsWith(word, i)) { "bad literal" }
            i += word.length
            return v
        }

        private fun num(): Any {
            val start = i
            if (s[i] == '-') i++
            while (i < s.length && (s[i].isDigit() || s[i] in ".eE+-")) i++
            val t = s.substring(start, i)
            return t.toLongOrNull() ?: t.toDoubleOrNull() ?: throw IllegalArgumentException("bad number")
        }

        private fun str(): String {
            i++
            val sb = StringBuilder()
            while (true) {
                require(i < s.length) { "unterminated string" }
                val c = s[i++]
                when (c) {
                    '"' -> return sb.toString()
                    '\\' -> {
                        require(i < s.length) { "bad escape" }
                        when (val e = s[i++]) {
                            'n' -> sb.append('\n'); 'r' -> sb.append('\r'); 't' -> sb.append('\t')
                            'b' -> sb.append('\b'); 'f' -> sb.append('\u000c')
                            'u' -> {
                                require(i + 4 <= s.length) { "bad unicode" }
                                sb.append(s.substring(i, i + 4).toInt(16).toChar()); i += 4
                            }
                            else -> sb.append(e)
                        }
                    }
                    else -> sb.append(c)
                }
            }
        }

        private fun arr(): List<Any?> {
            i++
            val out = ArrayList<Any?>()
            skipWs()
            if (s[i] == ']') { i++; return out }
            while (true) {
                out.add(value())
                skipWs()
                when (s[i++]) { ',' -> continue; ']' -> return out; else -> throw IllegalArgumentException("bad array") }
            }
        }

        private fun obj(): Map<String, Any?> {
            i++
            val out = LinkedHashMap<String, Any?>()
            skipWs()
            if (s[i] == '}') { i++; return out }
            while (true) {
                skipWs()
                require(s[i] == '"') { "key expected" }
                val k = str()
                skipWs()
                require(s[i++] == ':') { "colon expected" }
                out[k] = value()
                skipWs()
                when (s[i++]) { ',' -> continue; '}' -> return out; else -> throw IllegalArgumentException("bad object") }
            }
        }
    }
}
