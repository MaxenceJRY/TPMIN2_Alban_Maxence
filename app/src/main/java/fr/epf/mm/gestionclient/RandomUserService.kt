package fr.epf.mm.gestionclient

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//interface RandomUserService {
//    @GET("api")
//suspend  fun getUsers(@Query("results") size: Int) : GetUsersResult
//}

interface RestCountriesService {
    @GET("v3.1/all")
    suspend fun getAllCountries(): List<CountryResponse>
}

data class CountryResponse(
    val name: Name,
    val population: Long,
    val area: Double,
    val region: String,
    val subregion: String,
    val flags : Flags
)

data class Name(
    val common: String,
    val official: String
)

data class Flags(
    val png: String
)