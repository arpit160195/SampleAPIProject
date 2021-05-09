package sampletestpackage;
import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.ArrayList;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import files.payload;
import io.restassured.path.json.*; 

public class JiraApiTest1 {
	
	String sessionID;
	Object[][] apiDataInputObject = new Object[3][4];
	String jiraIssueId;
	SessionFilter session = new SessionFilter();
 	
	@Test(priority = 0)
	public void fetchJiraSession() {
		RestAssured.baseURI = "http://localhost:8080";
		String fetchJiraSessionResponse = given().log().all().header("Content-Type","application/json")
		.body(payload.fetchJiraSession())
		.when().filter(session).post("rest/auth/1/session")
		.then().log().all().assertThat().statusCode(200)
		.extract().response().asString();
			
		JsonPath js = new JsonPath(fetchJiraSessionResponse);
		sessionID = js.getString("session.value");
	}
	
	@Test(priority = 1, dataProvider = "apiDataInput")
	public void createJiraIssue(String project, String summary, String description, String issueType) {
		RestAssured.baseURI = "http://localhost:8080";
		String fetchJiraIssueId = given().log().all().header("Content-Type","application/json")
		.body(payload.createJiraIssue(project, summary, description, issueType))
		.when().filter(session).post("rest/api/2/issue")
		.then().log().all().assertThat().statusCode(201)
		.extract().response().asString();
			
		JsonPath js = new JsonPath(fetchJiraIssueId);
		jiraIssueId = js.getString("id");
	}
	
	@Test(priority = 2)
	public void addJiraComment() {
		RestAssured.baseURI = "http://localhost:8080";
		given().log().all().header("Content-Type","application/json").pathParam("issue", jiraIssueId)
		.body(payload.addComment("This is a comment that only administrators can see.Usig Rest API", "Administrators"))
		.when().filter(session).post("rest/api/2/issue/{issue}/comment")
		.then().log().all().assertThat().statusCode(201);
	}
	
	@DataProvider(name = "apiDataInput")
	public Object[][] apiDataInput(){
		Object[][] apiDataInputObject = {{"JIR", "Api test jira BUG using Postman bug 1 for creation","Creating of an BUG using project keys and BUG type names using the REST API with POSTMAN for Bug 1", "Bug"},
							   {"JIR", "Api test jira BUG using Postman bug 2 for creation","Creating of an BUG using project keys and BUG type names using the REST API with POSTMAN for Bug 2", "Bug"},
							   {"JIR", "Api test jira BUG using Postman bug 3 for creation","Creating of an BUG using project keys and BUG type names using the REST API with POSTMAN for Bug 3", "Bug"}};
		return apiDataInputObject;
	}
	
	@Test(priority = 2)
	public void addJiraFile() {
		RestAssured.baseURI = "http://localhost:8080";
		given().log().all().header("X-Atlassian-Token","nocheck").pathParam("issue", jiraIssueId)
		.header("Content-Type","multipart/form-data").multiPart("File",new File("SampleFile"))
		.when().filter(session).post("rest/api/2/issue/{issue}/attachments")
		.then().log().all().assertThat().statusCode(200);
	}
}
