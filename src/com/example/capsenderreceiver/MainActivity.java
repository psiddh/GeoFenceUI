package com.example.capsenderreceiver;

import java.util.HashMap;
import java.util.Map;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "Conscia: MainActivity >";
    private static final String FIREBASE_URL = "https://conscia-cap.firebaseIO.com";
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    String mUniqueID = null;
    String mEmailID = null;
    String mPhoneNumber = null;
    String mDeviceId = null;
    TelephonyManager mTelMgr = null;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    /**
     * This class uses the AccountManager to get the primary email address of the
     * current user.
     */
    public class UserEmailFetcher {

      String getEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);

        if (account == null) {
          return null;
        } else {
          return account.name;
        }
      }

      private Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
          account = accounts[0];
        } else {
          account = null;
        }
        return account;
      }
    }

    UserEmailFetcher mUserEmailFetcher = new UserEmailFetcher();

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
          Bundle bundle = intent.getExtras();
          if (bundle != null) {
            String apiName = bundle.getString(RestAPIService.API_NAME);
            if (apiName.equals(RestAPIService.RESTAPI_TYPE_REGISTER)) {
                String statusCode = bundle.getString(RestAPIService.STATUSCODE);
                String responseBody = bundle.getString(RestAPIService.RESPONSEBODY);
                Editor editor = sharedpreferences.edit();
                editor.putString("CAPReg", "Registered");
                editor.commit();
                Log.d(TAG, "Status Code : " + statusCode + " - responseBody : " + responseBody);
            }
          }
        }
      };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        mTelMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        mFirebaseRef = new Firebase(FIREBASE_URL);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEmailID = mUserEmailFetcher.getEmail(this);
        mPhoneNumber = mTelMgr.getLine1Number();
        mDeviceId = mTelMgr.getDeviceId();

        Log.d(TAG, "Attempt tp register for the first time with Firebase");
        checkWithFirebaseAndRegisterIfNotAlreadyRegistered();

        // This is only client side validation. Obviously not enough
        // TBD: Actual solution should have server side validation., but for Citi event this is OK!
        String regWithCAP = sharedpreferences.getString("CAPReg", null);
        if (regWithCAP == null) {
           Log.d(TAG, "Attempt tp register for the first time with CAP Server");
          registerWithCAPServer();
        } else {
            Log.d(TAG,"This device is already registered with CAP Server!");
        }
        // Set up receiver anyways!
        setupReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
	    switch(item.getItemId()) {
		    case android.R.id.home:
		        onBackPressed();
		        break;
		    case R.id.menu_item_map:
		    	// Launch Map mode
		        //Intent intent = new Intent(getBaseContext(), MapFragmentActivity.class);
		    	Intent intent = new Intent(getBaseContext(), CreateCoupon.class);
		        intent.putExtra("filter", "test");
		        startActivity(intent);
		    	break;
	    }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
      super.onResume();
      registerReceiver(mReceiver, new IntentFilter(RestAPIService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
      super.onPause();
      unregisterReceiver(mReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Clean up our listener so we don't have it attached twice.
        //mFirebaseRef.getRoot().child("users/").removeEventListener(mConnectedListener);
    }

    private void setupReceiver() {
       // By default every registered app is deemed to be a receiver (receive coupons!)
        // Set up a notification to let us know when we're connected or disconnected from the Firebase servers
        if (mUniqueID == null) {
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mUniqueID = sharedpreferences.getString("uuid", null);
            if(mUniqueID == null) {
              Log.d(TAG, "Unable to setup the receiver!");
              return;
            }
        }

        Firebase usersRef = mFirebaseRef.child("users/"+ mUniqueID + "/receiver");
        usersRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onCancelled(FirebaseError arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Receive Listener! onDataChange received!");
            }

        });
    }
    private void checkWithFirebaseAndRegisterIfNotAlreadyRegistered() {
        Firebase usersRef = mFirebaseRef.child("users");
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mUniqueID = sharedpreferences.getString("uuid", null);
        Log.d(TAG, "checkWithFirebaseAndRegisterIfNotAlreadyRegistered - " + mUniqueID);
        if(mUniqueID != null) {
            // Check with server!
            //Firebase uuidRef = mFirebaseRef.child("users/"+ mUniqueID);
            usersRef.child(mUniqueID).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        if (child.getName().equals("email") ||
                            child.getName().equals("deviceId") ||
                            child.getName().equals("phone") ||
                            child.getName().equals("receiver")) {
                            Log.d(TAG, "Registered with the server!");
                        } else {
                            Log.d(TAG,"Try to register with Firebase");
                            registerWithFirebaseServer();
                        }
                    }

                }

                @Override
                public void onCancelled(FirebaseError snapshot) {
                    // TODO Auto-generated method stub
                }
            });
        } else {
            registerWithFirebaseServer();
        }
    }

    private void registerWithFirebaseServer() {
        Firebase usersRef = mFirebaseRef.child("users");
        //String strHandles = "uuid: " + mUniqueID + ",email: " + mEmailID + ",\"phone\": " + mPhoneNumber + ",\"deviceid\": " + mDeviceId;
        /*HashMap<String, String> map = new HashMap<String, String>();
        //String str = "\"uuid\":" + mUniqueID + ",\"email\":" + mEmailID + ",\"phone\":" + mPhoneNumber;
        map.put("uuid", mUniqueID);
        map.put("email", mEmailID);
        map.put("phone", mPhoneNumber);
        JSONObject jsonObj = null;
        try {
            jsonObj = getJsonObjectFromMap(map);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (jsonObj == null) {
            Log.d(TAG,"Ooops! Registration did not complete!");
            return;
        }*/

        // Generate a reference to a new location and add some data using push()
        Firebase newPostRef = usersRef.push();
        // Add some data to the new location
        Map<String, String> postHM = new HashMap<String, String>();
        if (mEmailID != null)
            postHM.put("email", mEmailID);
        if (mPhoneNumber != null)
            postHM.put("phone", mPhoneNumber);
        if (mDeviceId != null)
            postHM.put("deviceId", mDeviceId);
        // Also add receiver node , though empty at this point!
        postHM.put("receiver", "");
        newPostRef.setValue(postHM);
        // Get the unique ID generated by push()
        mUniqueID = newPostRef.getName();

        newPostRef.setValue(postHM, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Toast.makeText(getApplicationContext(), "Data could not be saved. " + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Data saved successfully on the server!! " + mUniqueID, Toast.LENGTH_SHORT).show();
                    Editor editor = sharedpreferences.edit();
                    editor.putString("FirebaseReg", "Registered");
                    editor.putString("uuid", mUniqueID);
                    editor.commit();
                }
            }
        });
    }

    private void registerWithCAPServer() {
        String registerUrl = "http://50.59.22.184:8075/cap/v1/notification/register";
        HashMap<String, String> map = new HashMap<String, String>();
        //String str = "\"uuid\":" + mUniqueID + ",\"email\":" + mEmailID + ",\"phone\":" + mPhoneNumber;
        map.put("uuid", mUniqueID);
        map.put("email", mEmailID);
        map.put("phone", mPhoneNumber);
        Intent intent = new Intent(this, RestAPIService.class);
        // add infos for the service which file to download and where to store
        intent.putExtra(RestAPIService.RESTAPI_ENDPOINT_URL, registerUrl);
        intent.putExtra(RestAPIService.RESTAPI_ARGS, map);
        intent.putExtra(RestAPIService.API_NAME, RestAPIService.RESTAPI_TYPE_REGISTER);
        intent.putExtra(RestAPIService.RESTAPI_ARGS_TYPE, RestAPIService.ARGS_TYPE_MAP);
        startService(intent);
        Log.d(TAG, "Try registerWithCAPServer!");
    }
}
