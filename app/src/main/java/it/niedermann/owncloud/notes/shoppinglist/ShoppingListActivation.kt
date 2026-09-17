/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shoppinglist

object ShoppingListActivation {
    private const val BLANK_LINE = ""

    @JvmStatic
    fun activate(content: String, openLabel: String, doneLabel: String): String {
        val document = MarkdownDocument.parse(content)
        val withZones = ShoppingListZoneInserter.ensureZones(document.lines, openLabel, doneLabel)
        val unwrapped = ShoppingListCalloutUnwrapper.unwrap(withZones)
        val marked = unwrapped.dropLastWhile(String::isBlank) +
            listOf(BLANK_LINE, ShoppingListMarker.MARKER, BLANK_LINE)
        return ShoppingListSorter.sort(document.copy(lines = marked).toString())
    }

    @JvmStatic
    fun deactivate(content: String): String {
        val document = MarkdownDocument.parse(content)
        val lines = document.lines
            .filterNot(ShoppingListMarker::isMarkerLine)
            .dropLastWhile(String::isBlank) + BLANK_LINE
        return document.copy(lines = lines).toString()
    }
}
