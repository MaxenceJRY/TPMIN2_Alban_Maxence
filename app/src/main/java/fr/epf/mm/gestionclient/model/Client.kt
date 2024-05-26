package fr.epf.mm.gestionclient.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable


@Parcelize
data class Client(
    val firstName: String,
    val flag: String
) : Parcelable{
    companion object {

        fun generate(size : Int = 20) =
            (1..size).map {
                Client("Prénom${it}",
                    "flag${it}")
            }
    }
}