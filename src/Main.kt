import java.awt.*
import java.awt.event.*
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.Timer

class Main : JFrame() {
    init {
        add(Board())
        title = "Pacman"
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setSize(380, 420)
        setLocationRelativeTo(null)
        isVisible = true
    }
}

class Board : JPanel(), ActionListener {
    private val presenter = BoardPresenter()

    init {
        Timer(40, this).start()
        addKeyListener(KeyPressedAdapter { presenter.keyPressed(it.keyCode) })
        isFocusable = true
        background = Color.black
        isDoubleBuffered = true
    }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        drawNext(g as Graphics2D)
    }

    private fun drawNext(g2d: Graphics2D) {
        presenter.nextFrame(g2d)
        Toolkit.getDefaultToolkit().sync()
        g2d.dispose()
    }

    override fun actionPerformed(e: ActionEvent) {
        repaint()
    }
}

class KeyPressedAdapter(val f: (KeyEvent) -> Unit) : KeyAdapter() {
    override fun keyPressed(e: KeyEvent) {
        f(e)
    }
}

class BoardPresenter {
    private var model: Model = Model()
    private var signal: Signal = Signal()

    fun keyPressed(keyCode: Int) {
        signal = signalForGame(keyCode, model)
    }

    fun nextFrame(g2d: Graphics2D) {
        model = model.update(signal)
        display(g2d, model)
    }
}

fun main(args: Array<String>) {
    EventQueue.invokeLater {
        Main().isVisible = true
    }
}