
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import play.test.Helpers._
import play.test.TestServer

class AbstractSpec extends FlatSpec with Matchers with BeforeAndAfterAll {
  val Port = 3333
  val Host = "localhost"
  val BaseUri = s"http://$Host:$Port"
  val Timeout = 30000
  val JsonType = "application/json; charset=utf-8"

  var testServ: TestServer = _

  override def beforeAll() {
    testServ = testServer(Port)
    testServ.start()
  }

  override def afterAll() = testServ.stop()

}
