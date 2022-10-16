package com.zaar2.ProductFromCheque.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import static com.zaar2.ProductFromCheque.bcc.Global_variables.ACTUALITY_DB_VERSION;

import com.zaar2.ProductFromCheque.R;
import com.zaar2.ProductFromCheque.Report;

import java.util.ArrayList;
import java.util.Map;


public class DB_0_EntryToDatabaseUtilities {

    private static ArrayList<String> arrTblNames;

    /**
     * Метод вставки данных в одну таблицу.
     * @param formalizedString_input массив строк, где каждый элемент - это пара: {tag/column,value}
     * @param context ссылка на контекст
     * @return false-если кол-во заполненных колонок не совпадает с кол-вом элементов во входящей строке,
    true - если совпадает
     */
    public static boolean insertRows(String[][] formalizedString_input, String nameTable, Context context) {
        SQLiteDatabase database = new DB_00(context,ACTUALITY_DB_VERSION).getWritableDatabase();
        int countInserted = DB_03_utility_insert.insertRows_chequesAndItems(database, formalizedString_input,nameTable, context);
        database.close();
        return countInserted == formalizedString_input.length;
    }

    /**
     * Метод вставки данных в несколько таблиц, связанных между собой внешними ключами.
     * @param formalizedLists_input список с наборами данных для таблиц. Ключи для таблиц:
    [strDB_NAME_table_cheques]->набор данных для chequesTable
    (строки по всем продавцам/чекам (ArrayList<String[2]>));
    [strDB_NAME_table_purchase_product]->набор данных для purchase_productTable
    (строки по всем товарам из всех чеков (ArrayList<String[2]>)).
     * @param context ссылка на контекст
     * @return false-если кол-во вставленных строк не совпадает с кол-вом входящих строк во всех наборах данных,
    true - если совпадает
     */
    public static boolean insertRows(
            Map<String, ArrayList<String[][]>> formalizedLists_input,
            Report report,
            Context context
    ) {
        boolean result;
        SQLiteDatabase database = new DB_00(context,ACTUALITY_DB_VERSION).getWritableDatabase();
//        arrTblNames = DB_02_utility_common.getNameTables(database);
        int numTablesDB_before = DB_02_utility_common.getNameTables(database).size();
        if (numTablesDB_before != 6) {
            DB_02_utility_common.dropTemporaryTable(database, context.getResources());
            numTablesDB_before = DB_02_utility_common.getNameTables(database).size();
        }
        try {
            new DB_00(context,ACTUALITY_DB_VERSION).createTemporaryTable(database, context.getResources());
//            arrTblNames = DB_02_utility_common.getNameTables(database);
            result = DB_03_utility_insert.insertRows_chequesAndItems(database, formalizedLists_input,report, context);
        } finally {
            if (DB_02_utility_common.getNameTables(database).size() != numTablesDB_before)
                DB_02_utility_common.dropTemporaryTable(database, context.getResources());
            database.close();
        }
        return result;
    }

    /**
     * @param columns        список тегов/колонок по которым нужна информация
     * @param whereSelectors ограничения, в формате массива строк, каждая из которых - это пара: {tag,value}
     * @param orderBy        по какому тегу сортировать
     * @param orderByType    ASC or DESC
     * @param distinct       дубликаты значений, true-убрать, false-оставить
     * @return String[ROWS][COLUMNS][VALUE]
     */
    public static String[][][] select(
            String[] columns,
            String[][] whereSelectors,
            String orderBy,
            String orderByType,
            boolean distinct,
            Context context
    ) {
        String[][][] output = new String[1][1][1];
        SQLiteDatabase database = new DB_00(context,ACTUALITY_DB_VERSION).getReadableDatabase();
        String[] tables = DB_04_utility_select.checkTags_and_find_tableForUse(columns, whereSelectors, context.getResources());
        if (tables.length == 1) {
            output = DB_04_utility_select.select( tables[0], whereSelectors, columns, orderBy, orderByType, distinct, database, context.getResources());
        }
        if (tables.length==2){
            output = DB_04_utility_select.select( tables, whereSelectors, columns, orderBy, orderByType, distinct, database, context.getResources());
        }
        database.close();
        return output;
    }

    public static long countRowsForSelected(String[] columns,
                                            String[][] whereSelectors,
                                            boolean distinct,
                                            Context context) {
        long count = 0;
        SQLiteDatabase database = new DB_00(context, ACTUALITY_DB_VERSION).getReadableDatabase();
        String[] tables = DB_04_utility_select.checkTags_and_find_tableForUse(columns, whereSelectors, context.getResources());
        if (tables.length == 1) {
            count = DB_04_utility_select.countSelected(tables[0], whereSelectors, columns, distinct, database, context.getResources());
        }
        database.close();
        return count;
    }

    public static long countRows_forTable(String nameTable, Context context) {
        SQLiteDatabase database = new DB_00(context, ACTUALITY_DB_VERSION).getReadableDatabase();
        long output = DB_02_utility_common.getNumEntries(database, nameTable);
        database.close();
        return output;
    }

    public static int clearingDB(Context context) {
        SQLiteDatabase database = new DB_00(context,ACTUALITY_DB_VERSION).getWritableDatabase();
        int countDeleted = DB_02_utility_common.deletingRows(database, DB_01_value.NAME_TABLE_CHEQUES);
        countDeleted += DB_02_utility_common.deletingRows(database, DB_01_value.NAME_TABLE_PURCHASE_ITEMS);
        database.close();
        return countDeleted;
    }
}