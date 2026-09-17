/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shoppinglist

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShoppingListActivationTest {

    @Test
    fun calloutArchiveIsConvertedToHeadingsAndSorted() {
        val content = """
            # Einkaufsliste

            [[Haushaltsliste]]

            ## 🛒 Offen

            ### Bäcker

            - [ ] Brot

            ---

            ## ✅ Erledigt (Archiv)

            > [!success]- Zuletzt gekauft — Katalog der Stamm-Artikel (eingeklappt)
            >
            > **Obst & Gemüse & Eier**
            >
            > - [x] Paprika
            > - [ ] Zucchini
            >
            > **Bäcker**
            >
            > - [x] Brezn

            ---

            > [!tip]- So funktioniert die Liste
            > Oben unter **🛒 Offen** stehen nur noch nicht gekaufte Artikel.
        """.trimIndent()

        val expected = """
            # Einkaufsliste

            [[Haushaltsliste]]

            ## 🛒 Offen

            ### Obst & Gemüse & Eier

            - [ ] Zucchini

            ### Bäcker

            - [ ] Brot

            ---

            ## ✅ Erledigt (Archiv)

            ### Obst & Gemüse & Eier

            - [x] Paprika

            ### Bäcker

            - [x] Brezn

            ---

            > [!tip]- So funktioniert die Liste
            > Oben unter **🛒 Offen** stehen nur noch nicht gekaufte Artikel.

            <!-- einkaufsliste -->
        """.trimIndent() + "\n"

        assertEquals(expected, ShoppingListActivation.activate(content, OPEN, DONE))
    }

    @Test
    fun plainChecklistGetsZones() {
        val content = """
            # Einkauf

            - [ ] Milch
            - [x] Brot
        """.trimIndent()

        val expected = """
            # Einkauf

            ## 🛒 Open

            - [ ] Milch

            ## ✅ Done

            - [x] Brot

            <!-- einkaufsliste -->
        """.trimIndent() + "\n"

        assertEquals(expected, ShoppingListActivation.activate(content, OPEN, DONE))
    }

    @Test
    fun categorizedChecklistGetsZonesAroundCategories() {
        val content = """
            # Einkauf

            ### Bäcker

            - [x] Brot

            ### Kühltheke

            - [ ] Milch
        """.trimIndent()

        val expected = """
            # Einkauf

            ## 🛒 Open

            ### Kühltheke

            - [ ] Milch

            ## ✅ Done

            ### Bäcker

            - [x] Brot

            <!-- einkaufsliste -->
        """.trimIndent() + "\n"

        assertEquals(expected, ShoppingListActivation.activate(content, OPEN, DONE))
    }

    @Test
    fun deactivationRemovesOnlyTheMarker() {
        val activated = ShoppingListActivation.activate("- [ ] Milch", OPEN, DONE)
        assertTrue(ShoppingListMarker.isPresent(activated))

        val deactivated = ShoppingListActivation.deactivate(activated)

        assertFalse(ShoppingListMarker.isPresent(deactivated))
        assertEquals("## 🛒 Open\n\n- [ ] Milch\n\n## ✅ Done\n", deactivated)
    }

    private companion object {
        const val OPEN = "Open"
        const val DONE = "Done"
    }
}
