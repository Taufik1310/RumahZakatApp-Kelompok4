package com.example.rumahzakatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbHelper = DatabaseHelper(this)
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val sessionEmail = sharedPref.getString("SESSION_EMAIL", "") ?: ""
        val sessionNama = sharedPref.getString("SESSION_NAMA", "Donatur")

        // Tampilkan Nama
        val tvNama = findViewById<TextView>(R.id.tvNamaDashboard)
        tvNama.text = sessionNama

        // Tampilkan Total Donasi
        val tvTotalDonasi = findViewById<TextView>(R.id.tvTotalDonasiDashboard)
        val totalDonasi = dbHelper.getTotalDonasi(sessionEmail)

        // Format ke Rupiah
        val localeID = java.util.Locale("in", "ID")
        val formatRupiah = java.text.NumberFormat.getCurrencyInstance(localeID)
        tvTotalDonasi.text = formatRupiah.format(totalDonasi)

        // Binding Tombol di bagian "Akses Cepat"
        val btnDonasi = findViewById<Button>(R.id.btnMenuDonasi)
        val btnKurban = findViewById<Button>(R.id.btnMenuKurban)
        val btnZakat = findViewById<Button>(R.id.btnMenuZakat)
        val btnBansos = findViewById<Button>(R.id.btnMenuBansos)

        // Binding CardView "Tata Kelola Lembaga" (UC-05)
        val cvTataKelola = findViewById<CardView>(R.id.cvTataKelola)

        // Binding Bottom Navigation
        val navBeranda = findViewById<LinearLayout>(R.id.navBeranda)
        val navDonasi = findViewById<LinearLayout>(R.id.navDonasi)
        val navPenyaluran = findViewById<LinearLayout>(R.id.navPenyaluran)
        val navProfil = findViewById<LinearLayout>(R.id.navProfil)

        // ----------------------------------------------------
        // FUNGSI KLIK MENU AKSES CEPAT
        // ----------------------------------------------------

        btnDonasi.setOnClickListener {
            startActivity(Intent(this, DonasiActivity::class.java))
        }

        btnZakat.setOnClickListener {
            startActivity(Intent(this, ZakatActivity::class.java))
        }

        btnKurban.setOnClickListener {
            startActivity(Intent(this, LacakKurbanActivity::class.java))
        }

        btnBansos.setOnClickListener {
            startActivity(Intent(this, BansosActivity::class.java))
        }

        cvTataKelola.setOnClickListener {
            Toast.makeText(this, "Laporan Transparansi (Segera Hadir)", Toast.LENGTH_SHORT).show()
        }

        // ----------------------------------------------------
        // FUNGSI KLIK BOTTOM NAVIGATION
        // ----------------------------------------------------

        navBeranda.setOnClickListener {
            // Sudah berada di Beranda
            Toast.makeText(this, "Anda sedang di halaman Beranda", Toast.LENGTH_SHORT).show()
        }

        navDonasi.setOnClickListener {
            // Mengarahkan ke halaman daftar kampanye (UC-01)
            startActivity(Intent(this, DonasiActivity::class.java))
        }

        navPenyaluran.setOnClickListener {
            startActivity(Intent(this, LacakKurbanActivity::class.java))
        }

        navProfil.setOnClickListener {
            // Nanti bisa untuk fitur Logout
            Toast.makeText(this, "Fitur Profil Pengguna (Segera Hadir)", Toast.LENGTH_SHORT).show()
        }
    }
}