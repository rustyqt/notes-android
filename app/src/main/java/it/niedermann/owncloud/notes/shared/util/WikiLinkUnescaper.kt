/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shared.util

object WikiLinkUnescaper {
    private val ESCAPED_WIKI_LINK = Regex("""\\\[\\\[(.+?)\\]\\]""")
    private val ESCAPED_CHARACTER = Regex("""\\([\\`*_~|#\[\]])""")
    private const val FIRST_GROUP = "$1"

    @JvmStatic
    fun containsEscapedWikiLink(content: String): Boolean = ESCAPED_WIKI_LINK.containsMatchIn(content)

    @JvmStatic
    fun unescape(content: String): String = ESCAPED_WIKI_LINK.replace(content) { match ->
        "[[" + match.groupValues[1].replace(ESCAPED_CHARACTER, FIRST_GROUP) + "]]"
    }
}
