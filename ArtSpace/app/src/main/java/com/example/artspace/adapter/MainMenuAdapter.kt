package com.example.artspace.adapter

import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.artspace.R
import com.example.artspace.view.MainMenuFragment
import com.example.artspace.databinding.ItemMainMenuBinding
import com.example.artspace.model.MenuItem

class MainMenuAdapter(
    private val context: MainMenuFragment,
    private val dataset: List<MenuItem>,
    private val onItemClick: (MenuItem) -> Unit
) : RecyclerView.Adapter<MainMenuAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ItemMainMenuBinding) : RecyclerView.ViewHolder(binding.root)

    private lateinit var typeface: Typeface

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemMainMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        typeface = ResourcesCompat.getFont(parent.context, R.font.montserrat_medium) ?: Typeface.DEFAULT

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.binding.menuItemText.apply {
            text = context.resources.getString(item.nameRes).uppercase()
            setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.white))
            textSize = 35f
            gravity = Gravity.BOTTOM or Gravity.START
            setPadding(paddingLeft, 200, paddingRight, paddingBottom)
            setTypeface(typeface, Typeface.BOLD)
            setShadowLayer(4f, 2f, 2f, ContextCompat.getColor(holder.binding.root.context, R.color.darkGrey))
        }

        holder.binding.menuItemImage.apply {
            setImageResource(item.imageRes)

            val colorOverlay = ContextCompat.getColor(holder.binding.root.context, R.color.overlay_color)
            setColorFilter(colorOverlay, android.graphics.PorterDuff.Mode.OVERLAY)
            imageAlpha = 225
        }

        val layoutParams = holder.itemView.layoutParams
        layoutParams?.height = 600
        holder.itemView.layoutParams = layoutParams

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = dataset.size
}
