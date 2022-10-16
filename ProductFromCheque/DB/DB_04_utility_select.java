package com.zaar2.ProductFromCheque.DB;

import static com.zaar2.ProductFromCheque.DB.DB_02_utility_common.*;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.zaar2.ProductFromCheque.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class DB_04_utility_select {

    /**
     * @param nameTable      имя таблицы из которой нужно выбрать данные
     * @param columnsArr     список тегов по которым нужна информация
     * @param whereSelectors ограничения, в формате массива строк, каждая из которых - это пара: {tag,value}
     * @param orderBy        по какому тегу сортировать, сортировка происходит
     * @param orderByType    ASC or DESC
     * @param distinct       дубликаты значений, true-убрать, false-оставить
     * @param database       SQLiteDatabase
     * @param resources      ссылка на ресурсы
     * @return результат в виде массива записей (String[ROWS][COLUMNS][VALUE])
     */
    protected static String[][][] select(
            String nameTable,
            String[][] whereSelectors,
            String[] columnsArr,
            String orderBy,
            String orderByType,
            boolean distinct,
            SQLiteDatabase database,
            Resources resources
    ) {
        String[][][] output;
        String[] selectionArgs;
        String selection = null;
        String orderBy_this;
        if (orderBy == null) {
            orderBy_this = null;
        } else {
            orderBy_this = orderBy + " " + orderByType;
        }
        if (whereSelectors == null) {
            selectionArgs = null;
        } else {
            Map<String, String[]> selections = obtaining_selection_and_selectionArgs(whereSelectors, resources);
            selection = (Objects.requireNonNull(selections.get(resources.getString(R.string._selection))))[0];
            selectionArgs = selections.get(resources.getString(R.string._selectionArgs));
        }
        Cursor cursor = database.query(
                distinct,
                nameTable,
                columnsArr,
                selection,
                selectionArgs,
                null,
                null,
                orderBy_this,
                null
        );
        int count = cursor.getCount();
        if (count > 0) {
            output = new String[count][columnsArr.length][2];
            cursor.moveToFirst();
            for (int i = 0; i < count && !cursor.isAfterLast(); i++) {
                for (int j = 0; j < columnsArr.length; j++) {
                    int colIndex = cursor.getColumnIndex(columnsArr[j]);
                    output[i][j][0] = columnsArr[j];
                    output[i][j][1] = cursor.getString(colIndex);
                }
                cursor.moveToNext();
            }
        } else output = null;
        cursor.close();
        return output;
    }

    /**
     * выборка данных из нескольких таблиц
     * @param nameTable      список таблиц из которых нужно выбрать данные
     * @param columnsArr     список тегов/колонок по которым нужна информация
     * @param whereSelectors ограничения, в формате массива строк, каждая из которых - это пара: {tag,value}
     * @param orderBy        по какому тегу сортировать
     * @param orderByType    тип сортировки ASC or DESC
     * @param distinct       дубликаты значений, true-убрать, false-оставить
     * @param database       SQLiteDatabase
     * @param resources      ссылка на ресурсы
     * @return String[ROWS][COLUMNS][VALUE]
     */
    protected static String[][][] select(
            String[] nameTable,
            String[][] whereSelectors,
            String[] columnsArr,
            String orderBy,
            String orderByType,
            boolean distinct,
            SQLiteDatabase database,
            Resources resources
    ) {
        String[] selectionArgs = new String[whereSelectors.length];
        String[][][] output = new String[1][1][1];
        StringBuilder queryStr = new StringBuilder();
        String[] foreignKeys = resources.getStringArray(R.array.tagsCheque_foreignKey_forItem);
        //формирование строки
        queryStr.append("SELECT ");
        //модификатор - distinct
        if (distinct){
            queryStr.append("DISTINCT ");
        }
        //перечисление колонок для результирующей таблицы
        for (int i = 0; i < columnsArr.length; i++) {
            if (columnsArr[i].equals(DB_01_value.operation_DATE_TIME)) {
                queryStr.append(DB_01_value.NAME_TABLE_CHEQUES).append(".").append(DB_01_value.operation_DATE_TIME);
            } else queryStr.append(columnsArr[i]);
            if (i == (columnsArr.length - 1))
                queryStr.append(" ");
            else queryStr.append(", ");
        }
        //формирование раздела - FROM
        queryStr.append("FROM ").append(nameTable[0]);
        //формирование условий объединения таблиц - INNER JOIN
        queryStr.append(" INNER JOIN ").append(nameTable[1]).append(" ON ");
        for (int i = 0; i < foreignKeys.length; i++) {
                queryStr
                        .append(DB_01_value.NAME_TABLE_PURCHASE_ITEMS).append(".").append(foreignKeys[i])
                        .append("=")
                        .append(DB_01_value.NAME_TABLE_CHEQUES).append(".").append(foreignKeys[i]);
            if (i<(foreignKeys.length-1)){
                queryStr.append(" and ");
            }
        }
//        queryStr
//                .append(DB_01_value.NAME_TABLE_PURCHASE_ITEMS).append(".").append(DB_01_value.operation_FISCAL_DRIVE_NUMBER)
//                .append("=")
//                .append(DB_01_value.NAME_TABLE_CHEQUES).append(".").append(DB_01_value.operation_FISCAL_DRIVE_NUMBER);
//        queryStr.append(" and ");
//        queryStr
//                .append(DB_01_value.NAME_TABLE_PURCHASE_ITEMS).append(".").append(DB_01_value.operation_KKT_REG_ID)
//                .append("=")
//                .append(DB_01_value.NAME_TABLE_CHEQUES).append(".").append(DB_01_value.operation_KKT_REG_ID);
//        queryStr.append(" and ");
//        queryStr
//                .append(DB_01_value.NAME_TABLE_PURCHASE_ITEMS).append(".").append(DB_01_value.operation_DATE_TIME)
//                .append("=")
//                .append(DB_01_value.NAME_TABLE_CHEQUES).append(".").append(DB_01_value.operation_DATE_TIME);
        //формирование условий выборки
        if (whereSelectors != null) {
            queryStr.append(" WHERE ");
            Map<String, String[]> selections = obtaining_selection_and_selectionArgs(whereSelectors, resources);
            queryStr.append((Objects.requireNonNull(selections.get(resources.getString(R.string._selection))))[0]);
            selectionArgs = selections.get(resources.getString(R.string._selectionArgs));
        }
        //клаузула - orderBy
        if (orderBy!=null){
            queryStr
                    .append(" ORDER BY ");
            if (orderBy.equals(DB_01_value.operation_DATE_TIME)) {
                queryStr.append(DB_01_value.NAME_TABLE_CHEQUES).append(".").append(orderBy);
            } else queryStr.append(orderBy);
            queryStr
                    .append(" ")
                    .append(orderByType);
        }
        //________________
        String query = queryStr.toString();
        try {
            Cursor cursor = database.rawQuery(query, selectionArgs);
            int count = cursor.getCount();
            output = new String[count][columnsArr.length][2];
            cursor.moveToFirst();
            for (int i = 0; i < count && !cursor.isAfterLast(); i++) {
                for (int j = 0; j < columnsArr.length; j++) {
                    int colIndex = cursor.getColumnIndex(columnsArr[j]);
                    output[i][j][0] = columnsArr[j];
                    output[i][j][1] = cursor.getString(colIndex);
                }
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception ignored) {
        }
        return output;
    }

    /**
     * выдаёт таблицы, которые будут использованы в запросе,
     * в зависимости от названий полей из входящих данных (columns, whereSelectors)
     */
    protected static String[] checkTags_and_find_tableForUse(String[] columns, String[][] whereSelectors, Resources resources) {

        String[][] tableDB = new String[][]{
                resources.getStringArray(R.array.tagsCheque_table_cheques),
                resources.getStringArray(R.array.tagsCheque_table_items),
        };
        Set<String>
                tables_output = new HashSet<>(),
                setTags = new HashSet<>(Arrays.asList(columns));
        if (whereSelectors != null) {
            for (String[] pair : whereSelectors) {
                setTags.add(pair[0]);
            }
        }
        String[] tagsInput = setTags.toArray(new String[0]);
        for (String tag : tagsInput) {
            for (int i = 0; i < tableDB.length; i++) {
                for (String column : tableDB[i]) {
                    if (tag.equals(column)) {   //проверка -> к какому подмассиву/таблице относится это имя поля
                        if (i == 0)
                            tables_output.add(DB_01_value.NAME_TABLE_CHEQUES);
                        if (i == 1)
                            tables_output.add(DB_01_value.NAME_TABLE_PURCHASE_ITEMS);
                        break;
                    }
                }
            }
        }
        return tables_output.toArray(new String[0]);
    }

    protected static long countSelected(
            String nameTable,
            String[][] whereSelectors,
            String[] columnsArr,
            boolean distinct,
            SQLiteDatabase database,
            Resources resources
    ) {
        long count = 0;
        String[] selectionArgs = null;
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT ");
        queryStr.append("COUNT (*) FROM ");
        if (distinct){
            queryStr.append("(SELECT DISTINCT ");
            for (int i = 0; i < columnsArr.length; i++) {
                if (i == (columnsArr.length - 1)) {
                    queryStr.append(columnsArr[i]);
                } else queryStr.append(columnsArr[i]).append(", ");
            }
            queryStr.append(" FROM ").append(nameTable);
            queryStr.append(") ");
        }else queryStr.append(nameTable).append(" ");
        if (whereSelectors != null) {
            queryStr.append("WHERE ");
            Map<String, String[]> selections = obtaining_selection_and_selectionArgs(whereSelectors, resources);
            queryStr.append((Objects.requireNonNull(selections.get(resources.getString(R.string._selection))))[0]);
            selectionArgs = selections.get(resources.getString(R.string._selectionArgs));
        }
        String query = queryStr.toString();
        try {
            count = DatabaseUtils.longForQuery(database, query, selectionArgs);
        } catch (Exception ignored) {
        }
        return count;
    }
}