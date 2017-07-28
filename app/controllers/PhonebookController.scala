package controllers

import javax.inject._

import scala.concurrent.ExecutionContext

import forms.PhonebookEntryForm
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
