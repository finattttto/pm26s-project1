package com.example.registroturistico.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.sistemacontrolefinancas.entity.Localizacao


class DatabaseHandler (context : Context) : SQLiteOpenHelper ( context, DATABASE_NAME, null, DATABASE_VERSION ) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "longitude DOUBLE, latitude DOUBLE, nome TEXT, descricao TEXT, dataAdd TEXT, imageUri TEXT )")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL( "DROP TABLE IF EXISTS $TABLE_NAME" )
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "dbfile.sqlite"
        private const val DATABASE_VERSION = 2
        const val TABLE_NAME = "pontos_turisticos"
    }

    fun insert( localizacao: Localizacao) {
        val db = this.writableDatabase

        val registro = ContentValues()
        registro.put( "nome", localizacao.nome )
        registro.put( "descricao", localizacao.descricao)
        registro.put( "latitude", localizacao.latitude )
        registro.put( "longitude", localizacao.longitude )
        registro.put( "dataAdd", localizacao.dataAdd )
        registro.put( "imageUri", localizacao.imageUri )

        db.insert( TABLE_NAME, null, registro )
    }

    fun listCursor() : Cursor {
        val db = this.readableDatabase

        val registro = db.query( TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        return registro
    }

    fun getListLocalizacoes() : List<Localizacao> {
        val db = this.readableDatabase

        val cursor = db.query( TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        val locations = mutableListOf<Localizacao>()

        while (cursor.moveToNext()) {
            val id = cursor.getIntOrNull(cursor, "_id") ?: -1
            val longitude = cursor.getDoubleOrNull(cursor, "longitude") ?: 0.0
            val latitude = cursor.getDoubleOrNull(cursor, "latitude") ?: 0.0
            val nome = cursor.getStringOrNull(cursor, "nome") ?: "Desconhecido"
            val descricao = cursor.getStringOrNull(cursor, "descricao") ?: "Desconhecido"
            val dataAdd = cursor.getStringOrNull(cursor, "dataAdd") ?: "Data não disponível"
            val imageUri = cursor.getStringOrNull(cursor, "imageUri") ?: ""

            locations.add(Localizacao(id, longitude, latitude, nome, descricao , dataAdd, imageUri))
        }
        locations.forEach { println("Location: " + it.nome) }

        cursor.close() // Sempre fechar o cursor após o uso
        return locations
    }

    fun delete(id: Int) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "_id = ?", arrayOf(id.toString()))
    }



    private fun Cursor.getIntOrNull(cursor: Cursor, columnName: String): Int? {
        return try {
            val index = cursor.getColumnIndexOrThrow(columnName)
            if (!cursor.isNull(index)) cursor.getInt(index) else null
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    private fun Cursor.getDoubleOrNull(cursor: Cursor, columnName: String): Double? {
        return try {
            val index = cursor.getColumnIndexOrThrow(columnName)
            if (!cursor.isNull(index)) cursor.getDouble(index) else null
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    private fun Cursor.getStringOrNull(cursor: Cursor, columnName: String): String? {
        return try {
            val index = cursor.getColumnIndexOrThrow(columnName)
            if (!cursor.isNull(index)) cursor.getString(index) else null
        } catch (e: IllegalArgumentException) {
            null
        }
    }

}