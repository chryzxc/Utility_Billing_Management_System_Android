package local.ubms.com;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {
    TextView customerName,customerAccountNumber,customerContactNumber,customerAddress;
    TextView totalBills;
    static String customerID;

    List<BillList> bill_myLists;
    RecyclerView bill_rv;
    BillAdapter bill_adapter;

    List<PaidBillList> paid_bill_myLists;
    RecyclerView paid_bill_rv;
    PaidBillAdapter paid_bill_adapter;

    ChipNavigationBar chipNavigationBar;

    TextView refreshedDate;
    RequestQueue billsQueue,paidQueue;
    SwipeRefreshLayout billsRefreshLayout,paidRefreshLayout;

    LinearLayout bills_empty_state,paid_empty_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customerName = findViewById(R.id.customerName);
        customerAccountNumber = findViewById(R.id.customerAccountNumber);
        customerContactNumber = findViewById(R.id.customerContactNumber);
        customerAddress = findViewById(R.id.customerAddress);
        totalBills = findViewById(R.id.totalBills);

        bills_empty_state = findViewById(R.id.bills_empty_state);
        paid_empty_state = findViewById(R.id.paid_empty_state);


        chipNavigationBar = findViewById(R.id.menu);
        chipNavigationBar.setItemSelected(R.id.bills,true);
        TextView typeText = findViewById(R.id.typeText);
        ImageView homeCover = findViewById(R.id.homeCover);
        Glide.with(this).load(R.drawable.background).centerCrop().into(homeCover);



        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {

                switch (i) {

                    case R.id.bills:
                        typeText.setText("Bills");

                        billsRefreshLayout.setVisibility(VISIBLE);
                        paidRefreshLayout.setVisibility(View.GONE);
                        loadBills(customerID);


                        break;
                    case R.id.paid:

                        typeText.setText("Payment history");
                        paidRefreshLayout.setVisibility(VISIBLE);
                        billsRefreshLayout.setVisibility(View.GONE);
                        totalBills.setVisibility(View.GONE);
                        loadPaidBills(customerID);

                        break;
                    default:
                        break;
                }
            }
        });


        ImageView logoutImage = (ImageView) findViewById(R.id.logoutImage);
        logoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        bill_rv = (RecyclerView) findViewById(R.id.bill_rv);
        bill_rv.setHasFixedSize(true);
        bill_rv.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        bill_myLists = new ArrayList<>();

        paid_bill_rv = (RecyclerView) findViewById(R.id.paid_bill_rv);
        paid_bill_rv.setHasFixedSize(true);
        paid_bill_rv.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        paid_bill_myLists = new ArrayList<>();

        refreshedDate = findViewById(R.id.refreshedDate);
        refreshedDate.setText("(As of "+DateFormat.format("MMM dd yyyy hh:mm aa",new Date())+")");



        billsRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.billsRefreshLayout);
        paidRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.paidRefreshLayout);

        loadCustomer();

        billsRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        bill_myLists.clear();
                        billsQueue.stop();
                        loadBills(customerID);


                    }
                });

        paidRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        paid_bill_myLists.clear();
                        paidQueue.stop();
                        loadPaidBills(customerID);
                    }
                });




    }

    public void loadCustomer() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://utilitybilling.000webhostapp.com/android/customer_data.php";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray array = new JSONArray(response);
                            JSONObject obj;
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                customerID = obj.getString("id");
                                customerName.setText(obj.getString("fname") + " " + obj.getString("lname"));
                                customerAccountNumber.setText("Account number: " + obj.getString("acc_number"));
                                customerContactNumber.setText("Contact number: "+obj.getString("contact"));
                                customerAddress.setText("Address: "+obj.getString("addr"));

                            }

                            loadBills(customerID);

                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }




                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<>();
                Bundle extras = getIntent().getExtras();
                param.put("acc_number",extras.getString("acc_number"));
                return param;

            }
        };
        queue.add(stringRequest);

    }

    public MainActivity loadBills(String customerID){

        totalBills.setVisibility(VISIBLE);
        billsQueue = Volley.newRequestQueue(this);
        String url ="https://utilitybilling.000webhostapp.com/android/customer_bills.php";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        bill_myLists.clear();
                        refreshedDate.setText("(As of "+DateFormat.format("MMM dd yyyy hh:mm aa",new Date())+")");


                        try {
                            JSONArray array = new JSONArray(response);
                            JSONObject obj;
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                if (obj.getString("status").matches("Unpaid")){
                                    bill_myLists.add(new BillList( obj.getString("id"),
                                            obj.getString("customer_id"),
                                            obj.getString("bill_no"),
                                            obj.getString("meter_no"),
                                            obj.getString("period_from"),
                                            obj.getString("period_to"),
                                            obj.getString("bill_amount"),
                                            obj.getString("bill_date"),
                                            obj.getString("due_date"),
                                            obj.getString("encoded_by"),
                                            obj.getString("status"),
                                            obj.getString("amount_tendered"),
                                            obj.getString("balance"),
                                            obj.getString("date_created")));
                                }
                                
                            }

                            bill_adapter = new BillAdapter(bill_myLists, MainActivity.this);
                            bill_rv.setAdapter(bill_adapter);
                            bill_adapter.notifyDataSetChanged();

                            if (bill_myLists.size() != 0){

                                totalBills.setText("Total bills: "+String.valueOf(bill_myLists.size()));
                                bills_empty_state.setVisibility(View.GONE);
                            }else{
                               totalBills.setVisibility(View.GONE);
                               bills_empty_state.setVisibility(VISIBLE);
                            }

                            billsRefreshLayout.setRefreshing(false);


                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                billsRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<>();
                param.put("customer_id",customerID);
                return param;

            }
        };
        if (billsQueue != null){
            billsQueue.cancelAll(stringRequest);
        }

        billsQueue.add(stringRequest);

        return null;
    }

    public MainActivity loadPaidBills(String customerID){

        paidQueue = Volley.newRequestQueue(this);
        String url ="https://utilitybilling.000webhostapp.com/android/customer_paid_bills.php";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        paid_bill_myLists.clear();
                        refreshedDate.setText("(As of "+DateFormat.format("MMM dd yyyy hh:mm aa",new Date())+")");


                        try {
                            JSONArray array = new JSONArray(response);
                            JSONObject obj;
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                    paid_bill_myLists.add(new PaidBillList(obj.getString("id"),
                                            obj.getString("bill_id"),
                                            obj.getString("customer_id"),
                                            obj.getString("date_paid"),
                                            obj.getString("amount_tendered"),
                                            obj.getString("payment_method")));

                            }

                            paid_bill_adapter = new PaidBillAdapter(paid_bill_myLists, MainActivity.this);
                            paid_bill_rv.setAdapter(paid_bill_adapter);
                            paid_bill_adapter.notifyDataSetChanged();




                            if (paid_bill_myLists.size() != 0){
                                paid_empty_state.setVisibility(View.GONE);
                            }else{

                                paid_empty_state.setVisibility(VISIBLE);
                            }


                            paidRefreshLayout.setRefreshing(false);


                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                paidRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<>();
                param.put("customer_id",customerID);
                return param;

            }
        };

        if (paidQueue!= null){
            paidQueue.cancelAll(stringRequest);
        }

        paidQueue.add(stringRequest);

        return null;
    }

}