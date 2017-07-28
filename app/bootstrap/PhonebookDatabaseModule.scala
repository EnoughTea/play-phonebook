package bootstrap

import javax.inject.Inject

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Random, Try}

import com.google.inject.AbstractModule
import models.PhonebookEntry
import play.{Configuration, Environment}
import services.Phonebook

/** Module used to fill phonebook with random entries once on application start. */
class PhonebookDatabaseModule(environment: Environment,
                              configuration: Configuration) extends AbstractModule {

  override def configure(): Unit = {
    // Random entries are unwieldy for testing, so tests will create their own module with fixed DB values.
    if (!environment.isTest) {
      bind(classOf[RandomEntriesDBFiller]).asEagerSingleton()
    }
  }
}

/** Populates DB with a set of random entries. */
private[bootstrap] class RandomEntriesDBFiller @Inject()(phonebook: Phonebook)
                                                        (implicit executionContext: ExecutionContext) {
  val randomEntriesCount = 20
  Await.result(phonebook.count().map(entriesCount =>
    if (entriesCount == 0) {
      populatePhonebook(randomEntriesCount)
    }), Duration.Inf)


  lazy val names = List(
    "Anatoliy", "Artemiy", "Bogdan ", "Bratislav", "Eduard", "Fyodor", "Gennadiy", "Gerasim", "Gleb",
    "Ignatiy", "Igor", "Ilya", "Jaromir", "Konstantin", "Lazar", "Leontiy", "Makar", "Nikita", "Panteley",
    "Timofey", "Vasiliy", "Viktor", "Vitaliy", "Vladimir", "Vyacheslav", "Yaroslav", "Yefrem", "Yegor"
  )

  lazy val surnames = List("Aleyev", "Beriya", "Bukov", "Bulgakov", "Gagarin", "Savvin", "Madulin", "Lipov",
    "Kandinsky", "Klimov", "Kurpatov", "Ipatyev", "Konnikov", "Zimin", "Tabakov", "Varushkin",
    "Nosachyov", "Preobrazhensky", "Sokolov", "Skorobogatov", "Kudryashov", "Strekalov", "Schastlivtsev", "Russkikh",
    "Shubin", "Kutuzov", "Kustov", "Vinokurov", "Khantsev", "Golubev")


  /**
    * Generates random phonebook entries and inserts them into DB.
    *
    * @param generatedEntriesCount Number of entries to generate.
    * @return Affected rows count.
    */
  def populatePhonebook(generatedEntriesCount: Int): Future[Try[Int]] = {
    require(generatedEntriesCount >= 0)
    val generatedEntries = for (_ <- 0 until generatedEntriesCount) yield randomPhonebookEntry()
    phonebook.insert(generatedEntries)
  }

  /**
    * Generates a single random phonebook entry.
    *
    * @return Random phonebook entry.
    */
  def randomPhonebookEntry(): PhonebookEntry = {
    def randNumber(ra: Range): Int = ra.head + Random.nextInt(ra.end - ra.head)

    def randElem[A](xs: List[A]): A = xs.apply(Random.nextInt(xs.size))

    def randPhoneNumber(): String = {
      s"+7 ${randNumber(100 until 1000)} " +
        s"${randNumber(100 until 1000)}-${randNumber(10 until 100)}-${randNumber(10 until 100)}"
    }

    def randName(): String = List(names, surnames).map(randElem).mkString(" ")

    PhonebookEntry(randName(), randPhoneNumber())
  }
}