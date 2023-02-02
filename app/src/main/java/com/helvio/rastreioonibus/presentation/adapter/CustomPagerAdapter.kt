package com.helvio.rastreioonibus.presentation.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.PagerAdapter
import com.helvio.rastreioonibus.R

class CustomPagerAdapter(
    context: Context,
    private val layouts: Array<ViewBinding>
) : PagerAdapter() {

    private val tabTitles: Array<String> =
        context.resources.getStringArray(R.array.tab_names)

    override fun getCount() = layouts.size

    override fun isViewFromObject(view: View, `object`: Any) = view === `object`

    override fun getPageTitle(position: Int) = tabTitles[position]

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = layouts[position].root
        container.addView(view)
        return view
    }
}