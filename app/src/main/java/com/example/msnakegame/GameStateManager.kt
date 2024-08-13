package com.example.msnakegame

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Random

class GameStateManager(
    private val scope: CoroutineScope,
    context: Context? = null
) {

    private val mutex = Mutex()
    private val _state = MutableStateFlow(
        State(food = Pair(5, 5), snake = listOf(Pair(7, 7)))
    )
    val state: Flow<State> = _state

    private var gameLoopJob: Job? = null
    private var isPaused: Boolean = false

    private val sharedPreferences =
        context?.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
    private val highScoreKey = "high_score"

    var move = Pair(1, 0)
        set(value) {
            if (value.first != -move.first && value.second != -move.second) {
                scope.launch {
                    mutex.withLock {
                        field = value
                    }
                }
            }
        }

    init {
        startGame()
    }

    private val eatSoundPlayer: MediaPlayer? = context?.let { MediaPlayer.create(it, R.raw.ate) }
    private val gameOverSoundPlayer: MediaPlayer? = context?.let { MediaPlayer.create(it, R.raw.gameover) }


    private fun startGame() {
        gameLoopJob?.cancel()

        gameLoopJob = scope.launch {
            var snakeLength = 2
            var currentScore = 0

            while (true) {
                delay(150)
                if (isPaused) continue

                _state.update { currentState ->
                    if (currentState.isGameOver) {
                        gameOverSoundPlayer?.start()
                        return@update currentState
                    }

                    val newPosition = currentState.snake.first().let { head ->
                        mutex.withLock {
                            Pair(
                                (head.first + move.first + BOARD_SIZE) % BOARD_SIZE,
                                (head.second + move.second + BOARD_SIZE) % BOARD_SIZE
                            )
                        }
                    }

                    if (currentState.snake.contains(newPosition) ||
                        newPosition.first < 0 || newPosition.first >= BOARD_SIZE ||
                        newPosition.second < 0 || newPosition.second >= BOARD_SIZE
                    ) {
                        updateHighScore(currentScore)
                        return@update currentState.copy(isGameOver = true)
                    }

                    if (newPosition == currentState.food) {
                        eatSoundPlayer?.start()
                        snakeLength++
                        currentScore++
                    }

                    currentState.copy(
                        food = if (newPosition == currentState.food) Pair(
                            Random().nextInt(BOARD_SIZE),
                            Random().nextInt(BOARD_SIZE)
                        ) else currentState.food,
                        snake = listOf(newPosition) + currentState.snake.take(snakeLength - 1),
                        score = currentScore
                    )
                }
            }
        }
    }

    private fun updateHighScore(currentScore: Int) {
        val currentHighScore = sharedPreferences?.getInt(highScoreKey, 0)
        if (currentScore > currentHighScore!!) {
            sharedPreferences?.edit()?.putInt(highScoreKey, currentScore)?.apply()
        }
    }

    fun getHighScore(): Int {
        return sharedPreferences?.getInt(highScoreKey, 0) ?: 0
    }

    fun resetGame() {
        gameLoopJob?.cancel()

        scope.launch {
            mutex.withLock {
                move = Pair(1, 0)
                _state.value = State(
                    food = Pair(5, 5),
                    snake = listOf(Pair(7, 7)),
                    isGameOver = false
                )
            }
            startGame()
        }
    }

    fun pauseGame() {
        isPaused = true
    }

    fun resumeGame() {
        isPaused = false
    }

    companion object {
        const val BOARD_SIZE = 16
    }
}

data class State(
    val food: Pair<Int, Int>,
    val snake: List<Pair<Int, Int>>,
    val score: Int = 0,
    val isGameOver: Boolean = false
)
