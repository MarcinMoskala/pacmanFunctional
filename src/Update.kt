import Direction.*
import GameConstant.blocksize
import GameConstant.nrofblocks
import GameConstant.pacmanspeed
import GameConstant.startMaze
import GameConstant.validspeeds

fun Model.update(signal: Signal): Model = when {
    dying -> onDeath()
    !ingame && signal.startGame -> setStartGhostsAndPacman()
            .copy(ingame = true)
            .setStartGhostsAndPacman()
    else -> movePacman(signal)
            .eatDots()
            .moveGhosts()
            .checkMaze()
}

private fun Model.eatDots(): Model = when {
    ingame && onFinalPosition(pacman.x, pacman.y) && maze.getRoundedBlock(pacman.x, pacman.y).haveDot -> copy(
            score = score + 1,
            maze = mazeWithDotEaten()
    )
    else -> this
}

private fun Model.mazeWithDotEaten() = maze.mapIndexed { i, mazeBlock -> if (i == getRoundedBlockNumber(pacman.x, pacman.y)) mazeBlock.copy(haveDot = false) else mazeBlock }

private fun Model.onDeath() = when {
    lifes <= 1 -> Model()
    else -> copy(lifes = lifes - 1).setStartGhostsAndPacman()
}

private fun Model.setStartGhostsAndPacman() = copy(
        pacman = pacman.copy(x = 7 * blocksize, y = 11 * blocksize),
        ghosts = (1..6).map { randomSpeedGhost(ghostMeanSpeed) }
)

private fun randomSpeedGhost(meanSpeed: Int) = Ghost(
        speed = validspeeds[Math.min(validspeeds.lastIndex, (Math.random() * (meanSpeed + 1)).toInt())]
)

private fun Model.checkMaze(): Model = when {
    ghostAndPacmanAtTheSameSpat() -> onDeath()
    maze.none { it.haveDot } -> onLevelWon()
    else -> this
}

private fun Model.ghostAndPacmanAtTheSameSpat(): Boolean {
    val pacmanSpat = getRoundedBlockNumber(pacman.x, pacman.y)
    return ghosts.any { getRoundedBlockNumber(it.x, it.y) == pacmanSpat }
}

private fun Model.onLevelWon() = copy(score = score + 50, maze = startMaze, ghostMeanSpeed = ghostMeanSpeed + 1)
        .setStartGhostsAndPacman()

private fun Model.moveGhosts(): Model = copy(ghosts = ghosts.map { it.move(maze) })

private fun Ghost.move(maze: List<MazeBlock>): Ghost = changeDirection().moveToDirection(maze)

private fun Ghost.changeDirection(): Ghost = when {
    onFinalPosition(x, y) -> copy(direction = values()[(Math.random() * 4).toInt()])
    else -> this
}

private fun onFinalPosition(x: Int, y: Int) = x % blocksize == 0 && y % blocksize == 0

private fun Ghost.moveToDirection(maze: List<MazeBlock>): Ghost = when {
    onFinalPosition(x, y) && direction in maze.getRoundedBlock(x, y).wallOnDirection -> this
    else -> copy(
            x = x + speed * xDiff(direction),
            y = y + speed * yDiff(direction)
    )
}

private fun Model.movePacman(signal: Signal): Model = copy(pacman = pacman.turn(signal).pacmanGoToDirection(maze))

private fun Pacman.turn(signal: Signal): Pacman = when {
    onFinalPosition(x, y) && signal.newDirection != None -> copy(direction = signal.newDirection)
    else -> this
}

private fun Pacman.pacmanGoToDirection(maze: List<MazeBlock>): Pacman = when {
    onFinalPosition(x, y) && direction in maze.getRoundedBlock(x, y).wallOnDirection -> this
    else -> copy(
            x = x + pacmanspeed * xDiff(direction),
            y = y + pacmanspeed * yDiff(direction),
            stateForPic = stateForPic % 4 + 1
    )
}

private fun xDiff(direction: Direction): Int = when (direction) {
    Left -> -1
    Right -> 1
    else -> 0
}

private fun yDiff(direction: Direction): Int = when (direction) {
    Up -> -1
    Down -> 1
    else -> 0
}

private fun List<MazeBlock>.getRoundedBlock(x: Int, y: Int) = get(getRoundedBlockNumber(x, y))

private fun getRoundedBlockNumber(x: Int, y: Int) = (x / blocksize).toInt() + (y / blocksize).toInt() * nrofblocks