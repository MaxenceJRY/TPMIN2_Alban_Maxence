package fr.epf.mm.gestionclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epf.mm.gestionclient.model.Country

//public class ClientViewHolder extends RecyclerView.ViewHolder{
//
//    public ClientViewHolder(View view){
//        super(view)
//    }

const val CLIENT_ID_EXTRA = "clientId"

class ClientViewHolder(view : View) : RecyclerView.ViewHolder(view)


class ClientAdapter(private val clients: List<Country>) : RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flagImageview: ImageView = itemView.findViewById(R.id.flag_view_imageview)
        val nameTextView: TextView = itemView.findViewById(R.id.country_view_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.country_view, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]
        holder.nameTextView.text = client.name

        Glide.with(holder.itemView.context)
            .load(client.flag)
            .into(holder.flagImageview)
    }

    override fun getItemCount() = clients.size
}



