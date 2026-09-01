package com.banco.digital.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.banco.digital.data.model.EstadoKYC
import com.banco.digital.data.model.UsuarioRegistro

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "banco_digital.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USUARIOS = "usuarios_registro"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE = "nombre_completo"
        const val COLUMN_TIPO_DOC = "tipo_documento"
        const val COLUMN_NUM_DOC = "numero_documento"
        const val COLUMN_FECHA_NAC = "fecha_nacimiento"
        const val COLUMN_CORREO = "correo"
        const val COLUMN_CELULAR = "celular"
        const val COLUMN_PASSWORD_HASH = "password_hash"
        const val COLUMN_FOTO_DNI_FRONT = "foto_dni_frontal_uri"
        const val COLUMN_FOTO_DNI_REV = "foto_dni_reverso_uri"
        const val COLUMN_FOTO_SELFIE = "foto_selfie_uri"
        const val COLUMN_ESTADO_KYC = "estado_verificacion"
        const val COLUMN_FECHA_KYC = "fecha_verificacion"
        const val COLUMN_NUM_CUENTA = "numero_cuenta"
        const val COLUMN_FECHA_CREACION = "fecha_creacion_cuenta"
        const val COLUMN_SALDO = "saldo_inicial"
        const val COLUMN_ACEPTO_TERMS = "acepto_terminos"
        const val COLUMN_ACEPTO_DATOS = "acepto_tratamiento_datos"

        @Volatile
        private var INSTANCE: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_USUARIOS (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_TIPO_DOC TEXT NOT NULL,
                $COLUMN_NUM_DOC TEXT NOT NULL UNIQUE,
                $COLUMN_FECHA_NAC TEXT NOT NULL,
                $COLUMN_CORREO TEXT NOT NULL UNIQUE,
                $COLUMN_CELULAR TEXT NOT NULL,
                $COLUMN_PASSWORD_HASH TEXT NOT NULL,
                $COLUMN_FOTO_DNI_FRONT TEXT,
                $COLUMN_FOTO_DNI_REV TEXT,
                $COLUMN_FOTO_SELFIE TEXT,
                $COLUMN_ESTADO_KYC TEXT DEFAULT 'VERIFICADO',
                $COLUMN_FECHA_KYC INTEGER,
                $COLUMN_NUM_CUENTA TEXT NOT NULL UNIQUE,
                $COLUMN_FECHA_CREACION INTEGER NOT NULL,
                $COLUMN_SALDO REAL DEFAULT 0.0,
                $COLUMN_ACEPTO_TERMS INTEGER DEFAULT 1,
                $COLUMN_ACEPTO_DATOS INTEGER DEFAULT 1
            );
        """.trimIndent()
        db.execSQL(createTableQuery)
        Log.d("DatabaseHelper", "Tabla $TABLE_USUARIOS creada exitosamente en SQLite.")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }

    fun guardarUsuario(usuario: UsuarioRegistro): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, usuario.id)
            put(COLUMN_NOMBRE, usuario.nombreCompleto)
            put(COLUMN_TIPO_DOC, usuario.tipoDocumento)
            put(COLUMN_NUM_DOC, usuario.numeroDocumento)
            put(COLUMN_FECHA_NAC, usuario.fechaNacimiento)
            put(COLUMN_CORREO, usuario.correo)
            put(COLUMN_CELULAR, usuario.celular)
            put(COLUMN_PASSWORD_HASH, usuario.passwordHash)
            put(COLUMN_FOTO_DNI_FRONT, usuario.fotoDniFrontalUri)
            put(COLUMN_FOTO_DNI_REV, usuario.fotoDniReversoUri)
            put(COLUMN_FOTO_SELFIE, usuario.fotoSelfieUri)
            put(COLUMN_ESTADO_KYC, usuario.estadoVerificacion.name)
            put(COLUMN_FECHA_KYC, usuario.fechaVerificacion ?: System.currentTimeMillis())
            put(COLUMN_NUM_CUENTA, usuario.numeroCuenta)
            put(COLUMN_FECHA_CREACION, usuario.fechaCreacionCuenta)
            put(COLUMN_SALDO, usuario.saldoInicial)
            put(COLUMN_ACEPTO_TERMS, if (usuario.aceptoTerminos) 1 else 0)
            put(COLUMN_ACEPTO_DATOS, if (usuario.aceptoTratamientoDatos) 1 else 0)
        }

        val result = db.insertWithOnConflict(TABLE_USUARIOS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        Log.d("DatabaseHelper", "Usuario guardado en base de datos local SQLite: ${usuario.nombreCompleto}, filas: $result")
        return result != -1L
    }

    fun obtenerUsuarioPorDocumento(numDoc: String): UsuarioRegistro? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USUARIOS,
            null,
            "$COLUMN_NUM_DOC = ?",
            arrayOf(numDoc),
            null,
            null,
            null
        )

        return cursor.use {
            if (it.moveToFirst()) {
                UsuarioRegistro(
                    id = it.getString(it.getColumnIndexOrThrow(COLUMN_ID)),
                    nombreCompleto = it.getString(it.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                    tipoDocumento = it.getString(it.getColumnIndexOrThrow(COLUMN_TIPO_DOC)),
                    numeroDocumento = it.getString(it.getColumnIndexOrThrow(COLUMN_NUM_DOC)),
                    fechaNacimiento = it.getString(it.getColumnIndexOrThrow(COLUMN_FECHA_NAC)),
                    correo = it.getString(it.getColumnIndexOrThrow(COLUMN_CORREO)),
                    celular = it.getString(it.getColumnIndexOrThrow(COLUMN_CELULAR)),
                    passwordHash = it.getString(it.getColumnIndexOrThrow(COLUMN_PASSWORD_HASH)),
                    fotoDniFrontalUri = it.getString(it.getColumnIndexOrThrow(COLUMN_FOTO_DNI_FRONT)) ?: "",
                    fotoDniReversoUri = it.getString(it.getColumnIndexOrThrow(COLUMN_FOTO_DNI_REV)) ?: "",
                    fotoSelfieUri = it.getString(it.getColumnIndexOrThrow(COLUMN_FOTO_SELFIE)) ?: "",
                    estadoVerificacion = try {
                        EstadoKYC.valueOf(it.getString(it.getColumnIndexOrThrow(COLUMN_ESTADO_KYC)))
                    } catch (e: Exception) {
                        EstadoKYC.VERIFICADO
                    },
                    fechaVerificacion = it.getLong(it.getColumnIndexOrThrow(COLUMN_FECHA_KYC)),
                    numeroCuenta = it.getString(it.getColumnIndexOrThrow(COLUMN_NUM_CUENTA)),
                    fechaCreacionCuenta = it.getLong(it.getColumnIndexOrThrow(COLUMN_FECHA_CREACION)),
                    saldoInicial = it.getDouble(it.getColumnIndexOrThrow(COLUMN_SALDO)),
                    aceptoTerminos = it.getInt(it.getColumnIndexOrThrow(COLUMN_ACEPTO_TERMS)) == 1,
                    aceptoTratamientoDatos = it.getInt(it.getColumnIndexOrThrow(COLUMN_ACEPTO_DATOS)) == 1
                )
            } else null
        }
    }

    fun obtenerTodosLosUsuarios(): List<UsuarioRegistro> {
        val lista = mutableListOf<UsuarioRegistro>()
        val db = readableDatabase
        val cursor = db.query(TABLE_USUARIOS, null, null, null, null, null, "$COLUMN_FECHA_CREACION DESC")

        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    UsuarioRegistro(
                        id = it.getString(it.getColumnIndexOrThrow(COLUMN_ID)),
                        nombreCompleto = it.getString(it.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                        tipoDocumento = it.getString(it.getColumnIndexOrThrow(COLUMN_TIPO_DOC)),
                        numeroDocumento = it.getString(it.getColumnIndexOrThrow(COLUMN_NUM_DOC)),
                        fechaNacimiento = it.getString(it.getColumnIndexOrThrow(COLUMN_FECHA_NAC)),
                        correo = it.getString(it.getColumnIndexOrThrow(COLUMN_CORREO)),
                        celular = it.getString(it.getColumnIndexOrThrow(COLUMN_CELULAR)),
                        passwordHash = it.getString(it.getColumnIndexOrThrow(COLUMN_PASSWORD_HASH)),
                        numeroCuenta = it.getString(it.getColumnIndexOrThrow(COLUMN_NUM_CUENTA)),
                        fechaCreacionCuenta = it.getLong(it.getColumnIndexOrThrow(COLUMN_FECHA_CREACION)),
                        saldoInicial = it.getDouble(it.getColumnIndexOrThrow(COLUMN_SALDO))
                    )
                )
            }
        }
        return lista
    }
}
