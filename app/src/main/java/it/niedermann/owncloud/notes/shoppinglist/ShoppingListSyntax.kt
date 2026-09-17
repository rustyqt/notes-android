/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shoppinglist

import java.util.Locale

object ShoppingListSyntax {
    private const val MAX_HEADING_LEVEL = 6

    private val HEADING = Regex("""^(#{1,6})[ \t]+(.*)$""")
    private val CHECKBOX_ITEM = Regex("""^[-*+][ \t]+\[([ xX])][ \t]+(\S.*)$""")
    private val THEMATIC_BREAK = Regex("""^ {0,3}([-*_])[ \t]*(\1[ \t]*){2,}$""")
    private val CODE_FENCE = Regex("""^ {0,3}(```|~~~)""")

    fun headingLevel(line: String): Int? = HEADING.matchEntire(line)?.groupValues?.get(1)?.length

    fun headingText(line: String): String = HEADING.matchEntire(line)?.groupValues?.get(2)?.trim().orEmpty()

    fun heading(level: Int, text: String): String = "#".repeat(level.coerceIn(1, MAX_HEADING_LEVEL)) + " " + text

    fun isCheckboxItem(line: String): Boolean = CHECKBOX_ITEM.matches(line)

    fun isChecked(line: String): Boolean = CHECKBOX_ITEM.matchEntire(line)?.groupValues?.get(1)?.isNotBlank() == true

    fun itemKey(line: String): String =
        CHECKBOX_ITEM.matchEntire(line)?.groupValues?.get(2)?.trim()?.lowercase(Locale.ROOT).orEmpty()

    fun isContinuation(line: String): Boolean = line.isNotBlank() && line.first().isWhitespace()

    fun isThematicBreak(line: String): Boolean = THEMATIC_BREAK.matches(line)

    fun codeMask(lines: List<String>): List<Boolean> {
        var insideCode = false
        return lines.map { line ->
            val isFence = CODE_FENCE.containsMatchIn(line)
            val masked = insideCode || isFence
            if (isFence) insideCode = !insideCode
            masked
        }
    }
}
