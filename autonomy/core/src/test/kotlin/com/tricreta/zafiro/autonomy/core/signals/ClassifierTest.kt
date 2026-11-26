package com.tricreta.zafiro.autonomy.core.signals

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClassifierTest {
    private val cfg = SignalConfig(ownerSenderNames = listOf("Tricret", "+254 712 345 678"))

    private fun kind(raw: RawNotification, c: SignalConfig = cfg) =
        SignalClassifier.classify(raw, c, receivedMs = 1L).kind

    @Test fun platformIsInferredFromPackage() {
        assertEquals("substack", SignalClassifier.platformOf("com.substack.app"))
        assertEquals("tiktok", SignalClassifier.platformOf("com.ss.android.ugc.trill"))
        assertEquals("threads", SignalClassifier.platformOf("com.instagram.barcelona"))
        assertEquals("whatsapp", SignalClassifier.platformOf("com.whatsapp.w4b"))
        assertEquals("metricool", SignalClassifier.platformOf("com.metricool.mobile"))
        assertEquals("other", SignalClassifier.platformOf("com.example.unknown"))
    }

    @Test fun ownerMessageNeedsOwnerSenderOnAChatApp() {
        val owner = RawNotification("com.whatsapp.w4b", title = "Tricret", text = "STATUS", sender = "Tricret")
        assertEquals(SignalKind.OWNER_MESSAGE, kind(owner))
        assertEquals(SignalKind.OWNER_MESSAGE, kind(owner.copy(packageName = "org.telegram.messenger")))
        assertEquals(SignalKind.OTHER, kind(owner.copy(sender = "Someone Else", title = "Someone Else")))
    }

    @Test fun ownerMatchIsExactNotSubstring() {
        val spoof = RawNotification("com.whatsapp", title = "+1 555 0100 ~Tricret", text = "STOP ALL", sender = "+1 555 0100 ~Tricret")
        assertEquals(SignalKind.OTHER, kind(spoof))
        val spoof2 = RawNotification("com.whatsapp", title = "Tricret Fan Club", text = "hi", sender = "Tricret Fan Club")
        assertEquals(SignalKind.OTHER, kind(spoof2))
    }

    @Test fun ownerMatchIgnoresCaseAndSpacing() {
        assertEquals(SignalKind.OWNER_MESSAGE, kind(RawNotification("com.whatsapp", title = "  tricret ", text = "x", sender = "TRICRET")))
    }

    @Test fun ownerMatchByPhoneNumberDigits() {
        val n = RawNotification("com.whatsapp", title = "+254712345678", text = "hi", sender = "+254712345678")
        assertEquals(SignalKind.OWNER_MESSAGE, kind(n))
        assertEquals(SignalKind.OWNER_MESSAGE, kind(n.copy(sender = "0712 345 678", title = "0712 345 678")))
        assertEquals(SignalKind.OTHER, kind(n.copy(sender = "+254799999999", title = "+254799999999")))
    }

    @Test fun groupChatsNeverCommand() {
        val g = RawNotification("com.whatsapp", title = "Family", text = "STOP ALL", sender = "Tricret", conversation = "Family", isGroup = true)
        assertEquals(SignalKind.OTHER, kind(g))
    }

    @Test fun noConfiguredOwnerMeansNoCommands() {
        val n = RawNotification("com.whatsapp", title = "Tricret", text = "STATUS", sender = "Tricret")
        assertEquals(SignalKind.OTHER, kind(n, SignalConfig()))
    }

    @Test fun engagementKeywords() {
        fun e(text: String, pkg: String = "com.substack.app") = kind(RawNotification(pkg, title = "Substack", text = text))
        assertEquals(SignalKind.ENGAGEMENT, e("Jane commented on your post"))
        assertEquals(SignalKind.ENGAGEMENT, e("Sam restacked your note"))
        assertEquals(SignalKind.ENGAGEMENT, e("You have a new subscriber", "com.substack.app"))
        assertEquals(SignalKind.ENGAGEMENT, e("kim_k started following you", "com.instagram.android"))
        assertEquals(SignalKind.ENGAGEMENT, e("sent you a message", "com.instagram.android"))
        assertEquals(SignalKind.OTHER, e("Your weekly digest is ready"))
        assertEquals(SignalKind.OTHER, e("Admin of the group", "com.reddit.frontpage"))
    }

    @Test fun keywordsMatchWholeWordsOnly() {
        assertEquals(SignalKind.OTHER, kind(RawNotification("com.instagram.android", text = "Join the dmitri live room")))
        assertEquals(SignalKind.ENGAGEMENT, kind(RawNotification("com.instagram.android", text = "New DM from kim")))
    }

    @Test fun keywordTableCanBeReplaced() {
        val c = SignalConfig(engagementKeywords = listOf("pinged"))
        assertEquals(SignalKind.OTHER, kind(RawNotification("com.substack.app", text = "Jane commented on your post"), c))
        assertEquals(SignalKind.ENGAGEMENT, kind(RawNotification("com.substack.app", text = "Lee pinged you"), c))
    }

    @Test fun chatAppMessagesFromOthersAreNotEngagement() {
        assertEquals(SignalKind.OTHER, kind(RawNotification("com.whatsapp", title = "Bob", text = "please reply to my comment", sender = "Bob")))
    }

    @Test fun gmailIsEmail() {
        assertEquals(SignalKind.EMAIL, kind(RawNotification("com.google.android.gm", title = "Client", text = "Re: invoice")))
    }

    @Test fun allowListSupportsPrefixes() {
        val c = SignalConfig(allowedPackages = listOf("com.substack.app", "com.metricool.*"))
        assertTrue(c.allows("com.substack.app"))
        assertTrue(c.allows("com.metricool.mobile"))
        assertFalse(c.allows("com.instagram.android"))
        assertFalse(c.allows("com.metricoolx.evil"))
    }

    @Test fun configRoundTripsThroughJson() {
        val c = SignalConfig(enabled = false, allowedPackages = listOf("a.b"), ownerSenderNames = listOf("Me", "+254700000000"), engagementKeywords = listOf("pinged"))
        assertEquals(c, SignalConfig.fromJson(c.toJson()))
        assertEquals(SignalConfig(), SignalConfig.fromJson("not json"))
        assertEquals(SignalConfig(), SignalConfig.fromJson(null))
        assertEquals(listOf("a", "b"), SignalConfig.parseLines("a\n b ,a;\n\n b"))
    }

    @Test fun classifyTruncatesLongText() {
        val s = SignalClassifier.classify(RawNotification("com.substack.app", text = "x".repeat(5000)), cfg, 1L)
        assertEquals(SignalClassifier.MAX_TEXT, s.text.length)
    }
}
