package local.ubms.com;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.HashMap;
import java.util.Map;



public class Login extends AppCompatActivity {
    EditText loginAccount;
    Button loginButton;
    static KProgressHUD hud;
    ImageView loginCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginAccount = findViewById(R.id.loginAccount);
        loginCover = findViewById(R.id.loginCover);


        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hud != null){
                    hud.dismiss();
                }

                hud = KProgressHUD.create(Login.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Please wait")
                        .setCancellable(false)
                        .setMaxProgress(100)
                        .show();

                login(loginAccount.getText().toString());

            }
        });

        Glide.with(this).load(R.drawable.background).centerCrop().into(loginCover);


    }




    private void login(final String accountNumber){

        String uRl = "https://utilitybilling.000webhostapp.com/android/login.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("Login Success")){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("acc_number",accountNumber);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    hud.dismiss();
                }
                else {
                    Toast.makeText(Login.this, response, Toast.LENGTH_SHORT).show();
                    hud.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, error.toString(), Toast.LENGTH_SHORT).show();
                hud.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<>();
                param.put("acc_number",accountNumber);
                return param;

            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(Login.this).addToRequestQueue(request);
    }


}

