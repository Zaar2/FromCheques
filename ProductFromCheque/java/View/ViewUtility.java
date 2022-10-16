package com.zaar2.ProductFromCheque.View;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.zaar2.ProductFromCheque.DB.DB_0_EntryToDatabaseUtilities;
import com.zaar2.ProductFromCheque.R;
import com.zaar2.ProductFromCheque.listView.myItemForListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ViewUtility {

    /**
     * <p>
     * Форматирует и преобразует входные данные.
     * </p>
     * <p>
     * Например: дата во входных данных представлена в виде числа (секунд), в исходящем массиве представлена в формате "dd.MM.yyyy"
     * </p>
     *
     * @param inputData данные в необработанном виде
     * @param resources ссылка на ресурсы
     */
    public static void prepare_inputData(String[][][] inputData, Resources resources) {
        for (int rows = 0; rows < inputData.length; rows++) {
            for (int tags = 0; tags < inputData[rows].length; tags++) {
                //format dateTime: ___sec.->"dd.MM.yyyy"
                if (inputData[rows][tags][0].equals(resources.getString(R.string.tagCheque_dateTime))) {
                    String longV = inputData[rows][tags][1]; //получаем дату в секундах
                    long millisecond = Long.parseLong(longV) * 1000;
                    if (millisecond > 0) {
                        String dateString = DateFormat.format("dd.MM.yyyy", new Date(millisecond)).toString();
                        inputData[rows][tags][1] = dateString;
                    }
                }
                if (inputData[rows][tags][0].equals(resources.getString(R.string.tagCheque_quantity))) {
                }
                //format price: ___kop.->___rub.
                if (inputData[rows][tags][0].equals(resources.getString(R.string.tagCheque_price))) {
                    String kopekStr = inputData[rows][tags][1];
                    float rubFl = Float.parseFloat(kopekStr) / 100;
                    if (rubFl > 0) {
                        String price = rubFl + " " + resources.getString(R.string._rub);
                        inputData[rows][tags][1] = price;
                    }
                }
                if (inputData[rows][tags][0].equals(resources.getString(R.string.tagCheque_unit))) {
                }
            }
        }
    }

    /**
     * @param tableLayout_purchaseByProduct ссылка на View.TableLayout, который нужно заполнить
     * @param inputData                     подготовленные входные данные для размещения в таблице
     * @param title                         ключ (одно или несколько полей) по которому проводился отбор записей
     * @param context
     * @return заголовок и шапка  таблицы
     */
    public static StringBuilder initTableLayout_purchase
    (TableLayout tableLayout_purchaseByProduct, String[][][] inputData, String title, Context context) {
        tableLayout_purchaseByProduct.removeAllViews();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(title).append("\n");
        for (int i = 0; i < inputData[0].length; i++) {
            stringBuilder.append(" / ").append(inputData[0][i][0]);
        }
        for (int rows = 0; rows < inputData.length; rows++) {
            TableRow tableRow = new TableRow(context);
            tableRow.setLayoutParams(
                    new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    )
            );
            for (int tags = 0; tags < inputData[rows].length; tags++) {
                TextView text = new TextView(context);
                text.setText(" / " + inputData[rows][tags][1]);
                if (
                        inputData[rows][tags][0].equals(context.getResources().getString(R.string.tagCheque_nameItem))
                ) {
                    text.setGravity(Gravity.START);
                } else text.setGravity(Gravity.CENTER);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    text.setTextAppearance(R.style.font_product);
                }
                tableRow.addView(text, tags);
            }
            tableLayout_purchaseByProduct.addView(tableRow, rows);
        }
        return stringBuilder;
    }

    /**
     * for double type item of listView
     * editText=tag.name
     */
    public static ArrayList<myItemForListView> searchMatching(
            String editText, ArrayList<myItemForListView> itemsForLists, Resources resources) {
        return new ArrayList<>(
                searchMatching(0, editText, itemsForLists, resources));
    }

    /**
     * for double type item of listView
     * left=tag.user // right=tag.retailPlace
     */
    public static ArrayList<myItemForListView> searchMatching
    (String editText_left, String editText_right, ArrayList<myItemForListView> itemsForLists, Resources resources) {
        ArrayList<myItemForListView> productList_updated = new ArrayList<>();
        int idInArr_myItemsForListView = -1;

        //оба не пустые
        if (editText_left.length() > 1 && editText_right.length() > 1) {
            idInArr_myItemsForListView = 0;
            productList_updated.addAll(
                    searchMatching(idInArr_myItemsForListView, editText_left, itemsForLists, resources)
            );

            idInArr_myItemsForListView = 1;
            productList_updated = searchMatching(idInArr_myItemsForListView, editText_right, productList_updated, resources);
        }
        //не пуст только левый (user)
        if (editText_left.length() > 1 && editText_right.length() <= 1) {
            idInArr_myItemsForListView = 0;
            productList_updated.addAll(searchMatching(idInArr_myItemsForListView, editText_left, itemsForLists, resources));
        }
        //не пуст только правый (retailPlace)
        if (editText_left.length() <= 1 && editText_right.length() > 1) {
            idInArr_myItemsForListView = 1;
            productList_updated.addAll(
                    searchMatching(idInArr_myItemsForListView, editText_right, itemsForLists, resources)
            );
        }
        return productList_updated;
    }

    private static ArrayList<myItemForListView> searchMatching(
            int idInArr_myItemsForList, String editText, ArrayList<myItemForListView> itemsForLists, Resources resources) {
        ArrayList<myItemForListView> output = new ArrayList<>();
        for (myItemForListView item : itemsForLists) {
            String[] arr = new String[1];
            if (item.getCountTags() == 2) {
                arr = resources.getStringArray(R.array.nameTagsFor_sellersListView_user);
            }
            if (item.getCountTags() == 1) {
                arr = resources.getStringArray(R.array.nameTagsFor_itemsListView_nameItems);
            }
            String name = arr[idInArr_myItemsForList];
            int idName = item.find_idName(name);
            String val = item.getValue_byID(idName);
            String textFromListItems = val.toLowerCase(Locale.ROOT);
            String textFromView = editText.toLowerCase(Locale.ROOT);
            if (textFromListItems.contains(textFromView)) {
                output.add(item);
            }
        }
        return output;
    }

    /**
     * Выборка покупок у всех продавцов, по заданному товару
     *
     * @param nameItem товар по которому нужно выбрать все покупки
     * @param context
     * @return
     */
//    public static String[][][] selectPurchase_bySpecificParameter(String nameItem, Context context) {
//        return new DB_00(context, ACTUALITY_DB_VERSION).select(
//                context.getResources().getStringArray(R.array.tagListForPurchase_byProduct),
//                new String[][]{
//                        {
//                                context.getResources().getString(R.string.tagCheque_name),
//                                nameItem
//                        }
//                },
//                context.getResources().getString(R.string.tagCheque_dateTime),
//                "DESC",
//                false
//        );
//    }

    /**
     * Выборка всех покупок у заданного продавца
     * @param nameItem продавец по которому нужно выбрать все покупки.
     *                    Двухуровневый строковый массив, задающий ключ продавца, состоящий из одного/двух полей/тегов:
     *                    "user", "retailPlace".
     * @param context
     * @return
     */
    public static String[][][] selectPurchase_bySpecificParameter
    (String[][] nameItem, String typeItem_ofListView, Context context) {
        String[] tagListForPurchase = new String[1];
        if (typeItem_ofListView.equals(context.getResources().getString(R.string.itemsType_listView))) {
            tagListForPurchase = context.getResources().getStringArray(R.array.tagListForPurchase_byProduct);
        }
        if (typeItem_ofListView.equals(context.getResources().getString(R.string.sellersType_listView))) {
            tagListForPurchase = context.getResources().getStringArray(R.array.tagListForPurchase_bySeller);
        }
        return DB_0_EntryToDatabaseUtilities.select(
                tagListForPurchase,
                nameItem,
                context.getResources().getString(R.string.tagCheque_dateTime),
                "DESC",
                false,
                context
        );
    }

    public static ArrayAdapter<String> initAdapter(String[] itemsList, Context context) {
        return new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                itemsList
        );
    }
}