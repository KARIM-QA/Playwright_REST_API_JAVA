package com.qa.api.tests.Get;

import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class GetApiDisposeTest {
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
    public void disposeResponseTest(){
    APIResponse apiResponse= apiRequestContext.get("https://gorest.co.in/public/v2/users");
    int statusCode =apiResponse.status();
    String statusText= apiResponse.statusText();

    System.out.println("Response Status is :" + statusCode);
    System.out.println("Response Status Text is :" + statusText);

    //Validate status code
    Assert.assertEquals(statusCode ,200);

    //print response text
    System.out.println("Printing response text ... ");
    System.out.println(apiResponse.text());

    //Dispose only the body not the status code
    System.out.println("Printing response text after dispose ... ");
    apiResponse.dispose();

    try{
        System.out.println(statusText);
        System.out.println(apiResponse.text());
    }catch(PlaywrightException pe){
        System.out.println("response body is disposed !!!" + pe.getMessage());
    }

}
    @AfterTest
    public void tearDown(){

        playwright.close();
    }

}
