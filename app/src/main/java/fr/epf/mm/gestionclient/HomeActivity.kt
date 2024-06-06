package fr.epf.mm.gestionclient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup

class HomeActivity : AppCompatActivity() {

    private var selectedLanguage = "en"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val listButton = findViewById<Button>(R.id.home_list_button)
        val editText = findViewById<EditText>(R.id.searchCountry_editText)

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
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupLangue)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedLanguage = when (checkedId) {
                R.id.radioButtonFr -> "fr"
                R.id.radioButtonEn -> "en"
                R.id.radioButtonEs -> "es"
                else -> "fr"
            }
        }
    }
}
