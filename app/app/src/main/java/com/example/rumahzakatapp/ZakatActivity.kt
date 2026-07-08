package com.example.rumahzakatapp

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale

class ZakatActivity : AppCompatActivity() {

    private var nominalZakatFinal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zakat)

        findViewById<TextView>(R.id.btnBackZakat).setOnClickListener { finish() }

        val rgModeZakat = findViewById<RadioGroup>(R.id.rgModeZakat)
        val layoutKalkulator = findViewById<LinearLayout>(R.id.layoutKalkulator)
        val layoutManual = findViewById<LinearLayout>(R.id.layoutManual)

        val etHarta = findViewById<EditText>(R.id.etHartaZakat)
        val tvPeringatanNishab = findViewById<TextView>(R.id.tvPeringatanNishab)
        val etNominalManual = findViewById<EditText>(R.id.etNominalManual)
        val tvHasilNominal = findViewById<TextView>(R.id.tvHasilNominalZakat)
        val btnTunaikan = findViewById<Button>(R.id.btnTunaikanZakat)

        val nishabZakat = 6859394.0 // Asumsi Nishab per bulan
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

        // Fungsi Helper untuk mengunci/membuka tombol
        fun setButtonState(isEnabled: Boolean) {
            btnTunaikan.isEnabled = isEnabled
            btnTunaikan.backgroundTintList = getColorStateList(if (isEnabled) R.color.orange_primary else android.R.color.darker_gray)
        }

        // ALTERNATE FLOW 2A: Toggle Mode Kalkulator vs Manual
        rgModeZakat.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbKalkulator) {
                layoutKalkulator.visibility = View.VISIBLE
                layoutManual.visibility = View.GONE
                etHarta.text.clear()
            } else {
                layoutKalkulator.visibility = View.GONE
                layoutManual.visibility = View.VISIBLE
                etNominalManual.text.clear()
            }
            nominalZakatFinal = 0.0
            tvHasilNominal.text = "Rp0"
            setButtonState(false)
        }

        // Real-time perhitungan Mode Kalkulator (Normal Flow 3 & Exceptional 3E)
        etHarta.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (rgModeZakat.checkedRadioButtonId == R.id.rbKalkulator) {
                    val input = s.toString().trim()
                    if (input.isNotEmpty()) {
                        val harta = input.toDouble()

                        // EXCEPTIONAL FLOW 3E: Di bawah Nishab
                        if (harta < nishabZakat) {
                            tvPeringatanNishab.visibility = View.VISIBLE
                            nominalZakatFinal = 0.0
                            tvHasilNominal.text = "Rp0"
                            setButtonState(false) // Tombol dikunci
                        } else {
                            tvPeringatanNishab.visibility = View.GONE
                            nominalZakatFinal = harta * 0.025
                            tvHasilNominal.text = formatRupiah.format(nominalZakatFinal)
                            setButtonState(true) // Lolos Nishab, tombol dibuka
                        }
                    } else {
                        tvPeringatanNishab.visibility = View.GONE
                        setButtonState(false)
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Real-time perhitungan Mode Manual (Alternate Flow 2A)
        etNominalManual.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (rgModeZakat.checkedRadioButtonId == R.id.rbManual) {
                    val input = s.toString().trim()
                    if (input.isNotEmpty() && input.toDouble() > 0) {
                        nominalZakatFinal = input.toDouble()
                        tvHasilNominal.text = formatRupiah.format(nominalZakatFinal)
                        setButtonState(true)
                    } else {
                        nominalZakatFinal = 0.0
                        tvHasilNominal.text = "Rp0"
                        setButtonState(false)
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Eksekusi Pembayaran Zakat (Normal Flow 6, 7, 8 & Exceptional Flow 8E)
        btnTunaikan.setOnClickListener {
            // Simpan ke SQLite
            val dbHelper = DatabaseHelper(this)
            val sessionEmail = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("SESSION_EMAIL", "") ?: ""

            // Asumsi harta 0 jika manual (untuk kemudahan database prototipe)
            val hartaInsert = if (rgModeZakat.checkedRadioButtonId == R.id.rbKalkulator) etHarta.text.toString().toDouble() else nominalZakatFinal

            dbHelper.insertZakat(sessionEmail, hartaInsert, nominalZakatFinal)

            // Dialog Bukti Setor Pajak (BSP)
            val dialog = AlertDialog.Builder(this)
                .setTitle("✅ Transaksi Zakat Berhasil")
                .setMessage("Alhamdulillah, zakat sebesar ${formatRupiah.format(nominalZakatFinal)} telah kami terima.\n\nAnda berhak mendapatkan Bukti Setor Pajak (BSP) resmi sebagai pengurang Penghasilan Kena Pajak (PKP).")
                .setPositiveButton("Unduh BSP (PDF)") { _, _ ->
                    // EXCEPTIONAL FLOW 8E: Simulasi error jika nominal belakangnya angka 1 (misal 250001)
                    if (nominalZakatFinal % 10 == 1.0) {
                        Toast.makeText(this, "ERROR: Dokumen sedang diproses, mohon coba beberapa saat lagi (Log dikirim ke CXD).", Toast.LENGTH_LONG).show()
                    } else {
                        // NORMAL FLOW 8: Sukses Unduh
                        Toast.makeText(this, "Mengunduh BSP... PDF dengan Barcode resmi disimpan ke perangkat.", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
                .setNeutralButton("Kirim ke Email") { _, _ ->
                    // ALTERNATE FLOW 7A: Kirim BSP ke Email
                    Toast.makeText(this, "BSP PDF telah dilampirkan dan dikirim otomatis ke email Anda.", Toast.LENGTH_LONG).show()
                    finish()
                }
                .setCancelable(false)
                .create()

            dialog.show()
        }
    }
}