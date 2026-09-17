/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shoppinglist

data class ShoppingListZone(
    val type: ShoppingListZoneType,
    val headingIndex: Int,
    val level: Int,
    val endIndex: Int,
) {
    val bodyStart: Int get() = headingIndex + 1
}
