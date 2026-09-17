/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shoppinglist

object ShoppingListZoneLocator {

    fun locate(lines: List<String>): Map<ShoppingListZoneType, ShoppingListZone> {
        val codeMask = ShoppingListSyntax.codeMask(lines)
        val zoneStarts = ShoppingListZoneType.entries.mapNotNull { type ->
            lines.indices
                .firstOrNull { !codeMask[it] && isZoneHeading(lines[it], type) }
                ?.let { type to it }
        }.toMap()

        return zoneStarts.mapValues { (type, headingIndex) ->
            val level = checkNotNull(ShoppingListSyntax.headingLevel(lines[headingIndex]))
            val endIndex = (headingIndex + 1 until lines.size).firstOrNull { index ->
                !codeMask[index] && (index in zoneStarts.values || endsZone(lines[index], level))
            } ?: lines.size
            ShoppingListZone(type, headingIndex, level, endIndex)
        }
    }

    private fun isZoneHeading(line: String, type: ShoppingListZoneType): Boolean =
        ShoppingListSyntax.headingLevel(line) != null && ShoppingListSyntax.headingText(line).startsWith(type.emoji)

    private fun endsZone(line: String, zoneLevel: Int): Boolean {
        val headingLevel = ShoppingListSyntax.headingLevel(line)
        return (headingLevel != null && headingLevel <= zoneLevel) ||
            ShoppingListSyntax.isThematicBreak(line) ||
            ShoppingListMarker.isMarkerLine(line)
    }
}
