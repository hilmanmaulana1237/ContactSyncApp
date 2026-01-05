package com.contactsync.mobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val btnAdmin = findViewById<MaterialButton>(R.id.btnAdmin)
        val btnEmployee = findViewById<MaterialButton>(R.id.btnEmployee)
        
        btnAdmin.setOnClickListener {
            startActivity(Intent(this, AdminLoginActivity::class.java))
        }
        
        btnEmployee.setOnClickListener {
            startActivity(Intent(this, EmployeeActivity::class.java))
        }

        // Privacy Policy Click Listener
        findViewById<android.widget.TextView>(R.id.tvPrivacyPolicy).setOnClickListener {
            openPrivacyPolicy()
        }

        // Language Button
        findViewById<android.widget.Button>(R.id.btnLanguage).setOnClickListener {
            showLanguageDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        checkPrivacyPolicyHelper()
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("English", "Bahasa Indonesia")
        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.change_language))
            .setItems(languages) { _, which ->
                val locale = if (which == 0) androidx.core.os.LocaleListCompat.create(java.util.Locale.ENGLISH) 
                             else androidx.core.os.LocaleListCompat.create(java.util.Locale("in"))
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(locale)
            }
            .show()
    }

    private fun checkPrivacyPolicyHelper() {
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isAccepted = sharedPref.getBoolean("is_privacy_policy_accepted", false)

        if (!isAccepted) {
            showPrivacyPolicyDialog()
        }
    }

    private fun showPrivacyPolicyDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_privacy_policy, null)
        val svPrivacy = dialogView.findViewById<android.widget.ScrollView>(R.id.svPrivacy)
        val tvPrivacyContent = dialogView.findViewById<android.widget.TextView>(R.id.tvPrivacyContent)
        val cbAgree = dialogView.findViewById<android.widget.CheckBox>(R.id.cbAgree)
        val btnAgree = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAgree)

        // Set Privacy Policy Text with Link
        val policyText = """
            Privacy Policy for Contact Sync App
            Last updated: January 1, 2026

            Click here to read full version:
            https://sites.google.com/view/privacypolicyforcontactsyncapp/home

            This Privacy Policy describes how Contact Sync App collects, uses, and discloses your information.

            1. Information We Collect
            - Contact Information: Names, phone numbers, email addresses, organization details.
            - Device Information: Device model, OS version, unique identifiers.

            Why we need this:
            To synchronize your corporat contacts with your local device contacts.

            2. How We Use Your Information
            - Contact Synchronization
            - Authentication
            - App Functionality

            We do NOT sell your contact data.

            3. Data Safety & Security
            We use HTTPS encryption for all data transmission.

            4. Your Rights
            You can revoke permissions at any time in device settings.

            5. Contact Us
            If you have questions, please contact your administrator.
            
            (Scroll to bottom to accept)
            
            
            
            
        """.trimIndent()
        
        tvPrivacyContent.text = policyText
        // Make link clickable if we used Linkify, but here simple text is fine as they must scroll.
        // Or we can add an OnClick to the text view to open the link if they tap it?
        // Let's allow tapping the text view to open link if they want, simple hack.
        tvPrivacyContent.setOnClickListener {
             openPrivacyPolicy()
        }
        
        // Scroll Listener
        svPrivacy.viewTreeObserver.addOnScrollChangedListener {
            val view = svPrivacy.getChildAt(0)
            val bottomDiff = (view.bottom - (svPrivacy.height + svPrivacy.scrollY))
            
            // If scrolled to bottom (allow small margin of error like 50px)
            if (bottomDiff <= 50) {
                if (!cbAgree.isEnabled) {
                    cbAgree.isEnabled = true
                    cbAgree.text = "Saya telah membaca dan menyetujui Kebijakan Privasi (Bisa dicentang)"
                }
            }
        }

        // Checkbox Listener
        cbAgree.setOnCheckedChangeListener { _, isChecked ->
            btnAgree.isEnabled = isChecked
        }

        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Button Listener
        btnAgree.setOnClickListener {
            // Save acceptance
            getSharedPreferences("app_prefs", MODE_PRIVATE).edit()
                .putBoolean("is_privacy_policy_accepted", true)
                .apply()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun openPrivacyPolicy() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = android.net.Uri.parse("https://sites.google.com/view/privacypolicyforcontactsyncapp/home")
        startActivity(intent)
    }
}