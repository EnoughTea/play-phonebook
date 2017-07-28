package models

/** Represents a single person entry — name and phone number — in a phonebook.
  *
  * @param name        Person's name.
  * @param phoneNumber Person's phone number.
  * @param id          Id, usually assigned by a database.
  */
case class PhonebookEntry(name: String, phoneNumber: String, id: Long = 0) {
  override def toString: String = s"$name — $phoneNumber"
}