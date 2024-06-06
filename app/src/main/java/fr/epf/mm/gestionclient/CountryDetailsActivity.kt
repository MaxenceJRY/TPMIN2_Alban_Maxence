package fr.epf.mm.gestionclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val TAG = "CountryDetailsActivity"

class CountryDetailsActivity : AppCompatActivity() {
    private lateinit var geoNamesService: GeoNamesService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_country)

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

        nameTextView.text = countryName
        populationTextView.text = "$countryPopulation"
        areaTextView.text = "$countryArea km²"

        Glide.with(this)
            .load(countryFlag)
            .into(flagImageView)
    }
}
