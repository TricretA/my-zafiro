// 保护：PublicFileExporter 路径规范化与 StudioRenderBuiltin 的仓库校验/缺失 token 拦截；改坏路径清洗或校验会让文件写逃逸或调用空 token 失败。
package com.niki914.zafiro.chat.agentic.buildin.impl

import com.niki914.zafiro.api.StudioConfig
import com.niki914.zafiro.chat.agentic.buildin.BuiltinToolRequest
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StudioRenderBuiltinTest {

    @Test
    fun normalizeFolder_mapsUserAliasesAndRejectsTraversal() {
        assertEquals("Download/MyReels", PublicFileExporter.normalizeFolder("/sdcard/downloads/MyReels"))
        assertEquals("Movies/ClientA", PublicFileExporter.normalizeFolder("/storage/emulated/0/movies/ClientA/"))
        assertEquals("Download/Zafiro", PublicFileExporter.normalizeFolder("../escape"))
    }

    @Test
    fun studioRender_missingTokenBlocksWriteOpsWithoutThrowing() = runBlocking {
        val tool = StudioRenderBuiltin { StudioConfig(githubToken = "", repo = "TricretA/zafiro-studio", outputFolder = "Download/Zafiro") }
        val status = tool.invoke(BuiltinToolRequest("studio_render", """{"op":"status"}"""))
        assertTrue(status.ok)
        assertFalse(status.data.toString().contains("Bearer"))

        val dispatch = tool.invoke(BuiltinToolRequest("studio_render", """{"op":"dispatch","job_path":"jobs/a.json"}"""))
        assertFalse(dispatch.ok)
        assertEquals("TOKEN_MISSING", dispatch.code)
    }

    @Test
    fun studioRenderSupport_validatesRepoPathsAndPicksMp4Asset() {
        assertNull(StudioRenderSupport.validRepo("bad repo"))
        assertNull(StudioRenderSupport.validRepoPath("../secrets"))
        val assets = buildJsonArray {
            add(buildJsonObject { put("name", "out.probe.json"); put("id", 1) })
            add(buildJsonObject { put("name", "final.mp4"); put("id", 2) })
        }
        val picked = StudioRenderSupport.pickMp4Asset(assets, null)
        assertEquals("final.mp4", picked?.get("name")?.jsonPrimitive?.content)
    }
}
