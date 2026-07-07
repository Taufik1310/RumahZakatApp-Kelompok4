package com.example.rumahzakatapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper // Panggil DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DatabaseHelper(this) // Inisialisasi

        val etNama = findViewById<EditText>(R.id.etNamaRegister)
        val etEmail = findViewById<EditText>(R.id.etEmailRegister)
        val etPassword = findViewById<EditText>(R.id.etPasswordRegister)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)

        btnRegister.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua data wajib diisi!", Toast.LENGTH_SHORT).show()
            } else {
                // Masukkan ke SQLite
                val isInserted = dbHelper.insertUser(nama, email, password)
                if (isInserted) {
                    Toast.makeText(this, "Pendaftaran berhasil! Silakan Login", Toast.LENGTH_SHORT).show()
                    finish() // Kembali ke halaman Login
                } else {
                    Toast.makeText(this, "Gagal mendaftar, coba lagi", Toast.LENGTH_SHORT).show()
                }
            }
        }

        tvGoToLogin.setOnClickListener { finish() }
    }
}