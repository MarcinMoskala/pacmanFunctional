import Direction.*
import GameConstant.blocksize
import GameConstant.nrofblocks
import GameConstant.scrsize
import java.awt.*
import javax.swing.ImageIcon

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

private val d = Dimension(400, 400)
private val smallfont = Font("Helvetica", Font.BOLD, 14)
private val dotcolor = Color(192, 192, 0)
private var mazecolor: Color = Color(5, 100, 5)

fun display(g2d: Graphics2D, model: Model) {
    drawBackground(g2d)
    drawMaze(model, g2d)
    drawScore(g2d, model)

    if (model.ingame) {
        drawPacman(g2d, model)
        drawGhosts(g2d, model)
    } else
        showIntroScreen(g2d)
}

private fun drawBackground(g2d: Graphics2D) {
    g2d.color = Color.black
    g2d.fillRect(0, 0, d.width, d.height)
}

private fun drawMaze(model: Model, g2d: Graphics2D) {
    model.maze.forEachIndexed { i, mazeBlock ->
        val x = (i % nrofblocks) * blocksize
        val y = ((i - (i % nrofblocks)) / nrofblocks) * blocksize
        drawWalls(g2d, mazeBlock, x, y)
        drawDot(g2d, mazeBlock, x, y)
    }
}

private fun drawDot(g2d: Graphics2D, mazeBlock: MazeBlock, x: Int, y: Int) {
    if (mazeBlock.haveDot) {
        g2d.color = dotcolor
        g2d.fillRect(x + 11, y + 11, 2, 2)
    }
}

private fun drawWalls(g2d: Graphics2D, mazeBlock: MazeBlock, x: Int, y: Int) {
    g2d.color = mazecolor
    g2d.stroke = BasicStroke(2f)

    if (Left in mazeBlock.wallOnDirection)
        g2d.drawLine(x, y, x, y + blocksize - 1)

    if (Up in mazeBlock.wallOnDirection)
        g2d.drawLine(x, y, x + blocksize - 1, y)

    if (Right in mazeBlock.wallOnDirection)
        g2d.drawLine(x + blocksize - 1, y, x + blocksize - 1, y + blocksize - 1)

    if (Down in mazeBlock.wallOnDirection)
        g2d.drawLine(x, y + blocksize - 1, x + blocksize - 1, y + blocksize - 1)
}

private fun drawPacman(g2d: Graphics2D, model: Model) {
    g2d.drawImage(pacManPic(model), model.pacman.x + 1, model.pacman.y + 1, null)
}

private fun drawGhosts(g2d: Graphics2D, model: Model) {
    model.ghosts.forEach { g2d.drawImage(ghost, it.x, it.y, null) }
}

private fun drawScore(g2d: Graphics2D, model: Model) {
    g2d.font = smallfont
    g2d.color = Color(96, 128, 255)
    g2d.drawString("Score: " + model.score, scrsize / 2 + 96, scrsize + 16)

    (1..model.lifes).forEach {
        g2d.drawImage(pacman3right, it * 28 + 8, scrsize + 1, null)
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
        drawString(string, (scrsize - getFontMetrics(smallFont).stringWidth(string)) / 2, scrsize / 2)
    }
}

private fun pacManPic(model: Model) = when (model.pacman.direction) {
    Right -> pacmanRightPic(model)
    Down -> pacmanDownPic(model)
    Left -> pacmanLeftPic(model)
    Up -> pacmanUpPic(model)
    None -> pacman1
}

private fun pacmanUpPic(model: Model) = when (model.pacman.stateForPic % 4) {
    0 -> pacman2up
    1, 3 -> pacman3up
    2 -> pacman4up
    else -> pacman1
}

private fun pacmanDownPic(model: Model) = when (model.pacman.stateForPic % 4) {
    0 -> pacman2down
    1, 3 -> pacman3down
    2 -> pacman4down
    else -> pacman1
}

private fun pacmanLeftPic(model: Model) = when (model.pacman.stateForPic % 4) {
    0 -> pacman2left
    1, 3 -> pacman3left
    2 -> pacman4left
    else -> pacman1
}

private fun pacmanRightPic(model: Model) = when (model.pacman.stateForPic % 4) {
    0 -> pacman2right
    1, 3 -> pacman3right
    2 -> pacman4right
    else -> pacman1
}