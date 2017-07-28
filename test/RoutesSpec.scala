import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Application
import play.api.cache.ehcache.EhCacheModule
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.test.Helpers._


/** Tests routing scheme, making sure our routes are valid and return some response. */
class RoutesSpec extends PlaySpec with GuiceOneServerPerSuite {
  val homeUrl = s"http://localhost:$port"

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .disable[EhCacheModule]
    .build()

  "Default route (an index page) must exist" in {
    val wsClient = app.injector.instanceOf[WSClient]

    val response = await(wsClient.url(homeUrl).get())

    response.status mustBe OK
  }
}