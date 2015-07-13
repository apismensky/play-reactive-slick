import play.libs.ws.WS

class ApiIntegrationSpec extends AbstractSpec {

  val versions = List("v1", "v2") // API versions

  "GET /employees/1" should "return status 200" in {
    versions.foreach { version =>
      val response = WS.url(s"$BaseUri/api/$version/employees/1").get.get(Timeout)
      response.getBody should include(""""name":"John"""")
      response.getStatus should be(200)
    }
  }

  it should "return 500 if employee does not exist found " in {
    versions.foreach { version =>
      WS.url(s"$BaseUri/api/$version/employees/999999999").get.get(Timeout).getStatus should be(500)
    }
  }

  "GET /employees" should "return a list of employees" in {
    versions.foreach { version =>
      val response = WS.url(s"$BaseUri/api/$version/employees").get.get(Timeout)
      response.getBody should include(""""name":"John"""")
      response.getStatus should be(200)
    }
  }

  // curl -X POST http://localhost:9000/api/employees -H "Content-Type: application/json" -d '{"id":null, "name":"Foo","address":"Bar","designation":"Developer", "dob":null, "joiningDate":"2001-09-01"}' -v
  "POST /employees"  should "create record and redirect" in {
    versions.foreach { version =>
      val response = WS
        .url(s"$BaseUri/api/$version/employees")
        .setFollowRedirects(false)
        .setContentType(JsonType)
        .post( s"""{"id":null, "name":"Created By Test","address":"Created By Test","designation":"Developer", "dob":"2000-01-01", "joiningDate":"2001-09-01"}""")
        .get(Timeout)
      response.getStatus should be(303)
    }
  }

  // curl -X PUT http://localhost:9000/api/v1/employees/1 -H "Content-Type: application/json" -d '{"id":"1, "name":"Updated Name","address":"Updated address","designation":"Developer", "dob":"1900-01-01", "joiningDate":"1950-01-01"}' -v
  "PUT /employees/2"  should "update record" in {
    versions.foreach { version =>
      val response = WS
        .url(s"$BaseUri/api/$version/employees/2")
        .setContentType(JsonType)
        .setFollowRedirects(false)
        .put( s"""{"id":null, "name":"Updated By Test","address":"Updated By Test","designation":"Developer", "dob":"1900-01-01", "joiningDate":"1950-01-01"}""")
        .get(Timeout)
      response.getStatus should be(303)
    }
  }

  // curl -X DELETE http://localhost:9000/api/v1/employees/1  -v
  "DELETE /employees/1"  should "delete record" in {
    val response1 = WS
      .url(s"$BaseUri/api/v1/employees/1")
      .setFollowRedirects(false)
      .delete()
      .get(Timeout)
    response1.getStatus should be(303)

    val response2 = WS
      .url(s"$BaseUri/api/v2/employees/6")
      .setFollowRedirects(false)
      .delete()
      .get(Timeout)
    response2.getStatus should be(303)
  }
}
