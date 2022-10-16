package com.zaar2.ProductFromCheque.parser;

import java.io.BufferedReader;
import java.io.IOException;

public class Parser01_utils {

    protected static StringBuilder readBuffer(BufferedReader bufferedReader) {
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // читаем содержимое
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder;
    }

    protected static int count_indexOfSubstring(String inputStr, String substring) {
        int countSubstring = 0;
        int i = 0;
        while (i >= 0) {
            i = inputStr.indexOf(substring, i);
            if (i >= 0) {
                countSubstring++;
                i += substring.length();
            }
        }
        return countSubstring;
    }

    protected static String findValueOfTag(String string, String tag) {
        String
                valueOfTag_tentative,
                valueOfTag_final;
        int indexOfTag = string.indexOf(tag);
        if (indexOfTag == -1) {
            return "null";
        }
        int
                indexValueOfTag_start,
                indexValueOfTag_end;
        indexValueOfTag_start = (string.indexOf(
                ":",
                indexOfTag + tag.length()
        )) + 1;
        indexValueOfTag_end = (string.indexOf(
                ",\"",
                indexValueOfTag_start
        ));
        if (indexValueOfTag_end == -1) {
            indexValueOfTag_end = string.length() - 1;
        }
        valueOfTag_tentative = string.substring(indexValueOfTag_start, indexValueOfTag_end);
        if (valueOfTag_tentative.contains("\\\"")) {
            while (valueOfTag_tentative.contains("\\\"")) {
                valueOfTag_tentative = valueOfTag_tentative.replace("\\\"", "\"");
            }
        }
        valueOfTag_final = valueOfTag_tentative;
        return valueOfTag_final;
    }

    /**
     * формирует строку в формате подходящем для передачи в метод DB_00.insertRows (updateDB),
     * по одному чеку от одного продавца
     * @param tagsOfFormalizedString список тегов, значения которых которые надо собрать в исходящую строку
     * @param arrStr_itemsByTags все товары по одному продавцу, разделённые по тегам,
     *                           каждый элемент это массив пар {тег, значение}
     *                           по одному товару из одного чека от одного продавца
     * @param arrStr_userByTags все пары {тег, значение} по одному продавцу, кроме информации по товарам от этого продавца
     * @return исходящая строка, содержащая все покупки по данному продавцу, по одному чеку
     */
    protected static String[][] parsTo_formalizedString(
            String[] tagsOfFormalizedString,
            String[][][] arrStr_itemsByTags,
            String[][] arrStr_userByTags) {
        String[][] output = new String
                [arrStr_itemsByTags.length]
                [tagsOfFormalizedString.length];
        for (int i = 0; i < arrStr_itemsByTags.length; i++) {
            for (int j = 0; j < tagsOfFormalizedString.length; j++) {
                output[i][j] = Parser01_utils.valueByTag(arrStr_itemsByTags[i], arrStr_userByTags, tagsOfFormalizedString[j]);
            }
        }
        return output;
    }

    /**
     * Ищет значение заданного тега в массивах пар {тег, значение}
     * @param arrStr_itemsByTags массив пар {тег, значение} по одному товару
     * @param arrStr_userByTags все пары {тег, значение} по одному продавцу, кроме информации по товарам от этого продавца
     * @param tag искомый тег
     * @return значение искомого тега
     */
    protected static String valueByTag(String[][] arrStr_itemsByTags, String[][] arrStr_userByTags, String tag) {
        String value = "null";
        for (String[] pair : arrStr_itemsByTags) {
            if (pair[0].equals(tag)) {
                value = pair[1];
                break;
            }
        }
        if (value.equals("null")) {
            for (String[] pair : arrStr_userByTags) {
                if (pair[0].equals(tag)) {
                    value = pair[1];
                    break;
                }
            }
        }
        return value;
    }
}