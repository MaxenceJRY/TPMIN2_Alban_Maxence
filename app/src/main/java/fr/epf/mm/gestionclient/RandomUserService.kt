package fr.epf.mm.gestionclient

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoNamesService {
    @GET("countryInfoJSON")
    suspend fun searchCountries(
        @Query("username") username: String,
        @Query("lang") lang: String = "en",
        @Query("name") country: String? = null
    ): GeoNamesResponse
}


data class GeoNamesResponse(
    val geonames: List<GeoNameCountry>
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
