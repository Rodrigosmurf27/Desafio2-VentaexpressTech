package com.udb.ventaexpress

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.udb.ventaexpress.databinding.ActivityMainBinding
import com.udb.ventaexpress.view.clientes.ClientesActivity
import com.udb.ventaexpress.view.productos.ProductosActivity
import com.udb.ventaexpress.view.ventas.VentasActivity
import com.udb.ventaexpress.view.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Si no hay usuario logueado, volver al Login
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnProductos.setOnClickListener { startActivity(Intent(this, ProductosActivity::class.java)) }
        b.btnClientes.setOnClickListener  { startActivity(Intent(this, ClientesActivity::class.java)) }
        b.btnVentas.setOnClickListener    { startActivity(Intent(this, VentasActivity::class.java)) }
        b.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
