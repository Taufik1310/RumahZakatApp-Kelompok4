package com.example.rumahzakatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TransparansiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transparansi)

        val btnBack = findViewById<TextView>(R.id.btnBackTransparansi)
        val btn2024 = findViewById<Button>(R.id.btnTahun2024)
        val btn2025 = findViewById<Button>(R.id.btnTahun2025)
        val btn2026 = findViewById<Button>(R.id.btnTahun2026)

        val tvStatusLaporan = findViewById<TextView>(R.id.tvStatusLaporan)
        val btnUnduh = findViewById<Button>(R.id.btnUnduhLaporan)
        val btnShare = findViewById<Button>(R.id.btnShareLaporan)

        btnBack.setOnClickListener { finish() }

        // Fungsi bantuan untuk reset warna tombol tahun
        fun resetBtnColor() {
            val colorInactive = resources.getColor(R.color.white)
            val textInactive = resources.getColor(R.color.text_gray)

            btn2024.backgroundTintList = getColorStateList(R.color.white)
            btn2024.setTextColor(textInactive)

            btn2025.backgroundTintList = getColorStateList(R.color.white)
            btn2025.setTextColor(textInactive)

            btn2026.backgroundTintList = getColorStateList(R.color.white)
            btn2026.setTextColor(textInactive)
        }

        // FUNGSI NORMAL FLOW: Laporan sudah terbit (2024 & 2025)
        val onYearAvailableSelected = { btn: Button, year: String ->
            resetBtnColor()
            btn.backgroundTintList = getColorStateList(R.color.orange_primary)
            btn.setTextColor(resources.getColor(R.color.white))

            tvStatusLaporan.text = "✅ Dokumen Audit Laporan Tahunan Terintegrasi $year telah disahkan oleh KAP. Siap diunduh."
            tvStatusLaporan.setTextColor(resources.getColor(R.color.text_dark))

            btnUnduh.isEnabled = true
            btnUnduh.backgroundTintList = getColorStateList(R.color.orange_primary)
        }

        btn2024.setOnClickListener { onYearAvailableSelected(btn2024, "2024") }
        btn2025.setOnClickListener { onYearAvailableSelected(btn2025, "2025") }

        // FUNGSI EXCEPTIONAL FLOW 7E: Laporan Tahun Berjalan Belum Rilis (2026)
        btn2026.setOnClickListener {
            resetBtnColor()
            btn2026.backgroundTintList = getColorStateList(R.color.orange_primary)
            btn2026.setTextColor(resources.getColor(R.color.white))

            // Mengunci tombol unduh
            btnUnduh.isEnabled = false
            btnUnduh.backgroundTintList = getColorStateList(android.R.color.darker_gray)

            // Pesan peringatan (Sesuai dokumen OOAD Exceptional 7E)
            tvStatusLaporan.text = "⚠️ Dokumen dalam tahap audit eksternal Kantor Akuntan Publik (KAP), estimasi rilis: Kuartal 1 Tahun 2027."
            tvStatusLaporan.setTextColor(resources.getColor(android.R.color.holo_red_dark))
        }

        // Simulasi Klik Unduh untuk Normal Flow
        btnUnduh.setOnClickListener {
            Toast.makeText(this, "Mengunduh file Annual_Report.pdf...", Toast.LENGTH_SHORT).show()
        }

        // IMPLEMENTASI ALTERNATE FLOW 7A: Membagikan Laporan
        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            val shareMessage = "Lihat transparansi dan laporan tata kelola Rumah Zakat terkini. Kunjungi: https://www.rumahzakat.org/transparansi"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)

            // Membuka menu pemilihan aplikasi (WhatsApp, Telegram, Email, dll)
            startActivity(Intent.createChooser(shareIntent, "Bagikan Tautan Melalui"))
        }

        // Panggil status default awal aplikasi dibuka (2026 terpilih)
        btn2026.performClick()
    }
}