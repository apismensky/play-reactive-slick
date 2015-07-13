package models

import java.sql.{Date, ResultSet}

import play.api.db.DB._
import play.api.Play.current

import scala.collection.mutable.ArrayBuffer

class JdbcDAO  {

  implicit def longToDate(l: Long): Date = new Date(l)

  implicit def optionDateToLong(d: Option[java.util.Date]): Long = d match {
    case Some(date) => date.getTime
    case _ => 0
  }


  /**
   * Find employee by id
   */
  def findById(id: Long): Employee = withConnection("default") { conn =>
    val ps = conn.prepareStatement("""SELECT "id","name","address","date_of_birth","joining_date","designation" FROM "EMPLOYEE" WHERE "id"=?""")
    ps.setLong(1,id)
    val rs = ps.executeQuery()
    if(!rs.next())
      throw new IllegalArgumentException(s"invalid id: $id")
    Employee(
      Some(rs.getLong("id")),
      rs.getString("name"),
      rs.getString("address"),
      Some(rs.getLong("date_of_birth")),
      rs.getLong("joining_date"),
      Some(rs.getString("designation"))
    )
    
  }

  /**
   * Return a page of employees
   */

  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[Employee] = withConnection("default") { conn =>
    val offset = pageSize * page

    val ps = conn.prepareStatement(
      """SELECT "id","name","address","date_of_birth","joining_date","designation"
        | FROM "EMPLOYEE"
        | WHERE lower("name") like lower(?)
        | ORDER BY ?
        | LIMIT ?
        | OFFSET ? """.stripMargin)
    ps.setString(1, filter)
    ps.setInt(2, orderBy)
    ps.setInt(3, pageSize)
    ps.setInt(4, offset)
    val rs: ResultSet = ps.executeQuery()
    val es = ArrayBuffer[Employee]()
    while (rs.next()) {
      es += Employee(
        Some(rs.getLong("id")),
        rs.getString("name"),
        rs.getString("address"),
        Some(rs.getLong("date_of_birth")),
        rs.getLong("joining_date"),
        Some(rs.getString("designation"))
      )
    }
    val totalRows = count(filter)
    Page(es, page, offset, totalRows)
  }

  private def count(filter: String): Int = withConnection("default") { conn =>
    val ps = conn.prepareStatement(
      """SELECT COUNT(1)
        | FROM "EMPLOYEE"
        | WHERE lower("name") like lower(?) """.stripMargin)
    ps.setString(1, filter)
    val rs: ResultSet = ps.executeQuery()
    rs.next()
    rs.getInt(1)
  }

  def count: Int = withConnection("default") { conn =>
    val ps = conn.prepareStatement("""SELECT COUNT(1) FROM "EMPLOYEE" """)
    val rs = ps.executeQuery()
    rs.next()
    rs.getInt(1)
  }

  def insert(employee: Employee): Int = withConnection("default") { conn =>
    val ps = conn.prepareStatement("""INSERT INTO "EMPLOYEE"("name", "address", "date_of_birth", "joining_date", "designation") VALUES(?,?,?,?,?)""")
    ps.setString(1, employee.name)
    ps.setString(2, employee.address)
    ps.setLong(3, employee.dob)
    ps.setLong(4, employee.joiningDate.getTime)
    ps.setString(5, employee.designation.orNull)
    ps.executeUpdate()
  }

  def update(employee: Employee): Int = withConnection("default") { conn =>
    val ps = conn.prepareStatement(
      """UPDATE "EMPLOYEE"
        | SET "name"=?,
        |  "address"=?,
        |  "date_of_birth"=?,
        |  "joining_date"=?,
        |   "designation"=? WHERE "id"=?""".stripMargin)
    ps.setString(1, employee.name)
    ps.setString(2, employee.address)
    ps.setLong(3, employee.dob)
    ps.setLong(4, employee.joiningDate.getTime)
    ps.setString(5, employee.designation.orNull)
    ps.setLong(6, employee.id.get)
    ps.executeUpdate()
  }

  def delete(id: Long): Int = withConnection("default") { conn =>
    val ps = conn.prepareStatement("""DELETE FROM "EMPLOYEE" WHERE "id"=? """)
    ps.setLong(1, id)
    ps.executeUpdate()
  }

}
