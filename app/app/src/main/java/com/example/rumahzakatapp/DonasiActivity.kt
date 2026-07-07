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

data class Kampanye(val judul: String, val target: String)

class DonasiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donasi)

        // Tombol Kembali
        val btnBack = findViewById<TextView>(R.id.btnBackDonasi)
        btnBack.setOnClickListener { finish() }

        val rvKampanye = findViewById<RecyclerView>(R.id.rvKampanye)
        rvKampanye.layoutManager = LinearLayoutManager(this)

        val listKampanye = listOf(
            Kampanye("Bantuan Darurat Bencana Banjir Demak", "Rp 50.000.000"),
            Kampanye("Sedekah Jumat Berkah Pelosok", "Rp 15.000.000"),
            Kampanye("Beasiswa Anak Juara Nusantara", "Rp 25.000.000")
        )

        rvKampanye.adapter = KampanyeAdapter(listKampanye) { kampanye ->
            val intent = Intent(this, BayarDonasiActivity::class.java)
            intent.putExtra("JUDUL_KAMPANYE", kampanye.judul)
            startActivity(intent)
        }
    }
}

class KampanyeAdapter(
    private val list: List<Kampanye>,
    private val onClick: (Kampanye) -> Unit
) : RecyclerView.Adapter<KampanyeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJudul: TextView = view.findViewById(R.id.tvJudulKampanye)
        val tvTarget: TextView = view.findViewById(R.id.tvTargetDana)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_campaign, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvJudul.text = item.judul
        holder.tvTarget.text = "Target: ${item.target}"
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = list.size
}