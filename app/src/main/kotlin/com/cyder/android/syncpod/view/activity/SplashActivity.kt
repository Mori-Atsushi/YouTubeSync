package com.cyder.android.syncpod.view.activity

import android.os.Bundle
import com.cyder.android.syncpod.viewmodel.SplashActivityViewModel
import javax.inject.Inject

/**
 * Created by atsushi on 2018/04/10.
 */

class SplashActivity : BaseActivity() {
    @Inject
    lateinit var viewModel: SplashActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getComponent().inject(this)
        bindViewModel(viewModel)
        val uri = intent?.data
        val path = uri?.path
        if (path == "/room") {
            viewModel.roomKey = uri.getQueryParameter(ROOM_KEY)
        }
    }

    companion object {
        const val ROOM_KEY = "room_key"

    }
}