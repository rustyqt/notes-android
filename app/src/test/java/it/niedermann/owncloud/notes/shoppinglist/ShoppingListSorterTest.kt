/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shoppinglist

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ShoppingListSorterTest {

    @Test
    fun checkedItemMovesToItsCategoryInDoneZoneAndEmptyOpenCategoryDisappears() {
        val content = """
            # Einkaufsliste

            ## 🛒 Offen

            ### Bäcker

            - [x] Brot

            ### Kühltheke

            - [ ] Spätzle

            ---

            ## ✅ Erledigt

            ### Bäcker

            - [x] Brezn

            <!-- einkaufsliste -->
        """.trimIndent()

        val expected = """
            # Einkaufsliste

            ## 🛒 Offen

            ### Kühltheke

            - [ ] Spätzle

            ---

            ## ✅ Erledigt

            ### Bäcker

            - [x] Brezn
            - [x] Brot

            <!-- einkaufsliste -->
        """.trimIndent()

        assertEquals(expected, ShoppingListSorter.sortIfEnabled(content))
    }

    @Test
    fun uncheckedItemInDoneZoneMovesUpInCatalogOrder() {
        val content = """
            ## 🛒 Offen

            ### Kühltheke

            - [ ] Spätzle

            ## ✅ Erledigt

            ### Obst & Gemüse

            - [x] Paprika
            - [ ] Zucchini

            ### Kühltheke

            - [x] Milch
        """.trimIndent()

        val expected = """
            ## 🛒 Offen

            ### Obst & Gemüse

            - [ ] Zucchini

            ### Kühltheke

            - [ ] Spätzle

            ## ✅ Erledigt

            ### Obst & Gemüse

            - [x] Paprika

            ### Kühltheke

            - [x] Milch

        """.trimIndent()

        assertEquals(expected, ShoppingListSorter.sort(content))
    }

    @Test
    fun newCategoryKeepsItsRelativePosition() {
        val content = """
            ## 🛒 Offen
            ### Bäcker
            - [ ] Brot
            ### Apotheke
            - [x] Pflaster
            ### Kühltheke
            - [ ] Spätzle
            ## ✅ Erledigt
            ### Bäcker
            - [x] Brezn
            ### Kühltheke
            - [x] Milch
        """.trimIndent()

        val expected = """
            ## 🛒 Offen

            ### Bäcker

            - [ ] Brot

            ### Kühltheke

            - [ ] Spätzle

            ## ✅ Erledigt

            ### Bäcker

            - [x] Brezn

            ### Apotheke

            - [x] Pflaster

            ### Kühltheke

            - [x] Milch

        """.trimIndent()

        assertEquals(expected, ShoppingListSorter.sort(content))
    }

    @Test
    fun itemAlreadyInTargetCategoryIsNotDuplicated() {
        val content = """
            ## 🛒 Offen
            ### Kühltheke
            - [x] milch
            ## ✅ Erledigt
            ### Kühltheke
            - [x] Milch
        """.trimIndent()

        val expected = """
            ## 🛒 Offen

            ## ✅ Erledigt

            ### Kühltheke

            - [x] Milch

        """.trimIndent()

        assertEquals(expected, ShoppingListSorter.sort(content))
    }

    @Test
    fun uncategorizedItemsAndContinuationLinesMoveTogether() {
        val content = """
            ## 🛒 Offen
            - [x] Kaffee
              gemahlen, 500 g
            - [ ] Tee
            ## ✅ Erledigt
            ### Bäcker
            - [x] Brezn
        """.trimIndent()

        val expected = """
            ## 🛒 Offen

            - [ ] Tee

            ## ✅ Erledigt

            - [x] Kaffee
              gemahlen, 500 g

            ### Bäcker

            - [x] Brezn

        """.trimIndent()

        assertEquals(expected, ShoppingListSorter.sort(content))
    }

    @Test
    fun sortingIsIdempotent() {
        val content = """
            # Liste
            ## 🛒 Offen
            Notiz zum Einkauf
            ### Bäcker
            - [x] Brot
            - [ ] Semmel
            ---
            ## ✅ Erledigt
            ### Obst
            - [ ] Äpfel
            ---
            Fußzeile
        """.trimIndent()

        val sortedOnce = ShoppingListSorter.sort(content)

        assertEquals(sortedOnce, ShoppingListSorter.sort(sortedOnce))
    }

    @Test
    fun contentWithoutMarkerStaysUntouched() {
        val content = "## 🛒 Offen\n- [x] Brot\n## ✅ Erledigt\n"

        assertEquals(content, ShoppingListSorter.sortIfEnabled(content))
    }

    @Test
    fun windowsLineEndingsArePreserved() {
        val content = "## 🛒 Offen\r\n- [x] Brot\r\n## ✅ Erledigt\r\n"

        val sorted = ShoppingListSorter.sort(content)

        assertEquals("## 🛒 Offen\r\n\r\n## ✅ Erledigt\r\n\r\n- [x] Brot\r\n", sorted)
        assertFalse(sorted.replace("\r\n", "").contains("\n"))
    }
}
