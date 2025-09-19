package com.udb.ventaexpress.view.clientes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udb.ventaexpress.controller.ClienteController
import com.udb.ventaexpress.databinding.ActivityClienteFormBinding
import com.udb.ventaexpress.model.Cliente
import kotlinx.coroutines.*

class ClienteFormActivity : AppCompatActivity() {
    private lateinit var b: ActivityClienteFormBinding
    private val ctrl = ClienteController()
    private val scope = MainScope()
    private var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityClienteFormBinding.inflate(layoutInflater)
        setContentView(b.root)

        id = intent.getStringExtra("id")
        b.etNombre.setText(intent.getStringExtra("nombre") ?: "")
        b.etCorreo.setText(intent.getStringExtra("correo") ?: "")
        b.etTel.setText(intent.getStringExtra("tel") ?: "")

        b.btnGuardar.setOnClickListener {
            val nombre = b.etNombre.text.toString().trim()
            val correo = b.etCorreo.text.toString().trim()
            val tel = b.etTel.text.toString().trim()
            if (nombre.isEmpty() || correo.isEmpty() || tel.isEmpty()) {
                toast("Completa todos los campos"); return@setOnClickListener
            }
            val c = Cliente(id, nombre, correo, tel)
            scope.launch {
                try { ctrl.guardar(c); finish() }
                catch (e: Exception) { toast(e.message) }
            }
        }
    }

    private fun toast(m:String?)= Toast.makeText(this, m ?: "Error", Toast.LENGTH_LONG).show()
    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}
