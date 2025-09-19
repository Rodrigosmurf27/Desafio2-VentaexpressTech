package com.udb.ventaexpress.controller

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.udb.ventaexpress.model.Producto
import com.udb.ventaexpress.model.Venta
import kotlinx.coroutines.tasks.await

class VentaController(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private fun ventasRef(): DatabaseReference {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("No auth")
        return DbRefs.ventas(uid)
    }

    private fun productosRef(): DatabaseReference {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("No auth")
        return DbRefs.productos(uid)
    }

    fun escucharVentas(onChange: (List<Venta>) -> Unit, onError: (String) -> Unit) {
        ventasRef().orderByChild("fecha").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Venta>()
                snapshot.children.forEach { child ->
                    child.getValue(Venta::class.java)?.let { lista.add(it) }
                }

                onChange(lista.reversed())
            }
            override fun onCancelled(error: DatabaseError) { onError(error.message) }
        })
    }

    suspend fun registrarVenta(venta: Venta) {
        // Verifica stock disponible
        val stockSnapshot = productosRef().get().await()
        val mapActual = stockSnapshot.children.associate { it.key!! to it.getValue(Producto::class.java)!! }

        venta.items.forEach { item ->
            val prod = mapActual[item.productoId] ?: throw IllegalStateException("Producto no existe")
            require(item.cantidad > 0) { "Cantidad inválida" }
            if (prod.stock < item.cantidad) throw IllegalStateException("Stock insuficiente para ${prod.nombre}")
        }


        val key = venta.id ?: ventasRef().push().key!!


        val updates = hashMapOf<String, Any>()
        updates["${ventasRef().path}/$key"] = venta.copy(id = key)

        venta.items.forEach { item ->
            val actual = mapActual[item.productoId]!!
            val nuevoStock = actual.stock - item.cantidad
            updates["${productosRef().path}/${item.productoId}/stock"] = nuevoStock
        }

        FirebaseDatabase.getInstance().reference.updateChildren(updates).await()
    }
}
