package com.roam.app

import android.app.Application
import com.clerk.api.Clerk

class RoamApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val publishableKey = BuildConfig.CLERK_PUBLISHABLE_KEY
        if (publishableKey.startsWith("pk_test_") || publishableKey.startsWith("pk_live_")) {
            Clerk.initialize(this, publishableKey)
        }
    }
}
