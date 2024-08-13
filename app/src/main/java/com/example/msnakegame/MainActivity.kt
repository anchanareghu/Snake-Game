package com.example.msnakegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.msnakegame.ui.theme.MSnakeGameTheme
import com.example.msnakegame.ui.theme.PurpleHaze

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val game = GameStateManager(lifecycleScope, this)
        setContent {
            MSnakeGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PurpleHaze
                ) {
                    SnakeGame(game)
                }
            }
        }
    }
}