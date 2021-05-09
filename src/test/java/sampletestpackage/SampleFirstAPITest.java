package sampletestpackage;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import files.payload;
import io.restassured.path.json.*; 

public class SampleFirstAPITest {
	
	String placeId;
	JsonPath js;
	
	@Test(priority = 0)
	public void addPlace() {
		RestAssured.baseURI = "https://rahulshettyacademy.com";
		String response = given().log().all().queryParam("key", "qaclick123").header("Content-Type","application/json")
		.body(payload.AddPlace())
		.when().post("/maps/api/place/add/json")
		.then().log().all().assertThat().statusCode(200).body("scope", equalTo("APP"))
		.header("Server", "Apache/2.4.18 (Ubuntu)").extract().response().asString();
		
		js = new JsonPath(response);
		placeId = js.getString("place_id");
		System.out.println(placeId);
	}
	
	@Test(priority = 1)
	public void updatePlace() {
		RestAssured.baseURI = "https://rahulshettyacademy.com";
		given().log().all().queryParam("key", "qaclick123").header("Content-Type","application/json")
		.body("{\r\n"
				+ "\"place_id\":\""+placeId+"\",\r\n"
				+ "\"address\":\"70 Summer walk, USA\",\r\n"
				+ "\"key\":\"qaclick123\"\r\n"
				+ "}\r\n")
		.when().put("/maps/api/place/update/json")
		.then().log().all().assertThat().statusCode(200).body("msg", equalTo("Address successfully updated"))
		.header("Server", "Apache/2.4.18 (Ubuntu)");
		
		given().log().all().queryParam("key", "qaclick123").queryParam("place_id", placeId)
		.when().get("/maps/api/place/get/json")
		.then().log().all().assertThat().statusCode(200)
		.header("Server", "Apache/2.4.18 (Ubuntu)");
		
		given().log().all().queryParam("key", "qaclick123").header("Content-Type","application/json")
		.body("{\r\n"
				+ "\"place_id\":\""+placeId+"\",\r\n"
				+ "\"address\":\"29, side layout, cohen 09\",\r\n"
				+ "\"key\":\"qaclick123\"\r\n"
				+ "}\r\n")
		.when().put("/maps/api/place/update/json")
		.then().log().all().assertThat().statusCode(200)
		.header("Server", "Apache/2.4.18 (Ubuntu)");
	}
	
	@Test(priority = 2)
	public void testSampleNestedJson() {
		js = new JsonPath(payload.SampleNestedJson());
		List<Object> ls = js.getList("courses");
		System.out.println(ls.size());
		int courseSize = js.getInt("courses.size()");
		System.out.println(courseSize);
		int sum = 0;
		for(int i = 0;i < courseSize; i++) {
			System.out.println(js.getString("courses["+i+"].title"));
			System.out.println(js.getString("courses["+i+"].price"));
			if(js.getString("courses["+i+"].title")=="RPA") {
				System.out.println(js.getString("courses["+i+"].copies"));
			}
			sum = sum + (js.getInt("courses["+i+"].price") * js.getInt("courses["+i+"].copies"));
		}
		Assert.assertTrue(sum == js.getInt("dashboard.purchaseAmount"));
		System.out.println(sum + "; " + js.getInt("dashboard.purchaseAmount"));
	}
}
