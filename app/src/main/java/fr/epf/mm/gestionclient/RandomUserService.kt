package fr.epf.mm.gestionclient

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoNamesService {
    @GET("countryInfoJSON")
    suspend fun searchCountries(
        @Query("username") username: String,
        @Query("lang") lang: String = "fr",
        @Query("name") country: String? = null
    ): GeoNamesResponse

    @GET("weatherJSON")
    suspend fun searchWeather(
        @Query("north") north: Double,
        @Query("south") south: Double,
        @Query("east") east: Double,
        @Query("west") west: Double,
        @Query("username") username: String,
        @Query("maxRows") maxRows: Int = 10
    ): GeoNamesWeatherResponse
}

data class GeoNamesWeatherResponse(
    val weatherObservations: List<WeatherObservation>
)

data class WeatherObservation(
    val stationName: String,
    val temperature: String?,
    val humidity: String?,
    val clouds: String?,
    val datetime: String,
    val lat: Double,
    val lng: Double
)

data class GeoNamesResponse(
    val geonames: List<GeoNameCountry>
)
data class GeoNameCountry(
    val countryName: String,
    val population: Int,
    val areaInSqKm: Double,
    val capital: String,
    val countryCode: String,
    val north: Double,
    val south: Double,
    val east: Double,
    val west: Double,
) {
    val flag: String
        get() = "https://flagcdn.com/w80/${countryCode.lowercase()}.png"
}
