package com.example.rumahzakatapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 4 // Naikkan versi database
        private const val DATABASE_NAME = "RumahZakat.db"

        // Tabel User
        private const val TABLE_USER = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAMA = "nama"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"

        // Tabel Donasi (UC-01)
        private const val TABLE_DONASI = "tb_donasi"
        private const val COL_DONASI_EMAIL = "email_user"
        private const val COL_DONASI_KAMPANYE = "kampanye"
        private const val COL_DONASI_NOMINAL = "nominal"
        private const val COL_DONASI_ANONIM = "is_anonim"

        // Tabel Zakat (UC-02)
        private const val TABLE_ZAKAT = "tb_zakat"
        private const val COL_ZAKAT_EMAIL = "email_user"
        private const val COL_ZAKAT_PENGHASILAN = "penghasilan"
        private const val COL_ZAKAT_NOMINAL = "nominal_zakat"

        // Tabel Kurban (UC-03)
        private const val TABLE_KURBAN = "tb_kurban"
        private const val COL_KURBAN_ID = "kurban_id"
        private const val COL_KURBAN_EMAIL = "email_user"
        private const val COL_KURBAN_JENIS = "jenis_hewan" // Misal: "Kambing", "Sapi"
        private const val COL_KURBAN_PRODUK = "tipe_produk" // "Superqurban" / "Penebaran Langsung"
        private const val COL_KURBAN_STATUS = "status" // 1: Pengadaan, 2: Disembelih, 3: Dikirim

        // Tabel Bansos (UC-04)
        private const val TABLE_BANSOS = "tb_bansos"
        private const val COL_BANSOS_ID = "bansos_id"
        private const val COL_BANSOS_EMAIL = "email_user"
        private const val COL_BANSOS_NIK = "nik"
        private const val COL_BANSOS_PENDAPATAN = "pendapatan"
        private const val COL_BANSOS_STATUS = "status_pengajuan" // "Diterima" / "Ditolak"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TABLE_USER ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAMA TEXT, $COLUMN_EMAIL TEXT, $COLUMN_PASSWORD TEXT)")
        db.execSQL("CREATE TABLE $TABLE_DONASI (donasi_id INTEGER PRIMARY KEY AUTOINCREMENT, $COL_DONASI_EMAIL TEXT, $COL_DONASI_KAMPANYE TEXT, $COL_DONASI_NOMINAL REAL, $COL_DONASI_ANONIM INTEGER)")
        db.execSQL("CREATE TABLE $TABLE_ZAKAT (zakat_id INTEGER PRIMARY KEY AUTOINCREMENT, $COL_ZAKAT_EMAIL TEXT, $COL_ZAKAT_PENGHASILAN REAL, $COL_ZAKAT_NOMINAL REAL)")
        db.execSQL("CREATE TABLE $TABLE_KURBAN ($COL_KURBAN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_KURBAN_EMAIL TEXT, $COL_KURBAN_JENIS TEXT, $COL_KURBAN_PRODUK TEXT, $COL_KURBAN_STATUS INTEGER)")
        db.execSQL("CREATE TABLE $TABLE_BANSOS ($COL_BANSOS_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_BANSOS_EMAIL TEXT, $COL_BANSOS_NIK TEXT, $COL_BANSOS_PENDAPATAN REAL, $COL_BANSOS_STATUS TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DONASI")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ZAKAT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_KURBAN")
        onCreate(db)
    }

    data class RiwayatDonasi(val kampanye: String, val nominal: Double, val isAnonim: Int)

    // -- FUNGSI USER, DONASI, ZAKAT (Sama seperti sebelumnya) --
    fun insertUser(nama: String, email: String, password: String): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues().apply { put(COLUMN_NAMA, nama); put(COLUMN_EMAIL, email); put(COLUMN_PASSWORD, password) }
        return db.insert(TABLE_USER, null, cv) != -1L
    }
    fun checkUser(e: String, p: String): Boolean {
        val cursor = this.readableDatabase.rawQuery("SELECT * FROM $TABLE_USER WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?", arrayOf(e, p))
        val count = cursor.count
        cursor.close()
        return count > 0
    }
    fun getUserName(e: String): String {
        var nama = "Donatur"
        val cursor = this.readableDatabase.rawQuery("SELECT $COLUMN_NAMA FROM $TABLE_USER WHERE $COLUMN_EMAIL = ?", arrayOf(e))
        if (cursor.moveToFirst()) nama = cursor.getString(0)
        cursor.close()
        return nama
    }
    fun insertDonasi(e: String, k: String, n: Double, a: Int): Boolean {
        val cv = ContentValues().apply { put(COL_DONASI_EMAIL, e); put(COL_DONASI_KAMPANYE, k); put(COL_DONASI_NOMINAL, n); put(COL_DONASI_ANONIM, a) }
        return this.writableDatabase.insert(TABLE_DONASI, null, cv) != -1L
    }
    fun getTotalDonasi(e: String): Double {
        var total = 0.0
        val cursor = this.readableDatabase.rawQuery("SELECT SUM($COL_DONASI_NOMINAL) FROM $TABLE_DONASI WHERE $COL_DONASI_EMAIL = ?", arrayOf(e))
        if (cursor.moveToFirst()) total = cursor.getDouble(0)
        cursor.close()
        return total
    }
    fun insertZakat(e: String, p: Double, n: Double): Boolean {
        val cv = ContentValues().apply { put(COL_ZAKAT_EMAIL, e); put(COL_ZAKAT_PENGHASILAN, p); put(COL_ZAKAT_NOMINAL, n) }
        return this.writableDatabase.insert(TABLE_ZAKAT, null, cv) != -1L
    }

    // --- FUNGSI BARU UNTUK UC-03 (KURBAN) ---
    // Fungsi simulasi: Membuat data kurban otomatis saat user mengecek menu
    fun generateDummyKurban(email: String) {
        val db = this.writableDatabase
        // Cek apakah sudah ada datanya agar tidak ganda
        val cursor = db.rawQuery("SELECT * FROM $TABLE_KURBAN WHERE $COL_KURBAN_EMAIL = ?", arrayOf(email))
        if (cursor.count == 0) {
            val cv1 = ContentValues().apply { put(COL_KURBAN_EMAIL, email); put(COL_KURBAN_JENIS, "1 Ekor Kambing"); put(COL_KURBAN_PRODUK, "Superqurban"); put(COL_KURBAN_STATUS, 2) }
            val cv2 = ContentValues().apply { put(COL_KURBAN_EMAIL, email); put(COL_KURBAN_JENIS, "1/7 Sapi"); put(COL_KURBAN_PRODUK, "Penebaran Langsung"); put(COL_KURBAN_STATUS, 1) }
            db.insert(TABLE_KURBAN, null, cv1)
            db.insert(TABLE_KURBAN, null, cv2)
        }
        cursor.close()
    }

    // --- FUNGSI UC-04 (BANSOS) ---
    fun insertBansos(email: String, nik: String, pendapatan: Double, status: String): Boolean {
        val cv = ContentValues().apply {
            put(COL_BANSOS_EMAIL, email)
            put(COL_BANSOS_NIK, nik)
            put(COL_BANSOS_PENDAPATAN, pendapatan)
            put(COL_BANSOS_STATUS, status)
        }
        return this.writableDatabase.insert(TABLE_BANSOS, null, cv) != -1L
    }

    fun getRiwayatDonasi(email: String): List<RiwayatDonasi> {
        val list = mutableListOf<RiwayatDonasi>()
        val db = this.readableDatabase
        // Mengambil data berdasarkan email dan diurutkan dari yang terbaru (DESC)
        val cursor = db.rawQuery("SELECT kampanye, nominal, is_anonim FROM $TABLE_DONASI WHERE $COL_DONASI_EMAIL = ? ORDER BY donasi_id DESC", arrayOf(email))

        if (cursor.moveToFirst()) {
            do {
                val kampanye = cursor.getString(0)
                val nominal = cursor.getDouble(1)
                val isAnonim = cursor.getInt(2)
                list.add(RiwayatDonasi(kampanye, nominal, isAnonim))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}