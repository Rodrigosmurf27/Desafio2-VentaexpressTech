package com.udb.ventaexpress.controller

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.udb.ventaexpress.model.Cliente
import kotlinx.coroutines.tasks.await

class ClienteController(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private fun ref(): DatabaseReference {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("No auth")
        return DbRefs.clientes(uid)
    }

    fun escucharClientes(onChange: (List<Cliente>) -> Unit, onError: (String) -> Unit) {
        ref().addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Cliente>()
                snapshot.children.forEach { child ->
                    child.getValue(Cliente::class.java)?.let { lista.add(it) }
                }
                onChange(lista)
            }
            override fun onCancelled(error: DatabaseError) { onError(error.message) }
        })
    }

    suspend fun guardar(cliente: Cliente) {
        val key = cliente.id ?: ref().push().key!!
        ref().child(key).setValue(cliente.copy(id = key)).await()
    }

    suspend fun eliminar(id: String) {
        ref().child(id).removeValue().await()
    }
}
