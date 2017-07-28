import org.scalatest._
import org.scalatest.Matchers._

/** Makes sure that phonebook entry model behaves as expected. */
class PhonebookEntrySpec extends WordSpec {
  import models.PhonebookEntry

  "A PhonebookEntry" when {
    "having some name and phone" should {
      "present them when converted to string" in {
        val entry = PhonebookEntry("Vlad", "+7 (123) 4567890")

        entry.toString shouldBe s"${entry.name} — ${entry.phoneNumber}"
      }
    }
  }
}