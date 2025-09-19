package com.udb.ventaexpress.model

data class Venta(
    val id: String? = null,
    val clienteId: String = "",
    val clienteNombre: String = "",
    val items: List<VentaItem> = emptyList(),
    val total: Double = 0.0,
    val fecha: Long = System.currentTimeMillis()
)
