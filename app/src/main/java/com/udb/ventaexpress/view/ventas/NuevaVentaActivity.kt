package com.udb.ventaexpress.view.ventas

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.udb.ventaexpress.controller.ClienteController
import com.udb.ventaexpress.controller.DbRefs
import com.udb.ventaexpress.controller.VentaController
import com.udb.ventaexpress.databinding.ActivityNuevaVentaBinding
import com.udb.ventaexpress.model.*
import kotlinx.coroutines.*
import android.view.View
import android.widget.AdapterView
import com.udb.ventaexpress.ext.setOnItemSelectedListenerCompat


class NuevaVentaActivity : AppCompatActivity() {
    private lateinit var b: ActivityNuevaVentaBinding
    private val ventasCtrl = VentaController()
    private val clienteCtrl = ClienteController()
    private val scope = MainScope()

    private val productos = mutableListOf<Producto>()
    private val adapter = SeleccionProductoAdapter(
        onQtyChanged = { updateTotal() }
    )

    private var clientes = listOf<Cliente>()
    private var clienteSeleccionado: Cliente? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityNuevaVentaBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Productos
        b.rvProductos.layoutManager = LinearLayoutManager(this)
        b.rvProductos.adapter = adapter
        escucharProductos()

        // Clientes
        clienteCtrl.escucharClientes(
            onChange = {
                clientes = it
                val nombres = it.map { c -> c.nombre }
                b.spCliente.adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    nombres
                )

                b.spCliente.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        clienteSeleccionado =
                            if (position in clientes.indices) clientes[position] else null
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        clienteSeleccionado = null
                    }
                }
            },
            onError = { toast(it) }
        )


        b.btnRegistrar.setOnClickListener {
            val itemsSel = adapter.seleccionados()
            if (clienteSeleccionado == null) { toast("Selecciona un cliente"); return@setOnClickListener }
            if (itemsSel.isEmpty()) { toast("Selecciona al menos un producto"); return@setOnClickListener }

            val total = itemsSel.sumOf { it.subtotal }
            val venta = Venta(
                id = null,
                clienteId = clienteSeleccionado!!.id ?: "",
                clienteNombre = clienteSeleccionado!!.nombre,
                items = itemsSel,
                total = total,
                fecha = System.currentTimeMillis()
            )

            scope.launch {
                try {
                    ventasCtrl.registrarVenta(venta)
                    toast("Venta registrada por $${"%.2f".format(total)}")
                    finish()
                } catch (e: Exception) {
                    toast("Error: ${e.message}")
                }
            }
        }
    }

    private fun escucharProductos() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        DbRefs.productos(uid).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                productos.clear()
                snapshot.children.forEach { it.getValue(Producto::class.java)?.let(productos::add) }
                adapter.submit(productos)
                updateTotal()
            }
            override fun onCancelled(error: DatabaseError) { toast(error.message) }
        })
    }

    private fun updateTotal() {
        val total = adapter.seleccionados().sumOf { it.subtotal }
        b.tvTotal.text = "$ ${"%.2f".format(total)}"
    }

    private fun toast(m:String) = Toast.makeText(this, m, Toast.LENGTH_LONG).show()
    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}
