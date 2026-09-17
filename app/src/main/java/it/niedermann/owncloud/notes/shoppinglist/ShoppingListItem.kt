/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shoppinglist

data class ShoppingListItem(val lines: List<String>) {
    val checked: Boolean get() = ShoppingListSyntax.isChecked(lines.first())
    val key: String get() = ShoppingListSyntax.itemKey(lines.first())
}
