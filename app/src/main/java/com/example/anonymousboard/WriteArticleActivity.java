package com.example.anonymousboard;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class WriteArticleActivity extends AppCompatActivity {
    Handler handler;
    Button btnWriteUpload;
    EditText txtContent, txtTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writearticle);
        handler = new Handler();

        btnWriteUpload = (Button)findViewById(R.id.btnWriteUpload);
        txtContent = (EditText)findViewById(R.id.txtContent);
        txtTitle = (EditText)findViewById(R.id.txtTitle_WriteArticle);

        btnWriteUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadArticle(MainActivity.mainUserId, txtTitle.getText().toString(), txtContent.getText().toString());
            }
        }
        );


    }
    public void UploadArticle(final String id, final String title, final String content) {
        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                try {
                    final URL url = new URL("Http://andy1279.dothome.co.kr/writeArticle.php");
                    final HttpURLConnection http = (HttpURLConnection)url.openConnection();
                    http.setDefaultUseCaches(false);
                    http.setDoInput(true);
                    http.setDoOutput(true);
                    http.setRequestMethod("POST");
                    http.setRequestProperty("content-type", "application/x-www-form-urlencoded");


                    JSONObject buffer = new JSONObject();
                    buffer.put("id", id);
                    buffer.put("title", title);
                    buffer.put("content", content);


                    OutputStreamWriter osw = new OutputStreamWriter(http.getOutputStream(),"UTF-8");
                    osw.write(buffer.toString());
                    osw.flush();

                    final BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                    StringBuilder builder = new StringBuilder();
                    String str;
                    while((str = reader.readLine())!=null) {
                        builder.append(str);
                    }
                    final String resultData = builder.toString();
                    Log.e("Error", resultData);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WriteArticleActivity.this, "작성 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            //ViewArticleActivity.boardLoad();
                            finish();
                        }
                    });
                } catch(Exception e) {
                    Log.e("Error", "실행도중 문제가 발생했습니다. 확인 후 수정바랍니다.", e);
                }
            }
        }.start();
    }
}