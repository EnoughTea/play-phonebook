package forms

import models.PhonebookEntry
import play.api.data.Forms._
import play.api.data.{Form, FormError}
import play.api.i18n.{Messages, MessagesProvider}


/** Represents a form definition suitable for creation and editing of a [[PhonebookEntry]]. */
object PhonebookEntryForm {
  private val _phoneNumberValidationRegex = """\+?\s*[\d()\[\]\{\}#\*\- ]+"""

  // Form is a read-only case class, so we can store the form definition in a val.
  private val _form = Form(
    mapping(
      "name" -> nonEmptyText(maxLength = 1024),
      "phoneNumber" -> nonEmptyText(maxLength = 1024).verifying("error.invalidPhone", s =>
        s.isEmpty || (s.length() >= 7 && s.matches(_phoneNumberValidationRegex) && s.exists(_.isDigit)))
    )(PhonebookEntryFormData.apply)(PhonebookEntryFormData.unapply)
  )


  /** Gets an empty form definition. */
  def apply(): Form[PhonebookEntryFormData] = _form

  /** Gets a form definition filled using given form data. */
  def apply(entryData: PhonebookEntryFormData): Form[PhonebookEntryFormData] = {
    require(entryData != null)
    _form.bind(Map("name" -> entryData.name, "phoneNumber" -> entryData.phoneNumber))
  }

  /** Gets a form definition filled using given [[PhonebookEntry]]. */
  def fill(entry: PhonebookEntry): Form[PhonebookEntryFormData] = {
    require(entry != null)
    _form.fill(PhonebookEntryFormData(entry.name, entry.phoneNumber))
  }

  /** Defines a form with failed unicity check: other entry with the same name and phone number already exists. */
  def withFailedUnicity(entryData: PhonebookEntryFormData)
                       (implicit messagesProvider: MessagesProvider): Form[PhonebookEntryFormData] = {
    PhonebookEntryForm()
      .bind(Map("name" -> entryData.name, "phoneNumber" -> entryData.phoneNumber))
      .withError("error.entryAlreadyExists", Messages("error.entryAlreadyExists", entryData))
      // line below is used to highlight form fields without additional error messages.
      .withError(FormError("name", null)).withError(FormError("phoneNumber", null))
  }
}