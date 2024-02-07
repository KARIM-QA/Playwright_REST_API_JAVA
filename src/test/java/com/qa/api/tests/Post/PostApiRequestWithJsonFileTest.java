package com.qa.api.tests.Post;

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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class PostApiRequestWithJsonFileTest {
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
/*
    //generate a random email to avoid repetition that fail the test
    public static String getRandomEmail(){

        emailId= "karim.automation"+System.currentTimeMillis() +"@gmail.com" ;
        return emailId ;

    }

 */
    @Test
    public void createUserPostWithJsonFileTest() throws IOException {

       //get Json file
        byte [] fileBytes ;
        File file =new File("./src/test/jsonData/user.json");
        //convert file to byte []
        fileBytes= Files.readAllBytes(file.toPath());

        //Post call => create a user
        APIResponse apiPostResponse= apiRequestContext.post("https://gorest.co.in/public/v2/users/",

                RequestOptions.create()
                        .setHeader("Content-type","application/json")
                        .setHeader("Authorization","Bearer 4a1bf4771cf69674c459eac33d3831575aa60a13eff438e007befcf3a489e721")
                        .setData(fileBytes)
        );
        System.out.println("==============Validate Post Call Response================");
        System.out.println("Actual Status Code: " + apiPostResponse.status());

        //Assert.assertEquals(apiPostResponse.status(),"201");
       // Assert.assertEquals(apiPostResponse.statusText(),"Created");
        System.out.println("apiPostResponse.text() => " + apiPostResponse.text());
        ObjectMapper objectMapper =new ObjectMapper();
        JsonNode postJsonResponse= objectMapper.readTree(apiPostResponse.body());
        String prettyPostJsonResponse = postJsonResponse.toPrettyString();
        System.out.println(prettyPostJsonResponse);

        System.out.println("==============Validate Get Call Response================");
        //Capture Id from Json Response
        String userId = postJsonResponse.get("id").asText();
        System.out.println("userId is  :" +userId);

        //Get Call => fetch the same user by id
        APIResponse apiGetResponse =apiRequestContext.get("https://gorest.co.in/public/v2/users/"+userId,
                RequestOptions.create()
                        .setHeader("Authorization","Bearer 4a1bf4771cf69674c459eac33d3831575aa60a13eff438e007befcf3a489e721")
        ) ;
        // Assert.assertEquals(apiGetResponse.status(),"200");
        Assert.assertEquals(apiGetResponse.statusText(),"OK");
        System.out.println("apiGetResponse.text() => " +apiGetResponse.text());
        Assert.assertTrue(apiGetResponse.text().contains(userId));
        Assert.assertTrue(apiGetResponse.text().contains("Karim"));

        //Validate the email id
        Assert.assertTrue(apiGetResponse.text().contains("karim.automation007777778@gmail.com"));
    }



    @AfterTest
    public void tearDown(){
        playwright.close();
    }

}
