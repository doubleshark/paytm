package com.example.paytm;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    String m_id = "hDjofc47534841725025";
    String customer_id = "123";
    String order_id = UUID.randomUUID().toString().substring(0,28);
    String url = "https://double-shark.000webhostapp.com/Paytm_php/generateChecksum.php";
    String callbackurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }


    }

    public void paytm_onclick(View view) {

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("CHECKSUMHASH"))
                    {
                        String CHECKSUMHASH = jsonObject.getString("CHECKSUMHASH");
                        PaytmPGService paytmPGService = PaytmPGService.getStagingService();

                        HashMap<String, String> paramMap = new HashMap<String,String>();
                        paramMap.put( "MID" , m_id);
                        paramMap.put( "ORDER_ID" , order_id);
                        paramMap.put( "CUST_ID" , customer_id);
                        paramMap.put( "CHANNEL_ID" , "WAP");
                        paramMap.put( "TXN_AMOUNT" , "100.12");
                        paramMap.put( "WEBSITE" , "WEBSTAGING");
                        paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
                        paramMap.put( "CALLBACK_URL", callbackurl);
                        paramMap.put( "CHECKSUMHASH", CHECKSUMHASH);

                        PaytmOrder order = new PaytmOrder(paramMap);

                        paytmPGService.initialize(order,null);
                        paytmPGService.startPaymentTransaction(MainActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                            @Override
                            public void onTransactionResponse(Bundle inResponse) {

                                Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void networkNotAvailable() {

                                Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();


                            }

                            @Override
                            public void clientAuthenticationFailed(String inErrorMessage) {

                                Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage, Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void someUIErrorOccurred(String inErrorMessage) {

                                Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage , Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {

                                Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage, Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onBackPressedCancelTransaction() {

                                Toast.makeText(getApplicationContext(), "Transaction cancelled" , Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {

                                Toast.makeText(getApplicationContext(), "Transaction Cancelled" + inResponse.toString(), Toast.LENGTH_LONG).show();


                            }
                        });


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> paramMap = new HashMap<String,String>();
                paramMap.put( "MID" , m_id);
                paramMap.put( "ORDER_ID" , order_id);
                paramMap.put( "CUST_ID" , customer_id);
                paramMap.put( "CHANNEL_ID" , "WAP");
                paramMap.put( "TXN_AMOUNT" , "100.12");
                paramMap.put( "WEBSITE" , "WEBSTAGING");
                paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
                paramMap.put( "CALLBACK_URL", callbackurl);

                return paramMap;
            }
        };

        requestQueue.add(stringRequest);


    }
}
