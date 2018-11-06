package com.internshala.echo.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.internshala.echo.R
import com.internshala.echo.activities.MainActivity
import com.internshala.echo.fragments.AboutUsFragment
import com.internshala.echo.fragments.FavroidFragment
import com.internshala.echo.fragments.MainScreenFragment
import com.internshala.echo.fragments.SetttingFragment

/**
 * Created by user on 1/16/2018.
 */
class NavigationDrawerAdapter(_contentList:ArrayList<String>,_getImages:IntArray,_context:Context)
    :RecyclerView.Adapter<NavigationDrawerAdapter.NaviewHolder>(){
    var contentList:ArrayList<String>?=null
    var getImages:IntArray?=null
    var mcontext:Context?=null
    init{
        this.contentList=_contentList
        this.getImages=_getImages
        this.mcontext=_context
    }
    override fun onBindViewHolder(holder: NaviewHolder?, position: Int) {
        holder?.icon_GET?.setBackgroundColor(getImages?.get(position ) as Int)
        holder?.text_GET?.setText(contentList?.get(position))
        holder?.contentHolder?.setOnClickListener({
            if (position==0){
                var mainScreenFragment=MainScreenFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.detail_fragment,mainScreenFragment)
                        .commit()
            }else if(position==1)
            {
                var favoriteFragment=FavroidFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.detail_fragment,favoriteFragment)
                        .commit()
            }else if(position==2){
                var settingsFragment=SetttingFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.detail_fragment,settingsFragment)
                        .commit()
            }else{
                var aboutUsFragment=AboutUsFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.detail_fragment,aboutUsFragment)
                        .commit()
            }
            MainActivity.Statified.drawerLayout?.closeDrawers()
        })

    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): NaviewHolder {
       var itemView=LayoutInflater.from(parent?.context)
               .inflate(R.layout.row_custom_navigation,parent,false)
        val returnThis=NaviewHolder(itemView)
        return returnThis
    }

    override fun getItemCount( ): Int {
        return contentList?.size as Int
    }

    class NaviewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var icon_GET : ImageView?=null
        var text_GET:TextView?=null
        var contentHolder:RelativeLayout?=null
        init{
            icon_GET=itemView?.findViewById(R.id.icon_navdrawer)
            text_GET=itemView?.findViewById(R.id.text_navdrawer)
            contentHolder=itemView?.findViewById(R.id.navdrawer_item_content_holder)
        }

    }
}