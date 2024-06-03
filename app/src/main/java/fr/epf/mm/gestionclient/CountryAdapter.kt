package fr.epf.mm.gestionclient

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epf.mm.gestionclient.model.Country

interface OnCountryClickListener {
    fun onCountryClick(country: Country)
}
class ClientAdapter(private val countries: List<Country>, private val listener: OnCountryClickListener) : RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flagImageview: ImageView = itemView.findViewById(R.id.flag_view_imageview)
        val nameTextView: TextView = itemView.findViewById(R.id.country_view_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.country_view, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val country = countries[position]
        holder.nameTextView.text = country.name

        Glide.with(holder.itemView.context)
            .load(country.flag)
            .into(holder.flagImageview)

        holder.itemView.setOnClickListener {
            listener.onCountryClick(country)
        }
    }

    override fun getItemCount() = countries.size
}



