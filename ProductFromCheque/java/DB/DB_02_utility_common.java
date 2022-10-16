package com.zaar2.ProductFromCheque.DB;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.zaar2.ProductFromCheque.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DB_02_utility_common {

    protected static boolean tableExists(SQLiteDatabase db, String tableName) {
        if (tableName == null || db == null || !db.isOpen()) {
            return false;
        }
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) " +
                        "FROM sqlite_master " +
                        "WHERE type = ? AND name = ?",
                new String[]{"table", tableName});
        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    protected static void fill_regularTable(SQLiteDatabase database, String nameTable) {
        ContentValues values = new ContentValues();
        if (nameTable.equals(DB_01_value.NAME_TABLE_TYPE_NDS)) {
            for (int i = 0; i < DB_01_value.ROWS_and_VALUES_TABLE_typeNds.length; i++) {
                values.put(DB_01_value.ID, DB_01_value.ROWS_and_VALUES_TABLE_typeNds[i][0]);
                values.put(DB_01_value.VALUE, DB_01_value.ROWS_and_VALUES_TABLE_typeNds[i][1]);
                values.put(DB_01_value.NAME, DB_01_value.ROWS_and_VALUES_TABLE_typeNds[i][2]);
                database.insert(DB_01_value.NAME_TABLE_TYPE_NDS, null, values);
            }
        }
        if (nameTable.equals(DB_01_value.NAME_TABLE_OPERATION_TYPE)) {
            for (int i = 0; i < DB_01_value.ROWS_and_VALUES_TABLE_operationType.length; i++) {
                values.put(DB_01_value.ID, DB_01_value.ROWS_and_VALUES_TABLE_operationType[i][0]);
                values.put(DB_01_value.VALUE, DB_01_value.ROWS_and_VALUES_TABLE_operationType[i][1]);
                values.put(DB_01_value.NAME, DB_01_value.ROWS_and_VALUES_TABLE_operationType[i][2]);
                database.insert(DB_01_value.NAME_TABLE_OPERATION_TYPE, null, values);
            }
        }
    }

    /**
     * Удаляет все записи из указанной таблицы
     */
    protected static int deletingRows(SQLiteDatabase database, String nameTable) {
        return database.delete(nameTable, null, null);
    }

    /**
     * @param whereSelectors условия отбора строк для удаления. Массив строковых пар{column,value}.
     *                       Если передать null, то выполнится удаление всех строк из таблицы.
     * @return колличество удалённых строк или 0 если подходящих под условие строк не найдено или -1 если что-то пошло не так.
     */
    protected static int deletingRows
    (SQLiteDatabase database, String nameTable, String[][] whereSelectors, Resources resources) {
        if (whereSelectors != null) {
            Map<String, String[]> selections = obtaining_selection_and_selectionArgs(whereSelectors, resources);
            String selection = (Objects.requireNonNull(selections.get(resources.getString(R.string._selection))))[0];
            String[] selectionArgs = selections.get(resources.getString(R.string._selectionArgs));
            return database.delete(nameTable, selection, selectionArgs);
        } else {
            return deletingRows(database, nameTable);
        }
    }

    protected static Map<String, String[]> obtaining_selection_and_selectionArgs(String[][] whereSelectors, Resources resources) {
        Map<String, String[]> outputArrStr = new HashMap<>();
        String[] selection = new String[1];
        StringBuilder sel = new StringBuilder();
        String[] selectionArgs;
        if (whereSelectors == null) {
            selectionArgs = null;
        } else {
            selectionArgs = new String[whereSelectors.length];
            sel.append(whereSelectors[0][0]).append(" =?");
            selectionArgs[0] = whereSelectors[0][1];
            for (int i = 1; i < whereSelectors.length; i++) {
                sel.append(" AND ").append(whereSelectors[i][0]).append(" =?");
                selectionArgs[i] = whereSelectors[i][1];
            }
            selection[0] = sel.toString();

        }
        outputArrStr.put(resources.getString(R.string._selection), selection);
        outputArrStr.put(resources.getString(R.string._selectionArgs), selectionArgs);
        return outputArrStr;
    }


    protected static void dropTables(SQLiteDatabase database) {
        dropTable(database, DB_01_value.NAME_TABLE_TYPE_NDS);
        dropTable(database, DB_01_value.NAME_TABLE_OPERATION_TYPE);
        dropTable(database, DB_01_value.NAME_TABLE_PURCHASE_ITEMS);
        dropTable(database, DB_01_value.NAME_TABLE_SELLER);
        dropTable(database, DB_01_value.NAME_TABLE_CHEQUES);
    }

    protected static void dropTable(SQLiteDatabase database, String nameTable) {
        database.execSQL(
                "drop table if exists " + nameTable
        );
    }

    protected static void dropTemporaryTable(SQLiteDatabase database, Resources resources) {
        DB_02_utility_common.dropTable(database, resources.getString(R.string.strDB_NAME_temporaryTables_purchase_product));
    }

    protected static long getNumEntries(SQLiteDatabase database, String nameTable) {
        return DatabaseUtils.queryNumEntries(database, nameTable);
    }

    @SuppressLint("Range")
    protected static ArrayList<String> getNameTables(SQLiteDatabase database) {
        ArrayList<String> arrTblNames = new ArrayList<String>();
        Cursor cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                arrTblNames.add(cursor.getString(cursor.getColumnIndex("name")));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrTblNames;
    }

    protected static String[] combinesToFormOneString(String[] a, String[] b) {
        String[] output = new String[a.length + b.length];
        for (int i = 0; i < (a.length+ b.length); i++) {
            if (i < a.length) {
                output[i] = a[i];
            } else {
                output[i] = b[i - a.length];
            }
        }
        return output;
    }
}