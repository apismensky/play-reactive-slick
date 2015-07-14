package models

import slick.driver.PostgresDriver.api._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.db.DB
import play.api.Play.current

trait DAOComponent {

  def insert(employee: Employee): Future[Int]
  def update(id: Long, employee: Employee): Future[Int]
  def delete(id: Long): Future[Int]
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Future[Page[Employee]]
  def findById(id: Long): Future[Employee]
  def count: Future[Int]

}

object DAO extends DAOComponent {

  private val employees = TableQuery[Employees]

  val db: Database = Database.forDataSource(DB.getDataSource(),
    executor = AsyncExecutor("test1", numThreads=10, queueSize=1000))

  /**
   * Filter employee with id
   */
  private def filterQuery(id: Long): Query[Employees, Employee, Seq] =
    employees.filter(_.id === id)

  /**
   * Count employees with a filter
   */
  private def count(filter: String): Future[Int] =
    db.run(employees.filter(_.name.toLowerCase like filter.toLowerCase()).length.result)

  /**
   * Count total employees in database
   */
  override def count: Future[Int] =
    db.run(employees.length.result)

  /**
   * Find employee by id
   */
  override def findById(id: Long): Future[Employee] =
    db.run(filterQuery(id).result.head)

  /**
   * Create a new employee
   */
  override def insert(employee: Employee): Future[Int] =
    db.run(employees += employee)

  /**
   * Update employee with id
   */
  override def update(id: Long, employee: Employee): Future[Int] =
    db.run(filterQuery(id).update(employee))

  /**
   * Delete employee with id
   */
  override def delete(id: Long): Future[Int] =
    db.run(filterQuery(id).delete)

  /**
   * Return a page of employees
   */
  override def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Future[Page[Employee]] = {
    val offset = pageSize * page
    val query =
      (for {
        employee <- employees if employee.name.toLowerCase like filter.toLowerCase
      } yield (employee)).drop(offset).take(pageSize)
    val totalRows = count(filter)
    val result = db.run(query.result)
    result flatMap (employees => totalRows map (rows => Page(employees, page, offset, rows)))
  }

}
