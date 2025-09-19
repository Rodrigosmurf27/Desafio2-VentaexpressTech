package com.udb.ventaexpress.view.productos

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.udb.ventaexpress.controller.ProductoController
import com.udb.ventaexpress.databinding.ActivityProductosBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ProductosActivity : AppCompatActivity() {
    private lateinit var b: ActivityProductosBinding
    private val ctrl = ProductoController()
    private val scope = MainScope()

    private val adapter = ProductoAdapter(
        onEdit = { p ->
            val i = Intent(this, ProductoFormActivity::class.java).apply {
                putExtra("id", p.id)
                putExtra("nombre", p.nombre)
                putExtra("desc", p.descripcion)
                putExtra("precio", p.precio)
                putExtra("stock", p.stock)
            }
            startActivity(i)
        },
        onDelete = { p ->
            scope.launch {
                try { ctrl.eliminar(p.id!!) }
                catch (e: Exception) { toast(e.message) }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityProductosBinding.inflate(layoutInflater)
        setContentView(b.root)


        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Productos"

        // RecyclerView
        b.rv.layoutManager = LinearLayoutManager(this)
        b.rv.adapter = adapter

        // FAB: ir a formulario
        b.fabAdd.setOnClickListener {
            startActivity(Intent(this, ProductoFormActivity::class.java))
        }


        ctrl.escucharProductos(
            onChange = { adapter.submit(it) },
            onError = { toast(it) }
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { finish(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun toast(msg: String?) =
        Toast.makeText(this, msg ?: "Error", Toast.LENGTH_LONG).show()

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
