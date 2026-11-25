package com.niki914.zafiro.chat.agentic.buildin.impl

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.File

// 保护：分享工具只能读 Zafiro 自己的 files 目录（模型给的路径不可信），且类型要按扩展名猜对，否则目标应用不接收。
class ShareFileBuiltinTest {
    private val root = File(System.getProperty("java.io.tmpdir"), "zafiro-share-test").apply { mkdirs() }

    @Test
    fun rejectsPathsOutsideTheFilesFolder() {
        assertNull(ShareFileBuiltin.resolveInside(root, "../../etc/passwd"))
        assertNull(ShareFileBuiltin.resolveInside(root, "/etc/passwd"))
    }

    @Test
    fun acceptsRelativeAndAbsolutePathsInside() {
        assertNotNull(ShareFileBuiltin.resolveInside(root, "outputs/clip.mp4"))
        assertNotNull(ShareFileBuiltin.resolveInside(root, File(root, "outputs/clip.mp4").absolutePath))
    }

    @Test
    fun guessesTheMimeTypeFromTheExtension() {
        assertEquals("video/mp4", ShareFileBuiltin.mimeFor("outputs/Clip.MP4"))
        assertEquals("image/png", ShareFileBuiltin.mimeFor("a.png"))
        assertEquals("*/*", ShareFileBuiltin.mimeFor("noext"))
    }
}
