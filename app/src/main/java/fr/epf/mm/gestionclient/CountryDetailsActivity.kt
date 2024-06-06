package fr.epf.mm.gestionclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.ImageButton
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import fr.epf.mm.gestionclient.API.AppDatabase
import fr.epf.mm.gestionclient.API.DatabaseProvider
import fr.epf.mm.gestionclient.model.Country
import fr.epf.mm.gestionclient.model.FavoriteCountry


private const val TAG = "CountryDetailsActivity"

class CountryDetailsActivity : AppCompatActivity() {
    private lateinit var geoNamesService: GeoNamesService
    private lateinit var appDatabase: AppDatabase
    private lateinit var buttonAddToFavorites: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_country)
        buttonAddToFavorites = findViewById(R.id.buttonAddToFavorites)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://api.geonames.org/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        geoNamesService = retrofit.create(GeoNamesService::class.java)

        val countryName = intent.getStringExtra("country_name")
        val countryFlag = intent.getStringExtra("country_flag")
        val countryPopulation = intent.getIntExtra("country_population", 0)
        val countryArea = intent.getDoubleExtra("country_area", 0.0)

        val nameTextView: TextView = findViewById(R.id.country_name_textview)
        val flagImageView: ImageView = findViewById(R.id.country_flag_imageview)
        val populationTextView: TextView = findViewById(R.id.country_population_textview)
        val areaTextView: TextView = findViewById(R.id.country_area_textview)

        appDatabase = DatabaseProvider.getInstance(this)

        GlobalScope.launch {
            val isCountryInFavorites = countryName?.let { checkCountryInFavorites(it) }
            if (isCountryInFavorites != null) {
                updateFavoriteButtonImage(isCountryInFavorites)
            }
        }

        buttonAddToFavorites.setOnClickListener() {
            onAddToFavoritesClicked(countryName!!, countryPopulation, countryArea, countryFlag!!)
        }
        nameTextView.text = countryName
        populationTextView.text = "$countryPopulation"
        areaTextView.text = "$countryArea km²"

        Glide.with(this)
            .load(countryFlag)
            .into(flagImageView)


    }

    fun onAddToFavoritesClicked(name : String, population : Int, area : Double, flag : String) {
        val countryInfo = Country(name, population, area, flag)
        val favoriteCountry = FavoriteCountry.fromCountryInfo(countryInfo)
        GlobalScope.launch {
            saveCountryToFavorites(favoriteCountry)
        }
    }

    private suspend fun saveCountryToFavorites(countryInfo: FavoriteCountry) {
        updateFavoriteButtonImage(true)
        withContext(Dispatchers.IO) {
            appDatabase.favoriteCountryDao().insert(countryInfo)
        }
    }

    private suspend fun checkCountryInFavorites(countryName: String): Boolean {
        return withContext(Dispatchers.IO) {
            appDatabase.favoriteCountryDao().getCountryByName(countryName) != null
        }
    }

    private fun updateFavoriteButtonImage(isInFavorites: Boolean) {
        val imageResource = if (isInFavorites) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off
        buttonAddToFavorites.setImageResource(imageResource)
    }
}
