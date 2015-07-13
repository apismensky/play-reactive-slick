import java.text.SimpleDateFormat
import play.api._
import models._
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    StartData.insert()
  }

}

/**
 *  Starting set of data to be inserted into the sample application.
 */
object StartData {

  val jdbcDao = new JdbcDAO
  def insert(): Unit = {
    val size = jdbcDao.count

    if (size == 0) {
      val employees = ArrayBuffer(
        Employee(Option(1L), "John", "USA", None, new java.util.Date, Some("Trainee")),
        Employee(Option(2L), "Miles", "USA", None, new java.util.Date, Some("Assistant")),
        Employee(Option(3L), "Alexander", "USA", None, new java.util.Date, Some("Manager")),
        Employee(Option(4L), "Stefan", "USA", None, new java.util.Date, Some("Trainee")),
        Employee(Option(5L), "Robin", "USA", None, new java.util.Date, Some("Assistant")),
        Employee(Option(6L), "Ivan Drago", "USSR", None, new java.util.Date, Some("KGB Agent")))

      for (i <- 7 to 9900)
        employees += Employee(Option(i), s"Name_$i", "USA", None, new java.util.Date, Some("Generated"))

      employees.foreach(e => jdbcDao.insert(e))
    }

  }
}
