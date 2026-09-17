/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shoppinglist

object ShoppingListZoneInserter {
    private const val DEFAULT_ZONE_LEVEL = 2
    private const val TOP_HEADING_LEVEL = 1
    private const val BLANK_LINE = ""

    fun ensureZones(lines: List<String>, openLabel: String, doneLabel: String): List<String> {
        val withOpenZone = if (ShoppingListZoneType.OPEN in ShoppingListZoneLocator.locate(lines)) {
            lines
        } else {
            insertOpenZone(lines, openLabel)
        }
        val zones = ShoppingListZoneLocator.locate(withOpenZone)
        if (ShoppingListZoneType.DONE in zones) return withOpenZone

        val openZone = checkNotNull(zones[ShoppingListZoneType.OPEN])
        val doneZoneHeading = zoneHeading(openZone.level, ShoppingListZoneType.DONE, doneLabel)
        return withOpenZone.inserted(openZone.endIndex, doneZoneHeading)
    }

    private fun insertOpenZone(lines: List<String>, label: String): List<String> {
        val doneZone = ShoppingListZoneLocator.locate(lines)[ShoppingListZoneType.DONE]
        if (doneZone != null) {
            return lines.inserted(doneZone.headingIndex, zoneHeading(doneZone.level, ShoppingListZoneType.OPEN, label))
        }

        val codeMask = ShoppingListSyntax.codeMask(lines)
        val firstItemIndex = lines.indices.firstOrNull { !codeMask[it] && ShoppingListSyntax.isCheckboxItem(lines[it]) }
            ?: return lines.dropLastWhile(String::isBlank) + BLANK_LINE +
                zoneHeading(DEFAULT_ZONE_LEVEL, ShoppingListZoneType.OPEN, label)

        val categoryIndex = (firstItemIndex - 1 downTo 0).firstOrNull {
            !codeMask[it] && ShoppingListSyntax.headingLevel(lines[it]) != null
        }
        val categoryLevel = categoryIndex?.let { ShoppingListSyntax.headingLevel(lines[it]) } ?: TOP_HEADING_LEVEL
        if (categoryIndex == null || categoryLevel <= TOP_HEADING_LEVEL) {
            return lines.inserted(firstItemIndex, zoneHeading(DEFAULT_ZONE_LEVEL, ShoppingListZoneType.OPEN, label))
        }
        return lines.inserted(categoryIndex, zoneHeading(categoryLevel - 1, ShoppingListZoneType.OPEN, label))
    }

    private fun zoneHeading(level: Int, type: ShoppingListZoneType, label: String): List<String> =
        listOf(ShoppingListSyntax.heading(level, "${type.emoji} $label"), BLANK_LINE)

    private fun List<String>.inserted(index: Int, block: List<String>): List<String> =
        take(index) + block + drop(index)
}
