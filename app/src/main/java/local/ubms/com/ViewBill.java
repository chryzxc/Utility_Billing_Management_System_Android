package local.ubms.com;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.cancel.OnCancel;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PayPalButton;


import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;


public class ViewBill extends AppCompatActivity {

    private static String CLIENT_ID="AfKhS0HyGkgicx4aDmw9ZzBdsHKU9uu0zSqOb6iKw9_MhaTzBtTlZ46ch4m08m1SVJNM9lgo9k56KN4C";
    Bundle extras;
    static KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bill);
        ImageView viewCover = findViewById(R.id.viewCover);
        Glide.with(this).load(R.drawable.background).centerCrop().into(viewCover);


        extras = getIntent().getExtras();
        ImageView viewBillBack = findViewById(R.id.viewBillBack);
        viewBillBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewBill.super.onBackPressed();

            }
        });



        proceedToPayment();
        displayData();

    }

    public void displayData(){
        TextView viewBillNumber = findViewById(R.id.viewBillNumber);
        TextView viewDue = findViewById(R.id.viewDue);
        TextView viewMeter = findViewById(R.id.viewMeter);
        TextView viewFrom = findViewById(R.id.viewFrom);
        TextView viewTo = findViewById(R.id.viewTo);
        TextView viewAmount = findViewById(R.id.viewAmount);
        TextView viewDate = findViewById(R.id.viewDate);


        viewAmount.setText("Amount to pay: ₱"+extras.getString("bill_amount").toString());
        viewDate.setText(extras.getString("bill_date").toString());
        viewBillNumber.setText("Bill #"+extras.getString("bill_no").toString());
        viewDate.setText(extras.getString("date_created").toString());
        viewDue.setText("Due: "+extras.getString("due_date").toString());
        viewMeter.setText(extras.getString("meter_no").toString());
        viewFrom.setText(extras.getString("period_from").toString());
        viewTo.setText(extras.getString("period_to").toString());





    }

    public void proceedToPayment(){



        PayPalButton payPalButton = findViewById(R.id.payPalButton);


        CheckoutConfig config = new CheckoutConfig(
                getApplication(),CLIENT_ID, Environment.SANDBOX,String.format("%s://paypalpay",BuildConfig.APPLICATION_ID),
                CurrencyCode.PHP,
                UserAction.PAY_NOW,
                new SettingsConfig(
                        true,false
                )

        );
        PayPalCheckout.setConfig(config);



        payPalButton.setup(
                new CreateOrder() {

                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        if (hud != null){
                            hud.dismiss();
                        }
                        hud = KProgressHUD.create(ViewBill.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel("Preparing for payment")
                                .setDimAmount(50f)
                                .setCancellable(false)
                                .setMaxProgress(100)
                                .show();
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.PHP)
                                                        .value(extras.getString("bill_amount"))
                                                        .build()
                                        )
                                        .build()
                        );
                        Order order = new Order(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build(),
                                purchaseUnits
                        );
                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                    }
                },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        approval.getOrderActions().capture(new OnCaptureComplete() {
                            @Override
                            public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                hud.setLabel("Finishing payment");
                                Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));
                                updateDatabase(result.toString());
                                //Toast.makeText(ViewBill.this, result.toString(), Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                }, new OnCancel() {
                    @Override
                    public void onCancel() {
                        Toast.makeText(ViewBill.this, "Payment canceled", Toast.LENGTH_SHORT).show();
                    }
                }

        );

    }

    public void updateDatabase(String orderID){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://utilitybilling.000webhostapp.com/android/payment.php";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("Success")){
                            MainActivity mainActivity = new MainActivity().loadBills(MainActivity.customerID);
                            hud.dismiss();
                            ViewBill.super.onBackPressed();

                        }
                        else {
                            updateDatabase(orderID);
                            Toast.makeText(ViewBill.this, "Retrying ["+response+"]", Toast.LENGTH_SHORT).show();
                        }




                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewBill.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<>();
                Bundle extras = getIntent().getExtras();
                param.put("amount_tendered",extras.getString("bill_amount"));
                param.put("bill_amount",extras.getString("bill_amount"));
                param.put("balance",extras.getString("balance"));
                param.put("bill_date",extras.getString("bill_date"));
                param.put("bill_no",extras.getString("bill_no"));
                param.put("customer_id",extras.getString("customer_id"));
                param.put("payment_method","Paypal");
                param.put("due_date",extras.getString("due_date"));
                param.put("encoded_by","via Paypal");
                param.put("id",extras.getString("id"));
                param.put("meter_no",extras.getString("meter_no"));
                param.put("period_from",extras.getString("period_from"));
                param.put("period_to",extras.getString("period_to"));
                param.put("status",extras.getString("status"));
                return param;


            }
        };
        queue.add(stringRequest);
    }
}