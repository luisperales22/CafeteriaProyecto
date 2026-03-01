package com.example.cafeteriaproyecto

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cafeteriaproyecto.data.AppDatabaseHelper

class PedidoActivity : AppCompatActivity() {

    lateinit var btnAgregar: Button
    lateinit var btnFinalizar: Button
    lateinit var tvTotal: TextView
    lateinit var lvDetallePedido: ListView
    lateinit var db: AppDatabaseHelper

    var idPedido: Int = 0
    var idMesa: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pedido)

        db             = AppDatabaseHelper(this)
        btnAgregar     = findViewById(R.id.btnAgregar)
        btnFinalizar   = findViewById(R.id.btnFinalizar)
        tvTotal        = findViewById(R.id.tvTotal)
        lvDetallePedido = findViewById(R.id.lvDetallePedido)

        // recibe datos de kt mesas
        idPedido = intent.getIntExtra("id_pedido", 0)
        idMesa   = intent.getIntExtra("id_mesa", 0)

        btnAgregar.setOnClickListener {
            val intent = Intent(this, ProductosActivity::class.java)
            intent.putExtra("id_pedido", idPedido)
            startActivity(intent)
        }

        btnFinalizar.setOnClickListener {
            if (lvDetallePedido.adapter == null || lvDetallePedido.adapter.count == 0) {
                Toast.makeText(this, "El pedido está vacio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, PagoActivity::class.java)
            intent.putExtra("id_pedido", idPedido)
            intent.putExtra("id_mesa", idMesa)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDetalle()
    }

    private fun cargarDetalle() {
        val detalles = db.obtenerDetallePedido(idPedido)

        if (detalles.isEmpty()) {
            tvTotal.text = "Total: S/ 0.00"
            lvDetallePedido.adapter = null
            return
        }

        // pedido detalle
        val items = detalles.map { detalle ->
            val nombre   = detalle["producto_nombre"] as String
            val cantidad = detalle["cantidad"] as Int
            val subtotal = detalle["subtotal"] as Double
            "$cantidad x $nombre  |  S/ ${"%.2f".format(subtotal)}"
        }

        // total
        val total = detalles.sumOf { it["subtotal"] as Double }
        tvTotal.text = "Total: S/ ${"%.2f".format(total)}"

        // muestra de list con adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        lvDetallePedido.adapter = adapter
    }
}