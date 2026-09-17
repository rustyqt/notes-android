/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shoppinglist

data class MarkdownDocument(val lines: List<String>, val lineSeparator: String) {

    override fun toString(): String = lines.joinToString(lineSeparator)

    companion object {
        private const val CRLF = "\r\n"
        private const val LF = "\n"

        fun parse(content: String): MarkdownDocument {
            val lineSeparator = if (content.contains(CRLF)) CRLF else LF
            return MarkdownDocument(content.split(lineSeparator), lineSeparator)
        }
    }
}
