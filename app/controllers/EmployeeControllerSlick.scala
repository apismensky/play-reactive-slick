package controllers

import com.google.inject.Inject
import models.{Employee, DAO}
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent}
import scala.concurrent.ExecutionContext.Implicits.global

class EmployeeControllerSlick @Inject() (dao: DAO) extends ApiController {

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.EmployeeControllerSlick.list(0, 2, ""))

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
        Logger.error("Problem found in employee list process: " + ex.getMessage)
        InternalServerError(ex.getMessage)
    }
  }

  /**
   * Handle employee deletion
   */
  def delete(id: Long): Action[AnyContent] = Action.async { implicit request =>
      dao.delete(id).map { result => Home }.recover {
      case ex: Exception =>
        Logger.error("Problem found in employee delete process: " + ex.getMessage)
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
        Logger.error("Problem found in employee save process: " + ex.getMessage)
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
        Logger.error("Problem found in employee update process: " + ex.getMessage)
        InternalServerError(ex.getMessage)
    }
  }
}

