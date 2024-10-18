package com.example.sistemacontrolefinancas.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.sistemacontrolefinancas.entity.Localizacao


class DatabaseHandler (context : Context) : SQLiteOpenHelper ( context, DATABASE_NAME, null, DATABASE_VERSION ) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL( "CREATE TABLE IF NOT EXISTS ${TABLE_NAME} ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " longitude DOUBLE, latitude DOUBLE, nome TEXT, dataAdd TEXT )")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL( "DROP TABLE IF EXISTS ${TABLE_NAME}" )
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "dbfile.sqlite"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "pontos_turisticos"
        public const val ID = 0
        public const val longitude = 1
        public const val latitude = 2
        public const val nome = 3
    }

    fun insert( localizacao: Localizacao) {
        val db = this.writableDatabase

        val registro = ContentValues()
        registro.put( "nome", localizacao.nome )
        registro.put( "latitude", localizacao.latitude )
        registro.put( "longitude", localizacao.longitude )
        registro.put( "dataAdd", localizacao.dataAdd )

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



}