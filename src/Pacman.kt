import java.awt.EventQueue
import javax.swing.JFrame

class Pacman : JFrame() {

    init {
        add(Board())
        title = "Pacman"
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setSize(380, 420)
        setLocationRelativeTo(null)
        isVisible = true
    }
}

fun main(args: Array<String>) {
    EventQueue.invokeLater {
        Pacman().apply {
            isVisible = true
        }
    }
}