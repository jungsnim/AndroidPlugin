package jangkiplugin.sc.com.jangkiplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerNativeActivity;

public class MainActivity extends UnityPlayerNativeActivity {

    static final int RC_REQUEST = 10001;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    public static String SENDER_ID = "1008495205304";
    private boolean isReceiverRegistered;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    //private GoogleCloudMessaging gcm;
    //private String regid;
    //IabHelper mHelper;

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)) {
                    // 액션이 COMPLETE일 경우
                    String token = intent.getStringExtra("token");
                    UnityPlayer.UnitySendMessage("AndroidPlugin", "OnAndroidToken", token);
                }

                //if(mRegistrationBroadcastReceiver == null)
                //    RegsterGcmToken();
            }
        };
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        //결제 관련...
        //String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlOk9W0nl5AlnjWFBqPk/yc+Gsod6olVPO77ZC4UBULYmTb+vT5lEJrKgENdK6xWdEcUdfSxQH1hLeX3u/edjhp1K1SNz/wE5S1ote9vBACxMOknndM2kjYvpqsIz0Bq8xhXZFYL8HSLmyUrNCXQXdzkQNuQ17nWgbnfYS33Y8sDrz16peZyE/N55IDWON9I+oFgsdj2OUpS1tJyCMW5ZYlbVZhoV0BkSrytcN5L9TRAjlRrwSG89n+V1NMW9k1K8bp9SMR26INHvMf4o3uVZndjCq40xSqygMfbq8Zu/bAxgq0ZUD7aZ84cbvTR16XcD99CSd8dt0FPxH1bH+LAHqQIDAQAB";

        // Create the helper, passing it our context and the public key to verify signatures with
        // mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
       /* mHelper.enableDebugLogging(true);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.e("StartSetup", "Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //if (mHelper != null) mHelper.dispose();
        //mHelper = null;
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    // Listener that's called when we finish querying the items and subscriptions we own
    /*IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                Log.e("QueryInventoryFinishedListener", "result.isFailure()");
                return;
            }

            Log.d("QueryInventoryFinishedListener", "Query inventory was successful.");

            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
            Purchase gasPurchase = inventory.getPurchase("Donate");
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                mHelper.consumeAsync(gasPurchase, mConsumeFinishedListener);
                return;
            }
        }
    };
*/
    /*public void GiveDonate()
    {
        mHelper.launchPurchaseFlow(this, "Donate", RC_REQUEST,
                mPurchaseFinishedListener, "");
    }*/

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d("onActivityResult", "onActivityResult handled by IABUtil.");
        }
    }*/

    /** Verifies the developer payload of a purchase. */
    /*boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        *//*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         *//*

        return true;
    }*/

    // Callback for when a purchase is finished
    /*IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d("OnIabPurchaseFinishedListener", "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                Log.e("OnIabPurchaseFinishedListener", "Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                Log.e("OnIabPurchaseFinishedListener", "Error purchasing. Authenticity verification failed.");
                return;
            }

            Log.d("OnIabPurchaseFinishedListener", "Purchase successful.");

            if (purchase.getSku().equals("Donate"))
            {
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
        }
    };*/

    // Called when consumption is complete
    /*IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d("OnConsumeFinishedListener", "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            Boolean success = new Boolean( result.isSuccess());
            if (success) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d("OnConsumeFinishedListener", "Consumption successful. Provisioning.");
            }
            else {
                Log.e("OnConsumeFinishedListener","Error while consuming: " + result);
            }
            UnityPlayer.UnitySendMessage("AndroidPlugin","OnAndroidDonate",success.toString());
        }
    };*/
}
