package com.udb.ventaexpress.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udb.ventaexpress.controller.AuthController
import com.udb.ventaexpress.databinding.ActivityMenuBinding
import com.udb.ventaexpress.view.clientes.ClientesActivity
import com.udb.ventaexpress.view.productos.ProductosActivity
import com.udb.ventaexpress.view.ventas.VentasActivity

class MenuActivity : AppCompatActivity() {
    private lateinit var b: ActivityMenuBinding
    private val auth = AuthController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (auth.currentUser() == null) {
            goLoginClearTask()
            return
        }

        b = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.cardProductos.setOnClickListener { startActivity(Intent(this, ProductosActivity::class.java)) }
        b.cardClientes.setOnClickListener  { startActivity(Intent(this, ClientesActivity::class.java)) }
        b.cardVentas.setOnClickListener    { startActivity(Intent(this, VentasActivity::class.java)) }
        b.btnLogout.setOnClickListener {
            auth.logout()
            goLoginClearTask()
        }
    }

    private fun goLoginClearTask() {
        val i = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(i)
    }
}
