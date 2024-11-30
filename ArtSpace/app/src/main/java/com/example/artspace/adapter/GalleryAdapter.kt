package com.example.artspace.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.artspace.R
import com.example.artspace.databinding.ItemArtworkBinding
import com.example.artspace.model.ArtModel

class GalleryAdapter(
    private val artList: List<ArtModel>,
    private val onItemClick: (ArtModel) -> Unit
) : RecyclerView.Adapter<GalleryAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val binding: ItemArtworkBinding) : RecyclerView.ViewHolder(binding.root) {
        val artworkImage = binding.itemArtImage
        val artworkName = binding.itemArtTitle
        val artworkAuthor = binding.itemArtAuthor
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemArtworkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return artList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val artItem = artList[position]
        Log.d("GalleryAdapter", "Cargando imagen desde URL: ${artItem.webImage?.url}")
        // Cargar la imagen desde la URL usando Glide
        Glide.with(holder.itemView.context)
            .load(artItem.webImage?.url) // Usar art.webImage?.url
            .override(800, 600)  // Redimensiona la imagen a 800x600, pero mantiene la proporción
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false)
            .placeholder(R.drawable.loading)
            .error(R.drawable.loading)
            .into(holder.artworkImage)
        /*
        Glide.with(holder.itemView.context)
            .load("qEnlrp5MyHgLIVvrQR3HYtMBhQaLsxCmhBB15DCxX07l_rAvKqjKAXgCkgigYYxA2hGls9riG6Xfn_K_V5_GMfd_0bE") // URL de prueba
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.loading)
            .error(R.drawable.loading)
            .into(holder.artworkImage)*/

        holder.artworkName.text = artItem.title
        holder.artworkAuthor.text = artItem.author

        holder.itemView.setOnClickListener {
            onItemClick(artItem)
        }
    }
}
