package com.example.statusbartest

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardAdsManager(
    private val context: Context
) {
    private var _rewardAds: RewardedAd? = null

    init {
        initialize()
    }


    private fun initialize() {
        RewardedAd.load(
            context,
            "ca-app-pub-3940256099942544/5224354917",
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    _rewardAds = null
                }

                override fun onAdLoaded(p0: RewardedAd) {
                    super.onAdLoaded(p0)
                    _rewardAds = p0
                    setFullScreenCallback()
                }
            }
        )
    }

    private fun setFullScreenCallback() {
        _rewardAds?.fullScreenContentCallback = object : FullScreenContentCallback() {

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                _rewardAds = null

                Handler(Looper.getMainLooper()).post {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        with(context as Activity) {
                            window.statusBarColor = Color.Transparent.toArgb()
                        }
                    } else
                        (context as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                }
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
            }

            override fun onAdImpression() {
                super.onAdImpression()
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()

                Handler(Looper.getMainLooper()).post {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        with(context as Activity) {
                            window.statusBarColor = Color.White.toArgb()
                        }
                    } else {
                        (context as Activity).window.setFlags(
                            WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN
                        )
                    }
                }
            }
        }
    }

    fun show(activity: Activity, callback: (RewardItem) -> Unit) {
        _rewardAds?.setImmersiveMode(false)
        _rewardAds?.show(activity) {
            callback(it)
        }
    }
}