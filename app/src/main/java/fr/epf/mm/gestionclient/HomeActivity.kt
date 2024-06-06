package fr.epf.mm.gestionclient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val listButton = findViewById<Button>(R.id.home_list_button)
        val editText = findViewById<EditText>(R.id.searchCountry_editText)

        listButton.setOnClickListener {
            val query = editText.text.toString()
            if (query.isNotEmpty()) {
                val intent = Intent(this, ListCountryActivity::class.java)
                intent.putExtra("query", query)
                startActivity(intent)
            }
        }
    }
}
