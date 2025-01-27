package com.and04.naturealbum.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.and04.naturealbum.ui.theme.NatureAlbumTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val deeplink = handleDeepLink(intent)
        setContent {
            NatureAlbumTheme {
                NatureAlbumApp(startDestination = deeplink)
            }
        }
    }

    private fun handleDeepLink(intent: Intent?) =
        intent?.extras?.getString("deeplink") ?: intent?.data?.toString()
}
