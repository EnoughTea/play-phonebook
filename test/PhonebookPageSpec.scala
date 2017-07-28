import scala.collection.immutable

import org.scalatest.Matchers._
import org.scalatest._

class PhonebookPageSpec extends WordSpec {
  import models.{PhonebookEntry, PhonebookPage}

  val entries: immutable.IndexedSeq[PhonebookEntry] =
    for {i <- (0 until 30).map(_.toString) } yield PhonebookEntry(i, i)

  val pageSize = 10

  "A PhonebookPage of the phonebook with 3 pages of entries" when {
    "showing the first page" should {
      "have a valid next page and no previous pages" in {
        val index = 0
        val page = PhonebookPage(entries, index, pageSize)
        page.next shouldBe Some(index + 1)
        page.prev shouldBe None
      }
    }

    "showing the second page" should {
      "have a valid previous and next pages" in {
        val index = 1
        val page = PhonebookPage(entries, index, pageSize)
        page.next shouldBe Some(index + 1)
        page.prev shouldBe Some(index - 1)
      }
    }

    "showing the third page" should {
      "have a valid previous page and no next pages" in {
        val index = 2
        val page = PhonebookPage(entries, index, pageSize)
        page.next shouldBe None
        page.prev shouldBe Some(index - 1)
      }
    }
  }
}