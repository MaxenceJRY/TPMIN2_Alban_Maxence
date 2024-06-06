package fr.epf.mm.gestionclient.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.epf.mm.gestionclient.GeoNameCountry

@Entity(tableName = "favorite_countries")
data class FavoriteCountry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val countryName: String,
    val population: Int,
    val areaInSqKm: Double,
    val capital: String,
    val countryCode: String
){
    companion object {
        fun fromCountryInfo(countryInfo: GeoNameCountry): FavoriteCountry {
            return FavoriteCountry(
                countryCode = countryInfo.countryCode,
                countryName = countryInfo.countryName,
                areaInSqKm = countryInfo.areaInSqKm,
                capital = countryInfo.capital,
                population = countryInfo.population,
            )
        }
    }
}








