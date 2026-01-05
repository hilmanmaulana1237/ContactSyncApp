package com.contactsync.mobile.model

import com.google.gson.annotations.SerializedName

/**
 * Request body for adding a contact
 */
data class AddContactRequest(
    @SerializedName("companyId")
    val companyId: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("phone")
    val phone: String,
    
    @SerializedName("role")
    val role: String? = null,
    
    @SerializedName("email")
    val email: String? = null
)

/**
 * Response from adding a contact
 */
data class AddContactResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("contact")
    val contact: Contact
)
