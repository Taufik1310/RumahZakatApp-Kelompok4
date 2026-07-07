package com.example.rumahzakatapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Model Data Sementara
data class Kurban(val id: Int, val jenis: String, val tipe: String, val status: Int)

class LacakKurbanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lacak_kurban)

        val btnBack = findViewById<TextView>(R.id.btnBackLacak)
        btnBack.setOnClickListener { finish() }

        val dbHelper = DatabaseHelper(this)
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val sessionEmail = sharedPref.getString("SESSION_EMAIL", "") ?: ""

        // Generate data palsu khusus untuk demo prototipe OOAD
        dbHelper.generateDummyKurban(sessionEmail)

        // Tarik data dari SQLite (Simulasi)
        val rvKurban = findViewById<RecyclerView>(R.id.rvKurban)
        rvKurban.layoutManager = LinearLayoutManager(this)

        // Kita bypass query lengkap demi efisiensi, pakai list data objek hasil mapping
        val listKurban = listOf(
            Kurban(1, "1 Ekor Kambing", "Superqurban", 2),
            Kurban(2, "1/7 Sapi", "Penebaran Langsung", 1)
        )

        rvKurban.adapter = KurbanAdapter(listKurban) { kurban ->
            val intent = Intent(this, DetailKurbanActivity::class.java)
            intent.putExtra("JENIS", kurban.jenis)
            intent.putExtra("TIPE", kurban.tipe)
            intent.putExtra("STATUS", kurban.status)
            startActivity(intent)
        }
    }
}

class KurbanAdapter(private val list: List<Kurban>, private val onClick: (Kurban) -> Unit) : RecyclerView.Adapter<KurbanAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJenis: TextView = view.findViewById(R.id.tvJenisKurban)
        val tvTipe: TextView = view.findViewById(R.id.tvTipeProduk)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_kurban, parent, false))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvJenis.text = item.jenis
        holder.tvTipe.text = "Tipe Produk: ${item.tipe}"
        holder.itemView.setOnClickListener { onClick(item) }
    }
    override fun getItemCount() = list.size
}