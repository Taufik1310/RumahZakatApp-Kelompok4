package com.example.rumahzakatapp

import android.os.Bundle
import android.widget.TextView
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

        val tvStatusTahap2 = findViewById<TextView>(R.id.tvStatusTahap2)
        val tvPesanDokumentasi = findViewById<TextView>(R.id.tvPesanDokumentasi)

        val tvStatusTahap3 = findViewById<TextView>(R.id.tvStatusTahap3)
        val tvPesanLogistik = findViewById<TextView>(R.id.tvPesanLogistik)

        // IMPLEMENTASI UC-03: Pengecekan Status Tahap 2
        if (status >= 2) {
            tvStatusTahap2.text = "✅ Tahap 2: Proses Penyembelihan"
            // Exceptional Flow 5E: Jaringan di pelosok terhambat
            tvPesanDokumentasi.text = "Hewan Anda telah disembelih secara sah. Dokumentasi visual sedang diproses karena kendala jaringan di lokasi."
            tvPesanDokumentasi.setTextColor(resources.getColor(android.R.color.holo_red_dark))
        } else {
            tvStatusTahap2.text = "⏳ Tahap 2: Belum Dimulai"
            tvPesanDokumentasi.text = "Menunggu jadwal penyembelihan."
            tvPesanDokumentasi.setTextColor(resources.getColor(R.color.text_gray))
        }

        // IMPLEMENTASI UC-03: Pengecekan Tahap 3 (Logistik vs Penebaran Langsung)
        if (tipe == "Penebaran Langsung") {
            // Alternate Flow 6A: Melewati pelacakan kurir logistik
            tvStatusTahap3.text = "🥩 Tahap 3: Laporan Komunal"
            tvPesanLogistik.text = "Daging kurban dibagikan langsung ke wilayah pelosok/miskin tanpa dikalengkan. Laporan distribusi akan diupdate."
            tvPesanLogistik.setTextColor(resources.getColor(R.color.text_gray))
        } else if (tipe == "Superqurban") {
            // Exceptional Flow 6E: Gagal Tarik Data Resi API Ekspedisi
            tvStatusTahap3.text = "📦 Tahap 3: Pelacakan Logistik"
            tvPesanLogistik.text = "Data pelacakan kurir sedang dalam pemeliharaan (Gagal integrasi API resi)."
            tvPesanLogistik.setTextColor(resources.getColor(R.color.orange_primary))
        }
    }
}