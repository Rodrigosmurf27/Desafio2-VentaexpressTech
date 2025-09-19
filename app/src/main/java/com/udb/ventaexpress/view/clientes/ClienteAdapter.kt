package com.udb.ventaexpress.view.clientes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udb.ventaexpress.databinding.ItemClienteBinding
import com.udb.ventaexpress.model.Cliente

class ClienteAdapter(
    private val onEdit: (Cliente) -> Unit,
    private val onDelete: (Cliente) -> Unit
) : RecyclerView.Adapter<ClienteAdapter.VH>() {

    private val data = mutableListOf<Cliente>()
    fun submit(list: List<Cliente>) { data.clear(); data.addAll(list); notifyDataSetChanged() }

    inner class VH(val b: ItemClienteBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemClienteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = data[position]
        holder.b.tvNombre.text = c.nombre
        holder.b.tvCorreo.text = c.correo
        holder.b.tvTel.text = c.telefono
        holder.b.btnEdit.setOnClickListener { onEdit(c) }
        holder.b.btnDelete.setOnClickListener { onDelete(c) }
    }
}
