package fr.epf.mm.gestionclient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val addButton = findViewById<Button>(R.id.home_add_button)
        val listButton = findViewById<Button>(R.id.home_list_button)

        addButton.click {
            val intent = Intent(this, AddClientActivity::class.java)
            startActivity(intent)
        }

        listButton.click {
            val intent = Intent(this, ListClientActivity::class.java)
            startActivity(intent)
        }
    }
}