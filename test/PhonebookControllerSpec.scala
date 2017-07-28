import org.scalatest.Matchers._
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Application
import play.api.cache.ehcache.EhCacheModule
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._


/** Tests PhonebookController's responces for expected requests. */
class PhonebookControllerSpec extends PlaySpec with GuiceOneAppPerSuite {

  import controllers.PhonebookController

  val homeUrl = "/"
  val phonebookController = app.injector.instanceOf[PhonebookController]
  val reversePhonebookController = controllers.routes.PhonebookController

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .disable[EhCacheModule]
    .build()

  "Index should have appropriate url" in {
    reversePhonebookController.index().url shouldBe homeUrl
  }

  "Entries listing should have appropriate url" in {
    reversePhonebookController.list().url shouldBe homeUrl + "entries"
  }

  "Index should redirect to entries list" in {
    val result = phonebookController.index()(FakeRequest())

    status(result) shouldBe SEE_OTHER
    redirectLocation(result) mustBe Some("/entries")
  }

  "Entries list should return some HTML content" in {
    val result = phonebookController.list(0, "")(FakeRequest())

    status(result) shouldBe OK
    contentType(result) shouldBe Some("text/html")
    charset(result) shouldBe Some("utf-8")
    contentAsString(result).length should (be > 0)
  }
}