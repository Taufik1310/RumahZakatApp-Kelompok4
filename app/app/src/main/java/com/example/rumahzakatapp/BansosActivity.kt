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
        val ivIconHasil = findViewById<android.widget.ImageView>(R.id.ivIconHasilBansos)
        ivIconHasil.visibility = android.view.View.VISIBLE

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
                ivIconHasil.setImageResource(R.drawable.ic_cancel)
                ivIconHasil.setColorFilter(resources.getColor(android.R.color.holo_red_dark))
                tvHasil.text = "MAAF, PENGAJUAN DITOLAK\n\nBerdasarkan penilaian sistem, pendapatan Anda di atas garis prioritas. Kuota difokuskan untuk Desil 1."
                tvHasil.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            } else {
                ivIconHasil.setImageResource(R.drawable.ic_check_circle)
                ivIconHasil.setColorFilter(resources.getColor(R.color.orange_primary))
                tvHasil.text = "PENGAJUAN DITERIMA AWAL\n\nData lolos verifikasi. Tim surveyor akan segera menghubungi Anda."
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