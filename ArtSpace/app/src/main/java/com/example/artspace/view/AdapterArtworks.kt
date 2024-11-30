package com.example.artspace.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.artspace.R
import com.example.artspace.core.Constants
import com.example.artspace.model.ArtModel

class AdapterArtworks(
    val context: Context,
    var artList: List<ArtModel>
): RecyclerView.Adapter<AdapterArtworks.ViewHolder>()
{
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cvArt: LinearLayout = itemView.findViewById(R.id.item_artwork_card)
        val ivImage: ImageView = itemView.findViewById(R.id.itemArtImage)
        val tvText: TextView = itemView.findViewById(R.id.art_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.item_artwork, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return artList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val art = artList[position]

        //Imagen
        Glide.with(holder.itemView.context)
            .load(art.webImage?.url) // Usar art.webImage?.url
          //  .override(Constants.IMAGE_WIDTH, Constants.IMAGE_HEIGHT)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.perla)
            .error(R.drawable.perla)
            .into(holder.ivImage)

        //Info texto
        holder.tvText.text = art.title

        //Eventos
        holder.cvArt.setOnClickListener{
            showOverview(art.title, art.author)
        }
    }

    private fun showOverview(title: String, overview: String){
        val builder  =AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(overview)
        builder.show() //MIN 29:23
    }
}