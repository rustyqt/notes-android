/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shoppinglist

object ShoppingListZoneParser {

    fun parse(lines: List<String>, zone: ShoppingListZone): List<ShoppingListCategory> {
        val body = lines.subList(zone.bodyStart, zone.endIndex)
        val codeMask = ShoppingListSyntax.codeMask(body)
        val categories = mutableListOf(CategoryBuilder(headingLine = null))
        var index = 0
        while (index < body.size) {
            val line = body[index]
            val current = categories.last()
            when {
                codeMask[index] -> current.notes += line
                isCategoryHeading(line, zone.level) -> categories += CategoryBuilder(line)
                ShoppingListSyntax.isCheckboxItem(line) -> {
                    val itemEnd = findItemEnd(body, codeMask, index)
                    current.items += ShoppingListItem(body.subList(index, itemEnd).toList())
                    index = itemEnd
                    continue
                }
                else -> current.notes += line
            }
            index++
        }
        return categories.map(CategoryBuilder::build)
    }

    private fun isCategoryHeading(line: String, zoneLevel: Int): Boolean {
        val level = ShoppingListSyntax.headingLevel(line) ?: return false
        return level > zoneLevel
    }

    private fun findItemEnd(body: List<String>, codeMask: List<Boolean>, itemIndex: Int): Int =
        (itemIndex + 1 until body.size).firstOrNull { index ->
            codeMask[index] || !ShoppingListSyntax.isContinuation(body[index])
        } ?: body.size

    private class CategoryBuilder(val headingLine: String?) {
        val notes = mutableListOf<String>()
        val items = mutableListOf<ShoppingListItem>()

        fun build() = ShoppingListCategory(headingLine, normalizeBlankLines(notes), items.toList())

        private fun normalizeBlankLines(lines: List<String>): List<String> = lines
            .filterIndexed { index, line -> line.isNotBlank() || lines.getOrNull(index - 1)?.isNotBlank() == true }
            .dropWhile { it.isBlank() }
            .dropLastWhile { it.isBlank() }
    }
}
