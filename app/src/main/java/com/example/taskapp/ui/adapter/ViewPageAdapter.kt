package com.example.taskapp.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.ArrayList

class ViewPageAdapter(fragmentActivity: FragmentActivity):
    FragmentStateAdapter(fragmentActivity) {

    private val fragmentlist: MutableList<Fragment> = ArrayList()
    private  val titleList: MutableList<Int> = ArrayList()

    fun getTitle(position: Int): Int {
        return titleList[position]
    }

    fun addFragment(fragment: Fragment, title: Int) {
        fragmentlist.add(fragment)
        titleList.add(title)
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentlist[position]
    }

    override fun getItemCount(): Int {
        return fragmentlist.size
    }
}