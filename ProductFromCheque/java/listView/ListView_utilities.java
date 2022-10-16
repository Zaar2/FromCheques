package com.zaar2.ProductFromCheque.listView;

import android.content.Context;

import com.zaar2.ProductFromCheque.DB.DB_0_EntryToDatabaseUtilities;
import com.zaar2.ProductFromCheque.R;

import java.util.ArrayList;

public class ListView_utilities {

    /**
     * @param typeListView тип ListView, для которого необходимо сформировать данные
     * @param intermediateDate уже полученные предварительные данные в формате - String[TextView/columns/tags][text/VALUE],
     *                         может быть null
     * @return возвращает null если не удалось определить тип списка
     */
    public static ArrayList<myItemForListView> initItemsList(String typeListView, String[][] intermediateDate, Context context) {
        ArrayList<myItemForListView> list = new ArrayList<>();
        String[] tags;
        String[][][] itemsList = new String[1][1][1];
        tags = definingListType(typeListView, context);
        if (tags != null) {
            if (typeListView.equals(context.getResources().getString(R.string.information_listView))) {
                if (intermediateDate == null)
                    intermediateDate = obtainingDatabase_forInformationDB(tags, context);
                if (intermediateDate != null)
                    itemsList = listItemsFormation(intermediateDate);
            } else if (typeListView.equals(context.getResources().getString(R.string.sellersType_listView))) {
                itemsList = listItemsFormation(tags, context);
            } else if (typeListView.equals(context.getResources().getString(R.string.itemsType_listView))) {
                itemsList = listItemsFormation(tags, context);
            }
        } else return null;
        if (itemsList != null) {
            for (String[][] strDB : itemsList) {
                list.add(new myItemForListView(strDB));
            }
        }
        if (list.size() == 0) return null;
        else return list;
    }

    /**
     * определяем тип списка
     * @return список тегов
     */
    private static String[] definingListType(String typeListView, Context context){
        if (typeListView.equals(context.getResources().getString(R.string.sellersType_listView))) {
            return context.getResources().getStringArray(R.array.nameTagsFor_sellersListView_user);
        }
        if (typeListView.equals(context.getResources().getString(R.string.itemsType_listView))) {
            return context.getResources().getStringArray(R.array.nameTagsFor_itemsListView_nameItems);
        }
        if (typeListView.equals(context.getResources().getString(R.string.information_listView))) {
            return context.getResources().getStringArray(R.array.nameTagsFor_informationListView_nameItems);
        }
        return null;
    }

    /**
     * получаем подготовленный список элементов для ListView, данные выбираем из БД
     * @param tags список тегов для выборки из БД
     */
    private static String[][][] listItemsFormation(String[] tags,Context context){
        return DB_0_EntryToDatabaseUtilities.select(
                tags,
                null, null, null, true,
                context
        );
    }

    /**
     * получаем подготовленный список элементов для ListView, в случае если данные предварительно уже получены
     * @param intermediateDate массив пар значений, в формате - String[TextView/columns/tags][text/VALUE]
     */
    private static String[][][] listItemsFormation(String[][] intermediateDate){
        String[][][] itemsList = new String[intermediateDate.length][2][2]; //String[ITEMS][TextView/columns/tags][text/VALUE]
        String[] nameColumns = {"name", "value"};
        for (int i = 0; i < intermediateDate.length; i++) {
            for (int j = 0; j < 2; j++) {
                itemsList[i][j][0] = nameColumns[j];
                itemsList[i][j][1] = intermediateDate[i][j];
            }
        }
        return itemsList;
    }

    /**
     * предварительное получение данных
     * @param tags список тегов
     * @return массив, пар значений в формате - String[TextView/columns/tags][text/VALUE]; или null
     */
    private static String[][] obtainingDatabase_forInformationDB(String[] tags, Context context) {
        ArrayList<String[]> output = new ArrayList<>();
        String numEntry = "";
        for (String tag : tags) {
            if (tag.equals(context.getResources().getString(R.string.countOfCheques))) {
                numEntry = String.valueOf(
                        DB_0_EntryToDatabaseUtilities.countRows_forTable(
                                context.getResources().getString(R.string.strDB_NAME_table_cheques),
                                context
                        )
                );
            }
            if (tag.equals(context.getResources().getString(R.string.countOfItems))) {
                numEntry = String.valueOf(
                        DB_0_EntryToDatabaseUtilities.countRowsForSelected(
                                new String[]{context.getResources().getString(R.string.tagCheque_nameItem)},
                                null,
                                true,
                                context
                        )
                );
            }
            if (tag.equals(context.getResources().getString(R.string.countEntries_purchaseOfItems))) {
                numEntry = String.valueOf(
                        DB_0_EntryToDatabaseUtilities.countRows_forTable(
                                context.getResources().getString(R.string.strDB_NAME_table_purchase_product),
                                context
                        ));
            }
            if (!numEntry.equals("")) {
                output.add(new String[]{
                        tag,
                        numEntry
                });
                numEntry = "";
            }
        }
        if (output.size()==0) return null;
        else return output.toArray(new String[0][0]);
    }
}