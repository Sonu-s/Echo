package com.internshala.echo.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.internshala.echo.R
import com.internshala.echo.Songs
import com.internshala.echo.adapters.FavoriteAdapter
import com.internshala.echo.databases.EchoDatabase
import kotlinx.android.synthetic.main.fragment_favroid.*


/**
 * A simple [Fragment] subclass.
 */
class FavroidFragment : Fragment() {
    var myActivity:Activity?=null
    var getSongsList: ArrayList<Songs>?=null

    var noFavorites:TextView?=null
    var nowPlayingBottomBar :RelativeLayout?=null
    var playPauseButton : ImageButton?=null
    var songTitle : TextView?=null
    var recyclerView: RecyclerView?=null
    var trackPosition: Int=0
    var favoriteContent:EchoDatabase?=null

    var refreshList: ArrayList<Songs>?=null
    var getListfromDatabase:ArrayList<Songs>?=null

    object  Statified{
        var mediaPlayer: MediaPlayer?=null
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater!!.inflate(R.layout.fragment_favroid, container, false)

        noFavorites=view?.findViewById(R.id.noFavorites)
        nowPlayingBottomBar=view.findViewById(R.id.hiddenBarFavScreen)
        songTitle=view.findViewById(R.id.songTitle)
        playPauseButton=view.findViewById(R.id.PlayPauseButton)
        recyclerView=view.findViewById(R.id.favoriteRecycler)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity =context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity= Activity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favoriteContent= EchoDatabase(myActivity)
        getSongsList=getSongsFromPhone()
        if (getSongsList==null){
            recyclerView?.visibility= View.INVISIBLE
            noFavorites?.visibility= View.VISIBLE

        }else{
            var favoriteAdapter= FavoriteAdapter(getSongsList as ArrayList<Songs>, myActivity as Context)
            val mLayoutManager= LinearLayoutManager(activity)
            recyclerView?.layoutManager= mLayoutManager
            recyclerView?.itemAnimator=DefaultItemAnimator()
            recyclerView?.adapter= favoriteAdapter
            recyclerView?.setHasFixedSize(true)

        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
    }

    fun getSongsFromPhone():ArrayList<Songs>{
        var arrayList= java.util.ArrayList<Songs>()
        var contentResolve = myActivity?.contentResolver
        var songUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor=contentResolve?.query(songUri,null,null,null,null)
        if(songCursor!=null && songCursor.moveToFirst()){
            val songID =songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTItle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist=songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData=songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex=songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while(songCursor.moveToNext()){
                var currentID=songCursor.getLong(songID)
                var currentTitle=songCursor.getString(songTItle)
                var currentArtist=songCursor.getString(songArtist)
                var currentData=songCursor.getString(songData)
                var currentDate=songCursor.getLong(dateIndex)
                arrayList.add(Songs(currentID,currentTitle,currentArtist,currentData,currentDate))
            }
        }
        return arrayList
    }
    fun bottomBarSetup(){
        try {
            bottomBarClickHandler()
            songTitle?.setText(SongPlayingFragment.Stattified.currentSongHelper?.songTitle)
            SongPlayingFragment.Stattified.mediaPlayer?.setOnCompletionListener({
                songTitle?.setText(SongPlayingFragment.Stattified.currentSongHelper?.songTitle)
                SongPlayingFragment.Staticated.onSongComplete()

            })
            if (SongPlayingFragment.Stattified.mediaPlayer?.isPlaying as Boolean){
                nowPlayingBottomBar?.visibility = View.VISIBLE

            }else{
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun bottomBarClickHandler(){
        nowPlayingBottomBar?.setOnClickListener({
            Statified.mediaPlayer=SongPlayingFragment.Stattified.mediaPlayer

            val songPlayingFragment= SongPlayingFragment()
            var args = Bundle()
            args.putString("songArtist",SongPlayingFragment.Stattified.currentSongHelper?.songArtist)
            args.putString("path",SongPlayingFragment.Stattified.currentSongHelper?.songPath)
            args.putString("songTitle",SongPlayingFragment.Stattified.currentSongHelper?.songTitle)
            args.putInt("SongId",SongPlayingFragment.Stattified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition",SongPlayingFragment.Stattified.currentSongHelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songData",SongPlayingFragment.Stattified.fetchSongs)
            args.putString("FavBottomBar","success")

            songPlayingFragment.arguments=args

            fragmentManager.beginTransaction()
                    .replace(R.id.detail_fragment , songPlayingFragment)
                    .addToBackStack("SongPlayingFragment")
                    .commit()
        })

        playPauseButton?.setOnClickListener({
            if(SongPlayingFragment.Stattified.mediaPlayer?.isPlaying as Boolean){

                SongPlayingFragment.Stattified.mediaPlayer?.pause()
                trackPosition=SongPlayingFragment.Stattified.mediaPlayer?.currentPosition as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }else{
                SongPlayingFragment.Stattified.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.Stattified.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)

            }
        })

    }
    fun display_favorites_by_searching(){
        if (favoriteContent?.checkSize() as Int> 0){
            refreshList = ArrayList<Songs>()
            getListfromDatabase= favoriteContent?.queryDBList()
            var fetxhListfromDevice = getSongsFromPhone()
            if (fetxhListfromDevice !=null){
                for (i in 0..fetxhListfromDevice?.size-1){
                    for (j in 0..getListfromDatabase?.size as Int -1){
                        if ((getListfromDatabase?.get(j)?.songID)===(fetxhListfromDevice?.get(i)?.songID)){
                            refreshList?.add((getListfromDatabase as ArrayList<Songs>)[j])
                        }
                    }
                }
            }else{

            }


        }

    }

}// Required empty public constructor
