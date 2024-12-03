package com.example.artspace.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.artspace.R
import com.example.artspace.view.SecondaryMenuFragment
import com.example.artspace.model.MenuItem

class SecondaryMenuAdapter(
    private val context: SecondaryMenuFragment,
    private val dataset: List<MenuItem>,
    private val onMenuItemClick: (String) -> Unit
) : RecyclerView.Adapter<SecondaryMenuAdapter.ItemViewHolder>() {
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.secItemText)
        val imageView: ImageView = view.findViewById(R.id.secItemImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_secondary_menu, parent, false)
        return ItemViewHolder(adapterView)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val view = dataset[position]
        holder.textView.text = context.resources.getString(view.nameRes)
        holder.imageView.setImageResource(view.imageRes)

        holder.itemView.setOnClickListener {
            onMenuItemClick(context.resources.getString(view.nameRes))
        }
    }
}

