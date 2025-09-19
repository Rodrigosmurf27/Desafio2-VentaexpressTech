package com.udb.ventaexpress.view.productos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udb.ventaexpress.databinding.ItemProductoBinding
import com.udb.ventaexpress.model.Producto

class ProductoAdapter(
    private val onEdit: (Producto) -> Unit,
    private val onDelete: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.VH>() {

    private val data = mutableListOf<Producto>()

    fun submit(list: List<Producto>) {
        data.clear(); data.addAll(list); notifyDataSetChanged()
    }

    inner class VH(val b: ItemProductoBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = data[position]
        holder.b.tvNombre.text = p.nombre
        holder.b.tvPrecio.text = "$ ${"%.2f".format(p.precio)}"
        holder.b.tvStock.text  = "Stock: ${p.stock}"
        holder.b.tvDesc.text   = p.descripcion
        holder.b.btnEdit.setOnClickListener { onEdit(p) }
        holder.b.btnDelete.setOnClickListener { onDelete(p) }
    }
}
