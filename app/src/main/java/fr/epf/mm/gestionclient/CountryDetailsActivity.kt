package fr.epf.mm.gestionclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class CountryDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_country)

        val countryName = intent.getStringExtra("country_name")
        val countryFlag = intent.getStringExtra("country_flag")
        val countryPopulation = intent.getLongExtra("country_population", 0)
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
