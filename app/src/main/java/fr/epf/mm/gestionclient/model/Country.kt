package fr.epf.mm.gestionclient.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Country(
    val name: String,
    val flag: String
) : Parcelable{
    companion object {

        fun generate(size : Int = 20) =
            (1..size).map {
                Country("name${it}",
                    "flag${it}")
            }
    }
}