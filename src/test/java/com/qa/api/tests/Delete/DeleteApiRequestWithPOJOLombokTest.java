package com.qa.api.tests.Delete;

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


public class DeleteApiRequestWithPOJOLombokTest {
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
    public void deleteUserWithPojoLombokTest() throws IOException {

        /* steps
         1.Post call => create a user
         2. Delete call  => Delete a user with a specific userId
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
        System.out.println("==============Printing Post call => create a user ... ==============");
        APIResponse apiPostResponse= apiRequestContext.post("https://gorest.co.in/public/v2/users/",

                RequestOptions.create()
                        .setHeader("Content-type","application/json")
                        .setHeader("Authorization","Bearer 4a1bf4771cf69674c459eac33d3831575aa60a13eff438e007befcf3a489e721")
                        .setData(userWithLombok)
        );

        System.out.println("Actual Status Code: " + apiPostResponse.status());
        Assert.assertEquals(apiPostResponse.statusText(),"Created");
        Assert.assertEquals(apiPostResponse.status(),201);

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

        //fetch the userId
        String userId =actualUser.getId();
        System.out.println("New UserId is :" + userId);

        // *********************************************2.Delete call => Delete a user************************/
        System.out.println("==============Printing Delete call => Delete a user ... ==============");

        APIResponse apiDeleteResponse= apiRequestContext.delete("https://gorest.co.in/public/v2/users/"+userId,

                RequestOptions.create()
                        .setHeader("Authorization","Bearer 4a1bf4771cf69674c459eac33d3831575aa60a13eff438e007befcf3a489e721")

        );
        Assert.assertEquals(apiDeleteResponse.status(),204);
        System.out.println("Status is "+ apiDeleteResponse.status());
        System.out.println("Status text after Delete Request is "+ apiDeleteResponse.statusText());

        String deleteApiResponseText= apiDeleteResponse.text();
        System.out.println("Delete Response body is : "+ deleteApiResponseText);

        // *********************************************3.Get call => Validation the output message after deleting a user*********************/
        System.out.println("==============Printing Get call => Get a user with the last updated userId ... ==============");

        APIResponse GetApiResponse= apiRequestContext.get("https://gorest.co.in/public/v2/users/"+userId,
                RequestOptions.create()
                        .setHeader("Authorization","Bearer 4a1bf4771cf69674c459eac33d3831575aa60a13eff438e007befcf3a489e721")
        );

        //Validate status code
        Assert.assertEquals(GetApiResponse.status(),404);
        System.out.println("Status code is : "+ GetApiResponse.status());
        Assert.assertEquals(GetApiResponse.statusText(),"Not Found");
        System.out.println("Status text after deleting a user is :"+ GetApiResponse.statusText());

        //fetch the response text
        String getApiResponseText = GetApiResponse.text();
        System.out.println("response get text  => " + getApiResponseText);
        Assert.assertTrue(getApiResponseText.contains("Resource not found"));
    }


    @AfterTest
    public void tearDown(){
        playwright.close();
    }

}
