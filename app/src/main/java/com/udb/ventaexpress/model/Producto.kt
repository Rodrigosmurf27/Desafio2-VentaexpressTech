package com.udb.ventaexpress.model

data class Producto(
    val id: String? = null,
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val stock: Int = 0
)
