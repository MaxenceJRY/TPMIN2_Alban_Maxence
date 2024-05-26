package fr.epf.mm.gestionclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import fr.epf.mm.gestionclient.model.Client

class DetailsClientActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_client)

        val lastNameTextView =
            findViewById<TextView>(R.id.details_client_lastname_textview)

        intent.extras?.apply {
            val client = getParcelable(CLIENT_ID_EXTRA) as? Client

            client?.let {
                lastNameTextView.text = it.firstName
            }
        }
    }
}