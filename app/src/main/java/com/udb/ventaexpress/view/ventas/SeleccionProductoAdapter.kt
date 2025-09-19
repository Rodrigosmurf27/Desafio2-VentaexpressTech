package com.udb.ventaexpress.view.ventas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udb.ventaexpress.databinding.ItemSeleccionProductoBinding
import com.udb.ventaexpress.model.Producto
import com.udb.ventaexpress.model.VentaItem

class SeleccionProductoAdapter(
    private val onQtyChanged: () -> Unit
) : RecyclerView.Adapter<SeleccionProductoAdapter.VH>() {

    private val data = mutableListOf<Producto>()
    private val cantidades = hashMapOf<String, Int>()

    fun submit(list: List<Producto>) {
        data.clear(); data.addAll(list); cantidades.clear(); notifyDataSetChanged()
    }

    fun seleccionados(): List<VentaItem> {
        return data.mapNotNull { p ->
            val qty = cantidades[p.id] ?: 0
            if (qty > 0) VentaItem(
                productoId = p.id ?: "",
                nombre = p.nombre,
                cantidad = qty,
                precioUnitario = p.precio,
                subtotal = p.precio * qty
            ) else null
        }
    }

    inner class VH(val b: ItemSeleccionProductoBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemSeleccionProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = data[position]
        val id = p.id ?: return
        val qty = cantidades[id] ?: 0

        holder.b.tvNombre.text = p.nombre
        holder.b.tvPrecio.text = "$ ${"%.2f".format(p.precio)}"
        holder.b.tvStock.text  = "Disp: ${p.stock}"
        holder.b.tvQty.text    = qty.toString()

        holder.b.btnMas.setOnClickListener {
            val nuevo = (cantidades[id] ?: 0) + 1
            if (nuevo <= p.stock) {
                cantidades[id] = nuevo
                holder.b.tvQty.text = nuevo.toString()
                onQtyChanged()
            }
        }
        holder.b.btnMenos.setOnClickListener {
            val nuevo = (cantidades[id] ?: 0) - 1
            if (nuevo >= 0) {
                cantidades[id] = nuevo
                holder.b.tvQty.text = nuevo.toString()
                onQtyChanged()
            }
        }
    }
}
