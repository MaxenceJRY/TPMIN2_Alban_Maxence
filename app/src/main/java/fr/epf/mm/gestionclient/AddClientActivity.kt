package fr.epf.mm.gestionclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

class AddClientActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_client)

        val lastNameEditext = findViewById<EditText>(R.id.add_client_last_name_edittext)
        val addButton = findViewById<Button>(R.id.add_client_button)

        val genderRadioGroup = findViewById<RadioGroup>(R.id.add_client_gender_radiogroup)
        genderRadioGroup.check(R.id.add_client_gender_woman_radiobutton)

        val ageSeekBar = findViewById<SeekBar>(R.id.add_client_age_seekbar)
        val ageTextview = findViewById<TextView>(R.id.add_client_age_textview)
        val levelSpinner = findViewById<Spinner>(R.id.add_client_level_spinner)

        ageSeekBar.setOnSeekBarChangeListener( object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, p2: Boolean) {
               ageTextview.text = progress.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit
            override fun onStopTrackingTouch(p0: SeekBar?) = Unit

        })



        addButton.setOnClickListener{
            Log.d("EPF", "Nom : ${lastNameEditext.text}")
            val gender =
                if(genderRadioGroup.checkedRadioButtonId == R.id.add_client_gender_man_radiobutton) "Homme" else "Femme"
                Log.d("EPF", "Genre: ${gender}")
            Log.d("TAG", "Niveau : ${levelSpinner.selectedItem}")

            Toast.makeText(this, R.string.add_client_message, Toast.LENGTH_LONG).show()
            Toast.makeText(this, "tst 2", Toast.LENGTH_LONG).show()

            finish()
        }

    }
}