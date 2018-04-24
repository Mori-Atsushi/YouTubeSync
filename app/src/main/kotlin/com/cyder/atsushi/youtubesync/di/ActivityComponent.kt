package com.cyder.atsushi.youtubesync.di

import com.cyder.atsushi.youtubesync.di.scope.ActivityScope
import com.cyder.atsushi.youtubesync.view.activity.*
import dagger.Subcomponent

/**
 * Created by chigichan24 on 2018/01/12.
 */

@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
    //TODO when you create Activity, you have to create inject method
    fun inject(activity: SplashActivity)
    fun inject(activity: WelcomeActivity)
    fun inject(activity: SignInActivity)
    fun inject(activity: SignUpActivity)
    fun inject(activity: TopActivity)
    fun inject(activity: CreateRoomActivity)
    fun inject(activity: RoomActivity)
    fun inject(activity: SearchVideoActivity)
    fun inject(activity: UserReportActivity)
    fun plus(module: FragmentModule): FragmentComponent
}