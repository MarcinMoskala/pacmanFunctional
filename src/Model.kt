import Direction.Left
import GameConstant.blocksize
import GameConstant.startMaze

data class Model(
        val ingame: Boolean = false,
        val dying: Boolean = false,
        val pacman: Pacman = Pacman(),
        val lifes: Int = 3,
        val score: Int = 0,
        val ghostMeanSpeed: Int = 3,
        val maze: List<MazeBlock> = startMaze,
        val ghosts: List<Ghost> = emptyList()
)

data class Ghost(
        val x: Int = 4 * blocksize,
        val y: Int = 4 * blocksize,
        val direction: Direction = Left,
        val speed: Int
)

data class Pacman(
        val x: Int = 0,
        val y: Int = 0,
        val direction: Direction = Left,
        val stateForPic: Int = 0
)

data class MazeBlock(
        val wallOnDirection: List<Direction>,
        val haveDot: Boolean = false
)

enum class Direction {
    Up, Down, Left, Right, None
}