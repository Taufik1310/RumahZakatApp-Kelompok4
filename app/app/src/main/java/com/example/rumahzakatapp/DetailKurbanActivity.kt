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

        val ivIconTahap2 = findViewById<android.widget.ImageView>(R.id.ivIconTahap2)
        val ivIconTahap3 = findViewById<android.widget.ImageView>(R.id.ivIconTahap3)

        // IMPLEMENTASI UC-03: Pengecekan Status Tahap 2
        if (status >= 2) {
            ivIconTahap2.setImageResource(R.drawable.ic_check_circle)
            tvStatusTahap2.text = "Tahap 2: Proses Penyembelihan"
            tvPesanDokumentasi.text = "Hewan Anda disembelih sah. Dokumentasi visual diproses karena kendala jaringan."
            tvPesanDokumentasi.setTextColor(resources.getColor(android.R.color.holo_red_dark))
        } else {
            ivIconTahap2.setImageResource(R.drawable.ic_pending)
            tvStatusTahap2.text = "Tahap 2: Belum Dimulai"
            tvPesanDokumentasi.text = "Menunggu jadwal penyembelihan."
        }

        // IMPLEMENTASI UC-03: Pengecekan Tahap 3 (Logistik vs Penebaran Langsung)
        if (tipe == "Penebaran Langsung") {
            ivIconTahap3.setImageResource(R.drawable.ic_food)
            tvStatusTahap3.text = "Tahap 3: Laporan Komunal"
            tvPesanLogistik.text = "Daging kurban dibagikan langsung ke wilayah pelosok/miskin."
        } else if (tipe == "Superqurban") {
            ivIconTahap3.setImageResource(R.drawable.ic_kurban)
            tvStatusTahap3.text = "Tahap 3: Pelacakan Logistik"
            tvPesanLogistik.text = "Data pelacakan kurir sedang dalam pemeliharaan."
            tvPesanLogistik.setTextColor(resources.getColor(R.color.orange_primary))
        }
    }
}