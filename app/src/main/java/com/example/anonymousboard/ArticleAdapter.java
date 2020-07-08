package com.example.anonymousboard;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;

public class ArticleAdapter extends BaseAdapter {
    ArrayList<Article_LvItem> itemlist = new ArrayList<>();
    @Override
    public int getCount() {
        return itemlist.size();
    }

    @Override
    public Object getItem(int i) {
        return itemlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_viewarticle_listview, viewGroup, false);
        }

        TextView title = (TextView)view.findViewById(R.id.tvTitle);
        TextView content = (TextView)view.findViewById(R.id.tvContent);
        TextView postDate = (TextView)view.findViewById(R.id.tvDate);

        Article_LvItem article = itemlist.get(i);

        title.setText(article.getTitle());
        content.setText(article.getContent());
        postDate.setText(article.getDate());
        return view;
    }
    public void clear()
    {
        itemlist.clear();
    }
    public void addItem(String title, String content, String date) {
        Article_LvItem item = new Article_LvItem();
        item.setTitle(title);
        item.setContent(content);
        item.setDate(date);

        itemlist.add(item);
    }
}