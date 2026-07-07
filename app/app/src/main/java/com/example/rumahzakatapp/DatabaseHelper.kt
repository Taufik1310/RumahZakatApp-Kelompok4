package com.example.rumahzakatapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "RumahZakat.db"
        private const val TABLE_USER = "users"

        // Nama-nama Kolom
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAMA = "nama"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Query untuk membuat tabel users
        val createTableUser = ("CREATE TABLE $TABLE_USER ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_NAMA TEXT,"
                + "$COLUMN_EMAIL TEXT,"
                + "$COLUMN_PASSWORD TEXT)")
        db.execSQL(createTableUser)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    // Fungsi untuk Mendaftarkan User Baru (Register)
    fun insertUser(nama: String, email: String, password: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_NAMA, nama)
        contentValues.put(COLUMN_EMAIL, email)
        contentValues.put(COLUMN_PASSWORD, password)

        val result = db.insert(TABLE_USER, null, contentValues)
        db.close()
        return result != -1L // Mengembalikan true jika berhasil, false jika gagal
    }

    // Fungsi untuk Mengecek Login (Validasi Email & Password)
    fun checkUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USER WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?", arrayOf(email, password))
        val count = cursor.count
        cursor.close()
        db.close()
        return count > 0 // Mengembalikan true jika ada data yang cocok
    }
}