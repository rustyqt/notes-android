/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shoppinglist

object ShoppingListRenderer {
    private const val BLANK_LINE = ""

    fun render(categories: List<ShoppingListCategory>): List<String> = buildList {
        add(BLANK_LINE)
        categories.forEach { category ->
            category.headingLine?.let { addBlock(listOf(it)) }
            addBlock(category.notes)
            addBlock(category.items.flatMap(ShoppingListItem::lines))
        }
    }

    private fun MutableList<String>.addBlock(block: List<String>) {
        if (block.isEmpty()) return
        addAll(block)
        add(BLANK_LINE)
    }
}
