package com.example.cafeteriaproyecto

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cafeteriaproyecto.data.AppDatabaseHelper

class ProductosActivity : AppCompatActivity() {

    lateinit var btnAceptar: Button
    lateinit var layoutProductos: LinearLayout
    lateinit var db: AppDatabaseHelper

    var idPedido: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_productos)

        db              = AppDatabaseHelper(this)
        btnAceptar      = findViewById(R.id.btnAceptar)
        layoutProductos = findViewById(R.id.layoutProductos)

        idPedido = intent.getIntExtra("id_pedido", 0)

        cargarProductos()

        btnAceptar.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun cargarProductos() {
        layoutProductos.removeAllViews()
        val productos = db.obtenerProductos()

        productos.forEach { producto ->
            val idProducto = producto["id_producto"] as Int
            val nombre     = producto["nombre"] as String
            val precio     = producto["precio"] as Double
            val categoria  = producto["categoria"] as String

            val fila = LinearLayout(this)
            fila.orientation = LinearLayout.HORIZONTAL
            fila.gravity = android.view.Gravity.CENTER_VERTICAL
            val filaPadding = (10 * resources.displayMetrics.density).toInt()
            fila.setPadding(0, filaPadding, 0, filaPadding)

            //nombre + precio
            val tvProducto = TextView(this)
            tvProducto.text     = "$nombre - S/ ${"%.2f".format(precio)}"
            tvProducto.textSize = 15f
            tvProducto.setTextColor(0xFFFFFFFF.toInt())
            tvProducto.layoutParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )

            val btnAgregar = Button(this)
            btnAgregar.text = "+ Agregar"
            btnAgregar.textSize = 12f
            btnAgregar.setTextColor(0xFFFFFFFF.toInt())
            btnAgregar.setBackgroundColor(0xFF87644b.toInt())

            btnAgregar.setOnClickListener {
                db.agregarDetalle(
                    idPedido      = idPedido,
                    idProducto    = idProducto,
                    cantidad      = 1,
                    precioUnitario = precio
                )
                Toast.makeText(this, "$nombre agregado al pedido", Toast.LENGTH_SHORT).show()
            }

            fila.addView(tvProducto)
            fila.addView(btnAgregar)
            layoutProductos.addView(fila)


            val separador = android.view.View(this)
            separador.setBackgroundColor(0x44FFFFFF)
            separador.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1
            )
            layoutProductos.addView(separador)
        }
    }
}