package com.example.cafeteriaproyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PagoActivity : AppCompatActivity() {

    lateinit var tvResumen : TextView
    lateinit var tvTotal : TextView

    lateinit var btnPagar : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago)

        tvResumen = findViewById<TextView>(R.id.tvResumen)
        tvTotal = findViewById<TextView>(R.id.tvTotal)
        btnPagar = findViewById<Button>(R.id.btnPagar)

        //recibir los datos de pedidoActivity

        var intent : Intent =  intent
        var resumenPedido : String? = intent.getStringExtra("resumen")
        var totalPedido : Double = intent.getDoubleExtra("total", 0.0)

        tvResumen.text = resumenPedido
        tvTotal.text= "Total: S/ ${totalPedido}"
        btnPagar.setOnClickListener {
            Toast.makeText(this, "Pago registrado", Toast.LENGTH_LONG).show()
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}