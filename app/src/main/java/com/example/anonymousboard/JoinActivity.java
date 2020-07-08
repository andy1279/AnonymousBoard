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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class JoinActivity extends AppCompatActivity {
    Handler handler;
    EditText etId, etPw;
    Button btnJoin, btnDuplicateCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        handler = new Handler();

        etId = (EditText)findViewById(R.id.txtId_Join);
        etPw = (EditText)findViewById(R.id.txtPw_Join);

        btnJoin = (Button)findViewById(R.id.btnJoin);
        btnJoin.setEnabled(false);
        btnJoin.setOnClickListener(new View.OnClickListener() {
                @Override
            public void onClick(View view) {
                doJoin(etId.getText().toString(),etPw.getText().toString());
            }
        });
        btnDuplicateCheck = (Button)findViewById(R.id.btnDuplicateCheck);
        btnDuplicateCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDuplicate(etId.getText().toString());
            }
        });
    }
    public void checkDuplicate(final String Id) {
        new Thread() {
            @Override
            public void run() {
                HttpURLConnection http = null;
                InputStream inputStream;
                try {
                    URL url = new URL("http://andy1279.dothome.co.kr/duplicateCheck.php");
                    http = (HttpURLConnection)url.openConnection();
                    http.setUseCaches(false);
                    http.setDoInput(true);
                    http.setDoOutput(true);
                    http.setRequestMethod("POST");
                    http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    http.connect();

                    StringBuffer buffer = new StringBuffer();
                    buffer.append("id=").append(Id);
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
                                if (resultData.contains("DuplicateID")) {
                                    Toast.makeText(getApplicationContext(), "존재하는 계정입니다.", Toast.LENGTH_SHORT).show();
                                    etId.setText("");
                                } else {
                                    Toast.makeText(getApplicationContext(), "사용가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                                    etId.setEnabled(false);
                                    btnDuplicateCheck.setEnabled(false);
                                    btnJoin.setEnabled(true);
                                }
                            }
                            catch(Exception e)
                            {
                                Log.e("Error", "실행도중 문제가 발생했습니다. 확인 후 수정바랍니다.", e);
                            }
                        }
                    });
                } catch (Exception e) {
                    checkDuplicate(Id);
                    //Log.e("Error", "실행도중 문제가 발생했습니다. 확인 후 수정바랍니다.", e);
                }
            }
        }.start();
    }
    public void doJoin(final String Id, final String Pw) {
        new Thread() {
            @Override
            public void run() {
                HttpURLConnection http = null;
                InputStream inputStream;
                try {
                    URL url = new URL("http://andy1279.dothome.co.kr/join.php");
                    http = (HttpURLConnection)url.openConnection();
                    http.setUseCaches(false);
                    http.setDoInput(true);
                    http.setDoOutput(true);
                    http.setRequestMethod("POST");
                    http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    http.connect();

                    StringBuffer buffer = new StringBuffer();
                    buffer.append("id=").append(Id).append("&pw=").append(Pw);
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
                                if (resultData.contains("Success")) {
                                    Toast.makeText(getApplicationContext(), "가입이 성공적으로 완료되었습니다. 초기화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "알 수 없는 오류로 회원가입에 실패하였습니다. 초기화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                                }
                                finish();
                            }
                            catch(Exception e)
                            {
                                Log.e("Error", "실행도중 문제가 발생했습니다. 확인 후 수정바랍니다.", e);
                            }
                        }
                    });
                } catch (Exception e) {
                    doJoin(Id, Pw);
                    //Log.e("Error", "실행도중 문제가 발생했습니다. 확인 후 수정바랍니다.", e);
                }
            }
        }.start();
    }
}