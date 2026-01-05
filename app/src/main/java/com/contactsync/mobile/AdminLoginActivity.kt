package com.contactsync.mobile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.contactsync.mobile.model.AdminLoginRequest
import com.contactsync.mobile.network.RetrofitClient
import com.contactsync.mobile.utils.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etPasscode: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnRegister: MaterialButton
    private lateinit var btnBack: MaterialButton
    private lateinit var tvForgotPassword: TextView
    private lateinit var progressBar: CircularProgressIndicator

    // Support email
    private val supportEmail = "hilmanm12347050020@gmail.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (SessionManager.isLoggedIn(this)) {
            startActivity(Intent(this, AdminActivity::class.java))
            finish()
            return
        }
        
        setContentView(R.layout.activity_admin_login)
        initViews()
        setupListeners()
    }

    private fun initViews() {
        etUsername = findViewById(R.id.etUsername)
        etPasscode = findViewById(R.id.etPasscode)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        btnBack = findViewById(R.id.btnBack)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener { performLogin() }
        
        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterCompanyActivity::class.java))
        }
        
        btnBack.setOnClickListener { finish() }
        
        tvForgotPassword.setOnClickListener { showForgotPasswordDialog() }
    }

    private fun showForgotPasswordDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.forgot_password_title))
            .setMessage(getString(R.string.forgot_password_msg, supportEmail))
            .setPositiveButton(getString(R.string.btn_send_email)) { _, _ ->
                sendEmailToSupport()
            }
            .setNegativeButton(getString(R.string.btn_close), null)
            .show()
    }

    private fun sendEmailToSupport() {
        val username = etUsername.text.toString().trim()
        val subject = getString(R.string.email_subject)
        val body = getString(R.string.email_body, if (username.isNotEmpty()) username else "(...)")

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        try {
            startActivity(Intent.createChooser(intent, getString(R.string.btn_send_email)))
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.no_email_app), Toast.LENGTH_SHORT).show()
        }
    }

    private fun performLogin() {
        val username = etUsername.text.toString().trim()
        val passcode = etPasscode.text.toString().trim()

        if (username.isEmpty()) {
            etUsername.error = getString(R.string.username_hint)
            return
        }

        if (passcode.isEmpty()) {
            etPasscode.error = getString(R.string.passcode_required)
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.adminLogin(
                    AdminLoginRequest(username, passcode)
                )

                if (response.isSuccessful) {
                    val company = response.body()?.company
                    
                    if (company != null) {
                        SessionManager.saveSession(
                            this@AdminLoginActivity,
                            company.id,
                            company.name
                        )
                        
                        Toast.makeText(
                            this@AdminLoginActivity,
                            getString(R.string.login_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        
                        startActivity(Intent(this@AdminLoginActivity, AdminActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this@AdminLoginActivity,
                        getString(R.string.login_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@AdminLoginActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        btnLogin.isEnabled = !loading
        btnLogin.text = if (loading) getString(R.string.loading) else getString(R.string.login)
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}
