package com.udb.ventaexpress.view.ventas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udb.ventaexpress.databinding.ItemVentaBinding
import com.udb.ventaexpress.model.Venta
import java.text.SimpleDateFormat
import java.util.*

class VentaAdapter : RecyclerView.Adapter<VentaAdapter.VH>() {
    private val data = mutableListOf<Venta>()
    fun submit(list: List<Venta>) { data.clear(); data.addAll(list); notifyDataSetChanged() }

    inner class VH(val b: ItemVentaBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemVentaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val v = data[position]
        holder.b.tvCliente.text = v.clienteNombre
        holder.b.tvTotal.text = "$ ${"%.2f".format(v.total)}"
        holder.b.tvItems.text = "Items: ${v.items.sumOf { it.cantidad }}"
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        holder.b.tvFecha.text = sdf.format(Date(v.fecha))
    }
}
