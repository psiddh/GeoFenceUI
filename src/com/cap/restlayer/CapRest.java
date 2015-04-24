package com.cap.restlayer;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.capsenderreceiver.ApplicationController;

///////////////////////
// VERY IMPORTANT
// http://stackoverflow.com/questions/22428343/android-volley-double-post-when-have-slow-request
// Volley timeout
///////////////////////

public class CapRest {
	
	static final String TAG = "CapRest";
	
	//static final String cCAP_DOMAIN     = "http://50.59.22.184:8075";
	static final String cCAP_DOMAIN     = "http://50.59.22.188:17011";
	static final String cCAP_ENDPOINT   = "/cap/v1";
	static final String cCAP_REGISTER   = "/notification/register";
	static final String cACCOUNT_CREATE = "/account";
	static final String cACCOUNT_LOGIN  = "/account/login";
	
	//Coupon APIs
	static final String cCAP_CREATE_COUPON = "/couponing/coupon";
	static final String cCAP_GET_COUPON    = "/couponing/coupon";
	static final String cCAP_DELETE_COUPON = "/couponing/coupon";
	static final String cCAP_GET_COUPONS   = "/couponing/coupons";
	static final String cCAP_SUBMIT_COUPON = "/couponing/coupon/submit";
	static final String cCAP_REDEEM_COUPON = "/couponing/coupon/redeem";
	
	
	private RequestQueue mRequestQueue;

	protected String urlJsonObj;

	/*
	 * Creates a user account with the given 
	 * + username
	 * + password
	 * + email
	 */
	public void createAccount(Context context, HashMap<String, String> params){
		
		String URL = cCAP_DOMAIN+cCAP_ENDPOINT+cACCOUNT_CREATE;
		
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
			       new Response.Listener<JSONObject>() {
			           @Override
			           public void onResponse(JSONObject response) {
			               try {
			            	   //TODO: do something here
			            	   Log.d(TAG,"response");
			                   VolleyLog.v("Response:%n %s", response.toString(4));
			               } catch (JSONException e) {
			                   e.printStackTrace();
			               }
			           }
			       }, new Response.ErrorListener() {
			           @Override
			           public void onErrorResponse(VolleyError error) {
			               VolleyLog.e("Error: ", error.getMessage());
			           }
			       });
		
		//adds request to the global queue
		ApplicationController.getInstance().addToRequestQueue(req);
	}
	
	/*
	 * Logs into an existing account with the given credentials
	 */
	public void loginAccount(Context context, HashMap<String, String> params){

		String URL = cCAP_DOMAIN + cCAP_ENDPOINT + cACCOUNT_LOGIN;
		
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
			       new Response.Listener<JSONObject>() {
			           @Override
			           public void onResponse(JSONObject response) {
			               try {
			            	   //TODO: do something here
			            	   Log.d(TAG,"response");
			                   VolleyLog.v("Response:%n %s", response.toString(4));
			               } catch (JSONException e) {
			                   e.printStackTrace();
			               }
			           }
			       }, new Response.ErrorListener() {
			           @Override
			           public void onErrorResponse(VolleyError error) {
			               VolleyLog.e("Error: ", error.getMessage());
			           }
			       });
		
		//adds request to the global queue
		ApplicationController.getInstance().addToRequestQueue(req);
	}
	
	/*
	 * Creates a Coupon 
	 */
	public void createCoupon(Context context, HashMap<String, String> params){

		String URL = cCAP_DOMAIN + cCAP_ENDPOINT + cCAP_CREATE_COUPON;
		
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
			       new Response.Listener<JSONObject>() {
			           @Override
			           public void onResponse(JSONObject response) {
			               try {
			            	   //TODO: do something here
			            	   Log.d(TAG,"response");
			                   VolleyLog.v("Response:%n %s", response.toString(4));
			               } catch (JSONException e) {
			                   e.printStackTrace();
			               }
			           }
			       }, new Response.ErrorListener() {
			           @Override
			           public void onErrorResponse(VolleyError error) {
			               VolleyLog.e("Error: ", error.getMessage());
			           }
			       });
		
		//adds request to the global queue
		ApplicationController.getInstance().addToRequestQueue(req);
	}
	
	/*
	 * Submits a Coupon 
	 */
	public void submitCoupon(Context context, int index, HashMap<String, String> params){

		String URL = cCAP_DOMAIN + cCAP_ENDPOINT + cCAP_SUBMIT_COUPON+"/"+index;
		
		JsonObjectRequest req = new JsonObjectRequest(URL,new JSONObject(params),
			       new Response.Listener<JSONObject>() {
			           @Override
			           public void onResponse(JSONObject response) {
			               try {
			            
			            	   //TODO: do something here
			            	   Log.d(TAG,"response:"+response.toString(4));
			                   VolleyLog.v("Response:%n %s", response.toString(4));
			               } catch (JSONException e) {
			                   e.printStackTrace();
			               }
			           }
			       }, new Response.ErrorListener() {
			           @Override
			           public void onErrorResponse(VolleyError error) {
			               VolleyLog.e("Error: ", error.getMessage());
			           }
			       });
		
		//adds request to the global queue
		ApplicationController.getInstance().addToRequestQueue(req);
	}
	
	/*
	 * Redeems a Coupon 
	 */
	public void redeemCoupon(Context context, int index, HashMap<String, String> params){

		String URL = cCAP_DOMAIN + cCAP_ENDPOINT + cCAP_REDEEM_COUPON+"/"+index;
		
		JsonObjectRequest req = new JsonObjectRequest(URL,new JSONObject(params),
			       new Response.Listener<JSONObject>() {
			           @Override
			           public void onResponse(JSONObject response) {
			               try {
			            	   //TODO: do something here
			            	   Log.d(TAG,"response:"+response.toString(4));
			                   VolleyLog.v("Response:%n %s", response.toString(4));
			               } catch (JSONException e) {
			                   e.printStackTrace();
			               }
			           }
			       }, new Response.ErrorListener() {
			           @Override
			           public void onErrorResponse(VolleyError error) {
			               VolleyLog.e("Error: ", error.getMessage());
			           }
			       });
		
		//adds request to the global queue
		ApplicationController.getInstance().addToRequestQueue(req);
	}
	
	/*
	 * Get all coupons 
	 */
	public void getAllCoupons(Context context){

		String URL = cCAP_DOMAIN + cCAP_ENDPOINT + cCAP_GET_COUPONS;
		
		JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray> () {
		    @Override
		    public void onResponse(JSONArray response) {
		        try {
		        	Log.d(TAG,"response:"+response.toString(4));
		            VolleyLog.v("Response:%n %s", response.toString(4));
		        } catch (JSONException e) {
		            e.printStackTrace();
		        }
		    }
		}, new Response.ErrorListener() {
		    @Override
		    public void onErrorResponse(VolleyError error) {
		        VolleyLog.e("Error: ", error.getMessage());
		    }
		});
		
		//adds request to the global queue
		ApplicationController.getInstance().addToRequestQueue(req);
	}	
	
	/*
	 * Get all coupons 
	 */
	public void getCoupon(Context context, int couponId, HashMap<String, String> params){

		String URL = cCAP_DOMAIN + cCAP_ENDPOINT + cCAP_GET_COUPON+"/"+couponId;
		
		JsonObjectRequest req = new JsonObjectRequest(URL,null,
			       new Response.Listener<JSONObject>() {
			           @Override
			           public void onResponse(JSONObject response) {
			               try {
			            	   //TODO: do something here
			            	   Log.d(TAG,"response:"+response.toString(4));
			                   VolleyLog.v("Response:%n %s", response.toString(4));
			               } catch (JSONException e) {
			                   e.printStackTrace();
			               }
			           }
			       }, new Response.ErrorListener() {
			           @Override
			           public void onErrorResponse(VolleyError error) {
			               VolleyLog.e("Error: ", error.getMessage());
			           }
			       });
		
		//adds request to the global queue
		ApplicationController.getInstance().addToRequestQueue(req);
	}	
}
