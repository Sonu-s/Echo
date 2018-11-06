package com.internshala.echo.fragments


import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.internshala.echo.CurrentSongHelper
import com.internshala.echo.R
import com.internshala.echo.Songs
import com.internshala.echo.databases.EchoDatabase
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 */
class SongPlayingFragment : Fragment() {

    object Stattified {
        var myActivity: Activity? = null
        var mediaPlayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playpauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var seekbar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var shuffleImageButton: ImageButton? = null

        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null
        var currentSongHelper: CurrentSongHelper? = null
        var audioVisualization: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null
        var fab: ImageButton? = null
        var favoriteContent:EchoDatabase?=null
        var mSensorManager:SensorManager?=null
        var mSensorListener:SensorEventListener?=null
        var MY_PREFS_NAME="ShakeFeature"

        var updateSongTime =object :Runnable{
            override fun run() {
                val getcurrent = Stattified.mediaPlayer?.currentPosition
                Stattified.startTimeText?.setText(String.format("%d:%d",

                        TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getcurrent?.toLong() as Long) -
                                TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long))))
                Stattified.seekbar?.setProgress(getcurrent?.toInt() as Int)

                Handler().postDelayed(this,1000)
            }
        }
    }






    object  Staticated{
        var My_PREFS_SHUFFLE="Shuffle feature"
        var MY_PRESS_LOOP="Loop feature"

        fun onSongComplete(){
            if( Stattified.currentSongHelper?.isShuffle as Boolean){
                playNext("playNextNormalShuffle")
                Stattified.currentSongHelper?.isPlaying=true
            }else{
                if( Stattified.currentSongHelper?.isLoop as Boolean){
                    Stattified. currentSongHelper?.isPlaying=true
                    var nextSong= Stattified.fetchSongs?.get( Stattified.currentPosition)

                    Stattified.currentSongHelper?.songTitle=nextSong?.songTitle
                    Stattified. currentSongHelper?.songPath=nextSong?.songData
                    Stattified. currentSongHelper?.currentPosition= Stattified.currentPosition
                    Stattified. currentSongHelper?.songId=nextSong?.songID as Long
                    updateTextView( Stattified.currentSongHelper?.songTitle as String, Stattified.currentSongHelper?.songArtist as String)


                    Stattified.mediaPlayer?.reset()
                    try {
                        Stattified. mediaPlayer?.setDataSource( Stattified.myActivity,Uri.parse( Stattified.currentSongHelper?.songPath))
                        Stattified.mediaPlayer?.prepare()
                        Stattified.mediaPlayer?.start()
                        processInformation( Stattified.mediaPlayer as MediaPlayer)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }


                }else{
                    playNext("playNextNormal")
                    Stattified.currentSongHelper?.isPlaying=true

                }
            }
            if ( Stattified.favoriteContent?.checkifIdExists( Stattified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
                Stattified. fab?.setImageDrawable(ContextCompat.getDrawable( Stattified.myActivity,R.drawable.favorite_on))
            }else{
                Stattified. fab?.setImageDrawable(ContextCompat.getDrawable( Stattified.myActivity,R.drawable.favorite_off))
            }
        }


        fun updateTextView(songtitle: String,songArtist: String){
            Stattified.songTitleView?.setText(songtitle)
            Stattified. songArtistView?.setText(songArtist)
        }
        fun processInformation(mediaPlayer:MediaPlayer){
            val finalTime=mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition
            Stattified. seekbar?.max=finalTime
            Stattified.startTimeText?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()))))
            Stattified. endTimeText?.setText(String.format("%d:%d",

                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()))))

            Stattified. seekbar?.setProgress(startTime)
            Handler().postDelayed( Stattified.updateSongTime,1000)
        }

        fun playNext(check:String){
            if (check.equals("playNextNormal",ignoreCase = true)){
                Stattified.currentPosition= Stattified.currentPosition+1

            }else if(check.equals("playNextNormalShuffle",ignoreCase = true)){
                var randomObject=Random()
                var randomPosition=randomObject.nextInt( Stattified.fetchSongs?.size?.plus(1)as Int)
                Stattified.currentPosition=randomPosition
            }
            if ( Stattified.currentPosition== Stattified.fetchSongs?.size){
                Stattified.currentPosition=0
            }
            Stattified. currentSongHelper?.isLoop=false
            var nextSong= Stattified.fetchSongs?.get( Stattified.currentPosition)
            Stattified. currentSongHelper?.songTitle=nextSong?.songTitle
            Stattified. currentSongHelper?.songPath=nextSong?.songData
            Stattified. currentSongHelper?.currentPosition= Stattified.currentPosition
            Stattified. currentSongHelper?.songId=nextSong?.songID as Long
            updateTextView( Stattified.currentSongHelper?.songTitle as String, Stattified.currentSongHelper?.songArtist as String)
            Stattified. mediaPlayer?.reset()

            try {
                Stattified. mediaPlayer?.setDataSource( Stattified.myActivity,Uri.parse( Stattified.currentSongHelper?.songPath))
                Stattified. mediaPlayer?.prepare()
                Stattified. mediaPlayer?.start()
                processInformation( Stattified.mediaPlayer as MediaPlayer)
            }catch (e:Exception){
                e.printStackTrace()
            }
            if ( Stattified.favoriteContent?.checkifIdExists( Stattified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
                Stattified.fab?.setImageDrawable(ContextCompat.getDrawable( Stattified.myActivity,R.drawable.favorite_on))
            }else{
                Stattified. fab?.setImageDrawable(ContextCompat.getDrawable( Stattified.myActivity,R.drawable.favorite_off))
            }

        }
    }

    var mAccelaration:Float= 0f
    var mAccelarationCurrent:Float=0f
    var mAccelarationLast:Float= 0f

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
       var view =inflater!!.inflate(R.layout.fragment_song_playing,container,false)
        setHasOptionsMenu(true)
       Stattified. seekbar=view?.findViewById(R.id.seekBar)
        Stattified.startTimeText=view?.findViewById(R.id.startTime)
        Stattified. endTimeText=view?.findViewById(R.id.endTime)
        Stattified. playpauseImageButton=view?.findViewById(R.id.PlayPauseButton)
        Stattified. previousImageButton=view?.findViewById(R.id.previousButtom)
        Stattified.nextImageButton=view?.findViewById(R.id.nextButtom)
        Stattified.loopImageButton=view?.findViewById(R.id.loopButtom)
        Stattified.songArtistView=view?.findViewById(R.id.songArtist)
        Stattified.songTitleView=view?.findViewById(R.id.songTitle)
        Stattified.shuffleImageButton=view?.findViewById(R.id.suffleButtom)
        Stattified.glView=view?.findViewById(R.id.visualizer_view)
        Stattified.fab= view?.findViewById(R.id.favoriteIcon)
        Stattified.fab?.alpha=0.8f

        return view

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Stattified.audioVisualization= Stattified.glView as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Stattified. myActivity=context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Stattified. myActivity=activity
    }

    override fun onResume() {
        super.onResume()
        Stattified. audioVisualization?.onResume()
        Stattified.mSensorManager?.registerListener(Stattified.mSensorListener,
                Stattified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        Stattified.audioVisualization?.onPause()

        Stattified.mSensorManager?.unregisterListener(Stattified.mSensorListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Stattified. audioVisualization?.release()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Stattified.mSensorManager=Stattified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelaration=0.0f
        mAccelarationCurrent=SensorManager.GRAVITY_EARTH
        mAccelarationLast = SensorManager.GRAVITY_EARTH
        bindShakeListener()




    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu,menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val iteam:MenuItem?=menu?.findItem(R.id.action_redirect)
        iteam?.isVisible=true
        val iteam2:MenuItem?=menu?.findItem(R.id.action_sort)
        iteam2?.isVisible=false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_redirect ->{
                Stattified.myActivity?.onBackPressed()
                return false
            }
        }
        return false
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Stattified. favoriteContent = EchoDatabase( Stattified.myActivity)
        Stattified. currentSongHelper= CurrentSongHelper()
        Stattified. currentSongHelper?.isPlaying=true
        Stattified. currentSongHelper?.isLoop=false
        Stattified. currentSongHelper?.isShuffle=false

        var path:String?=null
        var _songTitle:String?=null
        var _songArtist:String?=null
        var songId:Long=0
        try {
            path=arguments.getString("path")
             _songTitle=arguments.getString("songTitle")
             _songArtist=arguments.getString("songArtist")
             songId=arguments.getInt("SongId").toLong()
            Stattified.currentPosition= arguments.getInt("songPosition")
            Stattified. fetchSongs=arguments.getParcelableArrayList("songData")

            Stattified. currentSongHelper?.songPath=path
            Stattified. currentSongHelper?.songTitle=_songTitle
            Stattified. currentSongHelper?.songArtist=_songArtist
            Stattified.currentSongHelper?.songId=songId
            Stattified.currentSongHelper?.currentPosition= Stattified.currentPosition

            Staticated. updateTextView( Stattified.currentSongHelper?.songTitle as String, Stattified.currentSongHelper?.songArtist as String)

        }catch (e:Exception){
            e.printStackTrace()
        }

        var fromFavBottomBar = arguments.get("favBottomBar") as? String
        if(fromFavBottomBar !=null){
            Stattified.mediaPlayer=FavroidFragment.Statified.mediaPlayer
        }else {

            Stattified.mediaPlayer = MediaPlayer()
            Stattified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                Stattified.mediaPlayer?.setDataSource(Stattified.myActivity, Uri.parse(path))
                Stattified.mediaPlayer?.prepare()

            } catch (e: Exception) {
                e.printStackTrace()
            }
            Stattified.mediaPlayer?.start()
        }
        Staticated. processInformation( Stattified.mediaPlayer as MediaPlayer)

        if ( Stattified.currentSongHelper?.isPlaying as Boolean){
            Stattified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)

        }else{
            Stattified. playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        Stattified. mediaPlayer?.setOnCompletionListener {
            Staticated. onSongComplete()
        }
        clickHandler()
        var visualizationHandler=DbmHandler.Factory.newVisualizerHandler( Stattified.myActivity as Context,0)
        Stattified. audioVisualization?.linkTo(visualizationHandler)
        var prefsForShuffle= Stattified.myActivity?.getSharedPreferences(Staticated.My_PREFS_SHUFFLE,Context.MODE_PRIVATE)
        var isShuffleAllowed=prefsForShuffle?.getBoolean("feature",false)
        if (isShuffleAllowed as Boolean){
            Stattified. currentSongHelper?.isShuffle=true
            Stattified.currentSongHelper?.isLoop=false
            Stattified. shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            Stattified. loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

        }else{
            Stattified. currentSongHelper?.isShuffle=false
            Stattified. shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        var prefsForLoop= Stattified.myActivity?.getSharedPreferences( Staticated.My_PREFS_SHUFFLE,Context.MODE_PRIVATE)
        var isLoopAllowed=prefsForLoop?.getBoolean("feature",false)
        if (isLoopAllowed as Boolean){
            Stattified. currentSongHelper?.isShuffle=false
            Stattified.currentSongHelper?.isLoop=true
            Stattified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            Stattified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)

        }else{
            Stattified.currentSongHelper?.isLoop=false
            Stattified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }
        if ( Stattified.favoriteContent?.checkifIdExists( Stattified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
            Stattified.fab?.setImageDrawable(ContextCompat.getDrawable( Stattified.myActivity,R.drawable.favorite_on))
        }else{
            Stattified. fab?.setImageDrawable(ContextCompat.getDrawable( Stattified.myActivity,R.drawable.favorite_off))
        }

    }
    fun clickHandler(){

        Stattified.  fab?.setOnClickListener({
            if ( Stattified.favoriteContent?.checkifIdExists( Stattified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
                Stattified.fab?.setImageDrawable(ContextCompat.getDrawable( Stattified.myActivity,R.drawable.favorite_off))
                Stattified.favoriteContent?.deleteFavourite( Stattified.currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText( Stattified.myActivity,"Remove from favorites",Toast.LENGTH_SHORT).show()
            }else{
                Stattified.fab?.setImageDrawable(ContextCompat.getDrawable( Stattified.myActivity,R.drawable.favorite_on))
                Stattified.favoriteContent?.storeAsFavorite( Stattified.currentSongHelper?.songId?.toInt(), Stattified.currentSongHelper?.songArtist,
                        Stattified.currentSongHelper?.songTitle, Stattified.currentSongHelper?.songPath)
                Toast.makeText( Stattified.myActivity,"Added to favorite", Toast.LENGTH_SHORT).show()
            }
        })
        Stattified. shuffleImageButton?.setOnClickListener({
            var editorShuffle= Stattified.myActivity?.getSharedPreferences( Staticated.My_PREFS_SHUFFLE,Context.MODE_PRIVATE)?.edit()
            var editorLoop= Stattified.myActivity?.getSharedPreferences( Staticated.MY_PRESS_LOOP,Context.MODE_PRIVATE)?.edit()

            if ( Stattified.currentSongHelper?.isShuffle as Boolean){
                Stattified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                Stattified.currentSongHelper?.isShuffle=false
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
            }else{
                Stattified.currentSongHelper?.isShuffle=true
                Stattified. currentSongHelper?.isLoop=false
                Stattified. shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                Stattified. loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature",true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()
            }

        })
        Stattified. nextImageButton?.setOnClickListener({
            Stattified. currentSongHelper?.isPlaying=true
            Stattified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            if ( Stattified.currentSongHelper?.isShuffle as Boolean){
                Staticated. playNext("playNextNormalShuffle")
            }else{
                Staticated.  playNext("playNextNormal")
            }

        })
        Stattified. previousImageButton?.setOnClickListener({
            Stattified.currentSongHelper?.isPlaying=true
            if ( Stattified.currentSongHelper?.isLoop as Boolean){
                Stattified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPrevious()
        })
        Stattified. loopImageButton?.setOnClickListener({
            var editorShuffle= Stattified.myActivity?.getSharedPreferences( Staticated.My_PREFS_SHUFFLE,Context.MODE_PRIVATE)?.edit()
            var editorLoop= Stattified.myActivity?.getSharedPreferences( Staticated.MY_PRESS_LOOP,Context.MODE_PRIVATE)?.edit()
            if ( Stattified.currentSongHelper?.isLoop as Boolean) {
                Stattified.currentSongHelper?.isLoop=false
                Stattified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()


            }else{
                Stattified.currentSongHelper?.isLoop=true
                Stattified.currentSongHelper?.isShuffle=false
                Stattified.  loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                Stattified. shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature",true)
                editorLoop?.apply()
            }
            })

        Stattified.playpauseImageButton?.setOnClickListener({
            if ( Stattified.mediaPlayer?.isPlaying as Boolean){
                Stattified. mediaPlayer?.pause()
                Stattified.currentSongHelper?.isPlaying=false
                Stattified. playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)

            }else{
                Stattified.mediaPlayer?.start()
                Stattified.currentSongHelper?.isPlaying=true
                Stattified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }


        })
    }

    fun playPrevious(){
        Stattified. currentPosition= Stattified.currentPosition-1
        if ( Stattified.currentPosition==-1){
            Stattified.currentPosition=0
        }
        if ( Stattified.currentSongHelper?.isPlaying as Boolean){
            Stattified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        }else{
            Stattified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }

        Stattified.currentSongHelper?.isLoop=false
        var nextSong= Stattified.fetchSongs?.get( Stattified.currentPosition)
        Stattified.currentSongHelper?.songTitle=nextSong?.songTitle
        Stattified.  currentSongHelper?.songPath=nextSong?.songData
        Stattified.currentSongHelper?.currentPosition= Stattified.currentPosition
        Stattified. currentSongHelper?.songId=nextSong?.songID as Long
      Staticated.updateTextView( Stattified.currentSongHelper?.songTitle as String, Stattified.currentSongHelper?.songArtist as String)


        Stattified. mediaPlayer?.reset()
        try {
            Stattified. mediaPlayer?.setDataSource(activity,Uri.parse( Stattified.currentSongHelper?.songPath))
            Stattified. mediaPlayer?.prepare()
            Stattified. mediaPlayer?.start()
            Staticated. processInformation( Stattified.mediaPlayer as MediaPlayer)
        }catch (e:Exception){
            e.printStackTrace()
        }
        if ( Stattified.favoriteContent?.checkifIdExists( Stattified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
            Stattified.fab?.setImageDrawable(ContextCompat.getDrawable( Stattified.myActivity,R.drawable.favorite_on))
        }else{
            Stattified. fab?.setImageDrawable(ContextCompat.getDrawable( Stattified.myActivity,R.drawable.favorite_off))
        }
    }

fun bindShakeListener(){
    Stattified.mSensorListener=object : SensorEventListener{
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

        override fun onSensorChanged(p0: SensorEvent) {
            val x = p0.values[0]
            val y = p0.values[1]
            val z = p0.values[2]

            mAccelarationLast=mAccelarationCurrent
            mAccelarationCurrent= Math.sqrt(((x*x + y*y + z*z).toDouble())).toFloat()
            val delta =mAccelarationCurrent - mAccelarationLast
            mAccelaration = mAccelaration * 0.9f + delta
            if (mAccelaration >12){
                val prefs =Stattified.myActivity?.getSharedPreferences(Stattified.MY_PREFS_NAME,Context.MODE_PRIVATE)
                val isAllowed = prefs?.getBoolean("feature",false)
                if(isAllowed as Boolean) {
                    Staticated.playNext("PlayNextNormal")
                }
            }

        }

    }
}


}// Required empty public constructor
