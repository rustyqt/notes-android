/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shoppinglist

import java.util.Locale

data class ShoppingListCategory(
    val headingLine: String?,
    val notes: List<String>,
    val items: List<ShoppingListItem>,
) {
    val key: String? get() = headingLine?.let { ShoppingListSyntax.headingText(it).lowercase(Locale.ROOT) }

    val isEmpty: Boolean get() = notes.isEmpty() && items.isEmpty()
}
