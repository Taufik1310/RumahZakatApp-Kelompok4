package com.example.rumahzakatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BayarDonasiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bayar_donasi)

        val tvNamaKampanye = findViewById<TextView>(R.id.tvNamaKampanye)
        val etNominal = findViewById<EditText>(R.id.etNominalDonasi)
        val swAnonim = findViewById<Switch>(R.id.swAnonim)
        val btnProses = findViewById<Button>(R.id.btnProsesDonasi)

        val judul = intent.getStringExtra("JUDUL_KAMPANYE")
        tvNamaKampanye.text = judul

        btnProses.setOnClickListener {
            val inputNominal = etNominal.text.toString().trim()

            if (inputNominal.isEmpty()) {
                Toast.makeText(this, "Nominal tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nominal = inputNominal.toDouble()

            // VALIDASI EXCEPTIONAL FLOW 6E: Minimal Donasi Rp 1.000
            // VALIDASI EXCEPTIONAL FLOW 6E: Minimal Donasi Rp 1.000
            if (nominal < 1000) {
                Toast.makeText(this, "Maaf, minimal donasi adalah Rp 1.000", Toast.LENGTH_LONG).show()
            } else {
                // Ambil sesi user
                val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                val sessionEmail = sharedPref.getString("SESSION_EMAIL", "") ?: ""

                // Simpan ke SQLite
                val dbHelper = DatabaseHelper(this)
                val statusAnonim = if (swAnonim.isChecked) 1 else 0
                val isSuccess = dbHelper.insertDonasi(sessionEmail, judul!!, nominal, statusAnonim)

                if (isSuccess) {
                    val namaDonatur = if (swAnonim.isChecked) "Hamba Allah" else sharedPref.getString("SESSION_NAMA", "Donatur")
                    Toast.makeText(this, "Alhamdulillah, $namaDonatur berhasil berdonasi!", Toast.LENGTH_LONG).show()

                    // Kembali ke Dashboard agar Total Donasi ter-refresh
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Menghapus tumpukan activity sebelumnya
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Gagal memproses donasi.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}