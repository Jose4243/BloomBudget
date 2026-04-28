package com.ST10447412.bloombudget

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class IconAdapter(private val icons: List<Int>, private val onSelected: (Int) -> Unit) :
    RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(180, 180) // Size for your custom icons
            setPadding(15, 15, 15, 15)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val iconRes = icons[position]
        val imageView = holder.itemView as ImageView
        imageView.setImageResource(iconRes)

        // Highlight the box when an icon is tapped
        if (selectedPosition == position) {
            imageView.setBackgroundColor(Color.parseColor("#E0E0E0"))
        } else {
            imageView.background = null
        }

        imageView.setOnClickListener {
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
            onSelected(iconRes)
        }
    }

    override fun getItemCount() = icons.size

    class IconViewHolder(view: View) : RecyclerView.ViewHolder(view)
}