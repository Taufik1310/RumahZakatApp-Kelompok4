package com.example.rumahzakatapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Memuat Data dari SQLite & Sesi Login
        val dbHelper = DatabaseHelper(this)
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val sessionEmail = sharedPref.getString("SESSION_EMAIL", "") ?: ""
        val sessionNama = sharedPref.getString("SESSION_NAMA", "Donatur Baik")

        // 2. Menampilkan Nama Pengguna
        val tvNama = findViewById<TextView>(R.id.tvNamaDashboard)
        tvNama.text = sessionNama

        // 3. Menampilkan Total Donasi (Format Rupiah)
        val tvTotalDonasi = findViewById<TextView>(R.id.tvTotalDonasiDashboard)
        val totalDonasi = dbHelper.getTotalDonasi(sessionEmail)
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        tvTotalDonasi.text = formatRupiah.format(totalDonasi)

        // 4. BINDING MENU AKSES CEPAT (Sekarang menggunakan LinearLayout)
        val btnDonasi = findViewById<LinearLayout>(R.id.btnMenuDonasi)
        val btnZakat = findViewById<LinearLayout>(R.id.btnMenuZakat)
        val btnKurban = findViewById<LinearLayout>(R.id.btnMenuKurban)
        val btnBansos = findViewById<LinearLayout>(R.id.btnMenuBansos)

        val btnDonasiSaya = findViewById<Button>(R.id.btnDonasiSayaDashboard)
        val cvTataKelola = findViewById<CardView>(R.id.cvTataKelola)

        // Fungsi Pindah Halaman Akses Cepat
        btnDonasi.setOnClickListener { startActivity(Intent(this, DonasiActivity::class.java)) }
        btnZakat.setOnClickListener { startActivity(Intent(this, ZakatActivity::class.java)) }
        btnKurban.setOnClickListener { startActivity(Intent(this, LacakKurbanActivity::class.java)) }
        btnBansos.setOnClickListener { startActivity(Intent(this, BansosActivity::class.java)) }

        btnDonasiSaya.setOnClickListener { startActivity(Intent(this, RiwayatDonasiActivity::class.java)) }
        cvTataKelola.setOnClickListener { startActivity(Intent(this, TransparansiActivity::class.java)) }

        // 5. BINDING BOTTOM NAVIGATION
        val navBeranda = findViewById<LinearLayout>(R.id.navBeranda)
        val navDonasi = findViewById<LinearLayout>(R.id.navDonasi)
        val navPenyaluran = findViewById<LinearLayout>(R.id.navPenyaluran)
        val navProfil = findViewById<LinearLayout>(R.id.navProfil)

        // Fungsi Pindah Halaman Navigasi Bawah
        navBeranda.setOnClickListener {
            Toast.makeText(this, "Anda sudah berada di Beranda", Toast.LENGTH_SHORT).show()
        }
        navDonasi.setOnClickListener { startActivity(Intent(this, DonasiActivity::class.java)) }
        navPenyaluran.setOnClickListener { startActivity(Intent(this, LacakKurbanActivity::class.java)) }
        navProfil.setOnClickListener { startActivity(Intent(this, ProfilActivity::class.java)) }
    }
}