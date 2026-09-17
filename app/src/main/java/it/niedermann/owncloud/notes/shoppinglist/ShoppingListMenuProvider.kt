/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shoppinglist

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.core.view.MenuProvider
import it.niedermann.owncloud.notes.R
import java.util.function.Consumer
import java.util.function.Supplier

class ShoppingListMenuProvider(
    private val activity: ComponentActivity,
    private val currentContent: Supplier<String?>,
    private val applyContent: Consumer<String>,
) : MenuProvider {

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) =
        menuInflater.inflate(R.menu.menu_shopping_list, menu)

    override fun onPrepareMenu(menu: Menu) {
        val content = currentContent.get()
        menu.findItem(R.id.menu_shopping_list)?.apply {
            isVisible = content != null
            isChecked = content != null && ShoppingListMarker.isPresent(content)
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId != R.id.menu_shopping_list) return false
        val content = currentContent.get() ?: return true
        applyContent.accept(toggle(content))
        activity.invalidateMenu()
        return true
    }

    private fun toggle(content: String): String {
        if (ShoppingListMarker.isPresent(content)) return ShoppingListActivation.deactivate(content)
        return ShoppingListActivation.activate(
            content,
            activity.getString(R.string.shopping_list_open),
            activity.getString(R.string.shopping_list_done),
        )
    }
}
