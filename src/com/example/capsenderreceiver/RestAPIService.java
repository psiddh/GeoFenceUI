package com.example.capsenderreceiver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


public class RestAPIService extends IntentService {

    private static final String TAG = "Conscia: RestAPIService >";
    private int result = 0;
    public static final String RESTAPI_ENDPOINT_URL = "url";
    public static final String RESTAPI_ARGS_TYPE = "argstype";
    public static final String RESTAPI_ARGS = "args";
    public static final String RESTAPI_TYPE = "apitype";

    public static final String API_NAME = "apitype";
    public static final String STATUSCODE = "statuscode";
    public static final String RESPONSEBODY = "responsebody";
    public static final String NOTIFICATION = "com.example.capsenderreceiver.service.receiver";

    public static final String ARGS_TYPE_STRING = "args_type_string";
    public static final String ARGS_TYPE_MAP = "args_type_map";
    
    public static final String RESTAPI_TYPE_REGISTER = "API_REGISTER";
    public static final String RESTAPI_TYPE_UNREGISTER = "API_UNREGISTER";
    public static final String RESTAPI_TYPE_COUPON = "API_COUPON";
    
    public String mApiName = null;
    public RestAPIService() {
      super("RestAPIService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub
        String url = intent.getStringExtra(RESTAPI_ENDPOINT_URL);
        String argstype = intent.getStringExtra(RESTAPI_ARGS_TYPE);
        mApiName = intent.getStringExtra(API_NAME);
        HashMap<String, String> hashMap ;
         
        if (argstype.equalsIgnoreCase(ARGS_TYPE_MAP)) {
          hashMap = (HashMap<String, String>)intent.getSerializableExtra(RESTAPI_ARGS);
          doPost(url, hashMap, new DefaultHttpClient());
        }
    }
    
    private JSONObject getJsonObjectFromMap(HashMap<String, String>  Map) throws JSONException {
        //all the passed parameters from the post request
        //iterator used to loop through all the parameters
        //passed in the post request
        Iterator<java.util.Map.Entry<String, String>> iter = Map.entrySet().iterator();

        //Stores JSON
        JSONObject holder = new JSONObject();
        //While there is another entry
        while (iter.hasNext())  {
            //gets an entry in the params
            java.util.Map.Entry<String, String> pairs = iter.next();

            //creates a key for Map
            String key = (String)pairs.getKey();

            //Create a new map
            String value = (String)pairs.getValue();   
            holder.put(key, value);
        }
        return holder;
    }
    
    private HttpResponse doPost(String mUrl, HashMap<String, String> hm, DefaultHttpClient httpClient) {
         
        HttpResponse response = null;
        HttpPost postMethod = new HttpPost(mUrl);
        String statusCode = null;
        String responseBody = null;
        if (hm == null) return null;
         
        try {
            //convert parameters into JSON object
            JSONObject holder = getJsonObjectFromMap(hm);

            //passes the results to a string builder/entity
            StringEntity se = new StringEntity(holder.toString());

            //sets the post request as the resulting string
            postMethod.setEntity(se);
            postMethod.setHeader("Accept", "application/json");
            postMethod.setHeader("Content-type", "application/json");
            response = httpClient.execute(postMethod);
            statusCode = String.valueOf(response.getStatusLine().getStatusCode());
            Log.d(TAG, "STATUS CODE: " + statusCode );
         
        } catch (Exception e) {
         
            Log.e("Exception", e.getMessage());
         
        } finally {
         
        }
        try {
            responseBody = EntityUtils.toString(response.getEntity());
            Log.d(TAG, " -- responseBody -- " + responseBody);
        } catch (ParseException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //Log.d(TAG, " -- response -- " + response.toString());
        publishResults(responseBody, statusCode);
        return response;
         
    }
    
    private void publishResults(String responseBody, String statusCode) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(API_NAME, mApiName);
        intent.putExtra(RESPONSEBODY, responseBody);
        intent.putExtra(STATUSCODE, statusCode);
        sendBroadcast(intent);
    }
}
