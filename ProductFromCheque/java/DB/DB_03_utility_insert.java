package com.zaar2.ProductFromCheque.DB;

import static com.zaar2.ProductFromCheque.DB.DB_02_utility_common.*;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.zaar2.ProductFromCheque.R;
import com.zaar2.ProductFromCheque.Report;

import java.util.ArrayList;
import java.util.Map;

public class DB_03_utility_insert {

    /**
     * @param database ссылка на БД
     * @param formalizedString_input массив строк, где каждый элемент - это пара: {tag/column,value}
     * @param context ссылка на контекст
     * @return кол-во заполненных колонок в строке
     */
    protected static int insertRows_chequesAndItems(SQLiteDatabase database, String[][] formalizedString_input, String nameTable, Context context) {
        int countInserted = 0;

        ContentValues values = new ContentValues();
        String[] formalizedString_tags = context.getResources().getStringArray(R.array.formalizedString_tags);
        for (String[] strings : formalizedString_input) {
            for (int i = 0; i < formalizedString_tags.length; i++) {
                values.put(formalizedString_tags[i], strings[i]);
            }
            if (
                    database.insert(nameTable, null, values) >= 0
            ) {
                countInserted++;
            }
        }
        return countInserted;
    }

    /**
     * Метод вставки данных в несколько таблиц, связанных между собой внешними ключами.
     * @param database ссылка на БД
     * @param formalizedLists_input список с наборами данных для таблиц. Ключи для таблиц:
    [strDB_NAME_table_cheques]->набор данных для chequesTable
    (строки по всем продавцам/чекам (ArrayList<String[2]>));
    [strDB_NAME_table_purchase_product]->набор данных для purchase_productTable
    (строки по всем товарам из всех чеков (ArrayList<String[2]>)).
     * @param context ссылка на контекст
     * @return false-если кол-во вставленных строк не совпадает с кол-вом входящих строк во всех наборах данных,
    true - если совпадает
     */
    @SuppressLint("Range")
    protected static boolean insertRows_chequesAndItems
    (SQLiteDatabase database, Map<String, ArrayList<String[][]>> formalizedLists_input, Report report, Context context) {
//        ArrayList<String[][]> reportOfInsert = new ArrayList<>();
//        ArrayList<String> arrTblNames = getNameTables(database);
        int
                recordRejected = 0,
                chequesLoaded = 0;
        long recordsLoaded = 0;
        ArrayList<String[][]> errStr_cheques = new ArrayList<>();
        String nameTemporaryTable = context.getResources().getString(R.string.strDB_NAME_temporaryTables_purchase_product);
        String[] namesTables_dest = context.getResources().getStringArray(R.array.allTablesDB);
        String[] foreignKeys_itemsTable = context.getResources().getStringArray(R.array.tagsCheque_foreignKey_forItem);
        int
                countRows_inserted,
                countRows_inDataSet;
        Bundle numInserted;
        database.execSQL("PRAGMA foreign_keys=on;");

        //1.filling tables (cheques and items->temporaryTable), duplicates are copied to array -> errStr
        //----------------
        numInserted = fillingTableCollection(
                nameTemporaryTable, namesTables_dest, errStr_cheques,
                formalizedLists_input,
                database, context.getResources()
        );
        countRows_inserted = numInserted.getInt(context.getResources().getString(R.string._countInserted));
        countRows_inDataSet = numInserted.getInt(context.getResources().getString(R.string._countRows_inDataSet));


        //2.deleting duplicates-relating entries of table of cheques, from temporary table items
        //----------------
//        long countRows = DB_02_utility_common.getNumEntries(database, nameTemporaryTable);
        //набираем параметры ограничивающие выбор при поиске строк для удаления (foreignKeys)
        for (String[][] row : errStr_cheques) {
            ArrayList<String[]> whereSel_oneRow = new ArrayList<>();
            for (String[] pair : row) {
                for (String key : foreignKeys_itemsTable) {
                    if (pair[0].equals(key)) {
                        whereSel_oneRow.add(pair);
                        break;
                    }
                }
            }
            if (whereSel_oneRow.size() == foreignKeys_itemsTable.length) {
                recordRejected += deletingRows(
                        database,
                        context.getResources().getString(R.string.strDB_NAME_temporaryTables_purchase_product),
                        whereSel_oneRow.toArray(new String[whereSel_oneRow.size()][2]),
                        context.getResources()
                );
            }
        }

//        arrTblNames = getNameTables(database);
//        long
//                countCheques = DB_02_utility_common.getNumEntries(database, DB_01_value.NAME_TABLE_CHEQUES),
//                countItems = DB_02_utility_common.getNumEntries(database, DB_01_value.NAME_TABLE_PURCHASE_ITEMS),
//                countTempItems = DB_02_utility_common.getNumEntries(database, nameTemporaryTable);
        //3.добавить записи из временной таблицы в основную таблицу
        //формирование единого списка имен всех колонок куда/откуда вставлять данные
        String[] tagsForEnum = combinesToFormOneString(
                context.getResources().getStringArray(R.array.tagsCheque_foreignKey_forItem),
                context.getResources().getStringArray(R.array.tagsIncludedInPurchaseTable)
        );
        //----------------
        //формирование строки запроса
        String query =
                "INSERT INTO "
                        //целевая таблица
                        + DB_01_value.NAME_TABLE_PURCHASE_ITEMS
                        + " (";
        //перечисление колонок - куда вставлять данные
        for (int i = 0; i < tagsForEnum.length; i++) {
            if (i == (tagsForEnum.length - 1)) {
                query += tagsForEnum[i];
            } else query += tagsForEnum[i] + ", ";
        }
        query += ") SELECT ";
        //перечисление колонок - из которых брать данные
        for (int i = 0; i < tagsForEnum.length; i++) {
            if (i == (tagsForEnum.length - 1)) {
                query += tagsForEnum[i];
            } else query += tagsForEnum[i] + ", ";
        }
        query +=
                " FROM "
                        //таблица с требуемыми данными
                        + context.getResources().getString(R.string.strDB_NAME_temporaryTables_purchase_product)
                        + ";";

        database.execSQL(query);

        recordsLoaded = DB_02_utility_common.getNumEntries(database, nameTemporaryTable);

        boolean fillReport = false;
        fillReport = report.addReportItem(
                context.getResources().getString(R.string.chequesLoaded_ofReport),
                String.valueOf((int) numInserted.get(context.getResources().getString(R.string.chequesLoaded_ofReport)))
        );
        fillReport = report.addReportItem(
                context.getResources().getString(R.string.chequesRejected_ofReport),
                String.valueOf(errStr_cheques.size())
        );
        fillReport = report.addReportItem(
                context.getResources().getString(R.string.recordsLoaded_ofReport),
                String.valueOf(recordsLoaded)
        );
        fillReport = report.addReportItem(
                context.getResources().getString(R.string.recordsRejected_ofReport),
                String.valueOf(recordRejected)
        );

//        arrTblNames = getNameTables(database);
//        dropTemporaryTable(database, context.getResources());
//        arrTblNames = getNameTables(database);
//        countCheques = DB_02_utility_common.getNumEntries(database, DB_01_value.NAME_TABLE_CHEQUES);
//        countItems = DB_02_utility_common.getNumEntries(database, DB_01_value.NAME_TABLE_PURCHASE_ITEMS);
        return ((countRows_inserted + errStr_cheques.size()) == countRows_inDataSet) && fillReport;
    }

    /**
     * filling tables (cheques and items->temporaryTable), duplicates are copied to errStr
     * @param errStr ссылка на список предназначенный для хранения строк не вставленных в таблицу
     * @param formalizedLists_input содержит dataSet-ы для каждой из таблиц данных
     * @return возвращаемый объект содержит 3 числа:
     <p>-число записей в обоих переданных dataSet-ах; key->'countRows_inDataSet'</p>
     <p>-колличество записей внесённых в обе таблицы; key->'countInserted'</p>
     <p>-колличество загруженных чеков; key->'Checks loaded'</p>
     */
    private static Bundle fillingTableCollection
    (String nameTable_items_temporary,
     String[] namesTables,
     ArrayList<String[][]> errStr,
     Map<String, ArrayList<String[][]>> formalizedLists_input,
     SQLiteDatabase database,
     Resources resources) {
        Bundle outputBundle = new Bundle();
        ArrayList<String[][]> dataSet_forFilling;
        int
                countRows_inDataSet = 0,
                countInserted = 0,
                countInserted_tmp,
                countInserted_cheques = 0;
        for (String namesTable : namesTables) {
            dataSet_forFilling = formalizedLists_input.get(namesTable);
            String nameTable_destForFilling = "";
            if (database != null) {
                if (namesTable.equals(DB_01_value.NAME_TABLE_CHEQUES))
                    nameTable_destForFilling = DB_01_value.NAME_TABLE_CHEQUES;
                if (namesTable.equals(DB_01_value.NAME_TABLE_PURCHASE_ITEMS))
                    nameTable_destForFilling = nameTable_items_temporary;
                assert dataSet_forFilling != null;
                countRows_inDataSet += dataSet_forFilling.size();
                countInserted_tmp = fillingTable(nameTable_destForFilling, dataSet_forFilling, database, errStr);
                if (nameTable_destForFilling.equals(DB_01_value.NAME_TABLE_CHEQUES))
                    countInserted_cheques = countInserted_tmp;
                countInserted += countInserted_tmp;
            }
        }
        outputBundle.putInt(resources.getString(R.string._countRows_inDataSet), countRows_inDataSet);
        outputBundle.putInt(resources.getString(R.string._countInserted), countInserted);

        outputBundle.putInt(resources.getString(R.string.chequesLoaded_ofReport), countInserted_cheques);
        return outputBundle;
    }

    /**
     * @param nameTable в какую таблицу вставляем записи
     * @param dataSet_forTable данные
     * @param errStr список строк из данных, которые не вставились по каким-то причинам (дубликаты, ...)
     * @return число строк вставленных в указанную таблицу или "-1" если что-то пошло не так
     */
    private static int fillingTable
    (String nameTable, ArrayList<String[][]> dataSet_forTable, SQLiteDatabase database, ArrayList<String[][]> errStr) {
        long countRows_before = DatabaseUtils.queryNumEntries(database, nameTable);
        int countInserted = 0;
        //набираем строки для вставки
        ContentValues values = new ContentValues();
        for (String[][] row : dataSet_forTable) {
            for (String[] pair : row) {
                values.put(pair[0], pair[1]);
            }
            //собственно вставляем
            long resIns = database.insert(nameTable, null, values);
            if (resIns >= 0)
                countInserted++;
            else errStr.add(row);
        }
        if ((countRows_before + countInserted) == DatabaseUtils.queryNumEntries(database, nameTable))
            return countInserted;
        else return -1;
    }

}