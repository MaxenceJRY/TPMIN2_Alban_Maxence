package fr.epf.mm.gestionclient.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Country(
    val name: String,
    val population: Int = 0,
    val area: Double = 0.0,
    val flag: String,
    val north: Double = 0.0,
    val south: Double = 0.0,
    val east: Double = 0.0,
    val west: Double = 0.0
) : Parcelable {
    companion object {
        fun generate(size: Int = 20) =
            (1..size).map {
                Country("name$it", it, it*1.0 , "flag$it")
            }
    }
}
