package fr.epf.mm.gestionclient.API
import androidx.room.Database
import androidx.room.RoomDatabase
import fr.epf.mm.gestionclient.model.FavoriteCountry

@Database(entities = [FavoriteCountry::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteCountryDao(): FavoriteCountryDao
}