package com.internshala.echo.activities

import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.internshala.echo.R
import com.internshala.echo.adapters.NavigationDrawerAdapter
import com.internshala.echo.fragments.MainScreenFragment

class MainActivity : AppCompatActivity() {

//    private lateinit var fusedLocationClient: FusedLocationProviderClient

    var navigationDrawerIconList:ArrayList<String> = arrayListOf()
    var images_for_navdrawer= intArrayOf(R.drawable.navigation_allsongs,R.drawable.navigation_favorites,
            R.drawable.navigation_settings,R.drawable.navigation_aboutus)
    object Statified{
        var drawerLayout:DrawerLayout?=null

    }






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar=findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        MainActivity.Statified.  drawerLayout=findViewById(R.id.drawer_layout)

        navigationDrawerIconList.add("All Songs")
        navigationDrawerIconList.add("Favorotes")
        navigationDrawerIconList.add("Setting")
        navigationDrawerIconList.add("About Us")

      //  fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)





        var toggle=ActionBarDrawerToggle(this@MainActivity, MainActivity.Statified.drawerLayout,toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        MainActivity.Statified.drawerLayout?.setDrawerListener(toggle)
        toggle.syncState()
        var mainscreenFragment= MainScreenFragment()
        this.supportFragmentManager
                .beginTransaction()
                .add(R.id.detail_fragment,mainscreenFragment,"MainScreenFragment")
                .commit()

        var _navigationAdapter=NavigationDrawerAdapter(navigationDrawerIconList, images_for_navdrawer,this)
                _navigationAdapter.notifyDataSetChanged()


        var navgation_recycler_view=findViewById<RecyclerView>(R.id.navigation_recycler_view)
        navgation_recycler_view.layoutManager=LinearLayoutManager(this)
        navgation_recycler_view.itemAnimator=DefaultItemAnimator()
        navgation_recycler_view.adapter= _navigationAdapter
        navgation_recycler_view.setHasFixedSize(true)


    }

    override fun onStart() {
        super.onStart()
    }

}

