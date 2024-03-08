package com.sandymist.hellomedia3

import android.content.ComponentName
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Forward5
import androidx.compose.material.icons.rounded.PauseCircleFilled
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.google.common.util.concurrent.MoreExecutors

@OptIn(UnstableApi::class) @Composable
fun MediaPlayer() {
    val context = LocalContext.current
    val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
    val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()


//    val uri = "https://audio-edge-2a8hd.sfo.he.radiomast.io/ref-128k-mp3-stereo"
//
//    val dataSourceFactory = DataSource.Factory {
//        val dataSource = DefaultHttpDataSource.Factory().createDataSource()
//        ////dataSource.setRequestProperty("Authorization", token)
//        dataSource
//    }
//
//    val mediaSourceFactory = HlsMediaSource.Factory(dataSourceFactory)
//        .createMediaSource(MediaItem.fromUri(uri))
//
//    val player = remember {
//        ExoPlayer.Builder(context)
//            //.setAudioAttributes()
//            .setMediaSourceFactory(
//                DefaultMediaSourceFactory(context).setDataSourceFactory(dataSourceFactory),
//            )
//            .build()
//            .apply {
//                ////addListener(listener)
//                //addMediaSource(mediaSourceFactory)
//                setMediaItem(MediaItem.fromUri(uri))
//                playWhenReady = true
//                prepare()
//                play()
//            }
//    }
//
//    val mediaSession = MediaSession.Builder(context, player).build()

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PlayerView(context).apply {
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL // full screen
                    useController = false // Hides the default Player Controller
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    ) // full screen
                }
            }
        ) { playerView ->
            controllerFuture.addListener({
                playerView.setPlayer(controllerFuture.get())
            }, MoreExecutors.directExecutor())

        }

        AudioPlayer(
            title = "Sample Media",
            author = "Joe B",
            remainingSeconds = 2000,
            thumbnailUrl = "imageUrl",
            isPlaying = true, //isContentPlaying.value,
            playerState = "Playing", //if (isContentPlaying.value) "Playing" else "Paused",
            currentChapter = 1,
            lastChapter = 10, //document.audiobook!!.chapters.size, // TODO: remove !!
            onPlay = {
                controllerFuture.get().play()
            },
            onPause = {
                controllerFuture.get().pause()
            },
        ) {
        }
    }
}

@Composable
fun AudioPlayer(
    title: String,
    author: String,
    remainingSeconds: Long,
    thumbnailUrl: String,
    isPlaying: Boolean,
    playerState: String,
    currentChapter: Int,
    lastChapter: Int,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onImageLoadError: () -> Unit,
) {
    val inPreview = LocalInspectionMode.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.padding(end = 5.dp, top = 15.dp)) {
            Spacer(Modifier.weight(1f))
            IconButton(
                modifier = Modifier.height(40.dp),
                onClick = { },
            ) {
                Icon(Icons.Default.MoreVert, contentDescription = "Overflow menu", tint = Color.White)
            }
        }
        Column {
            Text(
                title,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp, horizontal = 20.dp),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                author,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp, horizontal = 20.dp),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                "0:10",
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
                style = MaterialTheme.typography.titleSmall,
            )
        }

        Text(
            playerState,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, bottom = 5.dp),
            style = MaterialTheme.typography.titleSmall,
        )
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 25.dp, end = 25.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "0:23",
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    "Chapter $currentChapter of $lastChapter",
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    "45:29",
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            Slider(
                modifier = Modifier.padding(horizontal = 20.dp),
                value = 0.5f,
                onValueChange = { },
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 30.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(
                    onClick = { },
                ) {
                    Icon(
                        Icons.Rounded.SkipPrevious,
                        contentDescription = "Previous chapter",
                        tint = Color.White,
                    )
                }
                IconButton(
                    onClick = { },
                ) {
                    Icon(
                        Icons.Rounded.Replay,
                        contentDescription = "Skip backwards",
                        tint = Color.White,
                    )
                }
                IconButton(
                    onClick = {
                        if (isPlaying) onPause() else onPlay()
                    },
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.PauseCircleFilled else Icons.Rounded.PlayCircleFilled,
                        modifier = Modifier.size(60.dp),
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                    )
                }
                IconButton(
                    onClick = { },
                ) {
                    Icon(
                        Icons.Rounded.Forward5,
                        contentDescription = "Skip forward",
                        tint = Color.White,
                    )
                }
                IconButton(
                    onClick = { },
                ) {
                    Icon(
                        Icons.Rounded.SkipNext,
                        contentDescription = "Next chapter",
                        tint = Color.White,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}