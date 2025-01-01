import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BingoGameTest {

  @Test
  @DisplayName(
    """
    Given fixed Bingo-90 parameters, 
    when creating a Bingo game, 
    then a strip of 6 tickets is created,
     each ticket consists of 3 rows and 9 columns
     all numbers [1-90] are populated and there are no duplicates,
     all rows have exactly 5 numbers and 4 blanks
     and there is no column within a ticket with all blanks
  """
  )
  fun testProperBingoGameCreation() {
    // given, when
    val game = BingoGame()
    val tickets = game.createBingoStrip()

    // then
    validateTicketsSize(tickets)
    validateAllNumbersNoDuplicates(tickets)
    validateBlanksAndNumbersCount(tickets)
    validateNoEmptyColumns(tickets)
  }

  private fun validateTicketsSize(tickets: Array<Ticket>) {
    assertEquals(tickets.size, 6)

    for (ticket in tickets) {
      val ticketArray = ticket.getTicketArray()
      assertEquals(ticketArray.size, 3)
      assertEquals(ticketArray[0].size, 9)
    }
  }

  private fun validateBlanksAndNumbersCount(tickets: Array<Ticket>) {
    for (ticket in tickets) {
      val ticketArray = ticket.getTicketArray()
      for (row in 0 until ROWS_PER_TICKET) {
        val numbersCount = ticketArray[row].count { it != -1 }
        val blanksCount = ticketArray[row].count { it == -1 }
        assertEquals(numbersCount, 5)
        assertEquals(blanksCount, 4)
      }
    }
  }

  private fun validateAllNumbersNoDuplicates(tickets: Array<Ticket>) {
    // could prob also validate that numbers out of this range don't exist
    (1..90).forEach {
      var count = 0
      for (ticket in tickets) {
        val ticketArray = ticket.getTicketArray()
        for (row in 0 until ROWS_PER_TICKET) {
          for (column in 0 until COLUMNS_PER_TICKET) {
            if (ticketArray[row][column] == it) {
              count++
            }
          }
        }
      }
      assertEquals(count, 1)
    }
  }

  private fun validateNoEmptyColumns(tickets: Array<Ticket>) {
    for (ticket in tickets) {
      val ticketArray = ticket.getTicketArray()
      for (column in 0 until COLUMNS_PER_TICKET) {
        var blanksCount = 0
        for (row in 0 until ROWS_PER_TICKET) {
          if (ticketArray[row][column] == -1) {
            blanksCount ++
          }
        }
        assertTrue { blanksCount < 3 }
      }
    }
  }
}