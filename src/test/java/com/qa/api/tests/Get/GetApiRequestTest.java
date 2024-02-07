package com.qa.api.tests.Get;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

public class GetApiRequestTest {

    Playwright playwright;
    APIRequest apiRequest;
    APIRequestContext apiRequestContext ;

    @BeforeTest
    public void setUp(){
         playwright=Playwright.create();
         apiRequest= playwright.request();
         apiRequestContext =apiRequest.newContext();
    }



@Test
public void getUsersApiTest() throws IOException {

   APIResponse apiResponse= apiRequestContext.get("https://gorest.co.in/public/v2/users");
   int statusCode =apiResponse.status();
   String statusText= apiResponse.statusText();

   System.out.println("Response Status is :" + statusCode);
    System.out.println("Response Status Text is :" + statusText);

    //Validate status code
    Assert.assertEquals(statusCode ,200);

    //print response text
    System.out.println("Printing response text ... ");
    String responseText =apiResponse.text();
    System.out.println(responseText);

    //Convert to json response
    System.out.println("Printing Json Pretty response  ... ");
    ObjectMapper objectMapper = new ObjectMapper();
   JsonNode jsonResponse =objectMapper.readTree( apiResponse.body());
    String jsonPrettyResponse =jsonResponse.toPrettyString();
   System.out.println(jsonPrettyResponse) ;

   // validate url
    String getUsersUrl =apiResponse.url();
    Assert.assertEquals(getUsersUrl,"https://gorest.co.in/public/v2/users");

   //validate headers
    System.out.println("print headers ...");
   Map<String,String> headersMap = apiResponse.headers();
    System.out.println(headersMap);
    Assert.assertEquals(headersMap.get("content-type"),"application/json; charset=utf-8");


    }
    @Test
    //getting a specific user using parameters ,in our case status and gender
    public void getSpecificUserApiTest() throws IOException {

        APIResponse apiResponse= apiRequestContext.get("https://gorest.co.in/public/v2/users",

                RequestOptions.create()
                        .setQueryParam("status","active")
                        .setQueryParam("gender","male")

        );

        //Convert apiResponse to json response
        System.out.println("Printing Json Pretty response  ... ");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse =objectMapper.readTree( apiResponse.body());
        String jsonPrettyResponse =jsonResponse.toPrettyString();
        System.out.println(jsonPrettyResponse) ;

    }

    @AfterTest
    public void tearDown(){
        playwright.close();
    }
}
