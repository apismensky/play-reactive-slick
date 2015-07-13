package controllers

import java.util.Date
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

import models.{JdbcDAO, Page, Employee}
import play.api.libs.json.{JsPath, Reads, Json, Writes}
import play.api.mvc.{Controller, AnyContent, Action}

class EmployeeControllerJdbc extends Controller {
  implicit val employeeWrites = new Writes[Employee] {
    def writes(e: Employee) = Json.obj(
      "id" -> e.id,
      "name" -> e.name,
      "address" -> e.address,
      "dob" -> e.dob,
      "joiningDate" -> e.joiningDate,
      "designation" -> e.designation
    )
  }

  implicit val employeeReads: Reads[Employee] = (
    (JsPath \ "id").read[Option[Long]] and
      (JsPath \ "name").read[String] and
      (JsPath \ "address").read[String] and
      (JsPath \ "dob").read[Option[Date]] and
      (JsPath \ "joiningDate").read[Date] and
      (JsPath \ "designation").read[Option[String]]
    )(Employee.apply _)


  implicit val pageWrites = new Writes[Page[Employee]] {
    def writes(p: Page[Employee]) = {
      Json.obj(
        "next" -> p.next,
        "prev" -> p.prev,
        "items" -> p.items.map(e => employeeWrites.writes(e))
      )
    }
  }

  val dao = new JdbcDAO

  val Home = Redirect(routes.EmployeeControllerJdbc.list(0, 2, ""))

  def getById(id: Long) = Action { request =>
    Ok(Json.toJson(dao.findById(id)))
  }

  /**
   * Display the paginated list of employees.
   */
  def list(page: Int, orderBy: Int, filter: String): Action[AnyContent] = Action { request =>
    Ok(Json.toJson(dao.list(page, 10, orderBy, "%" + filter + "%")))
  }

  def update(id: Long) = Action { request =>
    val jsValue = request.body.asJson.getOrElse(throw new IllegalArgumentException("Can not read JSON request body"))
    val empCopy = jsValue.as[Employee].copy(id = Some(id))
    dao.update(empCopy)
    Home
  }

  def save = Action { request =>
    val jsValue = request.body.asJson.getOrElse(throw new IllegalArgumentException("Can not read JSON request body"))
    dao.insert(jsValue.as[Employee])
    Home
  }

  def delete(id: Long) = Action { request =>
    dao.delete(id)
    Home
  }

}
