import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Success

import models.PhonebookEntry
import org.scalatest.Matchers._
import org.scalatest._
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions
import play.api.test.Helpers._

/** Tests data access layer. */
class PhonebookEntriesDALSpec extends PlaySpec with GuiceOneAppPerSuite with BeforeAndAfterEach {

  import dal.PhonebookEntriesDAL

  val databaseApi: DBApi = app.injector.instanceOf[DBApi]
  val phonebookDAO: PhonebookEntriesDAL = app.injector.instanceOf(classOf[PhonebookEntriesDAL])


  "A PhonebookEntriesDAO" should {
    "delete entry by ID" in {
      await(phonebookDAO.delete(2)) shouldBe Success(1)

      await(phonebookDAO.count()) shouldBe 9
    }

    "not delete anything when invalid ID is passed" in {
      await(phonebookDAO.delete(20)) shouldBe Success(0)

      await(phonebookDAO.count()) shouldBe 10
    }

    "count entries total" in {
      await(phonebookDAO.count()) shouldBe 10
    }

    "find single existing entry by its name and phone" in {
      await(phonebookDAO.findByNameAndPhoneNumber("Gamma", "3")) shouldBe
        Some(PhonebookEntry("Gamma", "3", 4))
    }

    "find entries by their name or phone" in {
      val entries = await(phonebookDAO.findByNameOrPhoneNumber("beta", "0")).get

      entries.length shouldBe 2
      entries.head shouldBe PhonebookEntry("Alpha", "0", 1)
      entries(1) shouldBe PhonebookEntry("Beta", "2", 3)
    }

    "find entries by their name" in {
      val entries = await(phonebookDAO.findByNameOrPhoneNumber("%eta%", "%eta%")).get

      entries.length shouldBe 4
      entries.head shouldBe PhonebookEntry("Beta", "2", 3)
      entries(1) shouldBe PhonebookEntry("Zeta", "6", 7)
      entries(2) shouldBe PhonebookEntry("Eta", "7", 8)
      entries(3) shouldBe PhonebookEntry("Theta", "8", 9)
    }

    "find entries by their phone" in {
      val entries = await(phonebookDAO.findByNameOrPhoneNumber("%1%", "%1%")).get

      entries.length shouldBe 2
      entries.head shouldBe PhonebookEntry("Alpha (2)", "1", 2)
      entries(1) shouldBe PhonebookEntry("Iota", "123456789", 10)
    }

    "insert entry" in {
      await(phonebookDAO.insert(PhonebookEntry("Kappa", "10"))) shouldBe Success(1)

      await(phonebookDAO.count()) shouldBe 11
    }

    "insert entries" in {
      val entries = Seq(
        PhonebookEntry("Kappa", "10"),
        PhonebookEntry("Lambda", "11"),
        PhonebookEntry("Mu", "12")
      )

      await(phonebookDAO.insert(entries)) shouldBe Success(3)

      await(phonebookDAO.count()) shouldBe 13
    }

    "update entry" in {
      val newEntry = PhonebookEntry("Omega", "23", 2)
      await(phonebookDAO.update(newEntry.id, newEntry)) shouldBe Success(1)

      val all = await(phonebookDAO.selectAll()).get
      all(newEntry.id.toInt - 1) shouldBe newEntry
      await(phonebookDAO.count()) shouldBe 10
    }

    "do not update entry with invalid ID" in {
      val newEntry = PhonebookEntry("Omega", "23", 20)
      await(phonebookDAO.update(newEntry.id, newEntry)) shouldBe Success(0)
    }
  }

  /////////////////////////////////////////////
  // Initialize database for each test case.
  /////////////////////////////////////////////
  override def beforeEach(): Unit = {
    val db = databaseApi.database("default")
    Evolutions.applyEvolutions(db)
    Await.result({
      val entries = Seq(
        PhonebookEntry("Alpha", "0"),
        PhonebookEntry("Alpha (2)", "1"),
        PhonebookEntry("Beta", "2"),
        PhonebookEntry("Gamma", "3"),
        PhonebookEntry("Delta", "4"),
        PhonebookEntry("Epsilon", "5"),
        PhonebookEntry("Zeta", "6"),
        PhonebookEntry("Eta", "7"),
        PhonebookEntry("Theta", "8"),
        PhonebookEntry("Iota", "123456789")
      )
      phonebookDAO.insert(entries)
    }, Duration.Inf)
    db.shutdown()
  }

  override def afterEach(): Unit = {
    val db = databaseApi.database("default")
    Evolutions.cleanupEvolutions(db)
    db.shutdown()
  }
}
