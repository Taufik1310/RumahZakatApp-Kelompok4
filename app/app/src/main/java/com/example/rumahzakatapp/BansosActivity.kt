package com.example.rumahzakatapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BansosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bansos)

        val btnBack = findViewById<TextView>(R.id.btnBackBansos)
        val etNik = findViewById<EditText>(R.id.etNikBansos)
        val etPendapatan = findViewById<EditText>(R.id.etPendapatanBansos)
        val btnAjukan = findViewById<Button>(R.id.btnAjukanBansos)
        val tvHasil = findViewById<TextView>(R.id.tvHasilBansos)

        val dbHelper = DatabaseHelper(this)

        btnBack.setOnClickListener { finish() }

        btnAjukan.setOnClickListener {
            val nik = etNik.text.toString().trim()
            val inputPendapatan = etPendapatan.text.toString().trim()

            if (nik.isEmpty() || inputPendapatan.isEmpty()) {
                Toast.makeText(this, "Semua form wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nik.length < 16) {
                Toast.makeText(this, "NIK harus 16 digit!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pendapatan = inputPendapatan.toDouble()

            // Batas maksimal pendapatan untuk disetujui (Simulasi Desil)
            val batasKemiskinan = 2000000.0

            var statusPengajuan = ""

            // IMPLEMENTASI EXCEPTIONAL & NORMAL FLOW (Sistem Screening)
            if (pendapatan > batasKemiskinan) {
                // Exceptional Flow: Ditolak karena di atas standar
                statusPengajuan = "Ditolak (Non-Desil 1)"
                tvHasil.text = "❌ MAAF, PENGAJUAN DITOLAK\n\nBerdasarkan penilaian sistem, pendapatan Anda berada di atas garis prioritas. Saat ini kuota difokuskan untuk keluarga Desil 1 (Sangat Rentan)."
                tvHasil.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            } else {
                // Normal Flow: Diterima untuk diverifikasi lapangan
                statusPengajuan = "Menunggu Verifikasi Lapangan"
                tvHasil.text = "✅ PENGAJUAN DITERIMA AWAL\n\nData Anda lolos verifikasi sistem. Tim surveyor kami akan segera menghubungi Anda untuk pengecekan lapangan."
                tvHasil.setTextColor(resources.getColor(R.color.orange_primary))
            }

            // Simpan ke SQLite sebagai rekam jejak
            val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val sessionEmail = sharedPref.getString("SESSION_EMAIL", "") ?: ""

            dbHelper.insertBansos(sessionEmail, nik, pendapatan, statusPengajuan)

            // Kunci tombol agar tidak di-spam
            btnAjukan.isEnabled = false
            btnAjukan.text = "Pengajuan Telah Terekam"
        }
    }
}