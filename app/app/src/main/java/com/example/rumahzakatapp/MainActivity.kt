package com.example.rumahzakatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Binding Tombol di bagian "Akses Cepat"
        val btnDonasi = findViewById<Button>(R.id.btnMenuDonasi)
        val btnKurban = findViewById<Button>(R.id.btnMenuKurban)
        val btnZakat = findViewById<Button>(R.id.btnMenuZakat)

        // Binding CardView "Tata Kelola Lembaga"
        val cvTataKelola = findViewById<CardView>(R.id.cvTataKelola)

        // Mengarahkan tombol Infak Subuh ke form UC-01
        btnDonasi.setOnClickListener {
//            startActivity(Intent(this, DonasiActivity::class.java))
        }

        // Mengarahkan tombol Kalkulator Zakat ke form UC-02
        btnZakat.setOnClickListener {
//            startActivity(Intent(this, ZakatActivity::class.java))
        }

        // Placeholder untuk UC-03 (Kurban)
        btnKurban.setOnClickListener {
            Toast.makeText(this, "Fitur Pelacakan Superqurban (Segera Hadir)", Toast.LENGTH_SHORT).show()
        }

        // Placeholder untuk UC-05 (Tata Kelola)
        cvTataKelola.setOnClickListener {
            Toast.makeText(this, "Fitur Laporan Transparansi (Segera Hadir)", Toast.LENGTH_SHORT).show()
        }
    }
}