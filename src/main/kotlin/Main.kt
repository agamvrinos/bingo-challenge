import kotlin.system.measureTimeMillis

fun main() {
  val executionTime = measureTimeMillis {
    for (i in 1..10000) {
//    println("####################################################")
//    println("############# Generating bingo strip $i #############")
//    println("####################################################")
      val game = BingoGame()
      game.createBingoStrip()
//      game.printFullStrip()
    }
  }
  println("Execution time: $executionTime ms")

}
