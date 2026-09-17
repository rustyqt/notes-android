/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shared.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WikiLinkUnescaperTest {

    @Test
    fun escapedWikiLinksAreRestored() {
        assertEquals("[[Haushaltsliste]]", WikiLinkUnescaper.unescape("""\[\[Haushaltsliste\]\]"""))
        assertEquals(
            "- [x] [[Sushi]] und [[Käsekuchen]]",
            WikiLinkUnescaper.unescape("""- [x] \[\[Sushi\]\] und \[\[Käsekuchen\]\]"""),
        )
    }

    @Test
    fun embedsAliasesAndEscapedCharactersInsideLinksAreRestored() {
        assertEquals("![[bild.png]]", WikiLinkUnescaper.unescape("""!\[\[bild.png\]\]"""))
        assertEquals("[[Mein_Rezept|Rezept]]", WikiLinkUnescaper.unescape("""\[\[Mein\_Rezept\|Rezept\]\]"""))
    }

    @Test
    fun contentWithoutEscapedWikiLinksStaysUntouched() {
        val content = """[[Link]] and \[not a wiki link\] and [markdown](link)"""

        assertFalse(WikiLinkUnescaper.containsEscapedWikiLink(content))
        assertEquals(content, WikiLinkUnescaper.unescape(content))
    }

    @Test
    fun detectsEscapedWikiLinks() {
        assertTrue(WikiLinkUnescaper.containsEscapedWikiLink("""Siehe \[\[Haushaltsliste\]\]"""))
    }
}
