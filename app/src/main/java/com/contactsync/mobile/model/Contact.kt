package com.contactsync.mobile.model

import com.google.gson.annotations.SerializedName

/**
 * Data class representing a contact
 */
data class Contact(
    @SerializedName("id")
    val id: String? = null,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("phone")
    val phone: String,
    
    @SerializedName("role")
    val role: String? = null,
    
    @SerializedName("email")
    val email: String? = null
)
