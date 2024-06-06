package fr.epf.mm.gestionclient.API
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.epf.mm.gestionclient.model.FavoriteCountry

@Dao
interface FavoriteCountryDao {
    @Query("SELECT * FROM favorite_countries")
    fun getAll(): List<FavoriteCountry>

    @Insert
    fun insert(country: FavoriteCountry)
}