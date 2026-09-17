/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shoppinglist

object ShoppingListCalloutUnwrapper {
    private const val QUOTE_MARKER = ">"

    private val CALLOUT_TITLE = Regex("""^>[ \t]*\[![^\]]+][+-]?.*$""")
    private val QUOTE_PREFIX = Regex("""^>[ \t]?""")
    private val BOLD_LINE = Regex("""^\*\*(.+)\*\*$""")

    fun unwrap(lines: List<String>): List<String> =
        ShoppingListZoneLocator.locate(lines).values
            .sortedByDescending(ShoppingListZone::headingIndex)
            .fold(lines) { current, zone ->
                current.take(zone.bodyStart) +
                    unwrapBody(current.subList(zone.bodyStart, zone.endIndex), zone.level) +
                    current.drop(zone.endIndex)
            }

    private fun unwrapBody(body: List<String>, zoneLevel: Int): List<String> {
        var insideCallout = false
        return body.mapNotNull { line ->
            when {
                CALLOUT_TITLE.matches(line) -> {
                    insideCallout = true
                    null
                }
                insideCallout && line.startsWith(QUOTE_MARKER) -> unquote(line, zoneLevel)
                else -> {
                    insideCallout = false
                    line
                }
            }
        }
    }

    private fun unquote(line: String, zoneLevel: Int): String {
        val text = line.replaceFirst(QUOTE_PREFIX, "")
        val boldText = BOLD_LINE.matchEntire(text.trim())?.groupValues?.get(1) ?: return text
        return ShoppingListSyntax.heading(zoneLevel + 1, boldText.trim())
    }
}
