import play.libs.ws.WS

class ApiIntegrationSpec extends AbstractSpec {

  "GET /api/employees/1" should "return status 200" in {
    val response = WS.url(s"$BaseUri/api/employees/1").get.get(Timeout)
    response.getBody should include(""""name":"John"""")
    response.getStatus should be(200)
  }

  "GET /api/employees" should "return a list of employees" in {
    val response = WS.url(s"$BaseUri/api/employees").get.get(Timeout)
    response.getBody should include(""""name":"John"""")
    response.getStatus should be(200)
  }

  it should "return 500 if employee does not exist found " in {
    WS.url(s"$BaseUri/api/employees/999999999").get.get(Timeout).getStatus should be(500)
  }

  // curl -X POST http://localhost:9000/api/employees -H "Content-Type: application/json" -d '{"id":null, "name":"Foo","address":"Bar","designation":"Developer", "dob":null, "joiningDate":"2001-09-01"}' -v
  "POST /api/employees"  should "create record and redirect" in {
    val response = WS
      .url(s"$BaseUri/api/employees")
      .setFollowRedirects(false)
      .setContentType(JsonType)
      .post(s"""{"id":null, "name":"Created By Test","address":"Created By Test","designation":"Developer", "dob":"2000-01-01", "joiningDate":"2001-09-01"}""")
      .get(Timeout)
    response.getStatus should be(303)
  }

  // curl -X PUT http://localhost:9000/api/employees/1 -H "Content-Type: application/json" -d '{"id":"1, "name":"Updated Name","address":"Updated address","designation":"Developer", "dob":"1900-01-01", "joiningDate":"1950-01-01"}' -v
  "PUT /api/employees/2"  should "update record" in {
    val response = WS
      .url(s"$BaseUri/api/employees/2")
      .setContentType(JsonType)
      .setFollowRedirects(false)
      .put(s"""{"id":null, "name":"Updated By Test","address":"Updated By Test","designation":"Developer", "dob":"1900-01-01", "joiningDate":"1950-01-01"}""")
      .get(Timeout)
    response.getStatus should be(303)
  }

  // curl -X DELETE http://localhost:9000/api/employees/1  -v
  "DELETE /api/employees/1"  should "delete record" in {
    val response = WS
      .url(s"$BaseUri/api/employees/1")
      .setFollowRedirects(false)
      .delete()
      .get(Timeout)
    response.getStatus should be(303)
  }
}
