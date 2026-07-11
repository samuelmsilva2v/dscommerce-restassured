package com.devsuperior.dscommerce.controllers;

import com.devsuperior.dscommerce.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class ProductControllerRA {

    private String clientUsername, adminUsername, password;
    private String clientToken, adminToken, invalidToken;
    private Long existingProductId, nonExistingProductId, dependentProductId;
    private String productName;

    private Map<String, Object> postProductInstance;

    @BeforeEach
    public void setUp() throws Exception {
        baseURI = "http://localhost:8080";

        clientUsername = "maria@gmail.com";
        adminUsername = "alex@gmail.com";
        password = "123456";

        clientToken = TokenUtil.obtainAccessToken(clientUsername, password);
        adminToken = TokenUtil.obtainAccessToken(adminUsername, password);
        invalidToken = adminToken + "xpto";

        productName = "Macbook";

        postProductInstance = new HashMap<>();
        postProductInstance.put("name", "Meu produto");
        postProductInstance.put("description", "Lorem ipsum, dolor sit amet consectetur adipisicing elit. Qui ad, adipisci illum ipsam velit et odit eaque reprehenderit ex maxime delectus dolore labore, quisquam quae tempora natus esse aliquam veniam doloremque quam minima culpa alias maiores commodi. Perferendis enim");
        postProductInstance.put("imgUrl", "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg");
        postProductInstance.put("price", 50.0F);

        List<Map<String, Object>> categories = new ArrayList<>();

        Map<String, Object> category1 = new HashMap<>();
        category1.put("id", 2);
        Map<String, Object> category2 = new HashMap<>();
        category2.put("id", 3);

        categories.add(category1);
        categories.add(category2);

        postProductInstance.put("categories", categories);

    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() {
        existingProductId = 2L;

        given().get("/products/{id}", existingProductId)
                .then()
                .statusCode(200)
                .body("id", is(2))
                .body("name", equalTo("Smart TV"))
                .body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg"))
                .body("price", is(2190.0F))
                .body("categories.id", hasItems(2, 3))
                .body("categories.name", hasItems("Eletrônicos", "Computadores"));
    }

    @Test
    public void findAllShouldReturnPageProductsWhenProductNameIsEmpty() {
        given().get("/products?page=0")
                .then()
                .statusCode(200)
                .body("content.name", hasItems("Macbook Pro", "PC Gamer Tera"));
    }

    @Test
    public void findAllShouldReturnPageProductsWhenProductNameIsNotEmpty() {
        given().get("/products?name={productName}", productName)
                .then()
                .statusCode(200)
                .body("content.id[0]", is(3))
                .body("content.name[0]", equalTo("Macbook Pro"))
                .body("content.price[0]", is(1250.0F))
                .body("content.imgUrl[0]",
                        equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"));
    }

    @Test
    public void findAllShouldReturnPagedProductsWithPriceGreaterThan2000() {
        given().get("/products?size=25")
                .then()
                .statusCode(200)
                .body("content.findAll { it.price > 2000}.name", hasItems("Smart TV",
                        "PC Gamer Hera", "PC Gamer Weed", "PC Gamer Max",
                        "PC Gamer Min", "PC Gamer Boo", "PC Gamer Foo"));
    }

    @Test
    public void insertShouldReturnProductCreatedWhenAdminLogged() throws Exception {
        JSONObject newProduct = new JSONObject(postProductInstance);

        given().header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when().post("/products")
                .then().statusCode(201)
                .body("name", equalTo("Meu produto"))
                .body("price", is(50.0F))
                .body("imgUrl",
                        equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"))
                .body("categories.id", hasItems(2, 3));

    }

    @Test
    public void insertShouldReturnUnprocessableEntityCreatedWhenAdminLoggedAndInvalidName() throws Exception {
        postProductInstance.put("name", "ab");
        JSONObject newProduct = new JSONObject(postProductInstance);

        given().header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when().post("/products")
                .then().statusCode(422)
                .body("errors.message[0]", equalTo("Nome precisa ter de 3 a 80 caracteres"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityCreatedWhenAdminLoggedAndInvalidDescription() throws Exception {
        postProductInstance.put("description", "ab");
        JSONObject newProduct = new JSONObject(postProductInstance);

        given().header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when().post("/products")
                .then().statusCode(422)
                .body("errors.message[0]", equalTo("Descrição precisa ter no mínimo 10 caracteres"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityCreatedWhenAdminLoggedAndNegativePrice() throws Exception {
        postProductInstance.put("price", -50.0);
        JSONObject newProduct = new JSONObject(postProductInstance);

        given().header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when().post("/products")
                .then().statusCode(422)
                .body("errors.message[0]", equalTo("O preço deve ser positivo"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityCreatedWhenAdminLoggedAndPriceIsZero() throws Exception {
        postProductInstance.put("price", 0.0);
        JSONObject newProduct = new JSONObject(postProductInstance);

        given().header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when().post("/products")
                .then().statusCode(422)
                .body("errors.message[0]", equalTo("O preço deve ser positivo"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityCreatedWhenAdminLoggedAndProductHasNoCategory() throws Exception {
        postProductInstance.put("categories", null);
        JSONObject newProduct = new JSONObject(postProductInstance);

        given().header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when().post("/products")
                .then().statusCode(422)
                .body("errors.message[0]", equalTo("Deve ter pelo menos uma categoria"));
    }

    @Test
    public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
        JSONObject newProduct = new JSONObject(postProductInstance);

        given().header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when().post("/products")
                .then().statusCode(403);
    }

    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
        JSONObject newProduct = new JSONObject(postProductInstance);

        given().header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when().post("/products")
                .then().statusCode(401);
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExistsAndAdminLogged() throws Exception {
        existingProductId = 25L;

        given().header("Authorization", "Bearer " + adminToken)
                .when().delete("/products/{id}", existingProductId)
                .then().statusCode(204);
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() throws Exception {
        nonExistingProductId = 100L;

        given().header("Authorization", "Bearer " + adminToken)
                .when().delete("/products/{id}", nonExistingProductId)
                .then().statusCode(404);
    }

    @Test
    public void deleteShouldReturnBadRequestWhenDependentIdAndAdminLogged() throws Exception {
        dependentProductId = 3L;

        given().header("Authorization", "Bearer " + adminToken)
                .when().delete("/products/{id}", dependentProductId)
                .then().statusCode(400);
    }

    @Test
    public void deleteShouldReturnForbiddenWhenClientLogged() throws Exception {
        given().header("Authorization", "Bearer " + clientToken)
                .when().delete("/products/{id}", existingProductId)
                .then().statusCode(403);
    }

    @Test
    public void deleteShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
        given().header("Authorization", "Bearer " + invalidToken)
                .when().delete("/products/{id}", existingProductId)
                .then().statusCode(404);
    }
}
