package com.contactsync.mobile.utils

import android.content.ContentProviderOperation
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import com.contactsync.mobile.model.Contact

/**
 * Utility object untuk mengimport contacts ke phone's address book
 */
object ContactHelper {
    
    private const val TAG = "ContactHelper"
    
    /**
     * Import list of contacts ke phone's address book
     * @param context Application context
     * @param contacts List of contacts to import
     * @return Pair of (imported count, skipped count)
     */
    fun importContacts(context: Context, contacts: List<Contact>): Pair<Int, Int> {
        var importedCount = 0
        var skippedCount = 0
        
        val operations = ArrayList<ContentProviderOperation>()
        
        for (contact in contacts) {
            // Check if contact already exists
            if (isContactExists(context, contact.phone)) {
                Log.d(TAG, "Contact already exists: ${contact.name} (${contact.phone})")
                skippedCount++
                continue
            }
            
            // Get the index for this batch operation
            val rawContactInsertIndex = operations.size
            
            // Insert raw contact
            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build()
            )
            
            // Insert display name
            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        contact.name
                    )
                    .build()
            )
            
            // Insert phone number
            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.phone)
                    .withValue(
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                    )
                    .build()
            )
            
            // Insert organization/role if available
            if (!contact.role.isNullOrBlank()) {
                operations.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE
                        )
                        .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, contact.role)
                        .build()
                )
            }
            
            // Insert email if available
            if (!contact.email.isNullOrBlank()) {
                operations.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                        )
                        .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, contact.email)
                        .withValue(
                            ContactsContract.CommonDataKinds.Email.TYPE,
                            ContactsContract.CommonDataKinds.Email.TYPE_WORK
                        )
                        .build()
                )
            }
            
            importedCount++
        }
        
        // Apply batch operations
        if (operations.isNotEmpty()) {
            try {
                context.contentResolver.applyBatch(ContactsContract.AUTHORITY, operations)
                Log.d(TAG, "Successfully imported $importedCount contacts")
            } catch (e: Exception) {
                Log.e(TAG, "Error importing contacts: ${e.message}", e)
                return Pair(0, contacts.size)
            }
        }
        
        return Pair(importedCount, skippedCount)
    }
    
    /**
     * Check if contact with given phone number already exists
     */
    private fun isContactExists(context: Context, phoneNumber: String): Boolean {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        
        val cursor = context.contentResolver.query(
            uri,
            arrayOf(ContactsContract.PhoneLookup._ID),
            null,
            null,
            null
        )
        
        val exists = cursor?.use {
            it.count > 0
        } ?: false
        
        return exists
    }
}
