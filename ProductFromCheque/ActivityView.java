package com.zaar2.ProductFromCheque;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.zaar2.ProductFromCheque.listView.ListView_utilities;
import com.zaar2.ProductFromCheque.listView.myItemForListView;
import com.zaar2.ProductFromCheque.listView.MyAdapter;
import com.zaar2.ProductFromCheque.View.ViewUtility;

import java.util.ArrayList;

public class ActivityView extends AppCompatActivity {

    private static final String KEY_PRODUCT_LIST_IS_VIEW = "PRODUCT_LIST_IS_VIEW";

    private ListView listView;
    private ArrayList<myItemForListView> itemsForLists = new ArrayList<>();
    private ArrayList<myItemForListView> itemsForLists_updater = new ArrayList<>();
    private String typeListView;
    private TableLayout tableLayout_purchaseByProduct;
    private EditText editText_valueForSearch_left;
    private EditText editText_valueForSearch_right;
    private EditText editText_valueForSearch_activityView;
    private Button btn_ESC;

    private String
            editText_left,
            editText_right;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        initVariable();
        initView();
        initListView(itemsForLists);
        if (savedInstanceState != null) {
            settingViews_default(savedInstanceState.getBoolean(KEY_PRODUCT_LIST_IS_VIEW));
        } else settingViews_default(false);

        onItemClick();
        onClick_btn_ESC();

        if (typeListView.equals(getResources().getString(R.string.itemsType_listView))) {
            editTextSingle_addTextChangedListener();
        }
        if (typeListView.equals(getResources().getString(R.string.sellersType_listView))) {
            editTextLeft_addTextChangedListener();
            editTextRight_addTextChangedListener();
        }
    }

    private void initVariable() {
        typeListView = getIntent().getExtras().getString(getResources().getString(R.string._type_listView));
        itemsForLists = ListView_utilities.initItemsList(typeListView,null,this);
        editText_left = "";
        editText_right = "";
    }

    private void initView() {
        listView = findViewById(R.id.listView_ActivityView);
        tableLayout_purchaseByProduct = findViewById(R.id.tableView_purchaseByProduct2);
        if (typeListView.equals(getResources().getString(R.string.itemsType_listView))) {
            editText_valueForSearch_activityView = findViewById(R.id.valueForSearch_listView_name);
        }
        if (typeListView.equals(getResources().getString(R.string.sellersType_listView))) {
            editText_valueForSearch_left = findViewById(R.id.valueForSearch_listView_user);
            editText_valueForSearch_right = findViewById(R.id.valueForSearch_listView_retailPlace);
        }
        btn_ESC = findViewById(R.id.btn_escFrom_listPurchase);
    }

    private void initListView(ArrayList<myItemForListView> itemsForLists) {
        MyAdapter myAdapter = null;
        if (itemsForLists != null) {
            itemsForLists_updater = itemsForLists;
            myAdapter = new MyAdapter(itemsForLists, this);
        }
        listView.setAdapter(myAdapter);
    }

    private void settingViews_default(boolean productList_isView) {
        if (typeListView.equals(getResources().getString(R.string.itemsType_listView))) {
            findViewById(R.id.editText_forSingleView).setVisibility(View.VISIBLE);
            findViewById(R.id.editTexts_forDoubleView).setVisibility(View.GONE);
        } else {
            if (typeListView.equals(getResources().getString(R.string.sellersType_listView))) {
                findViewById(R.id.editTexts_forDoubleView).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.editTexts_forDoubleView).setVisibility(View.GONE);
            }
            findViewById(R.id.editText_forSingleView).setVisibility(View.GONE);
        }
        if (!productList_isView) {
            findViewById(R.id.layout_ListView).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_purchaseView).setVisibility(View.GONE);
            btn_ESC.setVisibility(View.GONE);
        } else {
            findViewById(R.id.layout_ListView).setVisibility(View.GONE);
            findViewById(R.id.layout_purchaseView).setVisibility(View.VISIBLE);
            btn_ESC.setVisibility(View.VISIBLE);
        }
    }



    private void editTextLeft_addTextChangedListener() {
        editText_valueForSearch_left.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.length() >= 2) {
                            editText_left = editable.toString();
                        } else editText_left = "";
                        if (editable.length() < 2 && editText_right.length() < 2) {
                            initListView(itemsForLists);
                        } else searchMatching();
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                }
        );
    }

    private void editTextRight_addTextChangedListener() {
        editText_valueForSearch_right.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.length() >= 2) {
                            editText_right = editable.toString();
                        } else editText_right = "";
                        if (editable.length() < 2 && editText_left.length() < 2) {
                            initListView(itemsForLists);
                        } else searchMatching();
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                }
        );
    }

    private void editTextSingle_addTextChangedListener() {
        editText_valueForSearch_activityView.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable editable) {
                        editText_left = editable.toString();
                        if (editable.length() >= 2) searchMatching();
                        if (editable.length() == 0) initListView(itemsForLists);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }
                }
        );
    }

    private void searchMatching() {
        ArrayList<myItemForListView> itemsList_updated = new ArrayList<>();
        if (typeListView.equals(getResources().getString(R.string.itemsType_listView))) {
            itemsList_updated = ViewUtility.searchMatching(editText_left, itemsForLists, getResources());
        }
        if (typeListView.equals(getResources().getString(R.string.sellersType_listView))) {
            itemsList_updated = ViewUtility.searchMatching(
                    editText_left, editText_right, itemsForLists, this.getResources()
            );
        }
        initListView(itemsList_updated);
    }

    void onItemClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String
                        valueItemLeft,
                        valueItemRight = "";
                int countTags = 0;
                if (typeListView.equals(getResources().getString(R.string.sellersType_listView)))
                    countTags = 2;
                if (typeListView.equals(getResources().getString(R.string.itemsType_listView)))
                    countTags = 1;
                String[][] itemList = new String[countTags][2];
                valueItemLeft = (String) (((TextView) ((LinearLayout) view).getChildAt(0)).getText());
                itemList[0] = new String[]{
                        itemsForLists_updater.get(position).findName(valueItemLeft),
                        valueItemLeft
                };
                if (typeListView.equals(getResources().getString(R.string.sellersType_listView))) {
                    valueItemRight = (String) (((TextView) ((LinearLayout) view).getChildAt(1)).getText());
                    itemList[1] = new String[]{
                            itemsForLists_updater.get(position).findName(valueItemRight),
                            valueItemRight
                    };
                }
                findViewById(R.id.layout_ListView).setVisibility(View.GONE);
                findViewById(R.id.layout_purchaseView).setVisibility(View.VISIBLE);
                btn_ESC.setVisibility(View.VISIBLE);
                String[][][] inputData = ViewUtility.selectPurchase_bySpecificParameter(
                        itemList,
                        typeListView,
                        getApplicationContext()
                );
                ViewUtility.prepare_inputData(inputData, getResources());
                initTableLayout_purchase(inputData, (valueItemLeft + ", " + valueItemRight));
            }
        });
    }

    private void initTableLayout_purchase(String[][][] inputData, String title) {
        StringBuilder stringBuilder =
                ViewUtility.initTableLayout_purchase(tableLayout_purchaseByProduct, inputData, title, this);
        ((TextView) findViewById(R.id.title_activityViewRetailPlace)).setText(stringBuilder);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        int visibility = findViewById(R.id.layout_purchaseView).getVisibility();
        if (visibility == View.VISIBLE) {
            outState.putBoolean(KEY_PRODUCT_LIST_IS_VIEW, true);
        }
    }

    private void onClick_btn_ESC() {
        btn_ESC.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        findViewById(R.id.layout_ListView).setVisibility(View.VISIBLE);
                        findViewById(R.id.layout_purchaseView).setVisibility(View.GONE);
                        btn_ESC.setVisibility(View.GONE);
                    }
                }
        );
    }
}