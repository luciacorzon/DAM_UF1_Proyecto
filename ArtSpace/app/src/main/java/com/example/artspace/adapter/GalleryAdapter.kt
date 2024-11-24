package com.example.artspace.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.artspace.GalleryFragment
import com.example.artspace.R
import com.example.artspace.model.ArtworkItem

class GalleryAdapter(private val artworkList: List<ArtworkItem>) :
    RecyclerView.Adapter<GalleryAdapter.ItemViewHolder>(){
    class ItemViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val artworkImage: ImageView = itemView.findViewById(R.id.itemArtImage)
        val artworkName: TextView = itemView.findViewById(R.id.itemArtTitle)
        val artworkAuthor: TextView = itemView.findViewById(R.id.itemArtAuthor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_artwork, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return artworkList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val artworkItem = artworkList[position]
        holder.artworkImage.setImageResource(artworkItem.artImageRes)
        holder.artworkName.text = holder.itemView.context.getString(artworkItem.artNameRes)
        holder.artworkAuthor.text = holder.itemView.context.getString(artworkItem.artAuthorRes)

    }
}