import Direction.*
import java.awt.Event
import java.awt.event.KeyEvent
import kotlin.concurrent.timer

data class Signal(
        val newDirection: Direction = None,
        val startGame: Boolean = false,
        val endGame: Boolean = false,
        val pause: Boolean = false
)

fun signalForGame(key: Int, model: Model): Signal =
        if (model.ingame) onGameKeyReactions(key)
        else beforeGameKeyReactions(key)

private fun onGameKeyReactions(key: Int): Signal = when (key) {
    KeyEvent.VK_LEFT -> Signal(newDirection = Left)
    KeyEvent.VK_RIGHT -> Signal(newDirection = Right)
    KeyEvent.VK_UP -> Signal(newDirection = Up)
    KeyEvent.VK_DOWN -> Signal(newDirection = Down)
    else -> Signal()
}

private fun beforeGameKeyReactions(key: Int) = when {
    key == 's'.toInt() || key == 'S'.toInt() -> Signal(startGame = true) //startGame()
    else -> Signal()
}