package com.example.kaan.radarcardisg;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.StrictMode;
import android.widget.ProgressBar;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static String GKN_URL = "http://gknsatinalma.midsoft.com.tr:81/MIDSOFT_JSON_WCF/JSON.svc/check_user/"; //caglayan.celik/123456"; gokhan.piskin/123456
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {setSdkLevel();} catch (IOException e) {e.printStackTrace();} catch (JSONException e) {e.printStackTrace();}
        setActionBar();
        setViews();
    }
    private void setSdkLevel() throws IOException,JSONException {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(MainActivity.this);
        }
        return super.dispatchTouchEvent(ev);
    }
    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
    private void setViews(){
        ImageButton btn = (ImageButton) findViewById(R.id.imageButtonSign);
        final EditText txtUserName = (EditText) findViewById(R.id.txtUserName);
        final EditText txtPassword = (EditText) findViewById(R.id.txtPassword);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.contentLoadingProgressBar2);
        txtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                Toast toast;
                int result;
                try{
                    result = RequestHandler.getRequestHandler().userJSonGetRequest(GKN_URL+txtUserName.getText().toString()+"/"+txtPassword.getText().toString());
                }catch (IllegalArgumentException e){
                    result = 0;
                }

                if(result >0){
                    Intent myIntent = new Intent(v.getContext(),TaskProviderActivity.class);
                    //myIntent.putExtra("userId",Integer.toString(result));
                    startActivityForResult(myIntent,0);

                }
                else {
                    progressBar.setVisibility(View.GONE);
                     toast= Toast.makeText(MainActivity.this,
                            "Kullanıcı Adı veya Parola Hatalı!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
            }
        });
    }
    private void setActionBar(){
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.trans)));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
    }
}