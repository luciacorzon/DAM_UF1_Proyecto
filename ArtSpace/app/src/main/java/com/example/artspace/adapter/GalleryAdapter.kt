package com.example.artspace.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.artspace.GalleryFragment
import com.example.artspace.R
import com.example.artspace.databinding.ItemArtworkBinding
import com.example.artspace.decorations.BottomBorderDecoration
import com.example.artspace.model.ArtworkItem

class GalleryAdapter(
    private val artworkList: List<ArtworkItem>,
    private val onItemClick: (ArtworkItem) -> Unit // Callback para manejar el clic
) : RecyclerView.Adapter<GalleryAdapter.ItemViewHolder>() {

    // ViewHolder que usa ViewBinding
    class ItemViewHolder(private val binding: ItemArtworkBinding) : RecyclerView.ViewHolder(binding.root) {
        val artworkImage = binding.itemArtImage
        val artworkName = binding.itemArtTitle
        val artworkAuthor = binding.itemArtAuthor
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // Inflamos el layout usando ViewBinding
        val binding = ItemArtworkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return artworkList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val artworkItem = artworkList[position]
        holder.artworkImage.setImageResource(artworkItem.artImageRes)
        holder.artworkName.text = holder.itemView.context.getString(artworkItem.artNameRes)
        holder.artworkAuthor.text = holder.itemView.context.getString(artworkItem.artAuthorRes)

        // Establecer el clic en el ítem
        holder.itemView.setOnClickListener {
            onItemClick(artworkItem) // Llamar al callback cuando se hace clic
        }
    }
}
