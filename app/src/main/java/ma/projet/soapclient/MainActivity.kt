package ma.projet.soapclient

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ma.projet.soapclient.adapter.CompteAdapter
import ma.projet.soapclient.beans.Compte
import ma.projet.soapclient.beans.TypeCompte
import ma.projet.soapclient.ws.Service

class MainActivity : AppCompatActivity() {
    private val adapter = CompteAdapter()
    private val service = Service()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
        adapter.onDeleteClick = { compte ->
            MaterialAlertDialogBuilder(this).setTitle("Supprimer le compte")
                .setMessage("Supprimer le compte ${compte.id} ?")
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Supprimer") { _, _ ->
                    runSoap({ service.deleteCompte(requireNotNull(compte.id)) }, "Compte supprimé")
                }.show()
        }
        adapter.onEditClick = { showCompteDialog(it) }
        findViewById<Button>(R.id.fabAdd).setOnClickListener { showCompteDialog(null) }
        loadComptes()
    }

    private fun showCompteDialog(existing: Compte?) {
        val form = layoutInflater.inflate(R.layout.popup, null)
        val solde = form.findViewById<TextInputEditText>(R.id.etSolde)
        val courant = form.findViewById<RadioButton>(R.id.radioCourant)
        val epargne = form.findViewById<RadioButton>(R.id.radioEpargne)
        if (existing != null) {
            solde.setText(existing.solde.toString())
            if (existing.type == TypeCompte.EPARGNE) epargne.isChecked = true
        }
        val dialog = MaterialAlertDialogBuilder(this).setTitle(if (existing == null) "Nouveau compte" else "Modifier le compte")
            .setView(form).setNegativeButton("Annuler", null).setPositiveButton("Enregistrer", null).create()
        dialog.setOnShowListener {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val amount = solde.text?.toString()?.toDoubleOrNull()
                if (amount == null || amount < 0) { solde.error = "Saisir un solde valide"; return@setOnClickListener }
                val type = if (courant.isChecked) TypeCompte.COURANT else TypeCompte.EPARGNE
                dialog.dismiss()
                runSoap({
                    if (existing == null) service.createCompte(amount, type)
                    else service.updateCompte(requireNotNull(existing.id), amount, type)
                }, "Compte enregistré")
            }
        }
        dialog.show()
    }

    private fun runSoap(action: () -> Boolean, success: String) {
        lifecycleScope.launch {
            try {
                val ok = withContext(Dispatchers.IO) { action() }
                Toast.makeText(this@MainActivity, if (ok) success else "Opération refusée", Toast.LENGTH_SHORT).show()
                if (ok) loadComptes()
            } catch (e: Exception) { Toast.makeText(this@MainActivity, e.message ?: "Erreur réseau", Toast.LENGTH_LONG).show() }
        }
    }

    private fun loadComptes() {
        lifecycleScope.launch {
            try { adapter.updateComptes(withContext(Dispatchers.IO) { service.getComptes() }) }
            catch (e: Exception) { Toast.makeText(this@MainActivity, e.message ?: "Erreur réseau", Toast.LENGTH_LONG).show() }
        }
    }
}
