package fr.epf.mm.gestionclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.room.Room
import com.bumptech.glide.Glide
import fr.epf.mm.gestionclient.API.AppDatabase
import fr.epf.mm.gestionclient.API.DatabaseProvider
import fr.epf.mm.gestionclient.model.FavoriteCountry
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers


class CountryDetailsActivity : AppCompatActivity() {

    private lateinit var appDatabase: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_country)
        val button = findViewById<ImageButton>(R.id.buttonAddToFavorites)

        val countryName = intent.getStringExtra("country_name")
        val countryFlag = intent.getStringExtra("country_flag")
        val countryPopulation = intent.getIntExtra("country_population", 0)
        val countryArea = intent.getDoubleExtra("country_area", 0.0)

        val nameTextView: TextView = findViewById(R.id.country_name_textview)
        val flagImageView: ImageView = findViewById(R.id.country_flag_imageview)
        val populationTextView: TextView = findViewById(R.id.country_population_textview)
        val areaTextView: TextView = findViewById(R.id.country_area_textview)

        button.setOnClickListener() {
            onAddToFavoritesClicked()
        }
        nameTextView.text = countryName
        populationTextView.text = "$countryPopulation"
        areaTextView.text = "$countryArea km²"

        Glide.with(this)
            .load(countryFlag)
            .into(flagImageView)

        appDatabase = DatabaseProvider.getInstance(this)
    }

    fun onAddToFavoritesClicked() {
        val countryInfo = GeoNameCountry("France", 67076000, 1.888334, "Paris", "fr")
        val favoriteCountry = FavoriteCountry.fromCountryInfo(countryInfo)
        GlobalScope.launch {
            saveCountryToFavorites(favoriteCountry)
        }
    }

    private suspend fun saveCountryToFavorites(countryInfo: FavoriteCountry) {
        withContext(Dispatchers.IO) {
            appDatabase.favoriteCountryDao().insert(countryInfo)
        }
    }
}
