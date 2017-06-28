package net.soundvibe.kata

enum class Player { PLAYER_ONE, PLAYER_TWO }
enum class Point { LOVE, FIFTEEN, THIRTY }

sealed class Score
data class Points(val playerOnePoint: Point, val playerTwoPoint: Point): Score()
data class Forty(val player: Player, val otherPlayerPoint: Point): Score()
object Deuce: Score()
data class Advantage(val player: Player): Score()
data class Game(val player: Player): Score()

fun newGame(): Score = Points(Point.LOVE, Point.LOVE)

fun Score.scoredBy(winner: Player) = score(this, winner)

fun score(current: Score, winner: Player) = when (current) {
    is Points -> scoreWhenPoints(current, winner)
    is Forty -> scoreWhenForty(current, winner)
    Deuce -> Advantage(winner)
    is Advantage -> scoreWhenAdvantage(current, winner)
    is Game -> Game(winner)
}

fun scoreSeq(vararg winners: Player) = winners.foldRight(newGame(),
        { player, score -> score(score, player) })

fun scoreWhenPoints(points: Points, scoredPlayer: Player): Score {
    val p1Scored = scoredPlayer == Player.PLAYER_ONE
    val pointToIncrease = if (scoredPlayer == Player.PLAYER_ONE) points.playerOnePoint else points.playerTwoPoint

    return pointToIncrease.increment()?.let {
        Points(if (p1Scored) it else points.playerOnePoint, if (p1Scored) points.playerTwoPoint else it)
    } ?:Forty(scoredPlayer, if (p1Scored) points.playerTwoPoint else points.playerOnePoint)
}

fun scoreWhenForty(forty: Forty, scoredPlayer: Player): Score =
        if (forty.player == scoredPlayer) Game(scoredPlayer)
        else forty.otherPlayerPoint.increment()?.let { Forty(forty.player, it) } ?: Deuce

fun scoreWhenAdvantage(advantage: Advantage, scoredPlayer: Player) =
        if (scoredPlayer == advantage.player) Game(advantage.player) else Deuce

fun Player.other() = if (this == Player.PLAYER_ONE) Player.PLAYER_TWO else Player.PLAYER_ONE

fun Player.points(current: Points) =
        if (this == Player.PLAYER_ONE) current.playerOnePoint else current.playerTwoPoint

fun Player.pointTo(point: Point, current: Points) =
        if (this == Player.PLAYER_ONE) Points(point, current.playerTwoPoint)
        else Points(current.playerOnePoint, point)

fun Point.increment(): Point? = when (this) {
    Point.LOVE -> Point.FIFTEEN
    Point.FIFTEEN -> Point.THIRTY
    Point.THIRTY -> null
}




