package fr.epf.mm.gestionclient

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

class ListCountryActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView

    private lateinit var gif_loading_layout: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_country)

        gif_loading_layout = findViewById(R.id.gif_loading_layout)
        recyclerView = findViewById(R.id.list_country_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = ClientAdapter(emptyList())

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

        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://restcountries.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()

        val restCountriesService = retrofit.create(RestCountriesService::class.java)

        gif_loading_layout.visibility = View.VISIBLE
        // Utilisation de CoroutineScope pour lancer une coroutine sur le dispatcher IO
        CoroutineScope(Dispatchers.IO).launch {
            // Nombre maximum de tentatives
            val maxRetries = 3

            // Délai de base entre les tentatives (en millisecondes)
            val baseDelay = 1000L

            // Variable pour stocker le nombre de tentatives effectuées
            var currentRetry = 0

            while (currentRetry < maxRetries) {
                try {
                    val countries = restCountriesService.getCountriesByName(query)
                    Log.d(TAG, "Fetched countries: $countries")

                    // Trier les pays par ordre alphabétique du nom commun
                    val sortedCountries = countries.sortedBy { it.name.common }

                    // Map sorted countries to clients or any other relevant data structure
                    val clients = sortedCountries.map {
                        Log.d(TAG, "Country: ${it.name.common}, Flag URL: ${it.flags.png}")
                        Country(
                            name = it.name.common, // Using the common name of the country
                            flag = it.flags.png // URL of the flag image
                        )
                    }

                    // Mettre à jour l'UI sur le thread principal
                    withContext(Dispatchers.Main) {
                        gif_loading_layout.visibility = View.GONE
                        val adapter = ClientAdapter(clients)
                        recyclerView.adapter = adapter
                    }

                    // Arrêter la boucle de tentatives si la requête réussit
                    break
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching countries: ${e.message}")

                    // Augmenter le délai de mise à jour après chaque tentative
                    val delay = baseDelay * Math.pow(2.0, currentRetry.toDouble()).toLong()

                    // Attendre avant la prochaine tentative
                    delay(delay)

                    currentRetry++
                }
            }

            // Si le nombre maximum de tentatives est atteint, afficher un message d'erreur
            if (currentRetry == maxRetries) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "Failed to fetch countries after $maxRetries retries")
                }
            }
        }
    }
}
