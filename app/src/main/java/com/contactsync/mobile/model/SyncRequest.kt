package com.contactsync.mobile.model

import com.google.gson.annotations.SerializedName

/**
 * Request body for syncing contacts
 */
data class SyncRequest(
    @SerializedName("companyName")
    val companyName: String,
    
    @SerializedName("passcode")
    val passcode: String
)

/**
 * Response from sync endpoint
 */
data class SyncResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("companyName")
    val companyName: String,
    
    @SerializedName("contacts")
    val contacts: List<Contact>
)
