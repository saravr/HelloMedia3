package com.sandymist.hellomedia3

import android.content.Intent
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class PlaybackService: MediaSessionService() {
    private val uri = "https://audio-edge-2a8hd.sfo.he.radiomast.io/ref-128k-mp3-stereo"
    private lateinit var player: ExoPlayer
    private var mediaSession: MediaSession? = null

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        Log.e("++++", "++++ SERVICE CREATED")
        val dataSourceFactory = DataSource.Factory {
            val dataSource = DefaultHttpDataSource.Factory().createDataSource()
            ////dataSource.setRequestProperty("Authorization", token)
            dataSource
        }

        player = ExoPlayer.Builder(this)
            //.setAudioAttributes()
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(this).setDataSourceFactory(dataSourceFactory),
            )
            .build()
            .apply {
                ////addListener(listener)
                //addMediaSource(mediaSourceFactory)
                setMediaItem(MediaItem.fromUri(uri))
                playWhenReady = true
                prepare()
                play()
            }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        mediaSession = MediaSession.Builder(this, player).build()
        Log.e("++++", "++++ MEDIA SESSION CREATE: $mediaSession")
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
            Log.e("++++", "++++ MEDIA SESSION TASK REMOVED")
        }
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
            Log.e("++++", "++++ MEDIA SESSION DESTROYED")
        }
        super.onDestroy()
    }
}
