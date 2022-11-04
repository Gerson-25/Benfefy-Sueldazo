package com.syntepro.sueldazo.ui.extras.adapter

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.entity.app.ScreenItem
import java.util.*

class IntroViewPagerAdapter(private val mListScreen: List<ScreenItem>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = container.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutScreen = Objects.requireNonNull(inflater).inflate(R.layout.layout_screen, null)
        val imgSlide = layoutScreen.findViewById<ImageView>(R.id.image_tutorial)
        imgSlide.setImageBitmap(decodeSampledBitmapFromResource(container.context.resources, mListScreen[position].screenImg))
        container.addView(layoutScreen)
        return layoutScreen
    }

    override fun getCount(): Int { return mListScreen.size }

    override fun isViewFromObject(view: View, o: Any): Boolean { return view === o }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) { container.removeView(`object` as View) }

    companion object {
        private fun decodeSampledBitmapFromResource(res: Resources, resId: Int): Bitmap {
            // First decode with inJustDecodeBounds=true to check dimensions
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeResource(res, resId, options)

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options)

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeResource(res, resId, options)
        }

        private fun calculateInSampleSize(options: BitmapFactory.Options): Int {
            // Raw height and width of image
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1
            if (height > 400 || width > 400) {
                val halfHeight = height / 2
                val halfWidth = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize > 400
                        && halfWidth / inSampleSize > 400) {
                    inSampleSize *= 2
                }
            }
            return inSampleSize
        }
    }
}