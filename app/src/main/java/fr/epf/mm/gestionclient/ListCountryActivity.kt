package fr.epf.mm.gestionclient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.epf.mm.gestionclient.model.Country
import kotlinx.coroutines.*
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private const val TAG = "ListClientActivity"

class ListCountryActivity : AppCompatActivity(), OnCountryClickListener {

    lateinit var recyclerView: RecyclerView
    private lateinit var gifLoadingLayout: LinearLayout
    private val geoNamesUsername = "maxenceepf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_country)

        gifLoadingLayout = findViewById(R.id.gif_loading_layout)
        recyclerView = findViewById(R.id.list_country_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = CountryAdapter(emptyList(), this)

        val countries = intent.getParcelableArrayListExtra<Country>("countries")
        if (countries != null && countries.isNotEmpty()) {
            gifLoadingLayout.visibility = View.GONE
            val adapter = CountryAdapter(countries, this@ListCountryActivity)
            recyclerView.adapter = adapter
        } else {
            val query = intent.getStringExtra("query")
            val language = intent.getStringExtra("language")?: "fr"
            Log.d(TAG, "Language: $language")
            if (query != null && query.isNotEmpty()) {
                searchCountries(query, language)
            } else {
                Log.e(TAG, "No query provided")
            }
        }
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

    private fun searchCountries(query: String, language: String) {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(45, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://api.geonames.org/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()

        val geoNamesService = retrofit.create(GeoNamesService::class.java)

        gifLoadingLayout.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val maxRetries = 3
            val baseDelay = 1000L
            var currentRetry = 0

            var countries = emptyList<Country>() // Liste pour stocker tous les pays

            while (currentRetry < maxRetries) {
                try {
                    val response = geoNamesService.searchCountries(geoNamesUsername, language)
                    countries = response.geonames.map {
                        Country(
                            name = it.countryName,
                            population = it.population,
                            area = it.areaInSqKm,
                            flag = it.flag
                        )
                    }

                    // Sortie de la boucle une fois que tous les pays sont obtenus
                    break
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching countries: ${e.message}")
                    val delay = baseDelay * Math.pow(2.0, currentRetry.toDouble()).toLong()
                    delay(delay)
                    currentRetry++
                }
            }

            if (currentRetry == maxRetries) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "Failed to fetch countries after $maxRetries retries")
                }
            }

            // Filtrer les pays en fonction de la requête
            val filteredCountries = countries.filter { it.name.contains(query, ignoreCase = true) }
                .sortedBy { it.name }

            withContext(Dispatchers.Main) {
                gifLoadingLayout.visibility = View.GONE
                val adapter = CountryAdapter(filteredCountries, this@ListCountryActivity)
                recyclerView.adapter = adapter
            }
        }
    }



    override fun onCountryClick(country: Country) {
        val intent = Intent(this, CountryDetailsActivity::class.java).apply {
            putExtra("country_name", country.name)
            putExtra("country_flag", country.flag)
            putExtra("country_population", country.population)
            putExtra("country_area", country.area)
        }
        startActivity(intent)
    }
}
