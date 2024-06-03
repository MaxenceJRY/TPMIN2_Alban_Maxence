package fr.epf.mm.gestionclient.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Country(
    val name: String,
    val flag: String,
    val population: Long = 0,
    val area: Double = 0.0,

) : Parcelable{
    companion object {

        fun generate(size : Int = 20) =
            (1..size).map {
                Country("name${it}",
                    "flag${it}")
            }
    }
}