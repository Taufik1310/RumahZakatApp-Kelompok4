package com.example.rumahzakatapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ZakatActivity : AppCompatActivity() {

    private var nilaiZakatWajib: Double = 0.0 // Variabel penampung nominal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zakat)

        // Binding View
        val btnBack = findViewById<TextView>(R.id.btnBackZakat)
        val etPenghasilan = findViewById<EditText>(R.id.etPenghasilanZakat)
        val btnHitung = findViewById<Button>(R.id.btnHitungZakat)
        val tvHasil = findViewById<TextView>(R.id.tvHasilZakat)
        val btnTunaikan = findViewById<Button>(R.id.btnTunaikanZakat)

        val dbHelper = DatabaseHelper(this)

        // Asumsi Nishab Zakat Penghasilan (Berdasarkan harga emas/beras terkini)
        val nishabZakat = 6859394.0

        // Tombol Kembali
        btnBack.setOnClickListener { finish() }

        // Tombol Hitung Zakat
        btnHitung.setOnClickListener {
            val input = etPenghasilan.text.toString().trim()

            if (input.isEmpty()) {
                tvHasil.text = "Harap masukkan nominal penghasilan Anda."
                tvHasil.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                btnTunaikan.visibility = View.GONE // Sembunyikan tombol
                return@setOnClickListener
            }

            val penghasilan = input.toDouble()
            val localeID = java.util.Locale("in", "ID")
            val formatter = java.text.NumberFormat.getCurrencyInstance(localeID)

            // IMPLEMENTASI EXCEPTIONAL FLOW (3E UC-02)
            if (penghasilan < nishabZakat) {
                // Menolak perhitungan karena di bawah Nishab
                tvHasil.text = "Harta Anda belum mencapai Nishab wajib Zakat (${formatter.format(nishabZakat)}).\n\nNamun, Anda tetap dapat menyalurkannya sebagai Infak/Sedekah melalui menu Donasi."
                tvHasil.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                btnTunaikan.visibility = View.GONE // Kunci (sembunyikan) tombol Tunaikan
            } else {
                // Berhasil melampaui batas Nishab
                nilaiZakatWajib = penghasilan * 0.025

                tvHasil.text = "Alhamdulillah, penghasilan Anda telah mencapai Nishab.\n\nKewajiban Zakat (2,5%):\n${formatter.format(nilaiZakatWajib)}"
                tvHasil.setTextColor(resources.getColor(R.color.orange_primary))
                btnTunaikan.visibility = View.VISIBLE // Munculkan tombol Tunaikan
            }
        }

        // Tombol Eksekusi Pembayaran
        btnTunaikan.setOnClickListener {
            val input = etPenghasilan.text.toString().trim()
            val penghasilan = input.toDouble()

            // Ambil Sesi Pengguna
            val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val sessionEmail = sharedPref.getString("SESSION_EMAIL", "") ?: ""

            // Simpan ke SQLite
            val isSuccess = dbHelper.insertZakat(sessionEmail, penghasilan, nilaiZakatWajib)

            if (isSuccess) {
                Toast.makeText(this, "Transaksi Zakat Berhasil Disimpan!", Toast.LENGTH_LONG).show()
                finish() // Menutup halaman zakat dan kembali ke Dashboard
            } else {
                Toast.makeText(this, "Terjadi kesalahan saat menyimpan data.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}