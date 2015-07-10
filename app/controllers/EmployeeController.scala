package controllers


import java.util.Date

import models.{Page, Employee, DAO, DAOComponent}
import play.api.Logger
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.mvc.{Action, AnyContent, Controller}
import scala.concurrent.ExecutionContext.Implicits.global

class EmployeeController(dao: DAOComponent) extends Controller {

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

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.EmployeeController.list(0, 2, ""))

  def getById(id: Long): Action[AnyContent] = Action.async { implicit request =>
    dao.findById(id).map(employee => Ok(Json.toJson(employee))).recover {
      case ex: Exception =>
        Logger.error("Problem in getById: " + ex.getMessage)
        InternalServerError(ex.getMessage)
    }
  }

  /**
   * Display the paginated list of employees.
   */
  def list(page: Int, orderBy: Int, filter: String): Action[AnyContent] = Action.async { implicit request =>
    dao.list(page, 10, orderBy, "%" + filter + "%").map { pageEmp =>
      Ok(Json.toJson(pageEmp))
    }.recover {
      case ex: Exception =>
        Logger.error("Problem found in employee list process")
        InternalServerError(ex.getMessage)
    }
  }

  /**
   * Handle employee deletion
   */
  def delete(id: Long): Action[AnyContent] = Action.async { implicit request =>
      dao.delete(id).map { result => Home }.recover {
      case ex: Exception =>
        Logger.error("Problem found in employee delete process")
        InternalServerError(ex.getMessage)
    }
  }

  /**
   * Handle the 'new employee form' submission.
   */
  def save: Action[AnyContent] = Action.async { implicit request =>
    val jsValue = request.body.asJson.getOrElse(throw new IllegalArgumentException("Can not read JSON request body"))
    dao.insert(jsValue.as[Employee]).map { result => Home }.recover {
      case ex: Exception =>
        Logger.error("Problem found in employee save process")
        InternalServerError(ex.getMessage)
    }
  }

  /**
   * Handle the 'edit form' submission
   */
  def update(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val jsValue = request.body.asJson.getOrElse(throw new IllegalArgumentException("Can not read JSON request body"))
    val e: Employee = jsValue.as[Employee]
    dao.update(id, e.copy(id = Some(id))).map { result => Home }.recover {
      case ex: Exception =>
        Logger.error("Problem found in employee update process")
        InternalServerError(ex.getMessage)
    }
  }
}

object EmployeeController extends EmployeeController(DAO)