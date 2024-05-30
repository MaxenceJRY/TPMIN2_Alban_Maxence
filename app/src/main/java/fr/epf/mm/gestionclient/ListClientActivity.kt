package fr.epf.mm.gestionclient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.epf.mm.gestionclient.model.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


private const val TAG = "ListClientActivity"

class ListClientActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_client)

        recyclerView =
            findViewById<RecyclerView>(R.id.list_client_recyclerview)

        recyclerView.layoutManager =
            LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = ClientAdapter(emptyList())

        val query = intent.getStringExtra("query")
        if (query != null && query.isNotEmpty()) {
            searchCountries(query)
        } else {
            Log.e(TAG, "No query provided")
        }

    }

    private fun searchCountries (query: String){
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS) // délai d'attente de connexion
            .readTimeout(30, TimeUnit.SECONDS)    // délai d'attente de lecture
            .writeTimeout(30, TimeUnit.SECONDS)   // délai d'attente d'écriture
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://restcountries.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()

        val restCountriesService = retrofit.create(RestCountriesService::class.java)

        // Utilisation de CoroutineScope pour lancer une coroutine sur le dispatcher IO
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val countries = restCountriesService.getCountriesByName(query)
                Log.d(TAG, "Fetched countries: $countries")

                // Trier les pays par ordre alphabétique du nom commun
                val sortedCountries = countries.sortedBy { it.name.common }

                // Map sorted countries to clients or any other relevant data structure
                val clients = sortedCountries.map {
                    Log.d(TAG, "Country: ${it.name.common}, Flag URL: ${it.flags.png}")
                    Client(
                        firstName = it.name.common, // Using the common name of the country
                        flag = it.flags.png // URL of the flag image
                    )
                }

                // Mettre à jour l'UI sur le thread principal
                withContext(Dispatchers.Main) {
                    val adapter = ClientAdapter(clients)
                    recyclerView.adapter = adapter
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching countries: ${e.message}")
            }
        }
    }
}