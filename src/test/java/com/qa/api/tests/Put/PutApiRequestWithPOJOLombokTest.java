package com.qa.api.tests.Put;

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


public class PutApiRequestWithPOJOLombokTest {
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
    public void UpdateUserPutWithPojoLombokTest() throws IOException {
         /* steps
         1.Post call => create a user
         2. put call  => Update a user with a specific userId
         3. Get call  => Get a user with a specific userId
         */

       //Create UserWithLombok object using builder pattern
        UserWithLombok userWithLombok = UserWithLombok.builder()
                .name("Karim Laribi")
                .email(getRandomEmail())
                .gender("male")
                .status("active")
                .build();



        // **********************************************1.Post call => create a user***************************/
        System.out.println("Printing Post call => create a user ... ");
        APIResponse apiPostResponse= apiRequestContext.post("https://gorest.co.in/public/v2/users/",

                RequestOptions.create()
                        .setHeader("Content-type","application/json")
                        .setHeader("Authorization","Bearer 4a1bf4771cf69674c459eac33d3831575aa60a13eff438e007befcf3a489e721")
                        .setData(userWithLombok)
        );

        System.out.println("Actual Status Code: " + apiPostResponse.status());
        Assert.assertEquals(apiPostResponse.statusText(),"Created");

        String postResponseText=apiPostResponse.text();
        System.out.println("response post text  => " + postResponseText);

        //Convert response text/Json to POJO (Deserialization)
        ObjectMapper objectMapper =new ObjectMapper();
        UserWithLombok actualUser = objectMapper.readValue(postResponseText,UserWithLombok.class);
        System.out.println("Actual user is :"+ actualUser);
        System.out.println("User name with lombok is  :"+ userWithLombok.getName());

         //Assertions after Post request
        Assert.assertNotNull(actualUser.getId());
        Assert.assertEquals(actualUser.getEmail(),userWithLombok.getEmail());
        Assert.assertEquals(actualUser.getName(),userWithLombok.getName());
        Assert.assertEquals(actualUser.getGender(),userWithLombok.getGender());
        Assert.assertEquals(actualUser.getStatus(),userWithLombok.getStatus());

        //fetch the userId
        String userId =actualUser.getId();
        System.out.println("New UserId is :" + userId);

        // update "Karim Laribi" to "King of Automation"
        userWithLombok.setName("King of Automation");

        // *********************************************2.Put call => Update a user************************/
        System.out.println("Printing Put call => Update a user ... ");
        APIResponse apiPutResponse= apiRequestContext.put("https://gorest.co.in/public/v2/users/"+userId,

                RequestOptions.create()
                        .setHeader("Content-type","application/json")
                        .setHeader("Authorization","Bearer 4a1bf4771cf69674c459eac33d3831575aa60a13eff438e007befcf3a489e721")
                        .setData(userWithLombok)
        );
        Assert.assertEquals(apiPutResponse.status(),200);
        System.out.println("Status updated is "+ apiPutResponse.status());
        System.out.println("Status text updated is "+ apiPutResponse.statusText());

        String putApiResponseText= apiPutResponse.text();
        UserWithLombok actualUserUpdated = objectMapper.readValue(putApiResponseText,UserWithLombok.class);
        System.out.println("Actual user updated is :"+ actualUserUpdated);
        System.out.println("User name with lombok updated  is  :"+ userWithLombok.getName());

        //Assertions after Put request
        Assert.assertEquals(actualUserUpdated.getId(),userId);
        Assert.assertEquals(actualUserUpdated.getEmail(),userWithLombok.getEmail());
        Assert.assertEquals(actualUserUpdated.getName(),userWithLombok.getName());
        Assert.assertEquals(actualUserUpdated.getGender(),userWithLombok.getGender());
        Assert.assertEquals(actualUserUpdated.getStatus(),userWithLombok.getStatus());

        // *********************************************3.Get call => Get a user with the last updated userId*********************/
        System.out.println("Printing Get call => Get a user with the last updated userId ... ");

        APIResponse GetApiResponse= apiRequestContext.get("https://gorest.co.in/public/v2/users/"+userId,
                RequestOptions.create()
                        .setHeader("Authorization","Bearer 4a1bf4771cf69674c459eac33d3831575aa60a13eff438e007befcf3a489e721")
                );

        //print url , be careful autorization must be specified
         System.out.println(GetApiResponse.url());

        //Validate status code
        Assert.assertEquals(GetApiResponse.status(),200);
        System.out.println("Status updated is "+ GetApiResponse.status());
        System.out.println("Status text updated is "+ GetApiResponse.statusText());

        //fetch the response text
        String getApiResponseText = GetApiResponse.text();
        System.out.println("response get text  => " + getApiResponseText);

        UserWithLombok getActualUser = objectMapper.readValue(getApiResponseText,UserWithLombok.class);

        //Assertions after get request
        Assert.assertEquals(getActualUser.getId(),userId);
        Assert.assertEquals(getActualUser.getEmail(),userWithLombok.getEmail());
        Assert.assertEquals(getActualUser.getName(),userWithLombok.getName());
        Assert.assertEquals(getActualUser.getGender(),userWithLombok.getGender());
        Assert.assertEquals(getActualUser.getStatus(),userWithLombok.getStatus());
    }



    @AfterTest
    public void tearDown(){
        playwright.close();
    }

}
