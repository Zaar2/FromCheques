package com.zaar2.ProductFromCheque;

import android.content.Context;
import android.os.Bundle;

public class Report {

    private final Bundle storage;
    private final String[] nameList;

    public Report(Context context) {
        storage = new Bundle();
        nameList = context.getResources().getStringArray(R.array.reportItems);
    }

    public boolean addReportItem(String name, String value) {
        if (confirmOfNameInList(name)) {
            String strVal = String.valueOf(storage.get(name));
            int intVal;
            if (strVal.equals("null")) {
                storage.putString(name, value);
            } else {
                intVal = Integer.parseInt(strVal) + Integer.parseInt(value);
                storage.putString(name, String.valueOf(intVal));
            }
            return true;
        } else return false;
    }

    private boolean confirmOfNameInList(String name) {
        for (String n : nameList) {
            if (name.equals(n)) {
                return true;
            }
        }
        return false;
    }

    public Bundle getStorage() {
        return storage;
    }

    public String[] getNameList() {
        return storage.keySet().toArray(new String[0]);
    }

    public String[][] toArray() {
        String[][] output = new String[nameList.length][2];
        for (int i = 0; i < nameList.length; i++) {
            output[i][0] = nameList[i];
            output[i][1] = storage.getString(nameList[i]);
        }
        return output;
    }
}