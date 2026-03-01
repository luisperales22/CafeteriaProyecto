package com.example.cafeteriaproyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cafeteriaproyecto.data.AppDatabaseHelper

class PagoActivity : AppCompatActivity() {

    lateinit var tvResumen: TextView
    lateinit var tvTotal: TextView
    lateinit var btnPagar: Button
    lateinit var radioGroup: RadioGroup
    lateinit var db: AppDatabaseHelper

    var idPedido: Int = 0
    var idMesa: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago)

        db        = AppDatabaseHelper(this)
        tvResumen = findViewById(R.id.tvResumen)
        tvTotal   = findViewById(R.id.tvTotal)
        btnPagar  = findViewById(R.id.btnPagar)
        radioGroup = findViewById(R.id.radioGroup)

        // Aca se recibe datos del kt de pedido
        idPedido = intent.getIntExtra("id_pedido", 0)
        idMesa   = intent.getIntExtra("id_mesa", 0)

        cargarResumen()

        btnPagar.setOnClickListener {
            val radioSeleccionado = radioGroup.checkedRadioButtonId
            if (radioSeleccionado == -1) {
                Toast.makeText(this, "Selecciona un metodo de pago", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val metodoPago = findViewById<RadioButton>(radioSeleccionado).text.toString()

            // calcula el total aut
            val detalles = db.obtenerDetallePedido(idPedido)
            val total    = detalles.sumOf { it["subtotal"] as Double }

            // finaliza pedido   y libera mesa
            db.finalizarPedido(idPedido = idPedido, metodoPago = metodoPago, total = total)
            db.actualizarMesa(idMesa = idMesa, estado = "libre")
            Toast.makeText(this, "Pago registrado correctamente", Toast.LENGTH_SHORT).show()

            // regresa a mesa
            val intent = Intent(this, MesasActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun cargarResumen() {
        val detalles = db.obtenerDetallePedido(idPedido)

        if (detalles.isEmpty()) {
            tvResumen.text = "Sin productos"
            tvTotal.text   = "Total: S/0.00"
            return
        }

        // resumen
        val resumen = detalles.joinToString("\n") { detalle ->
            val nombre   = detalle["producto_nombre"] as String
            val cantidad = detalle["cantidad"] as Int
            val subtotal = detalle["subtotal"] as Double
            "$cantidad x $nombre    S/ ${"%.2f".format(subtotal)}"
        }

        val total = detalles.sumOf { it["subtotal"] as Double }

        tvResumen.text = resumen
        tvTotal.text   = "Total: S/ ${"%.2f".format(total)}"
    }
}