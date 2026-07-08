package com.example.rumahzakatapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.text.NumberFormat
import java.util.Locale

class BansosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bansos)

        findViewById<TextView>(R.id.btnBackBansos).setOnClickListener { finish() }

        val etNik = findViewById<EditText>(R.id.etNikBansos)
        val etPendapatan = findViewById<EditText>(R.id.etPendapatanBansos)
        val etNominalPengajuan = findViewById<EditText>(R.id.etNominalPengajuan)

        val swErrorFile = findViewById<Switch>(R.id.swErrorFile)
        val tvErrorFile = findViewById<TextView>(R.id.tvErrorFile)
        val btnAjukan = findViewById<Button>(R.id.btnAjukanBansos)

        val cvHasil = findViewById<CardView>(R.id.cvHasilBansos)
        val ivIconHasil = findViewById<ImageView>(R.id.ivIconHasilBansos)
        val tvHasil = findViewById<TextView>(R.id.tvHasilBansos)

        val dbHelper = DatabaseHelper(this)
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

        // IMPLEMENTASI EXCEPTIONAL FLOW 1E (Validasi Ukuran & Format File)
        swErrorFile.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Tombol dikunci dan pesan error muncul (Sesuai dokumen)
                tvErrorFile.visibility = View.VISIBLE
                btnAjukan.isEnabled = false
                btnAjukan.backgroundTintList = getColorStateList(android.R.color.darker_gray)
            } else {
                tvErrorFile.visibility = View.GONE
                btnAjukan.isEnabled = true
                btnAjukan.backgroundTintList = getColorStateList(R.color.orange_primary)
            }
        }

        btnAjukan.setOnClickListener {
            val nik = etNik.text.toString().trim()
            val pendapatanStr = etPendapatan.text.toString().trim()
            val pengajuanStr = etNominalPengajuan.text.toString().trim()

            if (nik.length < 16 || pendapatanStr.isEmpty() || pengajuanStr.isEmpty()) {
                Toast.makeText(this, "NIK harus 16 digit dan semua nominal wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pendapatan = pendapatanStr.toDouble()
            val pengajuan = pengajuanStr.toDouble()

            val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val sessionEmail = sharedPref.getString("SESSION_EMAIL", "") ?: ""

            // IMPLEMENTASI EXCEPTIONAL FLOW 5E (Indikasi Manipulasi Data)
            // Asumsi: Jika NIK berakhiran "000", kita anggap NIK tersebut terindikasi Fraud.
            if (nik.endsWith("000")) {
                cvHasil.visibility = View.VISIBLE
                cvHasil.setCardBackgroundColor(resources.getColor("#FFF0F5".toInt() ?: android.graphics.Color.parseColor("#FFF0F5")))
                ivIconHasil.setImageResource(R.drawable.ic_cancel)
                ivIconHasil.setColorFilter(resources.getColor(android.R.color.holo_red_dark))

                // Pesan sesuai dokumen 5E
                tvHasil.text = "TOLAK PENGAJUAN (Fraud)\n\nAdmin Distribusi menemukan indikasi manipulasi data saat survei. Pengajuan ditolak dan NIK ini dimasukkan ke dalam daftar hitam (blacklist) selama 6 bulan."
                tvHasil.setTextColor(resources.getColor(android.R.color.holo_red_dark))

                dbHelper.insertBansos(sessionEmail, nik, pendapatan, "Ditolak (Fraud)")
                btnAjukan.isEnabled = false
                return@setOnClickListener
            }

            // Batas kemiskinan (Desil 1-5) diasumsikan berpendapatan di bawah Rp 1.500.000
            val batasDesil = 1500000.0

            cvHasil.visibility = View.VISIBLE

            // LOGIKA NORMAL FLOW 2 & ALTERNATE FLOW 2A (Pencocokan Desil)
            if (pendapatan <= batasDesil) {
                // NORMAL FLOW: Masuk prioritas Desil 1-5
                cvHasil.setCardBackgroundColor(android.graphics.Color.parseColor("#E0F2F1"))
                ivIconHasil.setImageResource(R.drawable.ic_check_circle)
                ivIconHasil.setColorFilter(resources.getColor(R.color.orange_primary))
                tvHasil.text = "Lolos Verifikasi Otomatis.\n\nSistem mengidentifikasi Anda sebagai prioritas Desil 1-5. Berkas telah diteruskan ke Dasbor Admin Wilayah."
            } else {
                // ALTERNATE FLOW 2A: Di luar prioritas, diberi tag kuning
                cvHasil.setCardBackgroundColor(android.graphics.Color.parseColor("#FFF9C4")) // Warna Kuning
                ivIconHasil.setImageResource(R.drawable.ic_warning)
                ivIconHasil.setColorFilter(android.graphics.Color.parseColor("#F57F17"))
                tvHasil.text = "Reguler / Prioritas Rendah (Tag Kuning).\n\nNIK tidak masuk Desil 1-5, namun berkas tetap diteruskan ke Dasbor Admin untuk ditinjau manual berdasarkan urgensi insidental."
            }

            dbHelper.insertBansos(sessionEmail, nik, pendapatan, "Menunggu Survei Admin")
            btnAjukan.text = "Pengajuan Telah Terekam"
            btnAjukan.isEnabled = false

            // SIMULASI RESPON ADMIN DISTRIBUSI (Normal Flow 6 & Alternate Flow 6A)
            // Menampilkan dialog seolah-olah waktu telah berlalu dan Admin sudah meninjau.
            AlertDialog.Builder(this)
                .setTitle("Simulasi Dasbor Admin Distribusi")
                .setMessage("Pilih tindakan Admin (Simulasi Prototype) terhadap pengajuan sebesar ${formatRupiah.format(pengajuan)} ini:")
                .setPositiveButton("Setujui Penuh (Flow 6)") { _, _ ->
                    Toast.makeText(this, "Status diubah menjadi 'Disetujui'. Dana penuh segera dicairkan.", Toast.LENGTH_LONG).show()
                }
                .setNegativeButton("Persetujuan Sebagian (Alt Flow 6A)") { _, _ ->
                    val danaSebagian = pengajuan * 0.5 // Disetujui 50% saja
                    Toast.makeText(this, "Anggaran terbatas. Disetujui sebagian (${formatRupiah.format(danaSebagian)}). Alasan penyesuaian telah dikirim ke Mustahiq.", Toast.LENGTH_LONG).show()
                }
                .setCancelable(false)
                .show()
        }
    }
}