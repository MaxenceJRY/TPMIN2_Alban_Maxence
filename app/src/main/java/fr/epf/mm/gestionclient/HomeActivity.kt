package fr.epf.mm.gestionclient
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import fr.epf.mm.gestionclient.API.AppDatabase
import fr.epf.mm.gestionclient.API.DatabaseProvider
import fr.epf.mm.gestionclient.model.Country
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var appDatabase: AppDatabase
    private lateinit var sharedPreferences: SharedPreferences

    private var selectedLanguage: String
        get() = sharedPreferences.getString("language", "fr") ?: "fr"
        set(value) = sharedPreferences.edit().putString("language", value).apply()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        setAppLanguage(selectedLanguage)
        setContentView(R.layout.activity_home)

        val mainLayout = findViewById<FrameLayout>(R.id.main_layout)
        Glide.with(this)
            .asGif()
            .load(R.drawable.background_ciel)
            .into(object : CustomTarget<GifDrawable>() {
                override fun onResourceReady(resource: GifDrawable, transition: Transition<in GifDrawable>?) {
                    mainLayout.background = resource
                    resource.start()
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })


        appDatabase = DatabaseProvider.getInstance(this)

        val listButton = findViewById<Button>(R.id.home_list_button)
        val editText = findViewById<EditText>(R.id.searchCountry_editText)
        val getAllButton = findViewById<Button>(R.id.get_all_button)

        listButton.setOnClickListener {
            val query = editText.text.toString()
            if (query.isNotEmpty()) {
                val intent = Intent(this, ListCountryActivity::class.java).apply {
                    putExtra("query", query)
                    putExtra("language", selectedLanguage)
                }
                startActivity(intent)
            }
        }

        getAllButton.setOnClickListener {
            displayAllItemsFromDatabase()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.list_countries, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item1 -> {
                updateLanguage("fr")
                true
            }
            R.id.item2 -> {
                updateLanguage("en")
                true
            }
            R.id.item3 -> {
                updateLanguage("es")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateLanguage(languageCode: String) {
        selectedLanguage = languageCode
        setAppLanguage(languageCode)


        val refresh = Intent(this, HomeActivity::class.java)
        finish()
        startActivity(refresh)
    }

    private fun setAppLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
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
                        flag = it.flag,
                        north = it.north,
                        south = it.south,
                        east = it.east,
                        west = it.west
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
