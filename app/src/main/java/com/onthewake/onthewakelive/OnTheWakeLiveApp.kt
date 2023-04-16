package com.onthewake.onthewakelive

import android.app.Application
import com.onesignal.OneSignal
import com.onthewake.onthewakelive.core.utils.Constants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OnTheWakeLiveApp: Application() {

    override fun onCreate() {
        super.onCreate()

        OneSignal.initWithContext(this)
        OneSignal.setAppId(Constants.ONESIGNAL_APP_ID)
    }
}