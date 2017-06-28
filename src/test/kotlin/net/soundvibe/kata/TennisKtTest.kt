package net.soundvibe.kata

import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.BehaviorSpec
import io.kotlintest.specs.StringSpec

/**
 * @author linas on 17.6.28.
 */

class TennisKtTest: BehaviorSpec() {
    init {

        given("advantaged player") {
            `when`("advantaged player wins") {
                then("score is correct") {
                    forAll(PlayerGenerator, { advantagedPlayer ->
                        assertThat {
                            val actual = score(Advantage(advantagedPlayer), advantagedPlayer)
                            val expected = Game(advantagedPlayer)
                            expected == actual
                        }
                    })
                }
            }

            `when`("other player scoredBy") {
                then("score is correct") {
                    forAll(PlayerGenerator, { advantagedPlayer ->
                        assertThat {
                            val actual = score(Advantage(advantagedPlayer), advantagedPlayer.other())
                            val expected = Deuce
                            expected == actual
                        }
                    })
                }
            }
        }

        given("a deuce") {
            val deuce = Deuce
            `when`("other player scoredBy") {
                then("score is correct") {
                    forAll(PlayerGenerator, { otherPlayer ->
                        assertThat {
                            val actual = score(deuce, otherPlayer)
                            val expected = Advantage(otherPlayer)
                            expected == actual
                        }
                    })
                }
            }
        }

        given("player has 30") {
            `when`("player scoredBy") {
                then("score is correct") {
                    forAll(PointsGenerator, PlayerGenerator, { points, scorer ->
                        assertThat {
                            val current = scorer.pointTo(Point.THIRTY, points)
                            val actual = score(current, scorer)
                            val expected = Forty(scorer, scorer.other().points(current))
                            expected == actual
                        }
                    })
                }
            }
        }

        given("player has 40") {
            `when`("player scoredBy") {
                then("score is correct") {
                    forAll(FortyGenerator, { forty ->
                        assertThat {
                            val actual = score(forty, forty.player)
                            val expected = Game(forty.player)
                            expected == actual
                        }
                    })
                }
            }

            `when`("other player has 30 and scoredBy") {
                then("score is correct") {
                    forAll(FortyGenerator, { (player) ->
                        assertThat {
                            val f = Forty(player, Point.THIRTY)
                            val actual = score(f, f.player.other())
                            val expected = Deuce
                            expected == actual
                        }
                    })
                }
            }

            `when`("other player has 15 and scoredBy") {
                then("score is correct") {
                    forAll(FortyGenerator, { (player) ->
                        assertThat {
                            val f = Forty(player, Point.FIFTEEN)
                            val actual = score(f, f.player.other())
                            val expected = Forty(f.player, Point.THIRTY)
                            expected == actual
                        }
                    })
                }
            }

            `when`("other player has Love and scoredBy") {
                then("score is correct") {
                    forAll(FortyGenerator, { (player) ->
                        assertThat {
                            val f = Forty(player, Point.LOVE)
                            val actual = score(f, f.player.other())
                            val expected = Forty(f.player, Point.FIFTEEN)
                            expected == actual
                        }
                    })
                }
            }
        }

    }
}

class GamePlayTests: StringSpec() {
    init {

        "Should play full game" {
            val newGame = newGame()
            val actual = newGame.scoredBy(Player.PLAYER_ONE)
                    .scoredBy(Player.PLAYER_ONE)
                    .scoredBy(Player.PLAYER_TWO)
                    .scoredBy(Player.PLAYER_ONE)
                    .scoredBy(Player.PLAYER_TWO)
                    .scoredBy(Player.PLAYER_ONE)

            val expected = Game(Player.PLAYER_ONE)
            actual shouldBe expected
        }

        "Should play game using seq" {
            val actual = scoreSeq(
                    Player.PLAYER_ONE,
                    Player.PLAYER_ONE,
                    Player.PLAYER_TWO,
                    Player.PLAYER_ONE,
                    Player.PLAYER_TWO,
                    Player.PLAYER_ONE)
            val expected = Game(Player.PLAYER_ONE)
            actual shouldBe expected
        }

        "Should play full game using seq and too many winners" {
            val actual = scoreSeq(
                    Player.PLAYER_ONE,
                    Player.PLAYER_ONE,
                    Player.PLAYER_TWO,
                    Player.PLAYER_ONE,
                    Player.PLAYER_TWO,
                    Player.PLAYER_ONE,
                    Player.PLAYER_TWO,
                    Player.PLAYER_TWO,
                    Player.PLAYER_TWO)
            val expected = Game(Player.PLAYER_ONE)
            actual shouldBe expected
        }
    }
}

fun assertThat(body: () -> Boolean) = body()

object PlayerGenerator : Gen<Player> {
    private val gen = Gen.oneOf(listOf(Player.PLAYER_ONE.name, Player.PLAYER_TWO.name))

    override fun generate(): Player = Player.valueOf(gen.generate())
}

object PointGenerator : Gen<Point> {
    private val gen = Gen.oneOf(listOf(Point.THIRTY.name, Point.FIFTEEN.name, Point.LOVE.name))

    override fun generate(): Point = Point.valueOf(gen.generate())
}

object PointsGenerator : Gen<Points> {
    override fun generate(): Points = Points(PointGenerator.generate(), PointGenerator.generate())
}

object FortyGenerator : Gen<Forty> {
    override fun generate(): Forty = Forty(PlayerGenerator.generate(), PointGenerator.generate())
}