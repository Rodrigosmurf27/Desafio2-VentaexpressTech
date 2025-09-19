package com.udb.ventaexpress.controller

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object DbRefs {
    private val db: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }

    fun userRoot(uid: String): DatabaseReference = db.reference.child("users").child(uid)
    fun productos(uid: String) = userRoot(uid).child("productos")
    fun clientes(uid: String) = userRoot(uid).child("clientes")
    fun ventas(uid: String) = userRoot(uid).child("ventas")
}
