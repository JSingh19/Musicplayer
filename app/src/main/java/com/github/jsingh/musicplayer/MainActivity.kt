package com.github.jsingh.musicplayer

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mp: MediaPlayer
    lateinit var seekBar: SeekBar
    private var totalTime: Int = 0
    private val TAG: String = "Main Activity"
    
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mp = MediaPlayer.create(this, R.raw.emptycups)
        mp.isLooping = true
        mp.setVolume(0.5f, 0.5f)
        totalTime = mp.duration

        // Position bar
        seekBar = findViewById(R.id.seekBar)
        seekBar.max = totalTime
        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                   mp.seekTo(progress)
               }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Thread
        Thread(Runnable {
            while(mp != null) {
                try {
                    val msg = Message()
                    msg.what = mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    Log.e(TAG, ":: Interrupted Exception in thread updating the seekbar position")
                }
            }
        }).start()

        playBtn.setOnClickListener {
            if(mp.isPlaying) {
                // Stop
                mp.pause()
                playBtn.setBackgroundResource(R.drawable.baseline_play_circle_filled_white_48)
            } else {
                //Start
                mp.start()
                playBtn.setBackgroundResource(R.drawable.baseline_pause_circle_filled_white_48)
            }
        }

    }

    @SuppressLint("HandlerLeak")
    var handler = object: Handler() {

        override fun handleMessage(msg: Message) {
            var currentPosition = msg.what

            // Update position bar
            seekBar.progress = currentPosition

            // Update elapsed time
            var elapsedTime1 = createTimeLabel(currentPosition)
            elapsedTime.text = elapsedTime1

            // Update remaining time
            var remainingTime1 = createTimeLabel(totalTime - currentPosition)
            remainingTime.text = "-$remainingTime1"
        }

    }

    fun createTimeLabel(time: Int): String {

        var timeLabel: String = ""
        var min = time / 1000 / 60
        var sec = time / 1000 % 60
        timeLabel = "$min:"
        if(sec < 10) timeLabel += "0"
        timeLabel += sec
        return timeLabel

    }

}
