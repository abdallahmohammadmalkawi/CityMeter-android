package uic.hcilab.citymeter.DB;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uic.hcilab.citymeter.Helpers.LogInHelper;

public class UsersDBHelper {
    // Declare a DynamoDBMapper object
    DynamoDBMapper dynamoDBMapper;
    Context ctx;

    public UsersDBHelper(Context context) {
        ctx = context;
        connect();
    }
    public void connect(){
        // AWSMobileClient enables AWS user credentials to access your table
        AWSMobileClient.getInstance().initialize(ctx, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {

            }
        }).execute();


        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();


        // Add code to instantiate a AmazonDynamoDBClient
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);

        dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(configuration)
                .build();
    }
    List<UsersDO> user;
    //CREATE
    public void createUser(String id, String name, String dob, String gender, String ethnicity, String education,  double isCo) {
        final UsersDO user = new UsersDO();

        user.setUserID(id);
        user.setName(name);
        user.setDob(dob);
        user.setGender(gender);
        user.setEducation(education);
        user.setEthnicity(ethnicity);
        user.setIsCoUser(isCo);
        connect();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dynamoDBMapper.save(user);
                    // Item saved
                } catch (Exception e) {
                    Log.i("BT", "Error writing to dB: " + e.toString());
                }
            }
        });
        try {
            thread.start();
            thread.join();
        } catch (Exception e){

        }
    }
    public boolean isCoUser(){
        Boolean isCoUser = false;
        try {
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(LogInHelper.getCurrUser()));

            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("userID = :val1").withExpressionAttributeValues(eav);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    user = dynamoDBMapper.scan(UsersDO.class, scanExpression);
                }
            });
            thread.start();
            thread.join();
            if(user.get(0).getIsCoUser() == 0.0){
                isCoUser = false;
            }
            else{
                isCoUser = true;
            }
        } catch (Exception e) {
            Log.i("settings" , "Error: " + e.toString());
        }

        return isCoUser;
    }
    public String getName(String id){
        String name = id;
        try {
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(id));

            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("userID = :val1").withExpressionAttributeValues(eav);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    user = dynamoDBMapper.scan(UsersDO.class, scanExpression);
                }
            });
            thread.start();
            thread.join();
            name = user.get(0).getName();

        } catch (Exception e) {
            Log.i("settings" , "Error: " + e.toString());
        }
        return name;
    }

}
