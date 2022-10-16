package com.zaar2.ProductFromCheque;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zaar2.ProductFromCheque.listView.ListView_utilities;
import com.zaar2.ProductFromCheque.listView.MyAdapter;
import com.zaar2.ProductFromCheque.listView.myItemForListView;

import java.util.ArrayList;

public class ActivityReportOfDB extends AppCompatActivity {
    private String typeListView;

    private ListView listView;
    private ArrayList<myItemForListView> itemsForLists = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_of_db);

        initVariable();
        initView();
        initListView(itemsForLists);

    }

    private void initVariable() {
        typeListView = getIntent().getExtras().getString(getResources().getString(R.string._type_listView));
        itemsForLists = ListView_utilities.initItemsList(typeListView,null,this);
    }

    private void initView() {
        listView = findViewById(R.id.listView_information);
    }

    private void initListView(ArrayList<myItemForListView> itemsForLists) {
        MyAdapter myAdapter = null;
        if (itemsForLists != null) {
            myAdapter = new MyAdapter(itemsForLists, this);
        }
        listView.setAdapter(myAdapter);
    }
}