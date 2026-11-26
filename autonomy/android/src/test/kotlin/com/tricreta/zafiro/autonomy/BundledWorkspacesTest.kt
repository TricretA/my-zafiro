package com.tricreta.zafiro.autonomy

import com.tricreta.zafiro.autonomy.workspace.SocialCatalog
import com.tricreta.zafiro.autonomy.workspace.WorkspaceDef
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import kotlin.test.assertTrue

/** Every bundled workspace must parse and only reference skills that are actually bundled. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class BundledWorkspacesTest {
    private val assets = File("../../app/src/main/assets")

    @Test fun everyBundledWorkspaceParsesAndReferencesRealSkills() {
        val files = File(assets, "workspaces").listFiles { f -> f.name.endsWith(".json") }.orEmpty()
        assertTrue(files.size >= 6, "expected the bundled workspaces, found ${files.size}")
        files.forEach { f ->
            val def = WorkspaceDef.parse(f.readText())
            assertTrue(def.id == f.nameWithoutExtension, "${f.name}: id must match the file name")
            if (def.id !in setOf("toolsmith", "health", "video-studio")) assertTrue(def.agents.size >= 8 && def.tasks.size >= 9, "${def.id}: a full team and schedule")
            (def.skills + def.agents.flatMap { it.skills }).distinct().forEach { s ->
                assertTrue(File(assets, "skills/$s/SKILL.md").isFile, "${def.id}: skill $s is not bundled")
            }
            if (def.id == "social") assertTrue(def.agents.map { it.id }.toSet() == setOf("director", "researcher", "strategist", "writer", "reviewer", "engager", "executor", "analyst").map { "social-$it" }.toSet(), "social has the full team")
            def.tasks.forEach { t -> assertTrue(t.maxMinutes in 5..120 && t.maxSteps in 10..200, "${def.id}/${t.key}: sane budget") }
        }
    }

    @Test fun socialCatalogueReferencesBundledSkillsAndRoutines() {
        val cat = SocialCatalog.parse(File(assets, "social/platforms.json").readText())
        assertTrue(cat.platforms.size >= 16)
        val required = setOf("facebook", "instagram", "tiktok", "youtube", "snapchat", "pinterest", "linkedin", "whatsapp", "messenger", "telegram", "discord", "x", "threads", "reddit", "twitch", "substack")
        assertTrue(cat.platforms.map { it.id }.containsAll(required))
        cat.platforms.forEach { p ->
            assertTrue(p.caps.isNotBlank(), "${p.id}: default caps")
            p.skills.forEach { s -> assertTrue(File(assets, "skills/$s/SKILL.md").isFile, "${p.id}: skill $s is not bundled") }
            assertTrue(File(assets, "skills/${p.id}-playbook/SKILL.md").isFile, "${p.id}: playbook")
            assertTrue(File(assets, "routines/${p.id}.open_home.json").isFile, "${p.id}: routines")
        }
    }

    @Test fun everyBundledSkillHasFrontmatterNameEqualToItsFolder() {
        File(assets, "skills").listFiles { f -> f.isDirectory }.orEmpty().forEach { d ->
            val head = File(d, "SKILL.md").takeIf { it.isFile }?.readText()?.lineSequence()?.take(8)?.toList().orEmpty()
            val name = head.firstOrNull { it.startsWith("name:") }?.removePrefix("name:")?.trim()?.trim('"', '\'')
            assertTrue(name == d.name, "${d.name}: frontmatter name is $name")
        }
    }
}
