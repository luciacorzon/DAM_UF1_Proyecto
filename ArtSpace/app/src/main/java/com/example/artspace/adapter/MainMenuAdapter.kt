package com.example.artspace.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.artspace.view.MainMenuFragment
import com.example.artspace.R
import com.example.artspace.databinding.ItemMainMenuBinding
import com.example.artspace.model.MenuItem

class MainMenuAdapter(
    private val context: MainMenuFragment,
    private val dataset: List<MenuItem>,
    private val onItemClick: (MenuItem) -> Unit
) : RecyclerView.Adapter<MainMenuAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val binding: ItemMainMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        val textView = binding.menuItemText
        val imageView = binding.menuItemImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemMainMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val view = dataset[position]

        holder.textView.text = context.resources.getString(view.nameRes)
        holder.imageView.setImageResource(view.imageRes)

        if (position % 2 == 0) {
            holder.textView.gravity = Gravity.END
            val blackColor = ContextCompat.getColor(holder.itemView.context, R.color.black)
            holder.textView.setTextColor(blackColor)
        } else {
            holder.textView.gravity = Gravity.START
        }

        holder.itemView.setOnClickListener {
            onItemClick(view)
        }
    }
}
