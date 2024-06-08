package fr.epf.mm.gestionclient

import WeatherAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private var isCountryInFavorites: Boolean = false

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
        val north = intent.getDoubleExtra("north", 0.0)
        val south = intent.getDoubleExtra("south", 0.0)
        val east = intent.getDoubleExtra("east", 0.0)
        val west = intent.getDoubleExtra("west", 0.0)

        val nameTextView: TextView = findViewById(R.id.country_name_textview)
        val flagImageView: ImageView = findViewById(R.id.country_flag_imageview)
        val populationTextView: TextView = findViewById(R.id.country_population_textview)
        val areaTextView: TextView = findViewById(R.id.country_area_textview)
        val recyclerView = findViewById<RecyclerView>(R.id.list_weather_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = WeatherAdapter(emptyList())

        appDatabase = DatabaseProvider.getInstance(this)

        GlobalScope.launch {
            isCountryInFavorites = countryName?.let { checkCountryInFavorites(it) }!!
            if (isCountryInFavorites != null) {
                updateFavoriteButtonImage(isCountryInFavorites)
            }

            val weatherObservations = fetchWeatherObservations(
                north = north,
                south = south,
                east = east,
                west = west,
            )

            withContext(Dispatchers.Main) {
                recyclerView.adapter = WeatherAdapter(weatherObservations)
            }
        }

        buttonAddToFavorites.setOnClickListener {
            if (countryName != null && countryFlag != null) {
                    if (isCountryInFavorites) {
                        onRemoveFromFavoritesClicked(countryName)
                    } else {
                        onAddToFavoritesClicked(countryName, countryPopulation, countryArea, countryFlag, north, south, east, west)
                    }
                }
        }

        nameTextView.text = countryName
        populationTextView.text = "$countryPopulation"
        areaTextView.text = "$countryArea km²"

        Glide.with(this)
            .load(countryFlag)
            .into(flagImageView)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.go_back_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_back -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun onAddToFavoritesClicked(name : String, population : Int, area : Double, flag : String, north: Double, south: Double, east: Double, west: Double) {
        val countryInfo = Country(name, population, area, flag, north, south, east, west)
        val favoriteCountry = FavoriteCountry.fromCountryInfo(countryInfo)
        GlobalScope.launch {
            saveCountryToFavorites(favoriteCountry)
        }
    }

    private suspend fun saveCountryToFavorites(countryInfo: FavoriteCountry) {
        updateFavoriteButtonImage(true)
        isCountryInFavorites = true
        withContext(Dispatchers.IO) {
            appDatabase.favoriteCountryDao().insert(countryInfo)
        }
    }

    fun onRemoveFromFavoritesClicked(name : String) {
        GlobalScope.launch {
            removeCountryFromFavorites(name)
        }
    }

    private suspend fun removeCountryFromFavorites(countryName: String) {
        updateFavoriteButtonImage(false)
        isCountryInFavorites = false
        withContext(Dispatchers.IO) {
            appDatabase.favoriteCountryDao().deleteByCountryName(countryName)
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

    suspend fun fetchWeatherObservations(north: Double, south: Double, east: Double, west: Double, ): List<WeatherObservation> {
        return geoNamesService.searchWeather(
            north = north,
            south = south,
            east = east,
            west = west,
            username = "maxenceepf",
        ).weatherObservations
    }

}
