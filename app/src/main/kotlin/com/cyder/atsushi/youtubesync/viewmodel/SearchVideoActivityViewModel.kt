package com.cyder.atsushi.youtubesync.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ObservableList
import android.view.inputmethod.EditorInfo
import com.cyder.atsushi.youtubesync.model.Video
import com.cyder.atsushi.youtubesync.repository.VideoRepository
import com.cyder.atsushi.youtubesync.repository.YouTubeRepository
import com.cyder.atsushi.youtubesync.view.activity.SearchVideoActivity
import com.cyder.atsushi.youtubesync.view.helper.Navigator
import com.cyder.atsushi.youtubesync.viewmodel.base.ActivityViewModel
import javax.inject.Inject


/**
 * Created by chigichan24 on 2018/04/23.
 */

class SearchVideoActivityViewModel @Inject constructor(
        private val navigator: Navigator,
        private val videoRepository: VideoRepository,
        private val repository: YouTubeRepository
) : ActivityViewModel() {
    var searchWord: ObservableField<String> = ObservableField()
    var videoViewModels: ObservableList<SearchVideoViewModel> = ObservableArrayList()
    override fun onStart() {
    }

    override fun onResume() {
    }

    override fun onPause() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
    }

    fun onBackButtonClicked() = navigator.closeActivity()

    fun onEnterKeyClicked(actionId: Int): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            val word = searchWord.get() ?: ""
            videoViewModels.removeAll(videoViewModels)
            repository.getYouTubeSearch(word)
                    .map { convertToViewModel(it) }
                    .subscribe({
                        videoViewModels.addAll(it)
                    }, {
                        
                    })
        }
        return false
    }

    fun onScrolled(isBottom: Boolean) {
        if (isBottom) {
            repository.getNextYouTubeSearch()
                    .map { convertToViewModel(it) }
                    .subscribe({
                        videoViewModels.addAll(it)
                    }, {

                    })

        }
    }

    private fun convertToViewModel(videos: List<Video>): List<SearchVideoViewModel> {
        return videos.map { SearchVideoViewModel(navigator, videoRepository, ObservableField(it)) }
    }
}