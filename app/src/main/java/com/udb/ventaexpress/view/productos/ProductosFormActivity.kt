package com.udb.ventaexpress.view.productos

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udb.ventaexpress.controller.ProductoController
import com.udb.ventaexpress.databinding.ActivityProductoFormBinding
import com.udb.ventaexpress.model.Producto
import kotlinx.coroutines.*

class ProductoFormActivity : AppCompatActivity() {
    private lateinit var b: ActivityProductoFormBinding
    private val ctrl = ProductoController()
    private val scope = MainScope()
    private var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityProductoFormBinding.inflate(layoutInflater)
        setContentView(b.root)

        id = intent.getStringExtra("id")
        b.etNombre.setText(intent.getStringExtra("nombre") ?: "")
        b.etDesc.setText(intent.getStringExtra("desc") ?: "")
        b.etPrecio.setText((intent.getDoubleExtra("precio", 0.0)).toString())
        b.etStock.setText((intent.getIntExtra("stock", 0)).toString())

        b.btnGuardar.setOnClickListener {
            val nombre = b.etNombre.text.toString().trim()
            val desc = b.etDesc.text.toString().trim()
            val precio = b.etPrecio.text.toString().toDoubleOrNull() ?: 0.0
            val stock = b.etStock.text.toString().toIntOrNull() ?: 0
            if (nombre.isEmpty()) { toast("Nombre requerido"); return@setOnClickListener }
            val p = Producto(id, nombre, desc, precio, stock)
            scope.launch {
                try { ctrl.guardar(p); finish() }
                catch (e: Exception) { toast(e.message) }
            }
        }
    }
    private fun toast(m:String?)= Toast.makeText(this, m ?: "Error", Toast.LENGTH_LONG).show()
    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}
