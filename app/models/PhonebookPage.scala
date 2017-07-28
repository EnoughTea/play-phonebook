package models


/**
  * Represents a phonebook page with its entries, linked to previous and next pages.
  * @param entries This page entries.
  * @param page This page index, starts with 0.
  * @param offset This page offset: amount of entries in pages before this page.
  * @param count Total count of entries in all of the pages.
  */
case class PhonebookPage(entries: Seq[PhonebookEntry], page: Int, offset: Int, count: Int) {
  require(entries != null)
  require(page >= 0)
  require(offset >= 0)
  require(count >= 0)

  /** Previous page index or [[None]]. */
  lazy val prev: Option[Int] = Option(page - 1).filter(_ >= 0)

  /** Next page index or [[None]]. */
  lazy val next: Option[Int] = Option(page + 1).filter(_ => (offset + entries.length) < count)
}


object PhonebookPage {
  /** Gets the default value for empty page. */
  val Empty = PhonebookPage(Seq[PhonebookEntry](), 0, 0, 0)

  /**
    * Creates a phonebook page of the specified index containing appropriate
    * portion of entries taken from the given sequence of total entries.
    * @param allEntries All entries in the phonebook.
    * @param page Index of the created page.
    * @param pageSize Number of entries per page.
    * @return Phonebook page containing appropriate portion of total entries.
    */
  def apply(allEntries: Seq[PhonebookEntry], page: Int, pageSize: Int): PhonebookPage = {
    val offset = pageSize * page
    val pageEntries = allEntries.slice(offset, offset + pageSize)
    PhonebookPage(pageEntries, page, offset, allEntries.length)
  }
}