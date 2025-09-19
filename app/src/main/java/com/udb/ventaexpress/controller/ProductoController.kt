package com.udb.ventaexpress.controller

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.udb.ventaexpress.model.Producto
import kotlinx.coroutines.tasks.await

class ProductoController(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private fun ref(): DatabaseReference {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("No auth")
        return DbRefs.productos(uid)
    }

    fun escucharProductos(onChange: (List<Producto>) -> Unit, onError: (String) -> Unit) {
        ref().addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Producto>()
                snapshot.children.forEach { child ->
                    child.getValue(Producto::class.java)?.let { lista.add(it) }
                }
                onChange(lista)
            }
            override fun onCancelled(error: DatabaseError) { onError(error.message) }
        })
    }

    suspend fun guardar(producto: Producto) {
        val key = producto.id ?: ref().push().key!!
        ref().child(key).setValue(producto.copy(id = key)).await()
    }

    suspend fun eliminar(id: String) {
        ref().child(id).removeValue().await()
    }
}
