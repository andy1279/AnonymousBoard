package com.example.anonymousboard;

import android.app.Activity;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewArticleActivity extends AppCompatActivity {
    Handler handler;
    ArticleAdapter adapter;
    String[][] storage;
    ListView list;
    Button btnWrite, btnSearch;
    EditText txtSearch;

    SwipeRefreshLayout pullToRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewarticle);
        handler = new Handler();
        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);

        list = (ListView)findViewById(R.id.articleList);
        adapter = new ArticleAdapter();
        list.setAdapter(adapter);
        boardLoad(handler, adapter, list);
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
                SearchArticle(handler, adapter, list, txtSearch.getText().toString());
            }
        });

        list.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Article_LvItem item = (Article_LvItem)list.getItemAtPosition(i);
                Intent intent = new Intent(ViewArticleActivity.this, ReadArticleActivity.class);
                intent.putExtra("no", item.getNo());
                Log.e("no", item.getNo());
                intent.putExtra("title", item.getTitle());
                intent.putExtra("content", item.getContent());
                intent.putExtra("writer", item.getWriter());
                intent.putExtra("postdate", item.getDate());
                startActivity(intent);
            }

        });
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //Here you can update your data from internet or from local SQLite data
                boardLoad(handler, adapter, list);
                pullToRefresh.setRefreshing(false);
            }
        });

    }

    public static void SearchArticle(final Handler handler, final ArticleAdapter adapter, final ListView list, final String strSearch) {
        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                try {

                    adapter.clear();
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
                    JSONObject obj = new JSONObject(builder.toString());
                    try {
                        JSONArray arr = obj.getJSONArray("return");
                        for (int i = 0; i < arr.length(); i++) {
                            adapter.addItem(arr.getJSONObject(i).getString("NO"), arr.getJSONObject(i).getString("TITLE"), arr.getJSONObject(i).getString("CONTENT"), arr.getJSONObject(i).getString("WRITER"), arr.getJSONObject(i).getString("POSTDATE"));
                        }
                    } catch(JSONException e) {
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            list.setAdapter(adapter);
                        }
                    });
                } catch(Exception e) {
                    SearchArticle(handler, adapter, list, strSearch);
                    //Log.e("Error", "실행도중 문제가 발생했습니다. 확인 후 수정바랍니다.", e);
                }
            }
        }.start();
    }
    public static void boardLoad(final Handler handler, final ArticleAdapter adapter, final ListView list) {
        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                try {

                    adapter.clear();
                    final URL url = new URL("Http://andy1279.dothome.co.kr/listArticle.php/");
                    final HttpURLConnection http = (HttpURLConnection)url.openConnection();
                    http.setDefaultUseCaches(false);
                    http.setDoInput(true);
                    http.setDoOutput(true);

                    http.setRequestMethod("POST");
                    http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

                    final BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                    StringBuilder builder = new StringBuilder();
                    String str;
                    while((str = reader.readLine())!=null) {
                        builder.append(str);
                    }
                    JSONObject obj = new JSONObject(builder.toString());
                    try {
                        JSONArray arr = obj.getJSONArray("return");
                        for (int i = 0; i < arr.length(); i++) {

                            adapter.addItem(arr.getJSONObject(i).getString("NO"), arr.getJSONObject(i).getString("TITLE"), arr.getJSONObject(i).getString("CONTENT"), arr.getJSONObject(i).getString("WRITER"), arr.getJSONObject(i).getString("POSTDATE"));
                        }
                    } catch(JSONException e) {
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            list.setAdapter(adapter);
                        }
                    });
                } catch(Exception e) {
                    boardLoad(handler, adapter, list);
                    //Log.e("Error", "실행도중 문제가 발생했습니다. 확인 후 수정바랍니다.", e);
                }
            }
        }.start();
    }
}