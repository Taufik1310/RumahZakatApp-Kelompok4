package com.example.rumahzakatapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "RumahZakat.db"

        // Tabel User
        private const val TABLE_USER = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAMA = "nama"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"

        // Tabel Donasi (UC-01)
        private const val TABLE_DONASI = "tb_donasi"
        private const val COL_DONASI_ID = "donasi_id"
        private const val COL_DONASI_EMAIL = "email_user"
        private const val COL_DONASI_KAMPANYE = "kampanye"
        private const val COL_DONASI_NOMINAL = "nominal"
        private const val COL_DONASI_ANONIM = "is_anonim"

        // Tabel Zakat (UC-02)
        private const val TABLE_ZAKAT = "tb_zakat"
        private const val COL_ZAKAT_ID = "zakat_id"
        private const val COL_ZAKAT_EMAIL = "email_user"
        private const val COL_ZAKAT_PENGHASILAN = "penghasilan"
        private const val COL_ZAKAT_NOMINAL = "nominal_zakat"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create Tabel User
        val createTableUser = ("CREATE TABLE $TABLE_USER ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_NAMA TEXT,"
                + "$COLUMN_EMAIL TEXT,"
                + "$COLUMN_PASSWORD TEXT)")
        db.execSQL(createTableUser)

        // Create Tabel Donasi
        val createTableDonasi = ("CREATE TABLE $TABLE_DONASI ("
                + "$COL_DONASI_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COL_DONASI_EMAIL TEXT,"
                + "$COL_DONASI_KAMPANYE TEXT,"
                + "$COL_DONASI_NOMINAL REAL,"
                + "$COL_DONASI_ANONIM INTEGER)")
        db.execSQL(createTableDonasi)

        // Create Tabel Zakat
        val createTableZakat = ("CREATE TABLE $TABLE_ZAKAT ("
                + "$COL_ZAKAT_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COL_ZAKAT_EMAIL TEXT,"
                + "$COL_ZAKAT_PENGHASILAN REAL,"
                + "$COL_ZAKAT_NOMINAL REAL)")
        db.execSQL(createTableZakat)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DONASI")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ZAKAT")
        onCreate(db)
    }

    // --- FUNGSI AUTENTIKASI ---
    fun insertUser(nama: String, email: String, password: String): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_NAMA, nama)
        cv.put(COLUMN_EMAIL, email)
        cv.put(COLUMN_PASSWORD, password)
        val result = db.insert(TABLE_USER, null, cv)
        db.close()
        return result != -1L
    }

    fun checkUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USER WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?", arrayOf(email, password))
        val count = cursor.count
        cursor.close()
        return count > 0
    }

    fun getUserName(email: String): String {
        val db = this.readableDatabase
        var nama = "Donatur"
        val cursor = db.rawQuery("SELECT $COLUMN_NAMA FROM $TABLE_USER WHERE $COLUMN_EMAIL = ?", arrayOf(email))
        if (cursor.moveToFirst()) {
            nama = cursor.getString(0)
        }
        cursor.close()
        return nama
    }

    // --- FUNGSI UC-01 DONASI ---
    fun insertDonasi(email: String, kampanye: String, nominal: Double, isAnonim: Int): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_DONASI_EMAIL, email)
        cv.put(COL_DONASI_KAMPANYE, kampanye)
        cv.put(COL_DONASI_NOMINAL, nominal)
        cv.put(COL_DONASI_ANONIM, isAnonim)
        val result = db.insert(TABLE_DONASI, null, cv)
        db.close()
        return result != -1L
    }

    fun getTotalDonasi(email: String): Double {
        val db = this.readableDatabase
        var total = 0.0
        val cursor = db.rawQuery("SELECT SUM($COL_DONASI_NOMINAL) FROM $TABLE_DONASI WHERE $COL_DONASI_EMAIL = ?", arrayOf(email))
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        return total
    }

    // --- FUNGSI UC-02 ZAKAT ---
    fun insertZakat(email: String, penghasilan: Double, nominalZakat: Double): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_ZAKAT_EMAIL, email)
        cv.put(COL_ZAKAT_PENGHASILAN, penghasilan)
        cv.put(COL_ZAKAT_NOMINAL, nominalZakat)
        val result = db.insert(TABLE_ZAKAT, null, cv)
        db.close()
        return result != -1L
    }
}