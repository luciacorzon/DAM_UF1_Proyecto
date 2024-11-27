package com.example.artspace.adapter

import android.print.PrintAttributes.Margins
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.recyclerview.widget.RecyclerView
import com.example.artspace.MainMenuFragment
import com.example.artspace.R
import com.example.artspace.model.MenuItem

class MainMenuAdapter(
    private val context: MainMenuFragment,
    private val dataset: List<MenuItem>,
    private val onItemClick: (MenuItem) -> Unit // AÑADIDO
) : RecyclerView.Adapter<MainMenuAdapter.ItemViewHolder>() {
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.menuItemText)
        val imageView: ImageView = view.findViewById(R.id.menuItemImage)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_main_menu, parent, false)
        return ItemViewHolder(adapterView)
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