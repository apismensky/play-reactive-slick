package controllers

import java.util.Date

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

import models.{Page, Employee}
import play.api.libs.json._
import play.api.mvc.Controller

class ApiController extends Controller {
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
    (__ \ "id").readNullable[Long] and
      (__ \ "name").read[String] and
      (__ \ "address").read[String] and
      (__ \ "dob").readNullable[Date] and
      (__ \ "joiningDate").read[Date] and
      (__ \ "designation").readNullable[String]
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
}
