package com.example.rumahzakatapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ProfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val btnBack = findViewById<TextView>(R.id.btnBackProfil)
        val tvNama = findViewById<TextView>(R.id.tvProfilNama)
        val tvEmail = findViewById<TextView>(R.id.tvProfilEmail)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        btnBack.setOnClickListener { finish() }

        // Mengambil data sesi
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val sessionNama = sharedPref.getString("SESSION_NAMA", "Donatur")
        val sessionEmail = sharedPref.getString("SESSION_EMAIL", "email@rz.com")

        tvNama.text = sessionNama
        tvEmail.text = sessionEmail

        // FUNGSI LOGOUT
        btnLogout.setOnClickListener {
            // Hapus semua data sesi
            sharedPref.edit().clear().apply()
            Toast.makeText(this, "Anda berhasil keluar", Toast.LENGTH_SHORT).show()

            // Kembali ke halaman Login dan hapus tumpukan halaman sebelumnya
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}