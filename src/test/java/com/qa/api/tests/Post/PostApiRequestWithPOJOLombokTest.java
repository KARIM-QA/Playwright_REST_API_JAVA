package com.qa.api.tests.Post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import com.qa.api.data.POJO.UserWithLombok;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;




public class PostApiRequestWithPOJOLombokTest {
    Playwright playwright;
    APIRequest apiRequest;
    APIRequestContext apiRequestContext ;
    static String emailId ;

    @BeforeTest
    public void setUp(){
        playwright=Playwright.create();
        apiRequest= playwright.request();
        apiRequestContext =apiRequest.newContext();
    }

    //generate a random email to avoid repetition that fail the test
    public static String getRandomEmail(){

        emailId= "karim.automation"+System.currentTimeMillis() +"@gmail.com" ;
        return emailId ;

    }

    @Test
    public void createUserPostWithPojoLombokTest() throws IOException {

       //Create UserWithLombok object using builder pattern

        UserWithLombok userWithLombok = UserWithLombok.builder()
                .name("Karim Laribi")
                .email(getRandomEmail())
                .gender("male")
                .status("active")
                .build();



        //Post call => create a user
        APIResponse apiPostResponse= apiRequestContext.post("https://gorest.co.in/public/v2/users/",

                RequestOptions.create()
                        .setHeader("Content-type","application/json")
                        .setHeader("Authorization","Bearer 4a1bf4771cf69674c459eac33d3831575aa60a13eff438e007befcf3a489e721")
                        .setData(userWithLombok)
        );
        System.out.println("==============Validate Post Call Response================");
        System.out.println("Actual Status Code: " + apiPostResponse.status());
        Assert.assertEquals(apiPostResponse.statusText(),"Created");

        String responseText=apiPostResponse.text();
        System.out.println("response post text  => " + responseText);

        //Convert response text/Json to POJO (Deserialization)
        ObjectMapper objectMapper =new ObjectMapper();
        UserWithLombok actualUser = objectMapper.readValue(responseText,UserWithLombok.class);
        System.out.println("Actual user is :"+ actualUser);
        System.out.println("User name with lombok is  :"+ userWithLombok.getName());

        Assert.assertEquals(actualUser.getEmail(),userWithLombok.getEmail());
        Assert.assertNotNull(actualUser.getId());
        Assert.assertEquals(actualUser.getName(),userWithLombok.getName());
        Assert.assertEquals(actualUser.getGender(),userWithLombok.getGender());
        Assert.assertEquals(actualUser.getStatus(),userWithLombok.getStatus());
    }



    @AfterTest
    public void tearDown(){
        playwright.close();
    }

}
