package com.zaar2.ProductFromCheque;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityMain extends Activity {

    private ListView listView_mainMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListView();

        onItemClick();
        onClick_btnInformationOfDatabase();
    }

    void initView() {
        listView_mainMenu = findViewById(R.id.listView_mainMenu);
    }

    void initListView() {
        String[] menuOptions_str = getResources().getStringArray(R.array.mainMenu_strArr);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_list_view_main_options,
                R.id.tv_mainListView,
                menuOptions_str
        );
        listView_mainMenu.setAdapter(adapter);
    }

    void onItemClick() {
        listView_mainMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String nameItem = ((TextView) ((LinearLayout) view).findViewById(R.id.tv_mainListView)).getText().toString();
                if (nameItem.equals(getResources().getString(R.string.menuOption_parser))) {
                    viewActivity(ActivityParser.class, getApplicationContext().getResources().getString(R.string.parserReport_listview));
                }
                if (nameItem.equals(getResources().getString(R.string.menuOption_product))) {
                    viewActivity(ActivityView.class, getResources().getString(R.string.itemsType_listView));
                }
                if (nameItem.equals(getResources().getString(R.string.menuOption_retailPlace))) {
                    viewActivity(ActivityView.class, getResources().getString(R.string.sellersType_listView));
                }
                if (nameItem.equals(getResources().getString(R.string.menuOption_deleteDB))) {
                }
            }
        });
    }

    private void onClick_btnInformationOfDatabase() {
        findViewById(R.id.btn_informationOfDatabase).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewActivity(ActivityReportOfDB.class, getResources().getString(R.string.information_listView));
                    }
                }
        );
    }

    private void viewActivity(Class<?> cls, String valueExtra) {
        Intent intent = new Intent(
                getApplicationContext(),
                cls
        );
        if (valueExtra != null)
            intent.putExtra(getResources().getString(R.string._type_listView), valueExtra);
        startNextActivity(intent);
    }

    private void startNextActivity(Intent intent) {
        if (intent != null) {
            startActivity(intent);
        }
    }
}