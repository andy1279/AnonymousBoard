package com.example.anonymousboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReadArticleActivity extends AppCompatActivity {
    Handler handler;
    TextView tvContent, tvTitle;
    FloatingActionButton btnDelete;

    SwipeRefreshLayout pullToRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readarticle);
        handler = new Handler();
        btnDelete = (FloatingActionButton)findViewById(R.id.btnDelete_ReadArticle);
        tvTitle = (TextView)findViewById(R.id.tvArticle_ReadArticle);
        tvContent = (TextView)findViewById(R.id.tvContent_ReadArticle);
        Intent si = getIntent();
        final String no = si.getStringExtra("no");
        String title = si.getStringExtra("title");
        String content = si.getStringExtra("content");
        final String writer = si.getStringExtra("writer");
        String postdate = si.getStringExtra("postdate");
        tvTitle.setText(title);
        tvContent.setText(content);

        btnDelete.setOnClickListener(
                new View.OnClickListener()
                {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    if (MainActivity.mainUserId.contentEquals(writer))
                                    {
                                        deleteArticle(no);
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ReadArticleActivity.this);
                                        builder.setMessage("권한이 없습니다.").show();
                                    }
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ReadArticleActivity.this);
                        builder.setMessage("정말로 해당 게시글을 삭제하시겠습니까?").setPositiveButton("삭제", dialogClickListener)
                                .setNegativeButton("돌아가기", dialogClickListener).show();
                    }
                }
        );
    }
    public void deleteArticle(final String articleNo) {
        new Thread() {
            @Override
            public void run() {
                HttpURLConnection http = null;
                InputStream inputStream;
                try {
                    URL url = new URL("http://andy1279.dothome.co.kr/deleteArticle.php");
                    http = (HttpURLConnection)url.openConnection();
                    http.setUseCaches(false);
                    http.setDoInput(true);
                    http.setDoOutput(true);
                    http.setRequestMethod("POST");
                    http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    http.connect();

                    JSONObject buffer = new JSONObject();
                    buffer.put("articleNo", articleNo);
                    OutputStreamWriter osw = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
                    osw.write(buffer.toString());
                    osw.flush();
                    osw.close();
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(ReadArticleActivity.this, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            catch(Exception e)
                            {
                                Log.e("Error", "실행도중 문제가 발생했습니다. 확인 후 수정바랍니다.", e);
                            }
                        }
                    });
                } catch (Exception e) {
                    deleteArticle(articleNo);
                }
            }
        }.start();
    }
}