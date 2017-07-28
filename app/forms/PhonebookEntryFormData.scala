package forms

import models.PhonebookEntry


/**
  * Holds [[PhonebookEntryForm]] field data.
  *
  * @param name        Name.
  * @param phoneNumber Phone number.
  */
case class PhonebookEntryFormData(name: String, phoneNumber: String) {
  /** Creates a phonebook entry using the form data. */
  def toEntry: PhonebookEntry = PhonebookEntry(name, phoneNumber)

  override def toString: String = s"$name — $phoneNumber"
}
