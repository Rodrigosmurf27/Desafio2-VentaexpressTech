package com.udb.ventaexpress.view.ventas

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.udb.ventaexpress.controller.VentaController
import com.udb.ventaexpress.databinding.ActivityVentasBinding

class VentasActivity : AppCompatActivity() {
    private lateinit var b: ActivityVentasBinding
    private val ctrl = VentaController()
    private val adapter = VentaAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityVentasBinding.inflate(layoutInflater)
        setContentView(b.root)


        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Ventas" // o getString(R.string.title_sales)

        // RecyclerView
        b.rv.layoutManager = LinearLayoutManager(this)
        b.rv.adapter = adapter

        // FAB
        b.fabAdd.setOnClickListener {
            startActivity(Intent(this, NuevaVentaActivity::class.java))
        }

        // Escuchar ventas
        ctrl.escucharVentas(
            onChange = { adapter.submit(it) },
            onError = { /* TODO */ }
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
}
