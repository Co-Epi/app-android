package org.coepi.android.repo.realm

import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.coepi.android.domain.model.Contact
import org.coepi.android.repo.RealmProvider
import org.coepi.android.repo.model.RealmContact

interface ContactRepo {
    fun addContact(contact: Contact)
    fun retrieveContacts(): List<Contact>
}

class RealmContactRepo(private val realmProvider: RealmProvider): ContactRepo {
    private val realm get() = realmProvider.realm

    override fun addContact(contact: Contact) {
        realm.executeTransaction { realm ->
            realm.createObject<RealmContact>().apply {
                cen = contact.cen
                date = contact.date
            }
        }
    }

    // Most simple version
    // Ideally executed as part of flow in a background thread (e.g. fetch - grouping - send to api)
    // If critical for performance, the mapping to plain objects can be left out
    override fun retrieveContacts(): List<Contact> =
        realm.where<RealmContact>().findAll().map {
            Contact(it.cen, it.date)
        }
}
