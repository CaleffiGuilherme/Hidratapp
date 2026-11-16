package com.example.hidratapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class BebidaAdapter(
    private val bebidas: List<Bebida>,
    private val onClick: (Bebida) -> Unit
) : RecyclerView.Adapter<BebidaAdapter.BebidaViewHolder>() {

    class BebidaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.tvTituloBebida)
        val data: TextView = view.findViewById(R.id.tvDataBebida)
        val ivFavorito: ImageView = view.findViewById(R.id.ivFavorito)
        val ivTipoIcon: ImageView = view.findViewById(R.id.ivTipoIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BebidaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bebida, parent, false)
        return BebidaViewHolder(view)
    }

    override fun onBindViewHolder(holder: BebidaViewHolder, position: Int) {
        val bebida = bebidas[position]
        val context = holder.itemView.context
        holder.titulo.text = context.getString(R.string.bebida_titulo, TipoBebida.nomeExibido(bebida.tipo, context), bebida.ml)
        holder.data.text = if (bebida.observacao.isNotBlank()) {
            context.getString(R.string.bebida_data_observacao, bebida.data, bebida.hora, bebida.observacao)
        } else {
            context.getString(R.string.bebida_data_hora, bebida.data, bebida.hora)
        }

        holder.ivTipoIcon.setBackgroundResource(if (Calculos.ehAlcool(bebida.tipo)) R.drawable.bg_soft_lavender else R.drawable.bg_soft_blue)
        val padding = (10 * context.resources.displayMetrics.density).toInt()
        holder.ivTipoIcon.setPadding(padding,padding,padding,padding)
        configurarInteracoes(holder.itemView)
        if (bebida.favorito) {
            holder.ivFavorito.setImageResource(R.drawable.ic_star)
            holder.ivFavorito.setColorFilter(ContextCompat.getColor(context, R.color.star_gold))
        } else {
            holder.ivFavorito.setImageResource(R.drawable.ic_star_border)
            holder.ivFavorito.setColorFilter(ContextCompat.getColor(context, R.color.text_secondary))
        }

        holder.itemView.setOnClickListener { onClick(bebida) }
    }

    override fun getItemCount() = bebidas.size
}
