package com.zaar2.ProductFromCheque.DB;


import android.content.Context;

import com.zaar2.ProductFromCheque.R;

public class DB_01_value {

    protected static String NAME_TABLE_CHEQUES;
    protected static String NAME_TABLE_PURCHASE_ITEMS;
    protected static String NAME_TABLE_SELLER;
    protected static String NAME_TABLE_TYPE_NDS;
    protected static String NAME_TABLE_OPERATION_TYPE;

    protected static String[][] ROWS_and_VALUES_TABLE_typeNds;
    protected static String[][] ROWS_and_VALUES_TABLE_operationType;

    protected static String ID = "_id";
    protected static String NAME = "Name";
    protected static String VALUE = "Value";

    /**
     * ROWS_and_VALUES_TABLE_operations{
//     * _id,
//     * PRODUCT._id,
//     * SELLER._id,
     * fiscalDriveNumber -> {@link #operation_FISCAL_DRIVE_NUMBER}
     * fiscalDocumentNumber, -> {@link #operation_FISCAL_DOCUMENT_NUMBER}
     * fiscalSign -> {@link #operation_FISCAL_SIGN}
     * dateTime, -> {@link #operation_DATE_TIME}
     * userInn, -> {@link #seller_userInn}
     * user, -> {@link #seller_user}
     * operationType, -> {@link #operation_OPERATION_TYPE}
     * retailPlace, -> {@link #operation_RETAIL_PLACE}
     * retailPlaceAddress, -> {@link #operation_RETAIL_PLACE_ADDRESS}
     * name, -> {@link #item_name}
     * quantity, -> {@link #item_QUANTITY}
     * price, -> {@link #item_PRICE}
     * unit, -> {@link #item_UNIT}
     * nds, -> {@link #item_NDS}
     * requestNumber, -> {@link #operation_REQUEST_NUMBER}

     * }
     * <p>
     * * OPERATION -> {@link #NAME_TABLE_CHEQUES}
     *
//     * _id -> {@link #}
//     * PRODUCT._id -> {@link #}
//     * SELLER._id -> {@link #}

     * fiscalDriveNumber (ФН) -> {@link #operation_FISCAL_DRIVE_NUMBER}
     * fiscalDocumentNumber (Порядковый номер фискального документа, ФД) -> {@link #operation_FISCAL_DOCUMENT_NUMBER}
     * fiscalSign (ФПД) -> {@link #operation_FISCAL_SIGN}
     * dateTime (Дата, время ФД. Реальное время в -> {@link #operation_DATE_TIME}
     * userInn (ИНН предприятия) -> {@link #seller_userInn}
     * user -> {@link #seller_user}
     * operationType (Признак расчета: -> {@link #operation_OPERATION_TYPE}
      • 1 – Приход
      • 2 – Возврат прихода
      • 3 – Расход
      • 4 – Возврат расхода)
     * retailPlace (Место расчетов (свободная -> {@link #operation_RETAIL_PLACE}
      форма))
     * retailPlaceAddress -> {@link #operation_RETAIL_PLACE_ADDRESS}
      место (адрес) осуществления
      расчетов)

     * requestNumber (Номер чека за смену) -> {@link #operation_REQUEST_NUMBER}
     * kktRegId (номер ККТ) -> {@link }
     */
//    protected static String operation_ID = "operationTable_ID";
//    protected static String operation_ID_PRODUCT = "operationTable_ID_PRODUCT";
//    protected static String operation_ID_SELLER = "operationTable_ID_SELLER";
    protected static String operation_FISCAL_DRIVE_NUMBER;
    protected static String operation_FISCAL_DOCUMENT_NUMBER;
    protected static String operation_FISCAL_SIGN;
    protected static String operation_DATE_TIME;
    protected static String operation_REQUEST_NUMBER;
    protected static String operation_OPERATION_TYPE;
    protected static String operation_RETAIL_PLACE;
    protected static String operation_RETAIL_PLACE_ADDRESS;
    protected static String item_QUANTITY;
    protected static String item_PRICE;
    protected static String item_UNIT;
    protected static String item_NDS;
    protected static String operation_KKT_REG_ID;

    /**
     * * PRODUCT -> {@link #NAME_TABLE_PURCHASE_ITEMS}
     * _id -> {@link #item_purchaseItems_ID}
     * name (Наименование предмета расчета. -> {@link #item_name}
      Может быть пустым (если тег
      1214 = 3, а также для ИП со
      значением тега 1055 = {2,3,8,16,32}
      при условии, что тег 1212 ≠ 1 до
      01.02.2021))
     * quantity (колличество) -> {@link #item_QUANTITY}
     * price (Цена за единицу с учетом скидок -> {@link #item_PRICE}
     и наценок.
     Не может быть пустым, если нет в
     ФД, то значение = -1. Если ФД не
     предусматривает наличие тега, то
     добавлять его со значением -1.)
     * unit (Единица измерения товара ) -> {@link #item_UNIT}
     * nds (Ставка НДС: -> {@link #item_NDS}
     • 1 – НДС 20%
     • 2 – НДС 10%
     • 3 – НДС 20/120
     • 4 – НДС 10/110
     • 5 – НДС 0%
     • 6 – НДС не облагается)
     * customName (не из json) -> {@link #item_customName}
     * article (не из json) артикул товара -> {@link #item_article}
     */

    protected static String item_purchaseItems_ID = "purchaseItems_ID";
    protected static String item_name;
    protected static String item_customName = "productTable_customName";
    protected static String item_article = "productTable_article";

    /**
     * * SELLER -> {@link #NAME_TABLE_SELLER}
     * _id -> {@link #seller_ID}
     * user -> {@link #seller_user}
     * userInn -> {@link #seller_userInn}
     * customNameUser (не из json) -> {@link #seller_customNameUser}
     */
    protected static String seller_ID = "sellerTable_ID";
    protected static String seller_user;
    protected static String seller_userInn;
    protected static String seller_customNameUser = "sellerTable_customNameUser";

    protected static void fillNameTables(Context context) {
        NAME_TABLE_CHEQUES = context.getResources().getString(R.string.strDB_NAME_table_cheques);
        NAME_TABLE_PURCHASE_ITEMS = context.getResources().getString(R.string.strDB_NAME_table_purchase_product);
        NAME_TABLE_TYPE_NDS = context.getResources().getString(R.string.strDB_NAME_table_type_nds);
        NAME_TABLE_OPERATION_TYPE = context.getResources().getString(R.string.strDB_name_table_operation_type);
        NAME_TABLE_SELLER = context.getResources().getString(R.string.strDB_NAME_table_seller);
    }

    protected static void fillNameColumns(Context context) {
        operation_FISCAL_DRIVE_NUMBER = context.getResources().getString(R.string.tagCheque_fiscalDriveNumber);
        operation_FISCAL_DOCUMENT_NUMBER = context.getResources().getString(R.string.tagCheque_fiscalDocumentNumber);
        operation_FISCAL_SIGN = context.getResources().getString(R.string.tagCheque_fiscalSign);
        operation_DATE_TIME = context.getResources().getString(R.string.tagCheque_dateTime);
        operation_REQUEST_NUMBER = context.getResources().getString(R.string.tagCheque_requestNumber);
        operation_OPERATION_TYPE = context.getResources().getString(R.string.tagCheque_operationType);
        operation_RETAIL_PLACE = context.getResources().getString(R.string.tagCheque_retailPlace);
        operation_RETAIL_PLACE_ADDRESS = context.getResources().getString(R.string.tagCheque_retailPlaceAddress);
        operation_KKT_REG_ID = context.getResources().getString(R.string.tagCheque_kktRegId);

        item_name = context.getResources().getString(R.string.tagCheque_nameItem);
        item_PRICE = context.getResources().getString(R.string.tagCheque_price);
        item_QUANTITY = context.getResources().getString(R.string.tagCheque_quantity);
        item_UNIT = context.getResources().getString(R.string.tagCheque_unit);
        item_NDS = context.getResources().getString(R.string.tagCheque_nds);

        seller_user = context.getResources().getString(R.string.tagCheque_user);
        seller_userInn = context.getResources().getString(R.string.tagCheque_userInn);
    }

    protected static void fillStringForQuery_regularTable(Context context) {
        ROWS_and_VALUES_TABLE_typeNds = new String[][]{  // {_id,Value,name}
                {"1", context.getResources().getString(R.string.valueDB_nds20), context.getResources().getString(R.string.strDB_nds20)},
                {"2", context.getResources().getString(R.string.valueDB_nds10), context.getResources().getString(R.string.strDB_nds10)},
                {"3", context.getResources().getString(R.string.valueDB_nds20_120), context.getResources().getString(R.string.strDB_nds20_120)},
                {"4", context.getResources().getString(R.string.valueDB_nds10_110), context.getResources().getString(R.string.strDB_nds10_110)},
                {"5", context.getResources().getString(R.string.valueDB_nds0), context.getResources().getString(R.string.strDB_nds0)},
                {"6", context.getResources().getString(R.string.valueDB_nds_notTaxable), context.getResources().getString(R.string.strDB_nds_notTaxable)}
        };
        ROWS_and_VALUES_TABLE_operationType = new String[][]{     // {_id,Value,null}
                {"1", context.getResources().getString(R.string.strDB_arrival), " "},
                {"2", context.getResources().getString(R.string.strDB_return_arrival), " "},
                {"3", context.getResources().getString(R.string.strDB_expense), " "},
                {"4", context.getResources().getString(R.string.strDB_return_expense), " "}
        };
    }
}