package controllers

import javax.inject._

import scala.concurrent.{ExecutionContext, Future}

import play.api.i18n._
import play.api.mvc._
import play.twirl.api.Html
import services.Phonebook


/** Implements web part of the phonebook logic, providing access for listing and editing phonebook entries. */
@Singleton
class PhonebookController @Inject()(cc: ControllerComponents,
                                    phonebook: Phonebook)
                                   (implicit ec: ExecutionContext)
  extends AbstractController(cc) with I18nSupport {

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
    Future.successful(Ok(Html("Phonebook entries will be here.")))
  }
}
