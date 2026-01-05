package com.contactsync.mobile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.contactsync.mobile.adapter.CompanyAdapter
import com.contactsync.mobile.model.Contact
import com.contactsync.mobile.model.SuperadminCompanyItem
import com.contactsync.mobile.model.UpdateCompanyRequest
import com.contactsync.mobile.model.UpdateContactRequest
import com.contactsync.mobile.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class SuperadminActivity : AppCompatActivity() {

    private lateinit var tvStats: TextView
    private lateinit var rvCompanies: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var btnLogout: MaterialButton
    
    private lateinit var companyAdapter: CompanyAdapter
    private var companiesList: MutableList<SuperadminCompanyItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_superadmin)
        initViews()
        setupRecyclerView()
        setupListeners()
        loadCompanies()
    }

    private fun initViews() {
        tvStats = findViewById(R.id.tvStats)
        rvCompanies = findViewById(R.id.rvCompanies)
        tvEmpty = findViewById(R.id.tvEmpty)
        progressBar = findViewById(R.id.progressBar)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun setupRecyclerView() {
        companyAdapter = CompanyAdapter(
            companiesList,
            onEditCompany = { company -> showEditCompanyDialog(company) },
            onDeleteCompany = { company -> showDeleteCompanyDialog(company) },
            onEditContact = { company, contact -> showEditContactDialog(company, contact) },
            onDeleteContact = { company, contact -> showDeleteContactDialog(company, contact) }
        )
        rvCompanies.layoutManager = LinearLayoutManager(this)
        rvCompanies.adapter = companyAdapter
    }

    private fun setupListeners() {
        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Apakah kamu yakin ingin logout?")
                .setPositiveButton("Ya") { _, _ ->
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun loadCompanies() {
        progressBar.visibility = View.VISIBLE
        rvCompanies.visibility = View.GONE
        tvEmpty.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getSuperadminCompanies()

                if (response.isSuccessful) {
                    val companies = response.body()?.companies ?: emptyList()
                    companiesList.clear()
                    companiesList.addAll(companies)
                    companyAdapter.updateCompanies(companiesList)

                    val totalContacts = companies.sumOf { it.contactCount }
                    tvStats.text = "${companies.size} Perusahaan • $totalContacts Kontak"

                    if (companies.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                        rvCompanies.visibility = View.GONE
                    } else {
                        tvEmpty.visibility = View.GONE
                        rvCompanies.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@SuperadminActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SuperadminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    // ========================
    // COMPANY CRUD
    // ========================

    private fun showEditCompanyDialog(company: SuperadminCompanyItem) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_company, null)
        val etCompanyName = dialogView.findViewById<TextInputEditText>(R.id.etCompanyName)
        val etUsername = dialogView.findViewById<TextInputEditText>(R.id.etUsername)
        val etPasscode = dialogView.findViewById<TextInputEditText>(R.id.etPasscode)
        val tvCurrentPasscode = dialogView.findViewById<TextView>(R.id.tvCurrentPasscode)

        etCompanyName.setText(company.name)
        etUsername.setText(company.username)
        tvCurrentPasscode.text = "🔑 Passcode saat ini: ${company.passcode ?: "****"}"

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val newPasscode = etPasscode.text.toString().trim()
                updateCompany(
                    company.id,
                    etCompanyName.text.toString().trim(),
                    etUsername.text.toString().trim(),
                    if (newPasscode.isNotEmpty()) newPasscode else null
                )
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun updateCompany(companyId: String, name: String, username: String, passcode: String?) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.updateCompany(
                    companyId,
                    UpdateCompanyRequest(name, username, passcode)
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@SuperadminActivity, "✅ Perusahaan berhasil diupdate", Toast.LENGTH_SHORT).show()
                    loadCompanies()
                } else {
                    val errorMsg = when (response.code()) {
                        409 -> "Username sudah digunakan"
                        else -> "Gagal update"
                    }
                    Toast.makeText(this@SuperadminActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SuperadminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteCompanyDialog(company: SuperadminCompanyItem) {
        AlertDialog.Builder(this)
            .setTitle("⚠️ Hapus Perusahaan")
            .setMessage("Hapus \"${company.name}\"?\n\nSemua ${company.contactCount} kontak akan ikut terhapus!")
            .setPositiveButton("Hapus") { _, _ -> deleteCompany(company) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteCompany(company: SuperadminCompanyItem) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteCompany(company.id)
                if (response.isSuccessful) {
                    Toast.makeText(this@SuperadminActivity, "✅ Perusahaan dihapus", Toast.LENGTH_SHORT).show()
                    loadCompanies()
                } else {
                    Toast.makeText(this@SuperadminActivity, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SuperadminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ========================
    // CONTACT CRUD
    // ========================

    private fun showEditContactDialog(company: SuperadminCompanyItem, contact: Contact) {
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
                    company.id,
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

    private fun updateContact(companyId: String, contactId: String, name: String, phone: String, role: String, email: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.superadminUpdateContact(
                    companyId,
                    contactId,
                    UpdateContactRequest(name, phone, role, email)
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@SuperadminActivity, "✅ Kontak berhasil diupdate", Toast.LENGTH_SHORT).show()
                    loadCompanies()
                } else {
                    Toast.makeText(this@SuperadminActivity, "Gagal update", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SuperadminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteContactDialog(company: SuperadminCompanyItem, contact: Contact) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Kontak")
            .setMessage("Hapus \"${contact.name}\" dari ${company.name}?")
            .setPositiveButton("Hapus") { _, _ -> deleteContact(company.id, contact.id ?: "") }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteContact(companyId: String, contactId: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.superadminDeleteContact(companyId, contactId)
                if (response.isSuccessful) {
                    Toast.makeText(this@SuperadminActivity, "Kontak dihapus", Toast.LENGTH_SHORT).show()
                    loadCompanies()
                } else {
                    Toast.makeText(this@SuperadminActivity, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SuperadminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
