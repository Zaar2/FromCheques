package com.zaar2.ProductFromCheque.listView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zaar2.ProductFromCheque.R;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final ArrayList<myItemForListView> itemsList;

    /**
     * @param itemsList набор данных для отображения в View ( ArrayList&lt;myItemForListView> ).
     *                  <p>в зависимости от количества тегов/колонок существующих в поданном классе myItemForListView,
     *                  изменяется и макет/layout для каждого item-а.
     *                  Это значение возвращается не статическим методом  - int myItemForListView.getCountTags()</p>
     */
    public MyAdapter(ArrayList<myItemForListView> itemsList, Context context) {
        this.itemsList = itemsList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        myItemForListView itemForList = getItemForList(position);
        View view = convertView;
        if (view == null) {
            if (itemForList.getCountTags() == 2) {
                view = inflater.inflate(R.layout.item_double_list_view, parent, false);
            } else {
                if (itemForList.getCountTags() == 1) {
                    view = inflater.inflate(R.layout.item_single_list_view, parent, false);
                }
            }
        }
        assert view != null;
        ((TextView) view.findViewById(R.id.tvLeft_ListView)).setText(itemForList.getValue()[0]);
        if (itemForList.getCountTags() > 1) {
            ((TextView) view.findViewById(R.id.tvRight_ListView)).setText(itemForList.getValue()[1]);
        }
        return view;
    }

    private myItemForListView getItemForList(int position) {
        return (myItemForListView) getItem(position);
    }
}