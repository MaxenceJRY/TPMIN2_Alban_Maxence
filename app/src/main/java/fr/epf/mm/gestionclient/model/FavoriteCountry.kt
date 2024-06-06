package fr.epf.mm.gestionclient.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_countries")
data class FavoriteCountry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val countryName: String,
    val population: Int,
    val areaInSqKm: Double,
    val flag: String
){
    companion object {
        fun fromCountryInfo(countryInfo: Country): FavoriteCountry {
            return FavoriteCountry(
                countryName = countryInfo.name,
                areaInSqKm = countryInfo.area,
                flag = countryInfo.flag,
                population = countryInfo.population,
            )
        }
    }
}








