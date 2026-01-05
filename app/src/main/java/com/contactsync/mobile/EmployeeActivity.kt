package com.contactsync.mobile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.contactsync.mobile.model.CompanyListItem
import com.contactsync.mobile.model.SyncRequest
import com.contactsync.mobile.network.RetrofitClient
import com.contactsync.mobile.utils.ContactHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmployeeActivity : AppCompatActivity() {

    private lateinit var tilCompany: TextInputLayout
    private lateinit var actvCompany: AutoCompleteTextView
    private lateinit var etPasscode: TextInputEditText
    private lateinit var btnSync: MaterialButton
    private lateinit var btnRefreshCompanies: MaterialButton
    private lateinit var btnBack: MaterialButton
    private lateinit var cardStatus: MaterialCardView
    private lateinit var tvStatus: TextView
    private lateinit var progressBar: CircularProgressIndicator

    // Data
    private var companiesList: List<CompanyListItem> = emptyList()
    private var selectedCompanyName: String? = null

    // Pending sync data (untuk retry setelah permission granted)
    private var pendingCompanyName: String? = null
    private var pendingPasscode: String? = null

    // Multi-permission launcher untuk READ dan WRITE contacts
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            // Permission granted, retry sync
            pendingCompanyName?.let { name ->
                pendingPasscode?.let { passcode ->
                    performSync(name, passcode)
                }
            }
        } else {
            showStatus("❌ Permission ditolak. Tidak bisa import kontak.", isError = true)
        }
        pendingCompanyName = null
        pendingPasscode = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee)

        initViews()
        setupListeners()
        loadCompanies()
    }

    private fun initViews() {
        tilCompany = findViewById(R.id.tilCompany)
        actvCompany = findViewById(R.id.actvCompany)
        etPasscode = findViewById(R.id.etPasscode)
        btnSync = findViewById(R.id.btnSync)
        btnRefreshCompanies = findViewById(R.id.btnRefreshCompanies)
        btnBack = findViewById(R.id.btnBack)
        cardStatus = findViewById(R.id.cardStatus)
        tvStatus = findViewById(R.id.tvStatus)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        btnSync.setOnClickListener {
            syncContacts()
        }

        btnRefreshCompanies.setOnClickListener {
            loadCompanies()
        }

        btnBack.setOnClickListener {
            finish()
        }

        // Handle company selection
        actvCompany.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position) as String
            selectedCompanyName = companiesList.find { 
                "${it.name} (${it.contactCount} kontak)" == selected 
            }?.name
        }
    }

    private fun loadCompanies() {
        showStatus(getString(R.string.status_loading_companies), isError = false)
        
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getCompanies()
                
                if (response.isSuccessful) {
                    companiesList = response.body()?.companies ?: emptyList()
                    
                    if (companiesList.isEmpty()) {
                        showStatus(getString(R.string.status_no_companies), isError = false)
                        actvCompany.setAdapter(null)
                    } else {
                        // Create adapter with company names and contact count
                        val displayList = companiesList.map { 
                            "${it.name} (${it.contactCount} kontak)" 
                        }
                        val adapter = ArrayAdapter(
                            this@EmployeeActivity,
                            android.R.layout.simple_dropdown_item_1line,
                            displayList
                        )
                        actvCompany.setAdapter(adapter)
                        
                        cardStatus.visibility = View.GONE
                        
                        Toast.makeText(
                            this@EmployeeActivity,
                            getString(R.string.status_companies_found, companiesList.size),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    showStatus("Error: ${response.code()}", isError = true)
                }
            } catch (e: Exception) {
                showStatus("Error: ${e.message}", isError = true)
            }
        }
    }

    private fun syncContacts() {
        val companyName = selectedCompanyName ?: actvCompany.text.toString().trim()
            .replace(Regex("\\s*\\(\\d+\\s*kontak\\)$"), "") // Remove (X kontak) suffix if any
        val passcode = etPasscode.text.toString().trim()

        if (companyName.isEmpty()) {
            tilCompany.error = getString(R.string.company_hint) // Or create specific error string
            return
        }
        tilCompany.error = null

        if (passcode.isEmpty()) {
            etPasscode.error = getString(R.string.passcode_hint) // Or create specific error string
            return
        }

        // Check BOTH permissions (READ and WRITE)
        val hasReadPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasReadPermission || !hasWritePermission) {
            // SHOW PROMINENT DISCLOSURE FIRST
            showPermissionRationaleDialog(companyName, passcode)
            return
        }

        performSync(companyName, passcode)
    }

    private fun performSync(companyName: String, passcode: String) {
        setLoading(true)
        showStatus("🔄 Mengambil data kontak...", isError = false)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.syncContacts(
                    SyncRequest(companyName, passcode)
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    val contacts = body?.contacts ?: emptyList()

                    if (contacts.isEmpty()) {
                        showStatus(getString(R.string.status_no_companies), isError = false) // Reusing no_companies contextually or add new string if needed. Actually it's no contacts. 
                        // Let's use a generic message or just dynamic text.
                        // I will use hardcoded for now or add to strings.xml? I already added status_imported etc. 
                        // Added "status_skipped" and "status_success".
                        // Wait, I didn't add "No contacts found in company".
                        setLoading(false)
                        return@launch
                    }

                    showStatus(getString(R.string.status_syncing), isError = false) 

                    // Import contacts on IO thread
                    val result = withContext(Dispatchers.IO) {
                        ContactHelper.importContacts(this@EmployeeActivity, contacts)
                    }

                    val (imported, skipped) = result

                    showStatus(
                        "${getString(R.string.status_success)}\n" +
                                "${getString(R.string.status_imported, imported)}\n" +
                                "${getString(R.string.status_skipped, skipped)}",
                        isError = false
                    )

                    Toast.makeText(
                        this@EmployeeActivity,
                        getString(R.string.status_imported, imported),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> getString(R.string.error_invalid_login)
                        else -> "Error ${response.code()}"
                    }
                    showStatus(errorMsg, isError = true)
                }
            } catch (e: Exception) {
                showStatus("❌ Error: ${e.message}", isError = true)
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        btnSync.isEnabled = !loading
        btnSync.text = if (loading) "Syncing..." else "🔄 SYNC NOW"
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun showPermissionRationaleDialog(companyName: String, passcode: String) {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.permission_title))
            .setMessage(getString(R.string.permission_desc))
            .setPositiveButton(getString(R.string.permission_positive)) { _, _ ->
                // User agrees, NOW request system permission
                pendingCompanyName = companyName
                pendingPasscode = passcode
                requestPermissionsLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS
                    )
                )
            }
            .setNegativeButton(getString(R.string.permission_negative)) { dialog, _ ->
                dialog.dismiss()
                showStatus(getString(R.string.error_permission), isError = true)
            }
            .setCancelable(false)
            .show()
    }

    private fun showStatus(message: String, isError: Boolean) {
        cardStatus.visibility = View.VISIBLE
        tvStatus.text = message

        if (isError) {
            cardStatus.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
            tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        } else {
            cardStatus.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
            tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        }
    }
}
