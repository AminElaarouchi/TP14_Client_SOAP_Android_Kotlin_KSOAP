package ma.projet.soapclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import ma.projet.soapclient.R
import ma.projet.soapclient.beans.Compte
import java.text.SimpleDateFormat
import java.util.Locale

class CompteAdapter : RecyclerView.Adapter<CompteAdapter.Holder>() {
    private val comptes = mutableListOf<Compte>()
    var onEditClick: ((Compte) -> Unit)? = null
    var onDeleteClick: ((Compte) -> Unit)? = null

    fun updateComptes(items: List<Compte>) { comptes.clear(); comptes.addAll(items); notifyDataSetChanged() }
    override fun getItemCount() = comptes.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false))
    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(comptes[position])

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val id = view.findViewById<TextView>(R.id.textId)
        private val solde = view.findViewById<TextView>(R.id.textSolde)
        private val type = view.findViewById<TextView>(R.id.textType)
        private val date = view.findViewById<TextView>(R.id.textDate)
        private val edit = view.findViewById<Button>(R.id.btnEdit)
        private val delete = view.findViewById<Button>(R.id.btnDelete)
        fun bind(compte: Compte) {
            id.text = "Compte numéro ${compte.id}"
            solde.text = "${compte.solde} DH"
            type.text = compte.type.name
            date.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(compte.dateCreation)
            edit.setOnClickListener { onEditClick?.invoke(compte) }
            delete.setOnClickListener { onDeleteClick?.invoke(compte) }
        }
    }
}
