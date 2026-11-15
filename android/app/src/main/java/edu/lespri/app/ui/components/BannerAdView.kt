package edu.lespri.app.ui.components

import android.content.Context
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import edu.lespri.app.domain.model.LespriConstants
import edu.lespri.app.util.AdManager

/**
 * Banner AdMob adaptif dengan safety margin 16 dp di bawahnya.
 *
 * Banner hanya akan ditampilkan jika [AdManager.adsEnabled] bernilai true.
 * Jika iklan tidak dimuat atau fitur iklan dimatikan, menampilkan placeholder kosong
 * dengan tinggi yang sama agar layout tidak berguncing.
 */
@Composable
fun BannerAdView(
    adManager: AdManager,
    modifier: Modifier = Modifier,
    showPlaceholder: Boolean = true
) {
    val context = LocalContext.current
    val adsEnabled by adManager.adsEnabled.collectAsState()

    if (!adsEnabled && !showPlaceholder) return

    AndroidBannerAdView(
        adManager = adManager,
        modifier = modifier
            .fillMaxWidth()
            .height(LespriConstants.BANNER_AD_HEIGHT_DP.dp),
        showPlaceholder = showPlaceholder
    )
}

/** Wrapper View untuk [AdView] (Android View) di dalam Compose. */
@Composable
private fun AndroidBannerAdView(
    adManager: AdManager,
    modifier: Modifier,
    showPlaceholder: Boolean
) {
    val context = LocalContext.current
    val bannerView = remember { BannerAdViewHolder(context, adManager) }

    DisposableEffect(bannerView) {
        onDispose {
            bannerView.destroy()
        }
    }

    androidx.compose.ui.platform.AndroidView(
        factory = { bannerView.view },
        modifier = modifier,
        update = {
            bannerView.updateAd()
        }
    )

    if (showPlaceholder) {
        Text(
            text = "Iklan",
            modifier = Modifier
                .fillMaxWidth()
                .height(LespriConstants.BANNER_AD_HEIGHT_DP.dp)
                .padding(vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.TextAlign.Center
        )
    }
}

/**
 * Menampung [AdView] native Android dan mengelola siklus hidup pemuatan iklan.
 */
private class BannerAdViewHolder(
    private val context: Context,
    private val adManager: AdManager
) {

    val view: FrameLayout = FrameLayout(context).apply {
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
    }

    private lateinit var adView: AdView

    init {
        if (!adManager.isInitialized) {
            adManager.initialize()
        }

        adView = AdView(context).apply {
            adUnitId = adManager.bannerAdUnitId
            adSize = AdSize.BANNER
            adListener = object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Silent fail — iklan tes mungkin belum siap, tidak mengganggu UX
                }
            }
        }
        view.addView(adView)
    }

    fun updateAd() {
        val request = AdRequest.Builder().build()
        adView.loadAd(request)
    }

    fun destroy() {
        adView.destroy()
    }
}
