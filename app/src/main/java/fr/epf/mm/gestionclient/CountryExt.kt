package fr.epf.mm.gestionclient

import android.util.Log
import android.view.View
import fr.epf.mm.gestionclient.model.Country

val Country.nomComplet : String
    get() = "${name}"

fun View.click( action : (View) -> Unit){
    Log.d("CLICK","click !")
    this.setOnClickListener(action)
}