package com.watchtower.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import com.watchtower.app.ui.WatchtowerNavHost
import com.watchtower.app.ui.theme.WatchtowerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WatchtowerTheme {
                // enableEdgeToEdge() draws behind the status/nav bars with no
                // padding of its own — without this, top-left touch targets
                // (like the detail screen's back button) can end up rendered
                // under the status bar, where taps land on the wrong pixels.
                Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                    WatchtowerNavHost()
                }
            }
        }
    }
}
