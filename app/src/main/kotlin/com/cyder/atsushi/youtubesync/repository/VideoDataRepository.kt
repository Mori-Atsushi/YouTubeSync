package com.cyder.atsushi.youtubesync.repository

import com.cyder.atsushi.youtubesync.BuildConfig
import com.cyder.atsushi.youtubesync.api.mapper.toModel
import com.cyder.atsushi.youtubesync.model.Video
import com.cyder.atsushi.youtubesync.websocket.Response
import com.google.android.youtube.player.YouTubePlayer
import com.google.gson.GsonBuilder
import com.hosopy.actioncable.Consumer
import com.hosopy.actioncable.Subscription
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject


/**
 * Created by chigichan24 on 2018/04/15.
 */
class VideoDataRepository @Inject constructor(
        private val consumer: Consumer,
        private val subscription: Subscription
) : VideoRepository {
    private val playingVideo: Subject<Video> = BehaviorSubject.create()
    private val playList: Subject<List<Video>> = BehaviorSubject.createDefault(listOf())
    private val isPlaying: Subject<Boolean> = BehaviorSubject.createDefault(false)
    override val developerKey: Flowable<String> = Flowable.just(BuildConfig.YOUTUBE_DEVELOPER_KEY)
    override val playerState: Flowable<YouTubePlayer.PlayerStateChangeListener>
        get() {
            val listener = object : YouTubePlayer.PlayerStateChangeListener {
                override fun onAdStarted() {
                }

                override fun onError(error: YouTubePlayer.ErrorReason?) {
                }

                override fun onLoaded(s: String?) {
                }

                override fun onLoading() {
                }

                override fun onVideoEnded() {
                    getNextVideo()?.apply {
                        playingVideo.onNext(this)
                    } ?: apply {
                        isPlaying.onNext(false)
                    }
                }

                override fun onVideoStarted() {
                }
            }
            return Flowable.just(listener)
        }

    init {
        startRouting()
    }

    override fun obserbleIsPlaying(): Flowable<Boolean> {
        return isPlaying.distinctUntilChanged().toFlowable(BackpressureStrategy.LATEST)
    }

    override fun obserbleNowPlayingVideo(): Flowable<Video> {
        return playingVideo.toFlowable(BackpressureStrategy.LATEST)
    }

    override fun getNoewPlayingVideo(): Flowable<Video> {
        subscription.perform(NOW_PLAYING)
        return obserbleNowPlayingVideo()
    }

    override fun getPlayList(): Flowable<List<Video>> {
        subscription.perform(PLAY_LIST)
        return Flowable.empty()
    }

    private fun startRouting() {
        subscription.onReceived = {
            if (it is String) {
                val response = it.toResponse()
                when (response.dataType) {
                    NOW_PLAYING, START_VIDEO -> {
                        response.data?.apply {
                            playingVideo.onNext(this.video.toModel())
                            isPlaying.onNext(true)
                        }
                    }
                    PLAY_LIST -> {
                        response.data?.apply {
                            this@VideoDataRepository.playList
                                    .onNext(this.playList.map { it.toModel() })
                        }
                    }
                }
            }
        }
    }

    private fun getNextVideo(): Video? {
        val playlist = playList.blockingFirst()
        val video = playlist.firstOrNull()
        video?.apply {
            playList.onNext(playlist.drop(1))
        }
        return video
    }

    private fun String.toResponse(): Response {
        return GsonBuilder().create().fromJson(this, Response::class.java)
    }

    companion object {
        const val NOW_PLAYING: String = "now_playing_video"
        const val START_VIDEO: String = "start_video"
        const val PLAY_LIST: String = "play_list"
    }
}

