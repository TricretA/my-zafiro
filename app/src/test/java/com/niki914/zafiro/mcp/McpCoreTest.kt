// 保护：畸形配置和 OAuth 刷新边界会导致 MCP 连接静默失效或误发请求。
package com.niki914.zafiro.mcp

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class McpCoreTest {
    @Test
    fun parsesMcpServersJsonAndResolvesPlaceholders() {
        val parsed = McpCore.parseMcpServersJson(
            """{"mcpServers":{"Example":{"type":"http","url":"https://example.com/mcp","headers":{"Authorization":"Bearer ${'$'}{TOKEN}"}}}}"""
        ).getOrThrow()
        assertEquals(1, parsed.size)
        assertEquals("https://example.com/mcp", parsed.single().url)
        assertEquals("Bearer secret", McpCore.resolvePlaceholders(parsed.single().headers["Authorization"].orEmpty(), mapOf("TOKEN" to "secret")))
    }

    @Test
    fun refreshUsesSixtySecondSkew() {
        val record = McpOAuthRecord(
            serverName = "Example",
            accessToken = "access",
            refreshToken = "refresh",
            expiresAtMs = 100_000,
        )
        assertTrue(McpCore.needsRefresh(record, 50_000))
        assertFalse(McpCore.needsRefresh(record, 20_000))
    }

    @Test
    fun shippedCatalogParsesAndContainsCategoryCoverage() {
        val file = listOf(
            File("app/src/main/assets/mcp_catalog.json"),
            File("../app/src/main/assets/mcp_catalog.json"),
            File("src/main/assets/mcp_catalog.json"),
        ).firstOrNull(File::isFile) ?: throw AssertionError("mcp_catalog.json not found")
        val entries = McpCore.parseCatalog(file.readText()).getOrThrow()
        assertTrue(entries.isNotEmpty())
        assertTrue(entries.any { it.id == "metricool" })
        assertTrue(entries.any { it.category == "Finances" })
        assertTrue(entries.any { it.kind == "skill" || it.kind == "workspace" })
    }

    @Test
    fun githubSkillInstallerOnlyAcceptsGithubRepositoryUrls() {
        assertEquals(
            "https://github.com/owner/repo",
            McpCore.githubSkillUrl("https://github.com/owner/repo/tree/main").getOrThrow(),
        )
        assertTrue(McpCore.githubSkillUrl("https://example.com/owner/repo").isFailure)
        assertTrue(McpCore.validSkillFrontmatter(
            "---\nname: safe-skill\nlicense: MIT\n---\n# Skill",
            "safe-skill",
        ))
        assertFalse(McpCore.validSkillFrontmatter(
            "---\nname: wrong-name\nlicense: GPL-3.0\n---\n# Skill",
            "safe-skill",
        ))
    }

    @Test
    fun fakeCipherStoreRoundTripsThroughInterface() {
        val cipher = InMemoryMcpSecretCipher()
        val record = McpOAuthRecord("Example", "access", "refresh", 1234)
        val cipherText = cipher.encrypt("access")
        assertEquals("access", cipher.decrypt(cipherText))
        assertEquals("refresh", record.refreshToken)
    }
}
