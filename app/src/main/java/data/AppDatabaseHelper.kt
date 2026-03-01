package com.example.cafeteriaproyecto.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "cafeteria.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        // Tabla usuario
        db.execSQL("""
            CREATE TABLE usuario (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                nombre TEXT,
                correo TEXT,
                contrasena TEXT,
                rol TEXT DEFAULT 'mesero'
            )
        """.trimIndent())

        // Tabla mesa
        db.execSQL("""
            CREATE TABLE mesa (
                id_mesa INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                numero INTEGER,
                estado TEXT DEFAULT 'libre',
                id_pedido_actual INTEGER
            )
        """.trimIndent())

        // Tabla producto
        db.execSQL("""
            CREATE TABLE producto (
                id_producto INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                nombre TEXT,
                precio REAL,
                categoria TEXT,
                disponible INTEGER DEFAULT 1
            )
        """.trimIndent())

        // Tabla pedido
        db.execSQL("""
            CREATE TABLE pedido (
                id_pedido INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                id_mesa INTEGER,
                id_usuario INTEGER,
                fecha TEXT,
                metodo_pago TEXT DEFAULT '',
                total REAL DEFAULT 0.0,
                estado TEXT DEFAULT 'activo',
                FOREIGN KEY (id_mesa) REFERENCES mesa (id_mesa),
                FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
            )
        """.trimIndent())

        // Tabla detalle pedido
        db.execSQL("""
            CREATE TABLE detalle_pedido (
                id_detalle INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                id_pedido INTEGER,
                id_producto INTEGER,
                cantidad INTEGER,
                precio_unitario REAL,
                subtotal REAL,
                FOREIGN KEY (id_pedido) REFERENCES pedido (id_pedido),
                FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
            )
        """.trimIndent())
        insertarDatosPrueba(db)
    }

    /**
     * Se utiliza cuando se cambia la versión de la base de datos,
     * para reiniciar todos los datos y la estructura
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS detalle_pedido")
        db.execSQL("DROP TABLE IF EXISTS pedido")
        db.execSQL("DROP TABLE IF EXISTS producto")
        db.execSQL("DROP TABLE IF EXISTS mesa")
        db.execSQL("DROP TABLE IF EXISTS usuario")
        onCreate(db)

        // En entornos de producción
//        if (oldVersion < 2) {
//            db.execSQL("ALTER TABLE usuario ADD COLUMN telefono TEXT")
//        }
    }
    //Datos de prueba
    private fun insertarDatosPrueba(db: SQLiteDatabase) {

        // Usuario
        listOf(
            Pair("Luis Alfonso",  "luis@caf.com"),
            Pair("Martin", "mart@caf.com")
        ).forEach { (nombre, correo) ->
            db.insert("usuario", null, ContentValues().apply {
                put("nombre",     nombre)
                put("correo",     correo)
                put("contrasena", "1234")
                put("rol",        "mesero")
            })
        }

        // Mesas
        for (i in 1..6) {
            db.insert("mesa", null, ContentValues().apply {
                put("numero", i)
                put("estado", "libre")
            })
        }

        // Productos
        listOf(
            Triple("Cafe Americano",     25.0,  "Bebidas"),
            Triple("Cappuccino",         35.0,  "Bebidas"),
            Triple("Latte",              38.0,  "Bebidas"),
            Triple("Te Verde",           22.0,  "Bebidas"),
            Triple("Croissant",          28.0,  "Repostería"),
            Triple("Muffin de Arandano", 30.0,  "Repostería"),
            Triple("Sandwich Club",      55.0,  "Comida"),
            Triple("Ensalada Cesar",     65.0,  "Comida"),
            Triple("Agua Natural 500ml", 15.0,  "Bebidas"),
            Triple("Jugo de Naranja",    32.0,  "Bebidas")
        ).forEach { (nombre, precio, categoria) ->
            db.insert("producto", null, ContentValues().apply {
                put("nombre",     nombre)
                put("precio",     precio)
                put("categoria",  categoria)
                put("disponible", 1)
            })
        }
    }
    //funcionalidades
    // Usuario

    fun login(correo: String, contrasena: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuario WHERE correo = ? AND contrasena = ?",
            arrayOf(correo, contrasena)
        )
        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }

    // Mesas

    fun obtenerMesas(): List<Map<String, Any>> {
        val lista = mutableListOf<Map<String, Any>>()
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM mesa ORDER BY numero", null
        )
        while (cursor.moveToNext()) {
            lista.add(mapOf(
                "id_mesa"          to cursor.getInt(cursor.getColumnIndexOrThrow("id_mesa")),
                "numero"           to cursor.getInt(cursor.getColumnIndexOrThrow("numero")),
                "estado"           to cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                "id_pedido_actual" to cursor.getInt(cursor.getColumnIndexOrThrow("id_pedido_actual"))
            ))
        }
        cursor.close()
        return lista
    }

    fun actualizarMesa(idMesa: Int, estado: String, idPedido: Int? = null) {
        writableDatabase.update("mesa", ContentValues().apply {
            put("estado", estado)
            if (idPedido != null) put("id_pedido_actual", idPedido)
            else putNull("id_pedido_actual")
        }, "id_mesa = ?", arrayOf(idMesa.toString()))
    }

    // Productos
    fun obtenerProductos(): List<Map<String, Any>> {
        val lista = mutableListOf<Map<String, Any>>()
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM producto WHERE disponible = 1 ORDER BY categoria", null
        )
        while (cursor.moveToNext()) {
            lista.add(mapOf(
                "id_producto" to cursor.getInt(cursor.getColumnIndexOrThrow("id_producto")),
                "nombre"      to cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                "precio"      to cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                "categoria"   to cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
            ))
        }
        cursor.close()
        return lista
    }


    // Pedidos
    fun crearPedido(idMesa: Int, idUsuario: Int, fecha: String): Int {
        val id = writableDatabase.insert("pedido", null, ContentValues().apply {
            put("id_mesa",    idMesa)
            put("id_usuario", idUsuario)
            put("fecha",      fecha)
            put("estado",     "activo")
        })
        return id.toInt()
    }

    fun obtenerPedidoActivoPorMesa(idMesa: Int): Map<String, Any>? {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM pedido WHERE id_mesa = ? AND estado = 'activo'",
            arrayOf(idMesa.toString())
        )
        val pedido = if (cursor.moveToFirst()) mapOf(
            "id_pedido"  to cursor.getInt(cursor.getColumnIndexOrThrow("id_pedido")),
            "id_mesa"    to cursor.getInt(cursor.getColumnIndexOrThrow("id_mesa")),
            "id_usuario" to cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
            "fecha"      to cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
            "total"      to cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
        ) else null
        cursor.close()
        return pedido
    }

    fun finalizarPedido(idPedido: Int, metodoPago: String, total: Double) {
        writableDatabase.update("pedido", ContentValues().apply {
            put("estado",      "pagado")
            put("metodo_pago", metodoPago)
            put("total",       total)
        }, "id_pedido = ?", arrayOf(idPedido.toString()))
    }

    // Detalle pedido
    fun agregarDetalle(idPedido: Int, idProducto: Int, cantidad: Int, precioUnitario: Double) {
        writableDatabase.insert("detalle_pedido", null, ContentValues().apply {
            put("id_pedido",      idPedido)
            put("id_producto",    idProducto)
            put("cantidad",       cantidad)
            put("precio_unitario", precioUnitario)
            put("subtotal",       cantidad * precioUnitario)
        })
    }

    fun obtenerDetallePedido(idPedido: Int): List<Map<String, Any>> {
        val lista = mutableListOf<Map<String, Any>>()
        val cursor = readableDatabase.rawQuery("""
            SELECT dp.*, p.nombre AS producto_nombre
            FROM detalle_pedido dp
            INNER JOIN producto p ON dp.id_producto = p.id_producto
            WHERE dp.id_pedido = ?
        """.trimIndent(), arrayOf(idPedido.toString()))

        while (cursor.moveToNext()) {
            lista.add(mapOf(
                "id_detalle"      to cursor.getInt(cursor.getColumnIndexOrThrow("id_detalle")),
                "producto_nombre" to cursor.getString(cursor.getColumnIndexOrThrow("producto_nombre")),
                "cantidad"        to cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                "precio_unitario" to cursor.getDouble(cursor.getColumnIndexOrThrow("precio_unitario")),
                "subtotal"        to cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal"))
            ))
        }
        cursor.close()
        return lista
    }


    //Reportes

    fun reporteMensual(): List<Map<String, Any>> {
        val lista = mutableListOf<Map<String, Any>>()
        val cursor = readableDatabase.rawQuery("""
        SELECT p.id_pedido, p.fecha, p.total, p.metodo_pago, m.numero AS numero_mesa
        FROM pedido p
        INNER JOIN mesa m ON p.id_mesa = m.id_mesa
        WHERE p.estado = 'pagado'
        ORDER BY p.fecha DESC
    """.trimIndent(), null)

        while (cursor.moveToNext()) {
            lista.add(mapOf(
                "id_pedido"    to cursor.getInt(cursor.getColumnIndexOrThrow("id_pedido")),
                "fecha"        to cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                "total"        to cursor.getDouble(cursor.getColumnIndexOrThrow("total")),
                "metodo_pago"  to cursor.getString(cursor.getColumnIndexOrThrow("metodo_pago")),
                "numero_mesa"  to cursor.getInt(cursor.getColumnIndexOrThrow("numero_mesa"))
            ))
        }
        cursor.close()
        return lista
    }

    fun totalVendidoMes(): Double {
        val cursor = readableDatabase.rawQuery("""
        SELECT SUM(total) FROM pedido WHERE estado = 'pagado'
    """.trimIndent(), null)
        val total = if (cursor.moveToFirst()) cursor.getDouble(0) else 0.0
        cursor.close()
        return total
    }

    fun cantidadPedidosMes(): Int {
        val cursor = readableDatabase.rawQuery("""
        SELECT COUNT(*) FROM pedido WHERE estado = 'pagado'
    """.trimIndent(), null)
        val cantidad = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return cantidad
    }
}