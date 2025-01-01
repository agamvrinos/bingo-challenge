import kotlin.random.Random

const val ROWS_PER_TICKET = 3
const val COLUMNS_PER_TICKET = 9

class Ticket(
  private val numbersCountPerColumn: Array<Int> = Array(9) { 0 },
  private val ticketArray: Array<Array<Int>> = Array(3) { Array(9) { -1 } },
) {

  fun getRowDelta(row: Int): Int {
    val numbersPerRow = 5
    return numbersPerRow - ticketArray[row].count { it != -1 }
  }

  fun populateTicket(availableNumbersForColumn: Array<MutableList<Int>>) {
    for (column in 0 until COLUMNS_PER_TICKET) {
      val numbersPerColumn = numbersCountPerColumn[column]
      val availableNumbers = availableNumbersForColumn[column]
      for (i in 1..numbersPerColumn) {
        val randomValueFromPoolIndex = Random.nextInt(0, availableNumbers.size)
        val value = availableNumbers[randomValueFromPoolIndex]
        var rowWithEmptySpotForColumn = -1
        while (rowWithEmptySpotForColumn == -1) {
          val randomRow = Random.nextInt(0, ROWS_PER_TICKET)
          if (isCellEmpty(randomRow, column)) {
            rowWithEmptySpotForColumn = randomRow
          }
        }
        ticketArray[rowWithEmptySpotForColumn][column] = value
        availableNumbers.removeAt(randomValueFromPoolIndex)
      }
    }
  }

  fun sortTicketColumnsAscending() {
    for (col in 0 until COLUMNS_PER_TICKET) {
      val columnValues = mutableListOf<Int>()
      for (row in 0 until ROWS_PER_TICKET) {
        if (!isCellEmpty(row, col)) {
          columnValues.add(ticketArray[row][col])
        }
      }
      columnValues.sort()
      var index = 0
      for (row in 0 until ROWS_PER_TICKET) {
        if (!isCellEmpty(row, col)) {
          ticketArray[row][col] = columnValues[index++]
        }
      }
    }
  }

  fun findEmptyCellForRow(row: Int): Int {
    while (true) {
      val randomColumn = Random.nextInt(0, COLUMNS_PER_TICKET)
      if (isCellEmpty(row, randomColumn)) {
        return randomColumn
      }
    }
  }

  fun findNonEmptyCellForRow(row: Int): Int {
    while (true) {
      val randomColumn = Random.nextInt(0, COLUMNS_PER_TICKET)
      if (!isCellEmpty(row, randomColumn)) {
        return randomColumn
      }
    }
  }

  fun findAndResetCellForColumn(startFromRow: Int, column: Int): Int {
    val rowStart = if (startFromRow == -1) 0 else startFromRow
    for (row in rowStart until ROWS_PER_TICKET) {
      val cellValue = ticketArray[row][column]
      // make sure that when removing, the column won't remain empty
      val willColumnRemainValid = numbersCountPerColumn[column] > 1 || startFromRow != -1
      if (!isCellEmpty(row, column) && willColumnRemainValid) {
        resetCell(row, column)
        return cellValue
      }
    }
    return -1
  }

  fun findAndSetCellForColumn(startFromRow: Int, column: Int, value: Int): Boolean {
    val rowStart = if (startFromRow == -1) 0 else startFromRow
    for (row in rowStart until ROWS_PER_TICKET) {
      if (isCellEmpty(row, column)) {
        setCell(row, column, value)
        return true
      }
    }
    return false
  }

  fun getTicketArray() = ticketArray

  fun setNumbersCountForColumn(column: Int, value: Int) {
    numbersCountPerColumn[column] = value
  }

  fun getNumbersCountForColumn(column: Int) =
    numbersCountPerColumn[column]

  private fun isCellEmpty(row: Int, column: Int) =
    ticketArray[row][column] == -1

  fun setCell(row: Int, column: Int, value: Int) {
    ticketArray[row][column] = value
    numbersCountPerColumn[column]++
  }

  fun getCell(row: Int, column: Int) =
    ticketArray[row][column]

  fun resetCell(row: Int, column: Int) {
    ticketArray[row][column] = -1
    numbersCountPerColumn[column]--
  }

  fun resetTicket() {
    for (row in 0 until ROWS_PER_TICKET) {
      for (column in 0 until COLUMNS_PER_TICKET) {
        ticketArray[row][column] = -1
      }
    }
    for (column in 0 until COLUMNS_PER_TICKET) {
      numbersCountPerColumn[column] = 0
    }
  }

  override fun toString(): String {
    return ticketArray.joinToString(separator = "\n") { row ->
      row.joinToString(separator = " | ") { cell ->
        if (cell == -1) "-" else cell.toString().padStart(2, ' ')
      }
    }
  }
}
