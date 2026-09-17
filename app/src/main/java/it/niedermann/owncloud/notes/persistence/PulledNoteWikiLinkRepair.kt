/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.persistence

import it.niedermann.owncloud.notes.persistence.entity.Account
import it.niedermann.owncloud.notes.persistence.entity.Note
import it.niedermann.owncloud.notes.shared.model.DBStatus
import it.niedermann.owncloud.notes.shared.util.WikiLinkUnescaper

object PulledNoteWikiLinkRepair {

    @JvmStatic
    fun repair(repo: NotesRepository, account: Account, remoteNotes: List<Note>) {
        remoteNotes
            .filter { it.modified != null && WikiLinkUnescaper.containsEscapedWikiLink(it.content) }
            .mapNotNull { it.remoteId }
            .mapNotNull { remoteId -> repo.getLocalIdByRemoteId(account.id, remoteId) }
            .mapNotNull { localId -> repo.getNoteById(localId) }
            .filter { it.status == DBStatus.VOID }
            .forEach { repo.updateNoteAndSync(account, it, WikiLinkUnescaper.unescape(it.content), it.title, null) }
    }
}
