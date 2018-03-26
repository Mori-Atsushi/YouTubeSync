package com.cyder.atsushi.youtubesync.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableList
import com.cyder.atsushi.youtubesync.model.Room
import com.cyder.atsushi.youtubesync.repository.RoomRepository
import com.cyder.atsushi.youtubesync.repository.UserRepository
import com.cyder.atsushi.youtubesync.view.helper.Navigator
import com.cyder.atsushi.youtubesync.viewmodel.base.ActivityViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * Created by chigichan24 on 2018/01/12.
 */

class TopActivityViewModel @Inject constructor(
        private val userRepository: UserRepository,
        private val roomRepository: RoomRepository,
        private val navigator: Navigator
) : ActivityViewModel() {

    var roomViewModels: ObservableList<RoomViewModel> = ObservableArrayList()
    var isLoading: ObservableBoolean = ObservableBoolean()
    override fun onStart() {
    }

    override fun onResume() {
        getRooms()
    }

    override fun onPause() {
    }

    override fun onStop() {
    }

    fun onJoinRoom() {
    }

    fun onCreateRoom() {
    }

    fun onRefresh() {
        isLoading.set(true)
        getRooms()
    }

    private fun getRooms() {
        val token = userRepository.getAccessToken().blockingGet()!!
        roomRepository.fetchJoinedRooms(token)
                .map { convertToViewModel(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ response ->
                    this.roomViewModels.clear()
                    this.roomViewModels.addAll(response)
                    isLoading.set(false)
                },{
                    isLoading.set(false)
                })
    }

    private fun convertToViewModel(rooms: List<Room>): List<RoomViewModel> {
        return rooms.map { RoomViewModel(navigator, ObservableField(it)) }
    }
}