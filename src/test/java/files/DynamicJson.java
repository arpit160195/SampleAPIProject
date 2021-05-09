package files;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.matcher.ResponseAwareMatcher;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.*;

public class DynamicJson {
	
	String bookID;
	
	@Test(dataProvider = "BookNameFetch")
	public void addBook(String name, String isbn, String aisle, String author) {
			RestAssured.baseURI = "http://216.10.245.166";
			String addBookResponse = given().log().all().header("Content-Type","application/json")
			.body(payload.addBook(name, isbn, aisle, author)).when()
			.post("Library/Addbook.php").then().log().all().assertThat().statusCode(200)
			.extract().response().asString();
						
			JsonPath js = new JsonPath(addBookResponse);
			System.out.println("the size of response is: " + js.config.jsonPathConfig());
			bookID = js.getString("ID");
	}
	
	@Test()
	public void deleteBook() {
		RestAssured.baseURI = "http://216.10.245.166";
		given().log().all().header("Content-Type","application/json")
		.body(payload.deleteBook(bookID)).when()
		.delete("Library/DeleteBook.php").then().log().all().assertThat().statusCode(200)
		.body("msg", equalTo("book is successfully deleted"));
	}
	
	@DataProvider(name = "BookNameFetch")
	public Object[][] apiData() {
		return new Object[][] {{"Learn Appium Automation with Java", "qscedfvb", "2343", "John Bullshit"},
							   {"Learn Selenium Automation with Java", "rgntyhjm", "8769", "John Lenon"},
							   {"Learn API Automation with Java", "oihgyfc", "2346", "John F Kennedy"}};
	}
}
