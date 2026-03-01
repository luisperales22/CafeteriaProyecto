package com.example.cafeteriaproyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cafeteriaproyecto.data.AppDatabaseHelper

class LoginActivity : AppCompatActivity() {

    lateinit var btnIngresar: Button
    lateinit var etUsuario: EditText
    lateinit var etPassword: EditText
    lateinit var db: AppDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        btnIngresar  = findViewById(R.id.btnIngresar)
        etUsuario     = findViewById(R.id.etUsuario)
        etPassword = findViewById(R.id.etPassword)
        db           = AppDatabaseHelper(this)

        btnIngresar.setOnClickListener {
            val correo     = etUsuario.text.toString().trim()
            val contrasena = etPassword.text.toString().trim()

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuarioValido = db.login(correo, contrasena)

            if (usuarioValido) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Correo o contraseña incorrecta", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}