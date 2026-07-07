package com.example.rumahzakatapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RiwayatDonasiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_donasi)

        findViewById<TextView>(R.id.btnBackRiwayat).setOnClickListener { finish() }

        val rvRiwayat = findViewById<RecyclerView>(R.id.rvRiwayat)
        rvRiwayat.layoutManager = LinearLayoutManager(this)

        val dbHelper = DatabaseHelper(this)
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val sessionEmail = sharedPref.getString("SESSION_EMAIL", "") ?: ""

        // Tarik data dari SQLite
        val listRiwayat = dbHelper.getRiwayatDonasi(sessionEmail)

        rvRiwayat.adapter = RiwayatAdapter(listRiwayat)
    }
}

// Adapter RecyclerView Riwayat
class RiwayatAdapter(private val list: List<DatabaseHelper.RiwayatDonasi>) : RecyclerView.Adapter<RiwayatAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvKampanye: TextView = view.findViewById(R.id.tvRiwayatKampanye)
        val tvNominal: TextView = view.findViewById(R.id.tvRiwayatNominal)
        val tvStatus: TextView = view.findViewById(R.id.tvRiwayatStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat_donasi, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvKampanye.text = item.kampanye

        val localeID = java.util.Locale("in", "ID")
        val formatRupiah = java.text.NumberFormat.getCurrencyInstance(localeID)
        holder.tvNominal.text = formatRupiah.format(item.nominal)

        holder.tvStatus.text = if (item.isAnonim == 1) "Berdonasi sebagai: Hamba Allah" else "Berdonasi sebagai: Publik"
    }
    override fun getItemCount() = list.size
}