package com.example.contactslite.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.example.contactslite.room.Contact
import com.example.contactslite.room.ContactDao
import kotlinx.coroutines.flow.Flow

class ContactRepository(private val contactDao: ContactDao) {

    val allContacts: Flow<List<Contact>> = contactDao.getAllContacts()

    suspend fun Insert(contact: Contact){
        contactDao.insert(contact)
    }
    suspend fun Update(contact: Contact){
        contactDao.update(contact)
    }
    suspend fun Delete(contact: Contact){
        contactDao.delete(contact)
    }
}