package com.zaar2.ProductFromCheque.DB;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Objects;

import com.zaar2.ProductFromCheque.R;

public class DB_00 extends SQLiteOpenHelper {

    private ArrayList<String> arrTblNames;

    public DB_00(@Nullable Context context, int version) {
        super(
                Objects.requireNonNull(context, "context - must not be null"),
                Objects.requireNonNull(context, "context.getResources() - must not be null").getResources().getString(R.string.name_database),
                null,
                version
        );
        DB_01_value.fillStringForQuery_regularTable(context);
        DB_01_value.fillNameTables(context);
        DB_01_value.fillNameColumns(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        arrTblNames = DB_02_utility_common.getNameTables(db);

        db.execSQL("PRAGMA foreign_keys=on;");
        createTable_regular(db, DB_01_value.NAME_TABLE_TYPE_NDS);
//        arrTblNames = DB_02_utility_common.getNameTables(db);
        createTable_regular(db, DB_01_value.NAME_TABLE_OPERATION_TYPE);
//        arrTblNames = DB_02_utility_common.getNameTables(db);
        createTable_cheques(db, DB_01_value.NAME_TABLE_CHEQUES);
//        arrTblNames = DB_02_utility_common.getNameTables(db);
        createTable_items(db, DB_01_value.NAME_TABLE_PURCHASE_ITEMS);
//        arrTblNames = DB_02_utility_common.getNameTables(db);
        createTable_seller(db, DB_01_value.NAME_TABLE_SELLER);
//        arrTblNames = DB_02_utility_common.getNameTables(db);
    }

    private void createTable_regular(SQLiteDatabase sqLiteDatabase, String nameTable) {
        boolean tableExists;
        tableExists = DB_02_utility_common.tableExists(sqLiteDatabase, nameTable);
        if (!tableExists) {
            sqLiteDatabase.execSQL(
                    "create table if not exists " + nameTable + " ("
                            + DB_01_value.ID + " INTEGER primary key, "
                            + DB_01_value.NAME + " text, "
                            + DB_01_value.VALUE + " text"
                            + ");"
            );
            DB_02_utility_common.fill_regularTable(sqLiteDatabase, nameTable);
        }
    }

    private void createTempTable_items(SQLiteDatabase sqLiteDatabase, String nameTable) {
        sqLiteDatabase.execSQL(
                "create table if not exists " + nameTable + " ("
                        + DB_01_value.item_purchaseItems_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "

                        + DB_01_value.operation_FISCAL_DRIVE_NUMBER + " INTEGER NOT NULL, "
                        + DB_01_value.operation_DATE_TIME + " INTEGER NOT NULL, "
                        + DB_01_value.operation_KKT_REG_ID + " INTEGER NOT NULL, "
                        + DB_01_value.operation_FISCAL_DOCUMENT_NUMBER + " INTEGER NOT NULL, "

                        + DB_01_value.item_name + " TEXT NOT NULL, "
                        + DB_01_value.item_QUANTITY + " INTEGER, "
                        + DB_01_value.item_PRICE + " INTEGER, "
                        + DB_01_value.item_UNIT + " TEXT, "
                        + DB_01_value.item_NDS + " TEXT"
                        + ");"
        );
    }

    private void createTable_items(SQLiteDatabase sqLiteDatabase, String nameTable) {
        sqLiteDatabase.execSQL(
                "create table if not exists " + nameTable + " ("
                        + DB_01_value.item_purchaseItems_ID + " INTEGER PRIMARY KEY, "

                        + DB_01_value.operation_FISCAL_DRIVE_NUMBER + " INTEGER NOT NULL, "
                        + DB_01_value.operation_DATE_TIME + " INTEGER NOT NULL, "
                        + DB_01_value.operation_KKT_REG_ID + " INTEGER NOT NULL, "
                        + DB_01_value.operation_FISCAL_DOCUMENT_NUMBER + " INTEGER NOT NULL, "

                        + DB_01_value.item_name + " TEXT NOT NULL, "
                        + DB_01_value.item_QUANTITY + " INTEGER, "
                        + DB_01_value.item_PRICE + " INTEGER, "
                        + DB_01_value.item_UNIT + " TEXT, "
                        + DB_01_value.item_NDS + " TEXT, "
                        + "FOREIGN KEY ("
                        + DB_01_value.operation_FISCAL_DRIVE_NUMBER + ", "
                        + DB_01_value.operation_KKT_REG_ID + ", "
                        + DB_01_value.operation_FISCAL_DOCUMENT_NUMBER + ", "
                        + DB_01_value.operation_DATE_TIME
                        + ") "
                        + "REFERENCES " + DB_01_value.NAME_TABLE_CHEQUES + "("
                        + DB_01_value.operation_FISCAL_DRIVE_NUMBER + ", "
                        + DB_01_value.operation_KKT_REG_ID + ", "
                        + DB_01_value.operation_FISCAL_DOCUMENT_NUMBER + ", "
                        + DB_01_value.operation_DATE_TIME
                        + ") "
                        + "ON DELETE CASCADE ON UPDATE CASCADE"
                        + ");"
        );
    }

    private void createTable_seller(SQLiteDatabase sqLiteDatabase, String nameTable) {
        sqLiteDatabase.execSQL(
                "create table if not exists " + nameTable + " ("
                        + DB_01_value.seller_ID + " INTEGER primary key, "
                        + DB_01_value.seller_user + " TEXT, "
                        + DB_01_value.seller_userInn + " INTEGER, "
                        + DB_01_value.seller_customNameUser + " TEXT"
                        + ");"
        );
    }

    private void createTable_cheques(SQLiteDatabase sqLiteDatabase, String nameTable) {
        sqLiteDatabase.execSQL(
                "create table if not exists " + nameTable + " ("
                        + DB_01_value.operation_FISCAL_DRIVE_NUMBER + " INTEGER NOT NULL, "
                        + DB_01_value.operation_DATE_TIME + " INTEGER NOT NULL, "
                        + DB_01_value.operation_KKT_REG_ID + " INTEGER NOT NULL, "
                        + DB_01_value.operation_FISCAL_DOCUMENT_NUMBER + " INTEGER NOT NULL, "

                        + DB_01_value.operation_FISCAL_SIGN + " INTEGER, "
                        + DB_01_value.seller_userInn + " INTEGER, "
                        + DB_01_value.seller_user + " TEXT, "
                        + DB_01_value.operation_OPERATION_TYPE + " TEXT, "
                        + DB_01_value.operation_RETAIL_PLACE + " TEXT, "
                        + DB_01_value.operation_RETAIL_PLACE_ADDRESS + " TEXT, "
                        + DB_01_value.operation_REQUEST_NUMBER + " INTEGER, "
                        + "PRIMARY KEY ("
                        + DB_01_value.operation_FISCAL_DRIVE_NUMBER + ", "
                        + DB_01_value.operation_KKT_REG_ID + ", "
                        + DB_01_value.operation_FISCAL_DOCUMENT_NUMBER + ", "
                        + DB_01_value.operation_DATE_TIME
                        + "));"
        );
    }

    protected void createTemporaryTable(SQLiteDatabase database, Resources resources){
        createTempTable_items(database,resources.getString(R.string.strDB_NAME_temporaryTables_purchase_product));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DB_02_utility_common.dropTables(db);
        onCreate(db);
    }
}