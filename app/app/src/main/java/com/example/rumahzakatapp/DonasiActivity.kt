package com.example.rumahzakatapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

// Menambahkan variabel terkumpul dan target dalam bentuk angka
data class Kampanye(val judul: String, val terkumpul: Double, val target: Double)

class DonasiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donasi)

        findViewById<TextView>(R.id.btnBackDonasi).setOnClickListener { finish() }

        val rvKampanye = findViewById<RecyclerView>(R.id.rvKampanye)
        rvKampanye.layoutManager = LinearLayoutManager(this)

        // Simulasi Data (Kampanye ke-3 diset 100% penuh untuk skenario Flow 6B)
        val listKampanye = listOf(
            Kampanye("Bantuan Darurat Bencana Banjir Demak", 15000000.0, 50000000.0),
            Kampanye("Sedekah Jumat Berkah Pelosok", 5000000.0, 15000000.0),
            Kampanye("Beasiswa Anak Juara Nusantara", 25000000.0, 25000000.0) // 100% Terpenuhi
        )

        rvKampanye.adapter = KampanyeAdapter(listKampanye) { kampanye ->
            val intent = Intent(this, BayarDonasiActivity::class.java)
            intent.putExtra("JUDUL_KAMPANYE", kampanye.judul)

            // Mengirim status apakah kampanye sudah 100% penuh (Alternate Flow 6B)
            val isFull = kampanye.terkumpul >= kampanye.target
            intent.putExtra("IS_FULL", isFull)

            startActivity(intent)
        }
    }
}

class KampanyeAdapter(private val list: List<Kampanye>, private val onClick: (Kampanye) -> Unit) : RecyclerView.Adapter<KampanyeAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJudul: TextView = view.findViewById(R.id.tvJudulKampanye)
        val tvTerkumpul: TextView = view.findViewById(R.id.tvTerkumpulDana)
        val tvTarget: TextView = view.findViewById(R.id.tvSisaTarget)
        val pbKampanye: ProgressBar = view.findViewById(R.id.pbKampanye)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_campaign, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

        holder.tvJudul.text = item.judul
        holder.tvTerkumpul.text = "Terkumpul: ${formatRupiah.format(item.terkumpul)}"
        holder.tvTarget.text = "Target: ${formatRupiah.format(item.target)}"

        // Kalkulasi Persentase untuk Progress Bar (Normal Flow 5)
        val percentage = ((item.terkumpul / item.target) * 100).toInt()
        holder.pbKampanye.progress = percentage

        holder.itemView.setOnClickListener { onClick(item) }
    }
    override fun getItemCount() = list.size
}