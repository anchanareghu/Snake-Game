package com.example.msnakegame

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.msnakegame.ui.theme.MSnakeGameTheme
import com.example.msnakegame.ui.theme.PastelGreen
import com.example.msnakegame.ui.theme.PurpleHaze
import com.example.msnakegame.ui.theme.Red
import com.example.msnakegame.ui.theme.pressStart2p
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.Locale


@Composable
fun SnakeGame(game: GameStateManager) {
    val state = game.state.collectAsState(initial = State(Pair(5, 5), listOf(Pair(7, 7))))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize()
    ) {
        state.value.let {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        game.resetGame()
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart"
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Score",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = String.format(
                            Locale.US,
                            "%04d",
                            it.score
                        ),
                        color = Color.White,
                        fontSize = 12.sp,
                    )
                }
            }
            Board(it)
            Buttons {
                game.move = it
            }
            Bottom(
                game
            )
        }
        if (state.value.isGameOver) {
            GameOverDialog(
                onDismiss = { game.resetGame() },
                score = state.value.score,
                highScore = game.getHighScore()
            )
        }
    }
}

@Composable
fun GameOverDialog(
    onDismiss: () -> Unit,
    score: Int,
    highScore: Int
) {
    AlertDialog(
        icon = {
            Icon(
                Icons.Default.Refresh,
                contentDescription = "Restart"
            )
        },
        title = { Text(text = "Game Over", fontFamily = pressStart2p) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            ) {
                Text(
                    text = "Score: $score",
                    textAlign = TextAlign.Start,
                    fontFamily = pressStart2p
                )
                Text(
                    text = "High Score: $highScore",
                    textAlign = TextAlign.Start,
                    fontFamily = pressStart2p
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss, colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleHaze,
                    contentColor = Color.Gray,
                )
            ) {
                Text(
                    text = "Play again?",
                    fontFamily = pressStart2p,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        },
        containerColor = Color.Gray,
        textContentColor = PurpleHaze,
        titleContentColor = PurpleHaze,
        iconContentColor = PurpleHaze
    )
}


@Composable
fun Buttons(onDirectionChange: (Pair<Int, Int>) -> Unit) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { onDirectionChange(Pair(0, -1)) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Gray,
                contentColor = Color.White
            )
        ) {
            Icon(
                Icons.Default.KeyboardArrowUp,
                contentDescription = "Up"
            )
        }
        Row {
            Button(
                onClick = { onDirectionChange(Pair(-1, 0)) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Left"
                )
            }
            Spacer(modifier = Modifier.size(48.dp))
            Button(
                onClick = { onDirectionChange(Pair(1, 0)) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Right"
                )
            }
        }
        ElevatedButton(
            onClick = { onDirectionChange(Pair(0, 1)) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Gray,
                contentColor = Color.White
            )
        ) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "Down"
            )
        }

    }
}

@Composable
fun Bottom(game: GameStateManager) {
    val isPaused = remember { mutableStateOf(false) }
    val icon = if (isPaused.value) R.drawable.play else R.drawable.pause

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "High Score", color = Color.Gray,
                fontSize = 12.sp
            )
            Text(
                text = String.format(Locale.US, "%04d", game.getHighScore()),
                color = Color.White,
                fontSize = 12.sp
            )
        }
        Button(
            onClick = {
                isPaused.value = !isPaused.value
                if (isPaused.value) {
                    game.pauseGame()
                } else {
                    game.resumeGame()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            modifier = Modifier.padding(0.dp, 16.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "Pause"
            )
        }

    }
}

@Composable
fun Board(state: State) {
    BoxWithConstraints(Modifier.padding(16.dp)) {
        val tileSize = maxWidth / GameStateManager.BOARD_SIZE
        Box(
            Modifier
                .size(maxWidth)
                .border(2.dp, Color.Gray)
        )

        Box(
            Modifier
                .offset(x = tileSize * state.food.first, y = tileSize * state.food.second)
                .size(tileSize)
                .background(
                    Red, CircleShape
                )
        )

        state.snake.forEach {
            Box(
                Modifier
                    .offset(x = tileSize * it.first, y = tileSize * it.second)
                    .size(tileSize)
                    .background(
                        PastelGreen
                    )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SnakeGamePreview() {
    MSnakeGameTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = PurpleHaze
        ) {
            SnakeGame(GameStateManager(CoroutineScope(Dispatchers.Default)))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameOverDialogPreview() {
    MSnakeGameTheme {
        GameOverDialog(
            onDismiss = {},
            score = 10,
            highScore = 20
        )
    }
}