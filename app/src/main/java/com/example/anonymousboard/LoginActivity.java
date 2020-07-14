package com.example.anonymousboard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    Handler handler;
    EditText etId, etPw;
    TextView tvRegister;
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        handler = new Handler();

        etId = (EditText)findViewById(R.id.txtId);
        etPw = (EditText)findViewById(R.id.txtPw);

        tvRegister = (TextView)findViewById(R.id.lblJoin);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
            public void onClick(View view) {
                doLogin(etId.getText().toString(),etPw.getText().toString());
            }
        });
    }
    public void doLogin(final String Id, final String Pw) {
        new Thread() {
            @Override
            public void run() {
                HttpURLConnection http = null;
                InputStream inputStream;
                try {
                    URL url = new URL("http://andy1279.dothome.co.kr/login.php");
                    http = (HttpURLConnection)url.openConnection();
                    http.setUseCaches(false);
                    http.setDoInput(true);
                    http.setDoOutput(true);
                    http.setRequestMethod("POST");
                    http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    http.connect();

                    JSONObject buffer = new JSONObject();
                    buffer.put("id", Id);
                    buffer.put("pw", Pw);
                    OutputStreamWriter osw = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
                    osw.write(buffer.toString());
                    osw.flush();
                    osw.close();

                    inputStream = http.getInputStream();

                    final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    String line;

                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }

                    final String resultData = builder.toString();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (resultData.contains("LoginSuccess")) {
                                    MainActivity.mainUserId = Id;
                                    Intent intent = new Intent(getApplicationContext(), ViewArticleActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 확인해 주십시오.", Toast.LENGTH_SHORT).show();
                                    etPw.setText("");
                                }
                            }
                            catch(Exception e)
                            {
                                Log.e("Error", "실행도중 문제가 발생했습니다. 확인 후 수정바랍니다.", e);
                            }
                        }
                    });
                } catch (Exception e) {
                    doLogin(Id,Pw);
                    //Log.e("Error", "실행도중 문제가 발생했습니다. 확인 후 수정바랍니다.", e);
                }
            }
        }.start();
    }
}