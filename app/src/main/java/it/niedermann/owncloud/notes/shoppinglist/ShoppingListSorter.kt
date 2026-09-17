/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shoppinglist

object ShoppingListSorter {

    @JvmStatic
    fun sortIfEnabled(content: String): String =
        if (ShoppingListMarker.isPresent(content)) sort(content) else content

    fun sort(content: String): String {
        val document = MarkdownDocument.parse(content)
        val zones = ShoppingListZoneLocator.locate(document.lines)
        val openZone = zones[ShoppingListZoneType.OPEN] ?: return content
        val doneZone = zones[ShoppingListZoneType.DONE] ?: return content

        val openCategories = ShoppingListZoneParser.parse(document.lines, openZone).mergedByKey()
        val doneCategories = ShoppingListZoneParser.parse(document.lines, doneZone).mergedByKey()
        val order = ShoppingListCategoryOrder.merge(doneCategories.keys.toList(), openCategories.keys.toList())

        val sortedLines = document.lines.toMutableList()
        listOf(
            openZone to rebuildZone(openZone.type, order, openCategories, doneCategories),
            doneZone to rebuildZone(doneZone.type, order, doneCategories, openCategories),
        )
            .sortedByDescending { (zone, _) -> zone.headingIndex }
            .forEach { (zone, categories) ->
                sortedLines.replaceZoneBody(zone, ShoppingListRenderer.render(categories))
            }

        return document.copy(lines = sortedLines).toString()
    }

    private fun rebuildZone(
        type: ShoppingListZoneType,
        order: List<String?>,
        ownCategories: Map<String?, ShoppingListCategory>,
        otherCategories: Map<String?, ShoppingListCategory>,
    ): List<ShoppingListCategory> = order.mapNotNull { key ->
        val own = ownCategories[key]
        val other = otherCategories[key]
        val headingLine = own?.headingLine ?: other?.headingLine
        val staying = own?.items.orEmpty().filter { it.checked == type.holdsCheckedItems }
        val stayingKeys = staying.map(ShoppingListItem::key).toSet()
        val arriving = other?.items.orEmpty()
            .filter { it.checked == type.holdsCheckedItems && it.key !in stayingKeys }
            .distinctBy(ShoppingListItem::key)
        ShoppingListCategory(headingLine, own?.notes.orEmpty(), staying + arriving)
            .takeUnless { it.headingLine != null && it.isEmpty }
    }

    private fun List<ShoppingListCategory>.mergedByKey(): Map<String?, ShoppingListCategory> =
        groupBy(ShoppingListCategory::key).mapValues { (_, duplicates) ->
            ShoppingListCategory(
                headingLine = duplicates.first().headingLine,
                notes = duplicates.flatMap(ShoppingListCategory::notes),
                items = duplicates.flatMap(ShoppingListCategory::items),
            )
        }

    private fun MutableList<String>.replaceZoneBody(zone: ShoppingListZone, body: List<String>) {
        subList(zone.bodyStart, zone.endIndex).clear()
        addAll(zone.bodyStart, body)
    }
}
