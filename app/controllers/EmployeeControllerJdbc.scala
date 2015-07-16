package controllers

import com.google.inject.Inject

import models.{JdbcDAO, Employee}
import play.api.libs.json.Json
import play.api.mvc.{AnyContent, Action}

class EmployeeControllerJdbc @Inject() (dao: JdbcDAO) extends ApiController {

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
