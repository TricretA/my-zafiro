package com.niki914.zafiro.app.browser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// 保护：标签页上限、关闭活动标签后的接任、以及哪些网址可以进入浏览器（javascript:/file: 等必须被拒）。
class BrowserTabsTest {
    @Test
    fun refusesTabsBeyondTheCap() {
        val m = TabManager<String>(2)
        assertEquals("t1", m.add("a")!!.id)
        assertEquals("t2", m.add("b")!!.id)
        assertNull(m.add("c"))
    }

    @Test
    fun closingTheActiveTabActivatesItsPreviousNeighbour() {
        val m = TabManager<String>(3)
        m.add("a"); m.add("b"); m.add("c")
        m.switchTo("t2")
        m.close("t2")
        assertEquals("t1", m.active!!.id)
        m.close("t1")
        assertEquals("t3", m.active!!.id)
        m.close("t3")
        assertNull(m.active)
    }

    @Test
    fun closingAnInactiveTabKeepsTheActiveOne() {
        val m = TabManager<String>(3)
        m.add("a"); m.add("b")
        m.close("t1")
        assertEquals("t2", m.active!!.id)
        assertFalse(m.switchTo("t1"))
    }

    @Test
    fun idsAreNotReusedAfterClose() {
        val m = TabManager<String>(3)
        m.add("a"); m.close("t1")
        assertEquals("t2", m.add("b")!!.id)
    }

    @Test
    fun normalizesUrlsAndRefusesDangerousSchemes() {
        assertEquals("https://example.com", WebUrls.normalize("example.com"))
        assertEquals("https://x.org/a?b=1", WebUrls.normalize(" https://x.org/a?b=1 "))
        assertEquals("http://localhost:8080/x", WebUrls.normalize("http://localhost:8080/x"))
        assertEquals("https://localhost:8080", WebUrls.normalize("localhost:8080"))
        assertNull(WebUrls.normalize("javascript:alert(1)"))
        assertNull(WebUrls.normalize("file:///data/data/x"))
        assertNull(WebUrls.normalize("intent://x#Intent;end"))
        assertNull(WebUrls.normalize("  "))
        assertTrue(WebUrls.normalize("best pizza nairobi")!!.startsWith(WebUrls.SEARCH))
    }

    @Test
    fun inPageNavigationAllowsOnlyWebSchemes() {
        assertTrue(WebUrls.allowedNavigation("https://a.com"))
        assertTrue(WebUrls.allowedNavigation("about:blank"))
        assertFalse(WebUrls.allowedNavigation("intent://a"))
        assertFalse(WebUrls.allowedNavigation("market://details?id=x"))
        assertFalse(WebUrls.allowedNavigation("file:///x"))
    }
}
