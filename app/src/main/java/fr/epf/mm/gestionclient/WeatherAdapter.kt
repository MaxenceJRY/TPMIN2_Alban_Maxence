import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.epf.mm.gestionclient.R
import fr.epf.mm.gestionclient.WeatherObservation

class WeatherAdapter(
    private val observations: List<WeatherObservation>,
) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stationNameTextView: TextView = itemView.findViewById(R.id.station_name_textview)
        val temperatureTextView: TextView = itemView.findViewById(R.id.temperature_textview)
        val humidityTextView: TextView = itemView.findViewById(R.id.humidity_textview)
        val cloudsImageView: ImageView = itemView.findViewById(R.id.clouds_imageview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.weather_view, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val observation = observations[position]
        holder.stationNameTextView.text = observation.stationName
        holder.temperatureTextView.text = observation.temperature + "°C"
        holder.humidityTextView.text = observation.humidity

        val cloudImageResId = when (observation.clouds) {
            "clear sky", "clouds and visibility OK", "no clouds detected", "nil significant cloud" -> R.drawable.icone_soleil
            "few clouds", "scattered clouds" -> R.drawable.nuage
            "broken clouds", "overcast" -> R.drawable.plui
            else -> R.drawable.erreur_404
        }
        holder.cloudsImageView.setImageResource(cloudImageResId)
    }

    override fun getItemCount() = observations.size
}
