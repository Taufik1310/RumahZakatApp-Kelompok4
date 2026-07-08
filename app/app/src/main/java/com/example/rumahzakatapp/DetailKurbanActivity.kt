package com.example.rumahzakatapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetailKurbanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_kurban)

        val jenis = intent.getStringExtra("JENIS")
        val tipe = intent.getStringExtra("TIPE")
        val status = intent.getIntExtra("STATUS", 1)

        findViewById<TextView>(R.id.tvDetailJenis).text = jenis
        findViewById<TextView>(R.id.tvDetailTipe).text = "Produk: $tipe"

        // Binding TAHAP 2
        val ivIconTahap2 = findViewById<ImageView>(R.id.ivIconTahap2)
        val tvStatusTahap2 = findViewById<TextView>(R.id.tvStatusTahap2)
        val tvPesanDokumentasi = findViewById<TextView>(R.id.tvPesanDokumentasi)

        // Binding TAHAP 3
        val ivIconTahap3 = findViewById<ImageView>(R.id.ivIconTahap3)
        val tvStatusTahap3 = findViewById<TextView>(R.id.tvStatusTahap3)
        val tvPesanLogistik = findViewById<TextView>(R.id.tvPesanLogistik)

        // Binding TAHAP 4 & Tombol (Flow 8, 9, 9E)
        val ivIconTahap4 = findViewById<ImageView>(R.id.ivIconTahap4)
        val tvStatusTahap4 = findViewById<TextView>(R.id.tvStatusTahap4)
        val tvPesanSertifikat = findViewById<TextView>(R.id.tvPesanSertifikat)
        val btnUnduhSertifikat = findViewById<Button>(R.id.btnUnduhSertifikat)
        val btnKirimWA = findViewById<Button>(R.id.btnKirimWA)

        // IMPLEMENTASI TAHAP 2 (Exceptional Flow 5E)
        if (status >= 2) {
            ivIconTahap2.setImageResource(R.drawable.ic_check_circle)
            tvStatusTahap2.text = "Tahap 2: Proses Penyembelihan"
            // Teks 5E Sesuai Dokumen OOAD
            tvPesanDokumentasi.text = "Hewan Anda telah disembelih secara sah. Dokumentasi visual sedang diproses karena kendala jaringan di lokasi."
            tvPesanDokumentasi.setTextColor(resources.getColor(android.R.color.holo_red_dark))
        } else {
            ivIconTahap2.setImageResource(R.drawable.ic_pending)
            tvStatusTahap2.text = "Tahap 2: Belum Dimulai"
            tvPesanDokumentasi.text = "Menunggu jadwal penyembelihan."
        }

        // IMPLEMENTASI TAHAP 3 (Alternate Flow 6A & Exceptional Flow 6E)
        if (tipe == "Penebaran Langsung") {
            // Alternate Flow 6A: Melewati logistik kurir
            ivIconTahap3.setImageResource(R.drawable.ic_food)
            tvStatusTahap3.text = "Tahap 3: Laporan Komunal"
            tvPesanLogistik.text = "Sistem melewati tahap pelacakan kurir. Daging dibagikan langsung ke wilayah pelosok beserta statistik wilayah dampak sosialnya."
            tvPesanLogistik.setTextColor(resources.getColor(R.color.text_gray))
        } else if (tipe == "Superqurban") {
            // Exceptional Flow 6E: Gagal Handshake API Kurir
            ivIconTahap3.setImageResource(R.drawable.ic_warning)
            tvStatusTahap3.text = "Tahap 3: Pelacakan Logistik"
            tvPesanLogistik.text = "Data pelacakan kurir sedang dalam pemeliharaan."
            tvPesanLogistik.setTextColor(resources.getColor(R.color.orange_primary))
        }

        // IMPLEMENTASI TAHAP 4 (Normal Flow 8, 9 & Exceptional Flow 9E)
        // (Asumsi demo prototipe: Semua kurban yang ada di list sudah berstatus Selesai di sistem)
        if (status >= 1) {
            ivIconTahap4.setImageResource(R.drawable.ic_check_circle)
            ivIconTahap4.setColorFilter(resources.getColor(R.color.orange_primary))
            tvStatusTahap4.text = "Tahap 4: Selesai & Bersertifikat"
            tvPesanSertifikat.text = "Paket/Distribusi telah diterima. Sistem telah men-generate Sertifikat Kurban digital berformat PDF untuk Anda."
            tvPesanSertifikat.setTextColor(resources.getColor(R.color.text_dark))

            // Munculkan tombol Unduh Sertifikat (Normal Flow 9)
            btnUnduhSertifikat.visibility = View.VISIBLE

            // Saat Donatur menekan "Unduh", kita trigger skenario Exceptional Flow 9E
            btnUnduhSertifikat.setOnClickListener {
                Toast.makeText(this, "ERROR: Berkas sertifikat korup saat diunduh. Gagal Enkripsi PDF.", Toast.LENGTH_LONG).show()

                // Sembunyikan tombol unduh, munculkan opsi alternatif (9E)
                btnUnduhSertifikat.visibility = View.GONE
                btnKirimWA.visibility = View.VISIBLE
            }

            btnKirimWA.setOnClickListener {
                Toast.makeText(this, "Mengirim dokumen sertifikat melalui akun WhatsApp resmi Rumah Zakat...", Toast.LENGTH_LONG).show()
            }
        }
    }
}