package services

import javax.inject._

import scala.concurrent.Future
import scala.util.Try

import dal._
import models._

/**
  * Represents a phonebook with a set of operations available, such as
  * listing the phonebook entries; storing, editing and deleting them.
  *
  * @param entriesDal Data access layer for [[PhonebookEntry]]'s.
  */
@Singleton
class Phonebook @Inject() (entriesDal: PhonebookEntriesDAL) {
  /** Phonebook entries total count. */
  def count(): Future[Int] = entriesDal.count()

  /**
    * Deletes phonebook entry with given id.
    *
    * @param id Id of the entry to delete.
    * @return Affected rows count.
    */
  def delete(id: Long): Future[Try[Int]] = entriesDal.delete(id)

  /**
    * Finds phonebook entry with given id.
    *
    * @param id Id of the entry to find.
    * @return Found entry or [[None]].
    */
  def findById(id: Long): Future[Option[PhonebookEntry]] = entriesDal.findById(id)

  /**
    * Finds phonebook entry with the specified name and phone number (case-insensitive).
    *
    * @param name        Target name.
    * @param phoneNumber Target phone number.
    * @return Found entry or [[None]].
    */
  def findByNameAndPhoneNumber(name: String, phoneNumber: String): Future[Option[PhonebookEntry]] =
    entriesDal.findByNameAndPhoneNumber(name, phoneNumber)

  /**
    * Inserts a new phonebook entry, gives back affected rows count.
    *
    * @param entry Entry to insert.
    * @return Number of affected rows, usually 1.
    */
  def insert(entry: PhonebookEntry): Future[Try[Int]] = entriesDal.insert(entry)

  /**
    * Inserts new phonebook entries, gives back an accumulated count of affected rows.
    *
    * @param entries Entries to insert.
    * @return Accumulated count, can be -1 if the database system does not provide counts for all rows.
    */
  def insert(entries: Seq[PhonebookEntry]): Future[Try[Int]] = entriesDal.insert(entries)

  /**
    * Divides phonebook entries with matched names or phone numbers into a page.
    *
    * @param page     Current page.
    * @param pageSize Number of entries per page.
    * @param filter   Filter for either phonebook entry name or phone number.
    * @return A page containing its phonebook entries.
    */
  def list(page: Int, pageSize: Int, filter: String = "%"): Future[Try[PhonebookPage]] =
    entriesDal.list(page, pageSize, filter)

  /**
    * Gets all currently present phonebook entries.
    *
    * @return All currently present phonebook entries.
    */
  def selectAll(): Future[Try[Seq[PhonebookEntry]]] = {
    entriesDal.selectAll()
  }

  /**
    * Updates an entry with the given id using the new entry.
    *
    * @param id    Id of the entry to update.
    * @param entry New entry to replace updated one.
    * @return Affected rows count.
    */
  def update(id: Long, entry: PhonebookEntry): Future[Try[Int]] = entriesDal.update(id, entry)
}
