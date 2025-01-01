import kotlin.math.abs
import kotlin.random.Random

private const val STRIP_SIZE = 6
private const val MAX_ATTEMPTS = 30

class BingoGame(
  private val tickets: Array<Ticket> = Array(STRIP_SIZE) { Ticket() },
  private var availableNumbersForColumn: Array<MutableList<Int>> = Array(COLUMNS_PER_TICKET) { index ->
    when (index) {
      0 -> (1..9).toMutableList()
      1 -> (10..19).toMutableList()
      2 -> (20..29).toMutableList()
      3 -> (30..39).toMutableList()
      4 -> (40..49).toMutableList()
      5 -> (50..59).toMutableList()
      6 -> (60..69).toMutableList()
      7 -> (70..79).toMutableList()
      8 -> (80..90).toMutableList()
      else -> mutableListOf()
    }
  },
) {

  fun createBingoStrip(): Array<Ticket> {
    setupInitialPositioning()
    populateStripTicketsWithNumbers()
    while (!validateAndReArrangeRows()) {
      resetAvailable()
      tickets.forEach { it.resetTicket() }
      setupInitialPositioning()
      populateStripTicketsWithNumbers()
    }

    tickets.forEach { it.sortTicketColumnsAscending() }
    return tickets
  }

  private fun resetAvailable() {
    availableNumbersForColumn = Array(COLUMNS_PER_TICKET) { index ->
      when (index) {
        0 -> (1..9).toMutableList()
        1 -> (10..19).toMutableList()
        2 -> (20..29).toMutableList()
        3 -> (30..39).toMutableList()
        4 -> (40..49).toMutableList()
        5 -> (50..59).toMutableList()
        6 -> (60..69).toMutableList()
        7 -> (70..79).toMutableList()
        8 -> (80..90).toMutableList()
        else -> mutableListOf()
      }
    }
  }

  private fun setupInitialPositioning() {
    for (column in 0 until COLUMNS_PER_TICKET) {
      val combination = generateCombinationForColumn(availableNumbersForColumn[column].size)
      for (ticketIdx in tickets.indices) {
        val currentTicket = tickets[ticketIdx]
        currentTicket.setNumbersCountForColumn(column, combination[ticketIdx])
      }
    }
  }

  private fun populateStripTicketsWithNumbers() =
    tickets.forEach { it.populateTicket(availableNumbersForColumn) }

  private fun validateAndReArrangeRows(): Boolean {
    for (ticketIdx in tickets.indices) {
      val currentTicket = tickets[ticketIdx]
      for (row in currentTicket.getTicketArray().indices) {
        val rowDelta = currentTicket.getRowDelta(row)
        val isSuccess = when {
          rowDelta > 0 -> addExtraCells(rowDelta, currentTicket, row, ticketIdx)
          rowDelta < 0 -> removeExtraCells(rowDelta, currentTicket, row, ticketIdx)
          else -> true // row is already balanced
        }
        if (!isSuccess) return false
      }
    }
    return true
  }

  private fun addExtraCells(
    rowDelta: Int,
    currentTicket: Ticket,
    row: Int,
    ticketIdx: Int,
  ): Boolean {
    var count = 0
    var remaining = rowDelta
    while (remaining > 0) {
      if (++count == MAX_ATTEMPTS) {
        return false
      }
      // find empty column to fill in
      val emptyColumn = currentTicket.findEmptyCellForRow(row)
      // find candidate
      for (candidateTicketIdx in ticketIdx until tickets.size) {
        val currentCandidateTicket = tickets[candidateTicketIdx]
        val startFromRow = if (candidateTicketIdx == ticketIdx) row else -1
        val cellValue = currentCandidateTicket.findAndResetCellForColumn(startFromRow, emptyColumn)
        if (cellValue != -1) {
          currentTicket.setCell(row, emptyColumn, cellValue)
          remaining--
          break
        }
      }
    }
    return true
  }

  private fun removeExtraCells(
    rowDelta: Int,
    currentTicket: Ticket,
    row: Int,
    ticketIdx: Int,
  ): Boolean {
    var count = 0
    var remaining = abs(rowDelta)
    while (remaining > 0) {
      if (++count == MAX_ATTEMPTS) {
        return false
      }
      // find non-empty column to remove from
      val nonEmptyColIndex = currentTicket.findNonEmptyCellForRow(row)
      // find candidate
      for (candidateTicketIdx in ticketIdx until tickets.size) {
        val numbersCountForColumn = currentTicket.getNumbersCountForColumn(nonEmptyColIndex)
        val willColumnBecomeInvalidAfterRemoval = numbersCountForColumn == 1 && candidateTicketIdx != ticketIdx
        if (willColumnBecomeInvalidAfterRemoval) {
          break // retry with a new column
        }

        val candidateTicket = tickets[candidateTicketIdx]
        val startFromRow = if (candidateTicketIdx == ticketIdx) row else -1
        val found = candidateTicket.findAndSetCellForColumn(
          startFromRow,
          nonEmptyColIndex,
          currentTicket.getCell(row, nonEmptyColIndex)
        )
        if (found) {
          currentTicket.resetCell(row, nonEmptyColIndex)
          remaining --
          break
        }
      }
    }
    return true
  }

  private fun generateCombinationForColumn(n: Int): List<Int> {
    require(n in 6..18) { "Sum must be between 6 and 18 for 6 values between 1 and 3" }
    val combinationListSize = 6
    val minValue = 1
    val maxValue = 3

    val result = MutableList(combinationListSize) { minValue }
    var remaining = n - combinationListSize
    while (remaining > 0) {
      val index = Random.nextInt(combinationListSize)
      if (result[index] < maxValue) {
        val add = minOf(remaining, maxValue - result[index])
        result[index] += add
        remaining -= add
      }
    }
    return result
  }

  fun printFullStrip() {
    for (i in tickets.indices) {
      println("--------- Ticket [${i + 1}] ---------")
      println(tickets[i])
    }
  }
}


