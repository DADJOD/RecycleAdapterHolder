package com.example.recycleadapterholder

import android.view.MenuItem
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class SaintHolder(
    itemView: View,
    private val adapter: RecyclerSaintAdapter,
    private val clickListener: RecyclerSaintAdapter.OnItemClickListener?,
    private val longClickListener: RecyclerSaintAdapter.OnItemLongClickListener
) : RecyclerView.ViewHolder(itemView) {
    var name: TextView
    var dob: TextView
    var dod: TextView
    var bar: RatingBar
    var button: ImageView

    init {
        name = itemView.findViewById<View>(R.id.text) as TextView
        dob = itemView.findViewById<View>(R.id.dob) as TextView
        dod = itemView.findViewById<View>(R.id.dod) as TextView
        bar = itemView.findViewById<View>(R.id.rating) as RatingBar
        button = itemView.findViewById<View>(R.id.threedots) as ImageView
        button.setOnClickListener { view ->
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                showPopupMenu(view, position)
            }
        }
        bar.onRatingBarChangeListener =
            OnRatingBarChangeListener { ratingBar, newRating, b ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    adapter.ratingChanged(position, newRating)
                }
            }
        itemView.setOnClickListener { // Triggers click upwards to the adapter on click
            if (clickListener != null) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(itemView, position)
                }
            }
        }
        itemView.setOnLongClickListener(OnLongClickListener {
            if (clickListener != null) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    longClickListener.onItemLongClick(itemView, position)
                }
                return@OnLongClickListener true
            }
            false
        })
    }

    private fun showPopupMenu(view: View, pos: Int) {
        val context = view.context
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.context)
        popupMenu
            .setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    when (item.itemId) {
                        R.id.context_delete -> {
                            adapter.remove(pos)
                            return true
                        }
                    }
                    return false
                }
            })
        popupMenu.show()
    }
}
