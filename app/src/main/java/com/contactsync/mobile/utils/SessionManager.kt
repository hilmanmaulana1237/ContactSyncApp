package com.contactsync.mobile.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Helper untuk menyimpan session admin ke SharedPreferences
 */
object SessionManager {
    
    private const val PREF_NAME = "ContactSyncSession"
    private const val KEY_COMPANY_ID = "company_id"
    private const val KEY_COMPANY_NAME = "company_name"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Save admin session
     */
    fun saveSession(context: Context, companyId: String, companyName: String) {
        getPrefs(context).edit().apply {
            putString(KEY_COMPANY_ID, companyId)
            putString(KEY_COMPANY_NAME, companyName)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    /**
     * Get saved company ID
     */
    fun getCompanyId(context: Context): String? {
        return getPrefs(context).getString(KEY_COMPANY_ID, null)
    }
    
    /**
     * Get saved company name
     */
    fun getCompanyName(context: Context): String? {
        return getPrefs(context).getString(KEY_COMPANY_NAME, null)
    }
    
    /**
     * Check if admin is logged in
     */
    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    /**
     * Clear session (logout)
     */
    fun logout(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
