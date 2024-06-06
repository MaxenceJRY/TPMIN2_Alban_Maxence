package fr.epf.mm.gestionclient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.epf.mm.gestionclient.model.Country
import kotlinx.coroutines.*
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.droidsonroids.gif.GifImageView
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private const val TAG = "ListClientActivity"

class ListCountryActivity : AppCompatActivity(), OnCountryClickListener {

    lateinit var recyclerView: RecyclerView
    private lateinit var gifLoadingLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_country)

        gifLoadingLayout = findViewById(R.id.gif_loading_layout)
        recyclerView = findViewById(R.id.list_country_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = ClientAdapter(emptyList(), this)

        val query = intent.getStringExtra("query")
        if (query != null && query.isNotEmpty()) {
            searchCountries(query)
        } else {
            Log.e(TAG, "No query provided")
        }
    }

    private fun searchCountries(query: String) {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val country = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(45, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://restcountries.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(country)
            .build()

        val restCountriesService = retrofit.create(RestCountriesService::class.java)

        gifLoadingLayout.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val maxRetries = 3
            val baseDelay = 1000L
            var currentRetry = 0

            while (currentRetry < maxRetries) {
                try {
                    val countries = restCountriesService.getCountriesByName(query)
                    Log.d(TAG, "Fetched countries: $countries")

                    val sortedCountries = countries.sortedBy { it.name.common }

                    val clients = sortedCountries.map {
                        Log.d(TAG, "Country: ${it.name.common}, Flag URL: ${it.flags.png}")
                        Country(
                            name = it.name.common,
                            flag = it.flags.png,
                            population = it.population, // Add population
                            area = it.area // Add area
                        )
                    }

                    withContext(Dispatchers.Main) {
                        gifLoadingLayout.visibility = View.GONE
                        val adapter = ClientAdapter(clients, this@ListCountryActivity)
                        recyclerView.adapter = adapter
                    }

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

