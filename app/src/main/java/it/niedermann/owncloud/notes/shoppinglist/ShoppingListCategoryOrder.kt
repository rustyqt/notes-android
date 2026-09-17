/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shoppinglist

object ShoppingListCategoryOrder {

    fun merge(primary: List<String?>, secondary: List<String?>): List<String?> {
        val merged = mutableListOf<String?>(null)
        primary.filterNotNull().distinct().forEach(merged::add)
        var insertionIndex = 1
        secondary.filterNotNull().distinct().forEach { key ->
            val existingIndex = merged.indexOf(key)
            if (existingIndex >= 0) {
                insertionIndex = existingIndex + 1
                return@forEach
            }
            merged.add(insertionIndex, key)
            insertionIndex++
        }
        return merged
    }
}
