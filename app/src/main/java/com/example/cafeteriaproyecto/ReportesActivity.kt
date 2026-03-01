package com.example.cafeteriaproyecto

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cafeteriaproyecto.data.AppDatabaseHelper

class ReportesActivity : AppCompatActivity() {

    lateinit var tvTotalMes: TextView
    lateinit var tvCantidadPedidos: TextView
    lateinit var lvPedidos: ListView
    lateinit var db: AppDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reportes)

        db                 = AppDatabaseHelper(this)
        tvTotalMes         = findViewById(R.id.tvTotalMes)
        tvCantidadPedidos  = findViewById(R.id.tvCantidadPedidos)
        lvPedidos          = findViewById(R.id.lvPedidos)

        cargarReporte()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun cargarReporte() {
        val pedidos  = db.reporteMensual()
        val total    = db.totalVendidoMes()
        val cantidad = db.cantidadPedidosMes()

        tvTotalMes.text        = "Total vendido: S/ ${"%.2f".format(total)}"
        tvCantidadPedidos.text = "Pedidos pagados: $cantidad"

        if (pedidos.isEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listOf("Sin pedidos registrados"))
            lvPedidos.adapter = adapter
            return
        }

        val items = pedidos.map { pedido ->
            val id     = pedido["id_pedido"] as Int
            val fecha  = pedido["fecha"] as String
            val mesa   = pedido["numero_mesa"] as Int
            val monto  = pedido["total"] as Double
            val metodo = pedido["metodo_pago"] as String
            "Pedido $id  |  Mesa $mesa\nFecha: $fecha  |  S/ ${"%.2f".format(monto)}\nPago: $metodo"
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        lvPedidos.adapter = adapter
    }
}