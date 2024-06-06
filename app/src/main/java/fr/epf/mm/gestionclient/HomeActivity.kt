package fr.epf.mm.gestionclient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.room.Room
import fr.epf.mm.gestionclient.API.AppDatabase
import fr.epf.mm.gestionclient.API.DatabaseProvider
import fr.epf.mm.gestionclient.model.Country
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var appDatabase: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val listButton = findViewById<Button>(R.id.home_list_button)
        val editText = findViewById<EditText>(R.id.searchCountry_editText)
        val getAllButton = findViewById<Button>(R.id.get_all_button)

        appDatabase = DatabaseProvider.getInstance(this)

        listButton.setOnClickListener {
            val query = editText.text.toString()
            if (query.isNotEmpty()) {
                val intent = Intent(this, ListCountryActivity::class.java).apply {
                    putExtra("query", query)
                }
                startActivity(intent)
            }
        }

        getAllButton.setOnClickListener {
            displayAllItemsFromDatabase()
        }
    }

    private fun displayAllItemsFromDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val allItems = appDatabase.favoriteCountryDao().getAll()
            withContext(Dispatchers.Main) {
                val countries = ArrayList(allItems.map {
                    Country(
                        name = it.countryName,
                        population = it.population,
                        area = it.areaInSqKm,
                        flag = it.flag
                    )
                })

                val intent = Intent(this@HomeActivity, ListCountryActivity::class.java).apply {
                    putParcelableArrayListExtra("countries", countries)
                }
                startActivity(intent)
            }
        }
    }
}
