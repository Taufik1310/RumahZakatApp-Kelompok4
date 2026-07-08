package com.example.rumahzakatapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale

class BayarDonasiActivity : AppCompatActivity() {

    private lateinit var btnProses: Button
    private lateinit var tvErrorNominal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bayar_donasi)

        findViewById<TextView>(R.id.btnBackBayar).setOnClickListener { finish() }

        val tvNamaKampanye = findViewById<TextView>(R.id.tvNamaKampanye)
        val etNominal = findViewById<EditText>(R.id.etNominalDonasi)
        val swAnonim = findViewById<Switch>(R.id.swAnonim)
        val cbDonasiRutin = findViewById<CheckBox>(R.id.cbDonasiRutin)
        val rgMetode = findViewById<RadioGroup>(R.id.rgMetodeBayar)

        btnProses = findViewById(R.id.btnProsesDonasi)
        tvErrorNominal = findViewById(R.id.tvErrorNominal)

        val judul = intent.getStringExtra("JUDUL_KAMPANYE") ?: "Kampanye Kebaikan"
        val isFull = intent.getBooleanExtra("IS_FULL", false)
        tvNamaKampanye.text = judul

        // ALTERNATE FLOW 6B: Kampanye 100% Penuh
        if (isFull) {
            AlertDialog.Builder(this)
                .setTitle("Target Telah Terpenuhi")
                .setMessage("Alhamdulillah, target dana kampanye ini sudah 100% terpenuhi.\n\nSistem merekomendasikan Anda untuk mengalihkan donasi ke kampanye lain yang masih membutuhkan. Namun, Anda tetap dapat melanjutkan donasi di sini jika berkenan.")
                .setPositiveButton("Tetap Donasi Di Sini") { dialog, _ -> dialog.dismiss() }
                .setNegativeButton("Pilih Kampanye Lain") { _, _ -> finish() }
                .setCancelable(false)
                .show()
        }

        // EXCEPTIONAL FLOW 6E: Validasi Real-time Minimal Rp 1.000
        etNominal.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val inputStr = s.toString().trim()
                if (inputStr.isNotEmpty()) {
                    val nominal = inputStr.toDoubleOrNull()
                    if (nominal == null || nominal < 1000) {
                        // Mengunci tombol dan memunculkan pesan error persis sesuai dokumen
                        tvErrorNominal.text = "Nominal minimal Rp1.000"
                        tvErrorNominal.visibility = View.VISIBLE
                        btnProses.isEnabled = false
                        btnProses.backgroundTintList = getColorStateList(android.R.color.darker_gray)
                    } else {
                        // Membuka kunci tombol
                        tvErrorNominal.visibility = View.GONE
                        btnProses.isEnabled = true
                        btnProses.backgroundTintList = getColorStateList(R.color.orange_primary)
                    }
                } else {
                    tvErrorNominal.visibility = View.GONE
                    btnProses.isEnabled = true
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Proses Eksekusi Pembayaran
        btnProses.setOnClickListener {
            val inputNominal = etNominal.text.toString().trim()

            if (inputNominal.isEmpty()) {
                Toast.makeText(this, "Harap isi nominal donasi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nominal = inputNominal.toDouble()

            // Simulasi Exceptional Flow 8E (Jika metode e-wallet sedang gangguan)
            val selectedMethodId = rgMetode.checkedRadioButtonId
            val selectedMethod = findViewById<RadioButton>(selectedMethodId).text.toString()

            if (nominal == 13000.0) { // Angka pancingan untuk mensimulasikan error timeout (8E)
                Toast.makeText(this, "ERROR: Pembayaran Timeout/Gagal diverifikasi. Laporan diteruskan ke CXD.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // SIMPAN DATA (Normal Flow 8 & Alternate Flow 6A)
            val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val sessionEmail = sharedPref.getString("SESSION_EMAIL", "") ?: ""
            val namaDonatur = if (swAnonim.isChecked) "Hamba Allah" else sharedPref.getString("SESSION_NAMA", "Donatur")

            val dbHelper = DatabaseHelper(this)
            val isSuccess = dbHelper.insertDonasi(sessionEmail, judul, nominal, if (swAnonim.isChecked) 1 else 0)

            if (isSuccess) {
                // Notifikasi Logika Donasi Rutin (6A)
                if (cbDonasiRutin.isChecked) {
                    Toast.makeText(this, "Jadwal donasi rutin bulanan telah diaktifkan.", Toast.LENGTH_SHORT).show()
                }

                // NORMAL FLOW 8: Menampilkan simulasi e-Struk
                val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(nominal)

                AlertDialog.Builder(this)
                    .setTitle("✅ Transaksi Berhasil (e-Struk)")
                    .setMessage("Jazakallah Khairan, $namaDonatur!\n\nDonasi sebesar $formatRupiah untuk kampanye '$judul' melalui $selectedMethod telah berhasil kami terima.\n\nNominal pada progress bar kampanye akan diperbarui secara real-time.")
                    .setPositiveButton("Tutup & Kembali") { _, _ ->
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
                    .setCancelable(false)
                    .show()
            } else {
                Toast.makeText(this, "Terjadi kesalahan sistem internal.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}