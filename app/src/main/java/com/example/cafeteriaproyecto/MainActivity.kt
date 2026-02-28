package com.example.cafeteriaproyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    lateinit var btnGestionarMesas: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnGestionarMesas = findViewById<Button>(R.id.btnGestionarMesas)
        btnGestionarMesas.setOnClickListener {
            var intent = Intent(this, MesasActivity::class.java)
            startActivity(intent)
        }

        }


        }

