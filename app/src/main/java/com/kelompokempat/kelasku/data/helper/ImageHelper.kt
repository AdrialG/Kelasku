package com.kelompokempat.kelasku.data.helper

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.kelompokempat.kelasku.R

class ImageHelper {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["imageUrl"], requireAll = false)
        fun loadImageRecipe(view: ImageView, imageUrl: String?) {

            view.setImageDrawable(null)

            imageUrl?.let {
                Glide
                    .with(view.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.default_pfp)
                    .error(R.drawable.baseline_error_24)
                    .into(view)

            }

        }

    }

}

class ImageBannerHelper {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["imageBannerUrl"], requireAll = false)
        fun loadImageRecipe(view: ImageView, imageUrl: String?) {

            view.setImageDrawable(null)

            imageUrl?.let {
                Glide
                    .with(view.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.default_banner)
                    .error(R.drawable.baseline_error_24)
                    .into(view)

            }

        }

    }

}