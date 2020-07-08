package com.example.anonymousboard;

import android.content.Intent;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewArticleActivity extends AppCompatActivity {
    public static Handler handler;
    Button btnWrite, btnSearch;
    ListView list;
    EditText txtSearch;
    public static ArticleAdapter adapter;
    public static String[][] storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewarticle);
        handler = new Handler();

        list = (ListView)findViewById(R.id.articleList);
        adapter = new ArticleAdapter();
        list.setAdapter(adapter);
        boardLoad();
        adapter.notifyDataSetChanged();

        btnWrite = (Button)findViewById(R.id.btnWrite);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        txtSearch = (EditText)findViewById(R.id.txtSearch);
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WriteArticleActivity.class);
                startActivity(intent);
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SearchArticle(txtSearch.getText().toString());
            }
        });


    }
    public static void SearchArticle(final String strSearch) {
        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                try {

                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    final URL url = new URL("Http://andy1279.dothome.co.kr/listArticle.php/");
                    final HttpURLConnection http = (HttpURLConnection)url.openConnection();
                    http.setDefaultUseCaches(false);
                    http.setDoInput(true);
                    http.setDoOutput(true);

                    http.setRequestMethod("POST");
                    http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("doSearch=1&strData=").append(strSearch);
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

                    final int firstArraySize = resultData.split("¿").length;
                    Log.e("Error", "FirstArraySize = " + firstArraySize);
                    final int secondArraySize = 3; // The number of table property
                    storage = new String[firstArraySize][secondArraySize];
                    String[] oneArray = resultData.split("¿");
                    for(int i = 0; i < firstArraySize; i++) {
                        String[] tmps = oneArray[i].split("/");
                        for(int j = 0; j < secondArraySize; j++) {
                            storage[i][j] = tmps[j];
                        }
                    }

                    for(int i = 0; i < storage.length; i++) {
                        Log.e("Error", "AddItem: Param1 : " + storage[i][0] + ", Param2 : " + storage[i][1] + ", Param3 : " + storage[i][2]);
                        adapter.addItem(storage[i][0], storage[i][1], storage[i][2]);

                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch(Exception e) {
                    adapter.notifyDataSetChanged();
                    SearchArticle(strSearch);
                    adapter.notifyDataSetChanged();
                    //Log.e("Error", "실행도중 문제가 발생했습니다. 확인 후 수정바랍니다.", e);
                }
            }
        }.start();
    }
    public static void boardLoad() {
        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                try {

                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    final URL url = new URL("Http://andy1279.dothome.co.kr/listArticle.php/");
                    final HttpURLConnection http = (HttpURLConnection)url.openConnection();
                    http.setDefaultUseCaches(false);
                    http.setDoInput(true);
                    http.setDoOutput(true);

                    http.setRequestMethod("POST");
                    http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                    //StringBuffer buffer = new StringBuffer();
                    //buffer.append("name").append("=").append("");
                    //OutputStreamWriter osw = new OutputStreamWriter(http.getOutputStream(),"UTF-8");
                   // osw.write(buffer.toString());
                    //osw.flush();

                    final BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                    StringBuilder builder = new StringBuilder();
                    String str;
                    while((str = reader.readLine())!=null) {
                        builder.append(str);
                    }
                    final String resultData = builder.toString();
                    Log.e("Error", resultData);

                    final int firstArraySize = resultData.split("¿").length;
                    Log.e("Error", "FirstArraySize = " + firstArraySize);
                    final int secondArraySize = 3; // The number of table property
                    storage = new String[firstArraySize][secondArraySize];
                    String[] oneArray = resultData.split("¿");
                    for(int i = 0; i < firstArraySize; i++) {
                        String[] tmps = oneArray[i].split("/");
                        for(int j = 0; j < secondArraySize; j++) {
                            storage[i][j] = tmps[j];
                        }
                    }

                    for(int i = 0; i < storage.length; i++) {
                        Log.e("Error", "AddItem: Param1 : " + storage[i][0] + ", Param2 : " + storage[i][1] + ", Param3 : " + storage[i][2]);
                        adapter.addItem(storage[i][0], storage[i][1], storage[i][2]);

                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch(Exception e) {
                    adapter.notifyDataSetChanged();
                    boardLoad();
                    //Log.e("Error", "실행도중 문제가 발생했습니다. 확인 후 수정바랍니다.", e);
                }
            }
        }.start();
    }
}