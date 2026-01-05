package com.contactsync.mobile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.contactsync.mobile.adapter.ContactAdapter
import com.contactsync.mobile.model.AddContactRequest
import com.contactsync.mobile.model.Contact
import com.contactsync.mobile.model.UpdateContactRequest
import com.contactsync.mobile.network.RetrofitClient
import com.contactsync.mobile.utils.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AdminActivity : AppCompatActivity() {

    private lateinit var tvCompanyName: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var tabAddContact: ScrollView
    private lateinit var tabViewContacts: View
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var btnLogout: MaterialButton
    
    // Add contact form
    private lateinit var etContactName: TextInputEditText
    private lateinit var etContactPhone: TextInputEditText
    private lateinit var etContactRole: TextInputEditText
    private lateinit var etContactEmail: TextInputEditText
    private lateinit var btnAddContact: MaterialButton
    
    // Contacts list
    private lateinit var rvContacts: RecyclerView
    private lateinit var tvEmptyContacts: TextView
    private lateinit var tvContactCount: TextView
    
    private lateinit var contactAdapter: ContactAdapter
    private var contactsList: MutableList<Contact> = mutableListOf()
    
    private var companyId: String = ""
    private var companyName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        
        if (!checkSession()) return
        
        initViews()
        setupTabs()
        setupRecyclerView()
        setupListeners()
    }

    private fun checkSession(): Boolean {
        if (!SessionManager.isLoggedIn(this)) {
            startActivity(Intent(this, AdminLoginActivity::class.java))
            finish()
            return false
        }
        companyId = SessionManager.getCompanyId(this) ?: ""
        companyName = SessionManager.getCompanyName(this) ?: ""
        return true
    }

    private fun initViews() {
        tvCompanyName = findViewById(R.id.tvCompanyName)
        tabLayout = findViewById(R.id.tabLayout)
        tabAddContact = findViewById(R.id.tabAddContact)
        tabViewContacts = findViewById(R.id.tabViewContacts)
        progressBar = findViewById(R.id.progressBar)
        btnLogout = findViewById(R.id.btnLogout)
        
        etContactName = findViewById(R.id.etContactName)
        etContactPhone = findViewById(R.id.etContactPhone)
        etContactRole = findViewById(R.id.etContactRole)
        etContactEmail = findViewById(R.id.etContactEmail)
        btnAddContact = findViewById(R.id.btnAddContact)
        
        rvContacts = findViewById(R.id.rvContacts)
        tvEmptyContacts = findViewById(R.id.tvEmptyContacts)
        tvContactCount = findViewById(R.id.tvContactCount)
        
        tvCompanyName.text = companyName
    }

    private fun setupTabs() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        tabAddContact.visibility = View.VISIBLE
                        tabViewContacts.visibility = View.GONE
                    }
                    1 -> {
                        tabAddContact.visibility = View.GONE
                        tabViewContacts.visibility = View.VISIBLE
                        loadContacts()
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter(
            contactsList,
            onEditClick = { contact -> showEditContactDialog(contact) },
            onDeleteClick = { contact -> showDeleteDialog(contact) }
        )
        rvContacts.layoutManager = LinearLayoutManager(this)
        rvContacts.adapter = contactAdapter
    }

    private fun setupListeners() {
        btnAddContact.setOnClickListener { addContact() }
        
        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Apakah kamu yakin ingin logout?")
                .setPositiveButton("Ya") { _, _ ->
                    SessionManager.logout(this)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun addContact() {
        val name = etContactName.text.toString().trim()
        val phone = etContactPhone.text.toString().trim()
        val role = etContactRole.text.toString().trim()
        val email = etContactEmail.text.toString().trim()

        if (name.isEmpty()) {
            etContactName.error = "Nama wajib diisi"
            return
        }
        if (phone.isEmpty()) {
            etContactPhone.error = "Nomor telepon wajib diisi"
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.addContact(
                    AddContactRequest(companyId, name, phone, role, email)
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@AdminActivity, "✅ Kontak berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    clearForm()
                } else {
                    val errorMsg = when (response.code()) {
                        409 -> "Nomor telepon sudah terdaftar"
                        else -> "Gagal menambahkan kontak"
                    }
                    Toast.makeText(this@AdminActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun clearForm() {
        etContactName.text?.clear()
        etContactPhone.text?.clear()
        etContactRole.text?.clear()
        etContactEmail.text?.clear()
    }

    private fun loadContacts() {
        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getCompanyContacts(companyId)

                if (response.isSuccessful) {
                    val contacts = response.body()?.contacts ?: emptyList()
                    contactsList.clear()
                    contactsList.addAll(contacts)
                    contactAdapter.updateContacts(contactsList)
                    
                    tvContactCount.text = "📊 ${contacts.size} kontak"
                    
                    if (contacts.isEmpty()) {
                        tvEmptyContacts.visibility = View.VISIBLE
                        rvContacts.visibility = View.GONE
                    } else {
                        tvEmptyContacts.visibility = View.GONE
                        rvContacts.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun showEditContactDialog(contact: Contact) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_contact, null)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etName)
        val etPhone = dialogView.findViewById<TextInputEditText>(R.id.etPhone)
        val etRole = dialogView.findViewById<TextInputEditText>(R.id.etRole)
        val etEmail = dialogView.findViewById<TextInputEditText>(R.id.etEmail)

        etName.setText(contact.name)
        etPhone.setText(contact.phone)
        etRole.setText(contact.role ?: "")
        etEmail.setText(contact.email ?: "")

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                updateContact(
                    contact.id ?: "",
                    etName.text.toString().trim(),
                    etPhone.text.toString().trim(),
                    etRole.text.toString().trim(),
                    etEmail.text.toString().trim()
                )
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun updateContact(contactId: String, name: String, phone: String, role: String, email: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.updateContact(
                    companyId,
                    contactId,
                    UpdateContactRequest(name, phone, role, email)
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@AdminActivity, "✅ Kontak berhasil diupdate", Toast.LENGTH_SHORT).show()
                    loadContacts()
                } else {
                    Toast.makeText(this@AdminActivity, "Gagal update kontak", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteDialog(contact: Contact) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Kontak")
            .setMessage("Hapus \"${contact.name}\"?")
            .setPositiveButton("Hapus") { _, _ -> deleteContact(contact) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteContact(contact: Contact) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteContact(companyId, contact.id ?: "")

                if (response.isSuccessful) {
                    Toast.makeText(this@AdminActivity, "Kontak dihapus", Toast.LENGTH_SHORT).show()
                    loadContacts()
                } else {
                    Toast.makeText(this@AdminActivity, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}
