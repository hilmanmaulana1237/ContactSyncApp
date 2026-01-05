package com.contactsync.mobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.contactsync.mobile.model.SuperadminLoginRequest
import com.contactsync.mobile.network.RetrofitClient
import com.contactsync.mobile.utils.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class SuperadminLoginActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnBack: MaterialButton
    private lateinit var progressBar: CircularProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_superadmin_login)
        initViews()
        setupListeners()
    }

    private fun initViews() {
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnBack = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            performLogin()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun performLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (username.isEmpty()) {
            etUsername.error = "Username wajib diisi"
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Password wajib diisi"
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.superadminLogin(
                    SuperadminLoginRequest(username, password)
                )

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@SuperadminLoginActivity,
                        "Login berhasil! Selamat datang Superadmin",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(Intent(this@SuperadminLoginActivity, SuperadminActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@SuperadminLoginActivity,
                        "Username atau password salah",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@SuperadminLoginActivity,
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
        btnLogin.text = if (loading) "Loading..." else "Login Superadmin"
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}
