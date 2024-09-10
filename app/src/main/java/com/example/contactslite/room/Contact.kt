package com.example.contactslite.room

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "contact")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val image: String,
    val name: String,
    val phoneNumber: String
    val email: String,

)
