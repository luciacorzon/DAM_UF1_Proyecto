package com.example.artspace.adapter

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

        Glide.with(holder.itemView.context)
            .load(artItem.webImage?.url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false)
            .placeholder(R.drawable.loading)
            .error(R.drawable.loading)
            .into(holder.artworkImage)


        holder.artworkName.text = artItem.title
        holder.artworkAuthor.text = artItem.author

        holder.itemView.setOnClickListener {
            onItemClick(artItem)
        }
    }
}
