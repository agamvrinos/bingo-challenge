import org.junit.jupiter.api.DisplayName
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import kotlin.random.Random

class TicketTest {

  @Test
  @DisplayName(
    """
    Given a predetermined numbers count for each column and available numbers per column, 
    when populating the ticket, 
    then numbers within the proper range per column should be filled in and the count 
    should be as expected 
  """
  )
  fun testProperTicketPopulation() {
    // given
    val ticket = Ticket()
    val availableNumbersForColumn = Array(COLUMNS_PER_TICKET) { index ->
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
    val copiedAvailableNumbersForColumn = Array(availableNumbersForColumn.size) { index ->
      availableNumbersForColumn[index].toMutableList()
    }
    for (column in 0 until COLUMNS_PER_TICKET) {
      val numbersInColumn = Random.nextInt(1, 4)
      ticket.setNumbersCountForColumn(column, numbersInColumn)
    }
    // when
    ticket.populateTicket(copiedAvailableNumbersForColumn)

    // then
    for (column in 0 until COLUMNS_PER_TICKET) {
      var numbersCount = 0
      for (row in 0 until ROWS_PER_TICKET) {
        val cellValue = ticket.getTicketArray()[row][column]
        if (cellValue != -1) {
          numbersCount++
          assertTrue { cellValue in availableNumbersForColumn[column] }
        }
      }
      assertTrue { numbersCount in 1..3 }
      assertEquals(ticket.getNumbersCountForColumn(column), numbersCount)
    }
  }

  @Test
  @DisplayName(
    """
    Given a ticket without sorted values per column,
    when sorting them, 
    the ticket should have them sorted from now on
  """
  )
  fun testSortTicketColumnsAscending() {
    // given
    val ticket = Ticket()
    val availableNumbersForColumn = Array(COLUMNS_PER_TICKET) { index ->
      when (index) {
        0 -> (1..9).toMutableList()
        else -> mutableListOf()
      }
    }
    ticket.setNumbersCountForColumn(0, 3)
    ticket.populateTicket(availableNumbersForColumn)
    // when
    ticket.sortTicketColumnsAscending()

    // then
    val ticketArray = ticket.getTicketArray()
    for (col in 0 until COLUMNS_PER_TICKET) {
      var lastValue = -1
      for (row in 0 until ROWS_PER_TICKET) {
        val value = ticketArray[row][col]
        if (value != -1) {
          assertTrue(value >= lastValue)
          lastValue = value
        }
      }
    }
  }

  @Test
  @DisplayName(
    """
    Given a ticket with some numbers,
    when resetting it, 
    then the ticket should no longer have numbers
  """)
  fun testResetTicket() {
    // given
    val ticket = Ticket()
    val availableNumbersForColumn = Array(COLUMNS_PER_TICKET) { index ->
      when (index) {
        0 -> (1..9).toMutableList()
        else -> mutableListOf()
      }
    }
    ticket.setNumbersCountForColumn(0, 3)
    ticket.populateTicket(availableNumbersForColumn)
    // when
    ticket.resetTicket()

    // then
    for (row in 0 until ROWS_PER_TICKET) {
      for (col in 0 until COLUMNS_PER_TICKET) {
        assertEquals(-1, ticket.getTicketArray()[row][col])
      }
    }
    for (col in 0 until COLUMNS_PER_TICKET) {
      assertEquals(0, ticket.getNumbersCountForColumn(col))
    }
  }

  // ++ more tests are possible for the remaining methods
}
