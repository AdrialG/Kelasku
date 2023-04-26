package com.kelompokempat.kelasku.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.crocodic.core.base.adapter.CoreListAdapter
import com.kelompokempat.kelasku.R

class CustomAdapter<VB: ViewDataBinding, T: Any?>(private var layoutRes: Int, private val context: Context) :  CoreListAdapter<VB, T>(layoutRes) {

    override fun onBindViewHolder(holder: ItemViewHolder<VB, T>, position: Int) {
        super.onBindViewHolder(holder, position)
        setAnimation(holder.itemView, position)
    }

//    private val context: Context
//    // Allows to remember the last item shown on screen
    private var lastPosition = -1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var text: TextView
//
//        // You need to retrieve the container (ie the root ViewGroup from your custom_item_layout)
//        // It's the view that will be animated
//        var container: FrameLayout
//
//        init {
//            container = itemView.findViewById(R.id.item_layout_container)
//            text = itemView.findViewById(R.id.item_layout_text)
//        }
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.home_item_start_anim)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

}
