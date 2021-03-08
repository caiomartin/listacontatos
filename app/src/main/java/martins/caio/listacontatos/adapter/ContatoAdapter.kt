package  martins.caio.listacontatos.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import martins.caio.listacontatos.R
import martins.caio.listacontatos.model.ContatosVO
import kotlinx.android.synthetic.main.item_contato.view.*

class ContatoAdapter(
    private val context: Context,
    private val lista: List<ContatosVO>,
    private val onClick: ((Int) -> Unit)
) : RecyclerView.Adapter<ContatoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_contato,parent,false)
        return ContatoViewHolder(view)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ContatoViewHolder, position: Int) {
        val contato = lista[position]
        with(holder.itemView){
            val hash = contato.hashCode()
            tvFirstName.text = contato.nome.first().toString().toUpperCase()
            tvFirstName.background = oval(Color.rgb(hash,hash / 2, 0))
            tvNome.text = contato.nome
            etTelefone.text = contato.telefone
            llItem.setOnClickListener { onClick(contato.id) }
        }
    }

    fun View.oval(@ColorInt color: Int): ShapeDrawable {
        val oval = ShapeDrawable(OvalShape())
        with(oval) {
            intrinsicHeight = height
            intrinsicWidth = width
            paint.color = color
        }
        return oval
    }


}

class ContatoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)