package fr.epf.mm.gestionclient.API
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.epf.mm.gestionclient.model.FavoriteCountry

@Dao
interface FavoriteCountryDao {
    @Query("SELECT * FROM favorite_countries")
    fun getAll(): List<FavoriteCountry>

    @Query("SELECT * FROM favorite_countries WHERE countryName = :countryName")
    fun getCountryByName(countryName: String): FavoriteCountry?

    @Query("DELETE FROM favorite_countries WHERE countryName = :countryName")
    fun deleteByCountryName(countryName: String)

    @Insert
    fun insert(country: FavoriteCountry)
}