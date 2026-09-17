/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shoppinglist

object ShoppingListMarker {
    const val MARKER = "<!-- einkaufsliste -->"

    fun isMarkerLine(line: String): Boolean = line.trim().equals(MARKER, ignoreCase = true)

    @JvmStatic
    fun isPresent(content: String): Boolean = content.lineSequence().any(::isMarkerLine)
}
