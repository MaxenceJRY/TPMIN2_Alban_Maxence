package fr.epf.mm.gestionclient

import retrofit2.http.GET
import retrofit2.http.Query

interface GeoNamesService {
    @GET("countryInfoJSON")
    suspend fun getCountries(
        @Query("username") username: String
    ): GeoNamesResponse
}


data class GeoNamesResponse(
    val geonames: List<GeoNameCountry>
)
data class Flags(
    val png: String
)
data class GeoNameCountry(
    val countryName: String,
    val population: Int,
    val areaInSqKm: Double,
    val capital: String,
    val countryCode: String
) {
    val flag: String
        get() = "https://flagcdn.com/w80/${countryCode.lowercase()}.png"
}
