package com.contactsync.mobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.contactsync.mobile.model.CreateCompanyRequest
import com.contactsync.mobile.network.RetrofitClient
import com.contactsync.mobile.utils.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RegisterCompanyActivity : AppCompatActivity() {

    private lateinit var etCompanyName: TextInputEditText
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPasscode: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var btnBack: MaterialButton
    private lateinit var progressBar: CircularProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_company)
        initViews()
        setupListeners()
    }

    private fun initViews() {
        etCompanyName = findViewById(R.id.etCompanyName)
        etUsername = findViewById(R.id.etUsername)
        etPasscode = findViewById(R.id.etPasscode)
        btnRegister = findViewById(R.id.btnRegister)
        btnBack = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        btnRegister.setOnClickListener {
            registerCompany()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun registerCompany() {
        val companyName = etCompanyName.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val passcode = etPasscode.text.toString().trim()

        if (companyName.isEmpty()) {
            etCompanyName.error = "Nama perusahaan wajib diisi"
            return
        }

        if (username.isEmpty()) {
            etUsername.error = "Username wajib diisi"
            return
        }

        if (passcode.isEmpty()) {
            etPasscode.error = "Passcode wajib diisi"
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.createCompany(
                    CreateCompanyRequest(companyName, username, passcode)
                )

                if (response.isSuccessful) {
                    val company = response.body()?.company
                    
                    if (company != null) {
                        // Auto-login after registration
                        SessionManager.saveSession(
                            this@RegisterCompanyActivity,
                            company.id,
                            company.name
                        )
                        
                        Toast.makeText(
                            this@RegisterCompanyActivity,
                            "✅ Perusahaan berhasil didaftarkan!",
                            Toast.LENGTH_SHORT
                        ).show()
                        
                        // Go to AdminActivity
                        val intent = Intent(this@RegisterCompanyActivity, AdminActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        409 -> "Username sudah digunakan"
                        else -> "Gagal mendaftarkan: ${response.code()}"
                    }
                    Toast.makeText(this@RegisterCompanyActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@RegisterCompanyActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        btnRegister.isEnabled = !loading
        btnRegister.text = if (loading) "Loading..." else "Daftarkan Perusahaan"
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}
