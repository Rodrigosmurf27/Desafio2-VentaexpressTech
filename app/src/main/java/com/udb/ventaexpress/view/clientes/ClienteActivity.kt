package com.udb.ventaexpress.view.clientes

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.udb.ventaexpress.controller.ClienteController
import com.udb.ventaexpress.databinding.ActivityClientesBinding
import com.udb.ventaexpress.model.Cliente
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ClientesActivity : AppCompatActivity() {
    private lateinit var b: ActivityClientesBinding
    private val ctrl = ClienteController()
    private val scope = MainScope()

    private val adapter = ClienteAdapter(
        onEdit = { c ->
            val i = Intent(this, ClienteFormActivity::class.java).apply {
                putExtra("id", c.id)
                putExtra("nombre", c.nombre)
                putExtra("correo", c.correo)
                putExtra("tel", c.telefono)
            }
            startActivity(i)
        },
        onDelete = { c ->
            scope.launch {
                try {
                    ctrl.eliminar(c.id!!)
                } catch (e: Exception) {
                    toast(e.message)
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityClientesBinding.inflate(layoutInflater)
        setContentView(b.root)


        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Clientes"


        b.rv.layoutManager = LinearLayoutManager(this)
        b.rv.adapter = adapter


        b.fabAdd.setOnClickListener {
            startActivity(Intent(this, ClienteFormActivity::class.java))
        }


        ctrl.escucharClientes(
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
