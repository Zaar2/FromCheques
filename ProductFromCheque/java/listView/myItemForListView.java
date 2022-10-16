package com.zaar2.ProductFromCheque.listView;

import java.util.ArrayList;

/**
 * <p>
 *     Данные распределяются в два строковых массива: name и value.
 * </p>
 * <p>
 *     name-> {@link #name}
 *     <p>
 *         название соответствующего тега/колонки
 *     </p>
 * </p>
 * <p>
 *     value-> {@link #value}
 *     <p>
 *         значение из полученной строки базы соответствующее тегу/колонке
 *     </p>
 *     <p>
 *         <p>Соответствующие пары:</p>
 *         <p>имя->значение</p>
 *         <p>name[i]->value[i]</p>
 *
 *     </p>
 * </p> */
public class myItemForListView {
    private String[]
            name,
            value;
    private boolean isValid = false;

    /**
     * @param strDB_byOneItem содержит заданные колонки из одной строки таблицы базы данных
     */
    public myItemForListView(String[][] strDB_byOneItem) {
        ArrayList<String>
                nameTMP = new ArrayList<>(),
                valueTMP = new ArrayList<>();
        for (String[] tagItem : strDB_byOneItem) {
            nameTMP.add(tagItem[0]);
            valueTMP.add(tagItem[1]);
        }
        name = nameTMP.toArray(new String[0]);
        value = valueTMP.toArray(new String[0]);
        if (name.length == value.length) isValid = true;
    }

    public String[] getName() {
        return name;
    }

    public String[] getValue() {
        return value;
    }

    public String getValue_byID(int id){
        return value[id];
    }

    public int find_idName(String name) {
        int id = -1;
        for (int i = 0; i < this.name.length; i++) {
            if (name.equals(this.name[i])) {
                id = i;
                break;
            }
        }
        return id;
    }

    public boolean isValid() {
        return isValid;
    }

    public int getCountTags() {
        return name.length;
    }

    public String findName(String value) {
        String name = "null";
        for (int i = 0; i < this.value.length; i++) {
            if (this.value[i].equals(value)) {
                name = this.name[i];
                break;
            }
        }
        return name;
    }
}