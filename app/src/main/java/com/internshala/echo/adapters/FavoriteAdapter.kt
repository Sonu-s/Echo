package com.internshala.echo.adapters

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.internshala.echo.R
import com.internshala.echo.Songs
import com.internshala.echo.fragments.SongPlayingFragment

/**
 * Created by user on 1/23/2018.
 */
class FavoriteAdapter(_songsDetails:ArrayList<Songs>, _context: Context): RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>(){

    var songDetails:ArrayList<Songs>?=null
    var mContext: Context?=null
    init {
        this.songDetails=_songsDetails
        this.mContext=_context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val songObject=songDetails?.get(position)
        holder.trackTitle?.text=songObject?.songTitle
        holder.trackTitle?.text=songObject?.artist
        holder.contentHolder?.setOnClickListener({
            val songPlayingFragment= SongPlayingFragment()
            var args = Bundle()
            args.putString("songArtist",songObject?.artist)
            args.putString("path",songObject?.songData)
            args.putString("songTitle",songObject?.songTitle)
            args.putInt("SongId",songObject?.songID?.toInt() as Int)
            args.putInt("songPosition",position)
            args.putParcelableArrayList("songData",songDetails)
            songPlayingFragment.arguments=args


            (mContext as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.detail_fragment,songPlayingFragment)
                    .addToBackStack("SongPlyingFragmentFavorite")
                    .commit()
        })

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.row_custom_mainscreen_adapter,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        if(songDetails==null){
            return 0

        }else{
            return (songDetails as ArrayList<Songs> ). size
        }

    }


    class MyViewHolder(View: View): RecyclerView.ViewHolder(View){
        var trackTitle: TextView?=null
        var trackArtist: TextView?=null
        var contentHolder: RelativeLayout?=null

        init {
            trackTitle = View.findViewById<TextView>(R.id.trackTitle)
            trackArtist = View.findViewById<TextView>(R.id.trackArtist)
            contentHolder= View.findViewById<RelativeLayout>(R.id.contentRow)
        }
    }

}