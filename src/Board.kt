import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.lang.Math.min
import javax.swing.ImageIcon
import javax.swing.JPanel
import javax.swing.Timer

class Board : JPanel(), ActionListener {

    private var d = Dimension(400, 400)
    private val smallfont = Font("Helvetica", Font.BOLD, 14)

    private val ii: Image? = null
    private val dotcolor = Color(192, 192, 0)
    private var mazecolor: Color = Color(5, 100, 5)

    private var ingame = false
    private var dying = false

    private val blocksize = 24
    private val nrofblocks = 15
    private val scrsize = nrofblocks * blocksize
    private val pacanimdelay = 2
    private val pacmananimcount = 4
    private val maxghosts = 12
    private val pacmanspeed = 6

    private var pacanimcount = pacanimdelay
    private var pacanimdir = 1
    private var pacmananimpos = 0
    private var nrofghosts = 6
    private var pacsleft: Int = 0
    private var score: Int = 0
    private var dx = IntArray(4)
    private var dy = IntArray(4)
    private var ghostx = IntArray(maxghosts)
    private var ghosty = IntArray(maxghosts)
    private var ghostdx = IntArray(maxghosts)
    private var ghostdy = IntArray(maxghosts)
    private var ghostspeed = IntArray(maxghosts)

    private val ghost = ImageIcon("images/ghost.gif").image
    private val pacman1 = ImageIcon("images/pacman.gif").image
    private val pacman2up = ImageIcon("images/up2.gif").image
    private val pacman3up = ImageIcon("images/up3.gif").image
    private val pacman4up = ImageIcon("images/up4.gif").image
    private val pacman2down = ImageIcon("images/down2.gif").image
    private val pacman3down = ImageIcon("images/down3.gif").image
    private val pacman4down = ImageIcon("images/down4.gif").image
    private val pacman2left = ImageIcon("images/left2.gif").image
    private val pacman3left = ImageIcon("images/left3.gif").image
    private val pacman4left = ImageIcon("images/left4.gif").image
    private val pacman2right = ImageIcon("images/right2.gif").image
    private val pacman3right = ImageIcon("images/right3.gif").image
    private val pacman4right = ImageIcon("images/right4.gif").image

    private var pacmanx: Int = 0
    private var pacmany: Int = 0
    private var pacmandx: Int = 0
    private var pacmandy: Int = 0
    private var reqdx: Int = 0
    private var reqdy: Int = 0
    private var viewdx: Int = 0
    private var viewdy: Int = 0

    private val validspeeds = intArrayOf(1, 2, 3, 4, 6, 8)

    private var currentspeed = 3
    private val screendata = arrayOf(19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22, 21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 21, 0, 0, 0, 17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20, 17, 18, 18, 18, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20, 17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 16, 24, 20, 25, 16, 16, 16, 24, 24, 28, 0, 25, 24, 24, 16, 20, 0, 21, 1, 17, 16, 20, 0, 0, 0, 0, 0, 0, 0, 17, 20, 0, 21, 1, 17, 16, 16, 18, 18, 22, 0, 19, 18, 18, 16, 20, 0, 21, 1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21, 1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21, 1, 17, 16, 16, 16, 16, 16, 18, 16, 16, 16, 16, 20, 0, 21, 1, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 21, 1, 25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 18, 20, 9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 25, 24, 24, 24, 28)
    private val timer = Timer(40, this)

    init {
        timer.start()
        addKeyListener(TAdapter())

        isFocusable = true
        background = Color.black
        isDoubleBuffered = true
    }

    override fun addNotify() {
        super.addNotify()
        initGame()
    }

    private fun doAnimation() {
        pacanimcount--
        if (pacanimcount <= 0) {
            pacanimcount = pacanimdelay
            pacmananimpos += pacanimdir

            if (pacmananimpos == pacmananimcount - 1 || pacmananimpos == 0) {
                pacanimdir = -pacanimdir
            }
        }
    }

    private fun playGame(g2d: Graphics2D) {
        if (dying) {
            death()
        } else {
            movePacman()
            drawPacman(g2d)
            moveGhosts(g2d)
            checkMaze()
        }
    }

    private fun showIntroScreen(g2d: Graphics2D) {
        g2d.apply {
            color = Color(0, 32, 48)
            fillRect(50, scrsize / 2 - 30, scrsize - 100, 50)
            color = Color.white
            drawRect(50, scrsize / 2 - 30, scrsize - 100, 50)

            val string = "Press s to start."
            val smallFont = Font("Helvetica", Font.BOLD, 14)
            color = Color.white
            font = smallFont
            drawString(string, (scrsize - this.getFontMetrics(smallFont).stringWidth(string)) / 2, scrsize / 2)
        }
    }

    private fun drawScore(g: Graphics2D) {
        var i: Int
        val s: String

        g.font = smallfont
        g.color = Color(96, 128, 255)
        s = "Score: " + score
        g.drawString(s, scrsize / 2 + 96, scrsize + 16)

        i = 0
        while (i < pacsleft) {
            g.drawImage(pacman3left, i * 28 + 8, scrsize + 1, this)
            i++
        }
    }

    private fun checkMaze() {
        val finished = (0..(nrofblocks * nrofblocks - 1)).none { screendata[it] and 48 != 0 }
        if (finished) {
            score += 50
            nrofghosts = min(nrofghosts + 1, maxghosts)
            currentspeed = min(currentspeed + 1, validspeeds.lastIndex)
            continueLevel()
        }
    }

    private fun death() {
        pacsleft--

        if (pacsleft == 0) {
            ingame = false
        }

        continueLevel()
    }

    private fun moveGhosts(g2d: Graphics2D) {
        var i: Int
        var pos: Int
        var count: Int

        i = 0
        while (i < nrofghosts) {
            if (ghostx[i] % blocksize == 0 && ghosty[i] % blocksize == 0) {
                pos = ghostx[i] / blocksize + nrofblocks * (ghosty[i] / blocksize).toInt()

                count = 0

                if (screendata[pos] and 1 == 0 && ghostdx[i] != 1) {
                    dx[count] = -1
                    dy[count] = 0
                    count++
                }

                if (screendata[pos] and 2 == 0 && ghostdy[i] != 1) {
                    dx[count] = 0
                    dy[count] = -1
                    count++
                }

                if (screendata[pos] and 4 == 0 && ghostdx[i] != -1) {
                    dx[count] = 1
                    dy[count] = 0
                    count++
                }

                if (screendata[pos] and 8 == 0 && ghostdy[i] != -1) {
                    dx[count] = 0
                    dy[count] = 1
                    count++
                }

                if (count == 0) {
                    if (screendata[pos] and 15 == 15) {
                        ghostdx[i] = 0
                        ghostdy[i] = 0
                    } else {
                        ghostdx[i] = -ghostdx[i]
                        ghostdy[i] = -ghostdy[i]
                    }

                } else {
                    count = min((Math.random() * count).toInt(), 3)
                    ghostdx[i] = dx[count]
                    ghostdy[i] = dy[count]
                }

            }

            ghostx[i] = ghostx[i] + ghostdx[i] * ghostspeed[i]
            ghosty[i] = ghosty[i] + ghostdy[i] * ghostspeed[i]
            drawGhost(g2d, ghostx[i] + 1, ghosty[i] + 1)

            if (pacmanx > ghostx[i] - 12 && pacmanx < ghostx[i] + 12
                    && pacmany > ghosty[i] - 12 && pacmany < ghosty[i] + 12
                    && ingame) {

                dying = true
            }
            i++
        }
    }

    private fun drawGhost(g2d: Graphics2D, x: Int, y: Int) {
        g2d.drawImage(ghost, x, y, this)
    }

    private fun movePacman() {
        val pos: Int
        val ch: Int

        if (reqdx == -pacmandx && reqdy == -pacmandy) {
            pacmandx = reqdx
            pacmandy = reqdy
            viewdx = pacmandx
            viewdy = pacmandy
        }

        if (pacmanx % blocksize == 0 && pacmany % blocksize == 0) {
            pos = pacmanx / blocksize + nrofblocks * (pacmany / blocksize).toInt()
            ch = screendata[pos]

            if (ch and 16 != 0) {
                screendata[pos] = (ch and 15).toInt()
                score++
            }

            if (reqdx != 0 || reqdy != 0) {
                if (!(reqdx == -1 && reqdy == 0 && ch and 1 != 0
                        || reqdx == 1 && reqdy == 0 && ch and 4 != 0
                        || reqdx == 0 && reqdy == -1 && ch and 2 != 0
                        || reqdx == 0 && reqdy == 1 && ch and 8 != 0)) {
                    pacmandx = reqdx
                    pacmandy = reqdy
                    viewdx = pacmandx
                    viewdy = pacmandy
                }
            }

            // Check for standstill
            if (pacmandx == -1 && pacmandy == 0 && ch and 1 != 0
                    || pacmandx == 1 && pacmandy == 0 && ch and 4 != 0
                    || pacmandx == 0 && pacmandy == -1 && ch and 2 != 0
                    || pacmandx == 0 && pacmandy == 1 && ch and 8 != 0) {
                pacmandx = 0
                pacmandy = 0
            }
        }
        pacmanx += pacmanspeed * pacmandx
        pacmany += pacmanspeed * pacmandy
    }

    private fun drawPacman(g2d: Graphics2D) {
        if (viewdx == -1) {
            drawPacnanLeft(g2d)
        } else if (viewdx == 1) {
            drawPacmanRight(g2d)
        } else if (viewdy == -1) {
            drawPacmanUp(g2d)
        } else {
            drawPacmanDown(g2d)
        }
    }

    private fun drawPacmanUp(g2d: Graphics2D) {
        g2d.drawImage(pacmanUpPic(), pacmanx + 1, pacmany + 1, this)
    }

    private fun pacmanUpPic() = when (pacmananimpos) {
        1 -> pacman2up
        2 -> pacman3up
        3 -> pacman4up
        else -> pacman1
    }

    private fun drawPacmanDown(g2d: Graphics2D) {
        g2d.drawImage(pacmanDownPic(), pacmanx + 1, pacmany + 1, this)
    }

    private fun pacmanDownPic() = when (pacmananimpos) {
        1 -> pacman2down
        2 -> pacman3down
        3 -> pacman4down
        else -> pacman1
    }

    private fun drawPacnanLeft(g2d: Graphics2D) {
        g2d.drawImage(pacmanLeftPic(), pacmanx + 1, pacmany + 1, this)
    }

    private fun pacmanLeftPic() = when (pacmananimpos) {
        1 -> pacman2left
        2 -> pacman3left
        3 -> pacman4left
        else -> pacman1
    }

    private fun drawPacmanRight(g2d: Graphics2D) {
        g2d.drawImage(pacmanRightPic(), pacmanx + 1, pacmany + 1, this)
    }

    private fun pacmanRightPic() = when (pacmananimpos) {
        1 -> pacman2right
        2 -> pacman3right
        3 -> pacman4right
        else -> pacman1
    }

    private fun drawMaze(g2d: Graphics2D) {
        var i: Int = 0
        var x: Int
        var y: Int

        y = 0
        while (y < scrsize) {
            x = 0
            while (x < scrsize) {

                g2d.color = mazecolor
                g2d.stroke = BasicStroke(2f)

                if (screendata[i] and 1 != 0) {
                    g2d.drawLine(x, y, x, y + blocksize - 1)
                }

                if (screendata[i] and 2 != 0) {
                    g2d.drawLine(x, y, x + blocksize - 1, y)
                }

                if (screendata[i] and 4 != 0) {
                    g2d.drawLine(x + blocksize - 1, y, x + blocksize - 1,
                            y + blocksize - 1)
                }

                if (screendata[i] and 8 != 0) {
                    g2d.drawLine(x, y + blocksize - 1, x + blocksize - 1,
                            y + blocksize - 1)
                }

                if (screendata[i] and 16 != 0) {
                    g2d.color = dotcolor
                    g2d.fillRect(x + 11, y + 11, 2, 2)
                }

                i++
                x += blocksize
            }
            y += blocksize
        }
    }

    private fun initGame() {
        pacsleft = 3
        score = 0
        continueLevel()
        nrofghosts = 6
        currentspeed = 3
    }

    private fun continueLevel() {
        var i: Int
        var dx = 1
        var random: Int

        i = 0
        while (i < nrofghosts) {

            ghosty[i] = 4 * blocksize
            ghostx[i] = 4 * blocksize
            ghostdy[i] = 0
            ghostdx[i] = dx
            dx = -dx
            random = (Math.random() * (currentspeed + 1)).toInt()

            if (currentspeed > validspeeds.lastIndex)
                currentspeed = validspeeds.lastIndex

            if (random > currentspeed)
                random = currentspeed

            ghostspeed[i] = validspeeds[random]
            i++
        }

        pacmanx = 7 * blocksize
        pacmany = 11 * blocksize
        pacmandx = 0
        pacmandy = 0
        reqdx = 0
        reqdy = 0
        viewdx = -1
        viewdy = 0
        dying = false
    }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        doDrawing(g)
    }

    private fun doDrawing(g: Graphics) {
        val g2d = g as Graphics2D

        g2d.color = Color.black
        g2d.fillRect(0, 0, d.width, d.height)

        drawMaze(g2d)
        drawScore(g2d)
        doAnimation()

        if (ingame)
            playGame(g2d)
        else
            showIntroScreen(g2d)

        g2d.drawImage(ii, 5, 5, this)
        Toolkit.getDefaultToolkit().sync()
        g2d.dispose()
    }

    internal inner class TAdapter : KeyAdapter() {

        override fun keyPressed(e: KeyEvent?) {
            val key = e!!.keyCode
            if (ingame) {
                onGameKeyReactions(key)
            } else {
                beforeGameKeyReactions(key)
            }
        }

        private fun onGameKeyReactions(key: Int) {
            when (key) {
                KeyEvent.VK_LEFT -> {
                    reqdx = -1
                    reqdy = 0
                }
                KeyEvent.VK_RIGHT -> {
                    reqdx = 1
                    reqdy = 0
                }
                KeyEvent.VK_UP -> {
                    reqdx = 0
                    reqdy = -1
                }
                KeyEvent.VK_DOWN -> {
                    reqdx = 0
                    reqdy = 1
                }
                KeyEvent.VK_ESCAPE -> if (timer.isRunning) {
                    ingame = false
                }
                KeyEvent.VK_PAUSE -> if (timer.isRunning) {
                    timer.stop()
                } else {
                    timer.start()
                }
            }
        }

        private fun beforeGameKeyReactions(key: Int) {
            if (key == 's'.toInt() || key == 'S'.toInt()) {
                ingame = true
                initGame()
            }
        }

        override fun keyReleased(e: KeyEvent?) {
            val key = e!!.keyCode

            if (key == Event.LEFT || key == Event.RIGHT
                    || key == Event.UP || key == Event.DOWN) {
                reqdx = 0
                reqdy = 0
            }
        }
    }

    override fun actionPerformed(e: ActionEvent) {
        repaint()
    }
}