package com.example.statusbartest

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RewardAdsManager(
    private val context: Context
) {
    private var _rewardAds: RewardedAd? = null

    init {
        initialize()
    }

    enum class AdsScreenState {
        Show,
        Dismiss
    }

    private val _state: MutableStateFlow<AdsScreenState> = MutableStateFlow(AdsScreenState.Dismiss)
    val state: StateFlow<AdsScreenState> = _state.asStateFlow()


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
                _state.update { AdsScreenState.Dismiss }
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
            }

            override fun onAdImpression() {
                super.onAdImpression()
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
            }
        }
    }

    fun show(activity: Activity, callback: (RewardItem) -> Unit) {
        _state.update { AdsScreenState.Show }
        _rewardAds?.show(activity) {
            callback(it)
        }
    }
}