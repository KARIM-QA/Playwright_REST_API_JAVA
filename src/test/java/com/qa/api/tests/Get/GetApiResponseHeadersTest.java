package com.qa.api.tests.Get;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.HttpHeader;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class GetApiResponseHeadersTest {

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
public void getHeadersTest(){

    APIResponse apiResponse= apiRequestContext.get("https://gorest.co.in/public/v2/users");

    //Validate headers usning Map
    Map<String,String> headersMap = apiResponse.headers();
    System.out.println("printing headers using Map ..." );
    System.out.println("Total headers is :" + headersMap.size());
    headersMap.forEach((key,value)->System.out.println("key : "+key + " || Value => " +value));
    Assert.assertEquals(headersMap.get("content-type"),"application/json; charset=utf-8");
    Assert.assertEquals(headersMap.get("x-links-current"),"https://gorest.co.in/public/v2/users?page=1");

    //Validate headers usning List

   List<HttpHeader> headersList= apiResponse.headersArray();
    System.out.println("printing headers using List ..." );
    for(HttpHeader header :headersList){
        System.out.println("header name :"+ header.name+ " || header value =>"+header.value );
    }
}
    @AfterTest
    public void tearDown(){
        playwright.close();
    }


}
