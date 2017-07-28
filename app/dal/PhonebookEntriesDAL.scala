package dal

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}
import scala.util._

import models.{PhonebookEntry, PhonebookPage}
import org.h2.util.StringUtils
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile


/** Defines DB table for [[PhonebookEntry]]. */
trait PhonebookEntriesComponent extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  class PhonebookEntries(tag: Tag) extends Table[PhonebookEntry](tag, "phonebook_entries") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def phoneNumber = column[String]("phone_number")

    // Two string indexes blow up in size pretty fast, but our small phonebook can afford it.
    def idxName = index("idx_name", name)

    def idxPhoneNumber = index("idx_phone_number", name)

    override def * =
      (name, phoneNumber, id) <> (PhonebookEntry.tupled, PhonebookEntry.unapply)
  }

}


/**
  * Performs DB operations related to [[PhonebookEntry]], serves as a data access layer.
  *
  * Uses dependency injection to obtain an instance of [[DatabaseConfigProvider]]
  * for the database named `default` defined in the **application.conf* file.
  *
  * @param dbConfigProvider Provider of a [[slick.basic.DatabaseConfig]] instance.
  * @param executionContext Execution context.
  */
@Singleton()
class PhonebookEntriesDAL @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                                   (implicit executionContext: ExecutionContext)
  extends PhonebookEntriesComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  /** Slick table query for a [[PhonebookEntry]] table row. */
  val entries: TableQuery[PhonebookEntries] = TableQuery[PhonebookEntries]

  /** Count all phonebook entries. */
  def count(): Future[Int] = {
    db.run(entries.length.result)
  }

  /**
    * Deletes phonebook entry with given id.
    *
    * @param id Id of the entry to delete.
    * @return Affected rows count.
    */
  def delete(id: Long): Future[Try[Int]] = {
    db.run(entries.filter(_.id === id).delete)
      .map(Success(_))
      .recover { case ex: Exception => Failure(ex) }
  }

  /**
    * Finds phonebook entry with given id.
    *
    * @param id Id of the entry to find.
    * @return Found entry or [[None]].
    */
  def findById(id: Long): Future[Option[PhonebookEntry]] = {
    db.run(entries.filter(_.id === id).result.headOption)
  }

  /**
    * Finds phonebook entry with the specified name and phone number (case-insensitive).
    *
    * @param name        Target name.
    * @param phoneNumber Target phone number.
    * @return Found entry or [[None]].
    */
  def findByNameAndPhoneNumber(name: String, phoneNumber: String): Future[Option[PhonebookEntry]] = {
    db.run(
      entries.filter(entry =>
        (entry.name.toLowerCase like getFilterOrAll(name).toLowerCase) &&
          (entry.phoneNumber like getFilterOrAll(phoneNumber)))
        .result.headOption)
  }

  /**
    * Finds phonebook entry which has either the specified name or phone number (case-insensitive).
    *
    * @param name        Target name.
    * @param phoneNumber Target phone number.
    * @return Found entry or [[None]].
    */
  def findByNameOrPhoneNumber(name: String, phoneNumber: String): Future[Try[Seq[PhonebookEntry]]] = {
    db.run(
      entries.filter(entry =>
        (entry.name.toLowerCase like getFilterOrAll(name).toLowerCase) ||
          (entry.phoneNumber like getFilterOrAll(phoneNumber)))
        .result)
      .map(Success(_))
      .recover { case ex: Exception => Failure(ex) }
  }

  /**
    * Inserts a new phonebook entry, gives back affected rows count.
    *
    * @param addedEntry Entry to insert.
    * @return Number of affected rows, usually 1.
    */
  def insert(addedEntry: PhonebookEntry): Future[Try[Int]] =
    db.run(entries += addedEntry)
      .map(Success(_))
      .recover { case ex: Exception => Failure(ex) }

  /**
    * Inserts new phonebook entries, gives back an accumulated count of affected rows.
    *
    * @param addedEntries Entries to insert.
    * @return Accumulated count, can be -1 if the database system does not provide counts for all rows.
    */
  def insert(addedEntries: Seq[PhonebookEntry]): Future[Try[Int]] =
    db.run(entries ++= addedEntries)
      .map(r => Success(r.getOrElse(-1)))
      .recover { case ex: Exception => Failure(ex) }

  /**
    * Divides phonebook entries with matched names or phone numbers into a page.
    *
    * @param page     Current page.
    * @param pageSize Number of entries per page.
    * @param filter   Filter for either phonebook entry name or phone number.
    * @return A page containing its phonebook entries.
    */
  def list(page: Int,
           pageSize: Int,
           filter: String = "%"): Future[Try[PhonebookPage]] = {

    val filterOrAll = getFilterOrAll(filter)
    for {filteredEntries <- findByNameOrPhoneNumber(filterOrAll, filterOrAll)}
      yield {
        filteredEntries.map(e => PhonebookPage(e.reverse, page, pageSize))
      }
  }

  /**
    * Gets all currently present phonebook entries.
    *
    * @return All currently present phonebook entries.
    */
  def selectAll(): Future[Try[Seq[PhonebookEntry]]] = {
    db.run(entries.result)
      .map(Success(_))
      .recover { case ex: Exception => Failure(ex) }
  }

  /**
    * Updates an entry with the given id using the new entry.
    *
    * @param id    Id of the entry to update.
    * @param entry New entry to replace updated one.
    * @return Affected rows count.
    */
  def update(id: Long, entry: PhonebookEntry): Future[Try[Int]] = {
    val entryToUpdate: PhonebookEntry = entry.copy(entry.name, entry.phoneNumber, id)
    db.run(entries.filter(_.id === id).update(entryToUpdate))
      .map(Success(_))
      .recover { case ex: Exception => Failure(ex) }
  }

  /**
    * Returns wide "all" filter when null or empty string is passed as a filter.
    *
    * @param filter Filter to check.
    * @return Passed filter or "all" filter.
    */
  private def getFilterOrAll(filter: String): String =
    if (!StringUtils.isNullOrEmpty(filter) && filter != "%") filter else "%"
}