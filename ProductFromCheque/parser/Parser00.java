package com.zaar2.ProductFromCheque.parser;

import android.content.Context;

import com.zaar2.ProductFromCheque.R;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Parser00 {

    public Map<String, ArrayList<String[][]>> parsTo_FormalizedString(BufferedReader bufferedReader, Context context) {
        Map<String, ArrayList<String[][]>> outputArrStr = new HashMap<>();
        String[]
                tagsOfUser = context.getResources().getStringArray(R.array.tagsCheque_table_cheques), //список тегов к каждому продавцу
                tagsOfItem = context.getResources().getStringArray(R.array.tagsCheque_table_items), //список тегов к каждому товару
                tags_foreignKey_forItem = context.getResources().getStringArray(R.array.tagsCheque_foreignKey_forItem); //внешние ключи для таблицы с товарами
        ArrayList<String[][]>
                output_temp_cheques = new ArrayList<>(),
                output_temp_items = new ArrayList<>();
        StringBuilder stringBuilder_input = Parser01_utils.readBuffer(bufferedReader);
        String inputString = stringBuilder_input.toString();
        String[] arrStr_Sellers = //входная строка разбитая на отд.строки по продавцам
                splitBy_groupTag(inputString, "\"user\"");
        for (int i = 0; i < arrStr_Sellers.length; i++) {
            String itemsByOnlySeller = //все товары одной строкой
                    (splitBy_groupTag(arrStr_Sellers[i], "\"items\""))[0];
            String[] arrStr_itemsByOnlySeller = //itemsByOnlySeller разбитая на строки по товарам
                    splitBy_groupTag(itemsByOnlySeller, "},{");
            String[][]
                    arrStr_userByTags = parsToStringArr_byTags(arrStr_Sellers[i], tagsOfUser), //строка по одному продавцу разбитая на отд.строки по тегам
                    arrStr_foreignKeyItem_oneCheque = parsToStringArr_byTags(arrStr_Sellers[i], tags_foreignKey_forItem);
            ArrayList<String[][]> arrStr_itemsByTags = new ArrayList<>();  //все товары этого продавца, разделённые по тегам
            for (int j = 0; j < arrStr_itemsByOnlySeller.length; j++) {
                //вставляем все товары разбитые по тегам (из одного продавца/чека)
                String[][] tempArr_oneItem = parsToStringArr_byTags(arrStr_itemsByOnlySeller[j], tagsOfItem);
                String[][] arrItems_oneSeller = new String
                        [tagsOfItem.length + tags_foreignKey_forItem.length] //теги
                        [2];                                           //пара->{тег,значение}
                System.arraycopy( //добавляем внешние ключи
                        arrStr_foreignKeyItem_oneCheque, 0,
                        arrItems_oneSeller, 0,
                        arrStr_foreignKeyItem_oneCheque.length
                );
                System.arraycopy(
                        tempArr_oneItem, 0,
                        arrItems_oneSeller, arrStr_foreignKeyItem_oneCheque.length,
                        tempArr_oneItem.length
                );
                arrStr_itemsByTags.add(arrItems_oneSeller); //накапливаем строки по всем товарам с одного чека
            }
            output_temp_cheques.add(arrStr_userByTags);//накапливаем строки по всем продавцам/чекам (StringArray->ArrayList)
            output_temp_items.addAll(arrStr_itemsByTags); //накапливаем строки по всем товарам из всех чеков
        }

        outputArrStr.put(context.getResources().getString(R.string.strDB_NAME_table_cheques), output_temp_cheques);
        outputArrStr.put(context.getResources().getString(R.string.strDB_NAME_table_purchase_product), output_temp_items);
        return outputArrStr;
    }

    private String[] splitBy_groupTag(String inputString, String tag) {
        String[] arraySubstring_output = new String[1];

        if (tag.equals("\"user\"")) {
            arraySubstring_output = selectingSubstringByTag_user(inputString, tag);
        }
        if (tag.equals("\"items\"")) {
            arraySubstring_output = new String[]{selectingSubstringByTag_items(inputString, tag)};
        }
        if (tag.equals("},{")) {
            arraySubstring_output = selectingSubstringByTag_dividerOfItems(inputString, tag);
        }
        return arraySubstring_output;
    }

    private String selectingSubstringByTag_items(String inputString, String tag) {
        int startIndexForFind = 1;
        int index_searchResult;
        int
                indexStart_substringByTag,
                indexEnd_substringByTag;

        startIndexForFind = inputString.indexOf(tag);

        index_searchResult = inputString.indexOf("[", startIndexForFind + tag.length());
        startIndexForFind = indexStart_substringByTag = index_searchResult + 1;

        index_searchResult = inputString.indexOf("]", startIndexForFind);
        indexEnd_substringByTag = index_searchResult;

        if (inputString.substring(startIndexForFind, indexEnd_substringByTag).contains("[")) {
            indexEnd_substringByTag = counterForNestedObj(
                    inputString,
                    "[",
                    "]",
                    startIndexForFind,
                    1
            );
        }

        return inputString.substring(
                indexStart_substringByTag,
                indexEnd_substringByTag
        );
    }

    private String[] selectingSubstringByTag_user(String inputString, String tag) {
        String[] arraySubstring_output = new String[1];
        int startIndexForFind = 1;
        int index_searchResult;
        int
                indexStart_substringByTag,
                indexEnd_substringByTag;

        int countDivide = Parser01_utils.count_indexOfSubstring(inputString, tag);
        if (countDivide == 1) {
            arraySubstring_output = new String[1];
            arraySubstring_output[0] = inputString.substring(
                    1,
                    inputString.length() - 1
            );
        }
        if (countDivide > 1) {
            arraySubstring_output = new String[countDivide];
            for (int i = 0; i < countDivide; i++) {
                //определяем начало подстроки
                index_searchResult = (inputString.indexOf(tag, startIndexForFind)); //поиск вхождения с заданной отметки
                startIndexForFind = index_searchResult + tag.length(); //переставляем отметку
                indexStart_substringByTag = index_searchResult - 1; //включая предыдущий символ->"{"
                //определяем конец подстроки
                index_searchResult = (inputString.indexOf(tag, startIndexForFind)); //поиск следующего вхождения с заданной отметки
                if (index_searchResult == -1) {
                    //если далее искомых фрагментов больше не найдено, т.е. это последний в списке
                    startIndexForFind = indexEnd_substringByTag = inputString.length() - 1;//исключая последний символ->"]"
                } else {
                    startIndexForFind = indexEnd_substringByTag = index_searchResult - 2;//ислючая начальные символы следующей подстроки->",{"
                }
                //копируем подстроку в массив
                arraySubstring_output[i] = inputString.substring(
                        indexStart_substringByTag,
                        indexEnd_substringByTag
                );
            }
        }
        return arraySubstring_output;
    }

    private String[] selectingSubstringByTag_dividerOfItems(String inputString, String tag) {
        String[] arraySubstring_output = new String[1];
        int startIndexForFind = 0;
        int index_searchResult;
        int
                indexStart_substringByTag,
                indexEnd_substringByTag;

        int countParts = (Parser01_utils.count_indexOfSubstring(inputString, tag)) + 1;
        if (countParts == 1) {
            arraySubstring_output = new String[1];
            arraySubstring_output[0] = inputString;
        }
        if (countParts > 1) {
            arraySubstring_output = new String[countParts];
            for (int i = 0; i < countParts; i++) {
                //определяем начало подстроки
                index_searchResult = (inputString.indexOf("{", startIndexForFind)); //поиск начала подстроки
                startIndexForFind = indexStart_substringByTag = index_searchResult; //переставляем отметку
                //определяем конец подстроки
                index_searchResult = (inputString.indexOf("}", startIndexForFind)); //поиск следующего вхождения с заданной отметки
                startIndexForFind = indexEnd_substringByTag = index_searchResult + 1;

                if (inputString.substring(indexStart_substringByTag + 1, indexEnd_substringByTag - 1).contains("{")) {
                    int nextItem = indexStart_substringByTag;
                    for (int j = 0; j < 2; j++) {
                        if (i < (countParts - 1)) {
                            nextItem = inputString.indexOf("\"name\"", nextItem) + 1;
                        } else {
                            nextItem = inputString.length();
                        }
                    }
                    startIndexForFind = indexEnd_substringByTag = counterForNestedObj(
                            inputString.substring(indexStart_substringByTag, nextItem),
                            "{",
                            "}",
                            1,
                            0
                    )
                            + indexStart_substringByTag + 1;
                    if (i == (countParts - 1)) {
                        indexEnd_substringByTag++;
                    }
                }

                //копируем подстроку в массив
                arraySubstring_output[i] = inputString.substring(
                        indexStart_substringByTag,
                        indexEnd_substringByTag
                );
            }
        }
        return arraySubstring_output;
    }

    private int counterForNestedObj(String inputString, String openingCharacters, String closingCharacters, int startMarker, int countOpening) {
        int marker = startMarker;
        int count = countOpening;
        while (marker < inputString.length()) {
            marker = inputString.indexOf(openingCharacters, marker);
            if (marker >= 0) {
                marker++;
                count++;
            } else {
                break;
            }
        }
        marker = startMarker;
        while (marker < inputString.length() && count > 0) {
            marker = inputString.indexOf(closingCharacters, marker);
            if (marker >= 0) {
                marker++;
                count--;
            }
        }
        return marker - 1;
    }

    private String[][] parsToStringArr_byTags(String inputString, String[] tagsForFind) {
        String
                tag,
                valueOfTag;
        String[][] pairs_tagAndValue = new String[tagsForFind.length][2];
        for (int i = 0; i < tagsForFind.length; i++) {
            tag = "\"" + tagsForFind[i] + "\"";
            valueOfTag = Parser01_utils.findValueOfTag(inputString, tag);
            pairs_tagAndValue[i][0] = tagsForFind[i];
            pairs_tagAndValue[i][1] = valueOfTag;
        }
        return pairs_tagAndValue;
    }

    public static StringBuilder readBuffer(BufferedReader bufferedReader) {
        return Parser01_utils.readBuffer(bufferedReader);
    }
}