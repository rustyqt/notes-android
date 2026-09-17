/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shoppinglist

enum class ShoppingListZoneType(val emoji: String, val holdsCheckedItems: Boolean) {
    OPEN("🛒", false),
    DONE("✅", true),
}
