package controllers

import javax.inject._

import scala.concurrent.{ExecutionContext, Future}

import forms.{PhonebookEntryForm, PhonebookEntryFormData}
import models.PhonebookPage
import play.api.i18n._
import play.api.mvc._
import services.Phonebook
import views.html


/** Implements web part of the phonebook logic, providing access for listing and editing phonebook entries. */
@Singleton
class PhonebookController @Inject()(cc: ControllerComponents,
                                    phonebook: Phonebook)
                                   (implicit ec: ExecutionContext)
  extends AbstractController(cc) with I18nSupport {

  /** Amount of phonebook entries shown on a single page. */
  val entriesPerPage: Int = 15

  /** This result directly redirect to the application home. */
  val home = Redirect(routes.PhonebookController.list())

  /** Handles default path requests, redirects to phonebook entries list. */
  def index: Action[AnyContent] = Action {
    home
  }

  /** Handles the 'new entry form' submission. */
  def create(): Action[AnyContent] = Action.async { implicit rs: Request[_] =>
    /** Creates a task which returns true if 'name — phone number'
      * from entry data is already present in the phonebook; false otherwise. */
    def entryAlreadyExistsTask(entryData: PhonebookEntryFormData): Future[Boolean] = {
      phonebook.findByNameAndPhoneNumber(entryData.name.trim(), entryData.phoneNumber.trim()).map(_.isDefined)
    }

    phonebook.list(0, entriesPerPage).flatMap(entries =>
      PhonebookEntryForm().bindFromRequest.fold(
        // Display simple validation errors:
        formWithErrors => Future.successful(BadRequest(html.listEntries(entries.getOrElse(PhonebookPage.Empty), "", formWithErrors))),
        // Perform more complex unicity validation:
        entryData => entryAlreadyExistsTask(entryData).flatMap(entryExists =>
          if (entryExists) {
            Future.successful(BadRequest(html.listEntries(entries.getOrElse(PhonebookPage.Empty), "", PhonebookEntryForm.withFailedUnicity(entryData))))
          } else {
            phonebook.insert(entryData.toEntry)
              .map(_ => home.flashing("success" -> Messages("info.entryCreated", entryData)))
          }
        )
      )
    )
  }

  /**
    * Handles phonebook entry deletion.
    *
    * @param id Id of the entry to delete.
    */
  def delete(id: Long): Action[AnyContent] = Action.async {
    implicit rs: Request[_] =>
      for {
        entry <- phonebook.findById(id) // Somewhat wasteful, but more consistent with other messages.
        deletionResult <- phonebook.delete(id)
      } yield {
        if (deletionResult.isSuccess) {
          home.flashing("success" -> Messages("info.entryDeleted", entry.getOrElse(id)))
        } else {
          home.flashing("error" -> Messages("error.entryNotFoundForDeletion", id))
        }
      }
  }

  /**
    * Display the 'edit entry form' for an existing phonebook entry.
    *
    * @param id Id of the entry to edit.
    */
  def edit(id: Long): Action[AnyContent] = Action.async { implicit rs: Request[_] =>
    phonebook.findById(id).map({
      case Some(e) => Ok(html.editEntryForm(id, PhonebookEntryForm.fill(e)))
      case None => NotFound
    })
  }

  /**
    * Handle the 'edit entry form' submission.
    *
    * @param id Id of the entry to edit.
    */
  def update(id: Long): Action[AnyContent] = Action.async { implicit rs: Request[_] =>
    PhonebookEntryForm().bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(html.editEntryForm(id, formWithErrors))),
      entryData => for {
        _ <- phonebook.update(id, entryData.toEntry)
      } yield home.flashing("success" -> Messages("info.entryUpdated", entryData))
    )
  }

  /**
    * Displays the paginated list of phonebook entries.
    *
    * @param page   Current page number (starts from 0).
    * @param filter Filter applied on entry names and phone numbers. Implemented via SQL 'like' statement.
    */
  def list(page: Int, filter: String): Action[AnyContent] = Action.async { implicit request: Request[_] =>
    phonebook.list(page, entriesPerPage, "%" + filter + "%")
      .map(cs => Ok(html.listEntries(cs.getOrElse(PhonebookPage.Empty), filter, PhonebookEntryForm())))
  }
}
