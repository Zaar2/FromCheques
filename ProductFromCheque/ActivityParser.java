package com.zaar2.ProductFromCheque;

import com.zaar2.ProductFromCheque.DB.DB_0_EntryToDatabaseUtilities;
import com.zaar2.ProductFromCheque.listView.ListView_utilities;
import com.zaar2.ProductFromCheque.listView.MyAdapter;
import com.zaar2.ProductFromCheque.listView.myItemForListView;
import com.zaar2.ProductFromCheque.parser.MyDialogFragment_Parser;
import com.zaar2.ProductFromCheque.parser.Parser00;


import android.Manifest;
import android.app.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.os.Handler;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.Map;
import java.util.Objects;

public class ActivityParser extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private final String FILES_LIST ="filesList_parse";
    private String REPORT;

    private Report report;

    private File directory_AppStorageSource;
    private String downloadsFolder_path;

    TextView textView;
    Button btnRead;
    Button btnDel;
    Button btnHelp;
    Button btn_reFind;
    Button btn_delFile;
    ListView listView;
    String[] array_files;

    ProgressBar progressBar;
    TextView readProcessing_textView;

    private Handler parsingProcessing = new Handler();
    MyDialogFragment_Parser myDialogFragment_Parser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parser);

        initVariables();
        verifyStoragePermissions(this);
        initViews();
        initDirectory_AppStorageSource();
        try {
            initListView(FILES_LIST);
        } catch (IOException e) {
            e.printStackTrace();
        }
        onClick_btnRead();
        onClick_btnDel();
        onClick_btnHelp();
        onClick_btnReFind();
        onClick_btnDelFile();
    }

    private void initVariables() {
        REPORT = getResources().getString(R.string.information_listView);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        );

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        while (permission != PackageManager.PERMISSION_GRANTED) {
            permission = ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            );
        }
    }

    void initViews() {
        textView = findViewById(R.id.textView);
        btnRead = findViewById(R.id.btnRead);
        btnDel = findViewById(R.id.btnDel);
        btn_delFile = findViewById(R.id.btn_delFile_parse);
        listView = findViewById(R.id.listView_parser);
        btnHelp = findViewById(R.id.btn_helpParser);
        btn_reFind = findViewById(R.id.btn_reFindFile);
        progressBar = findViewById(R.id.progress_bar);
        readProcessing_textView = findViewById(R.id.textView_readProcessing);
    }

    void initDirectory_AppStorageSource() {
        downloadsFolder_path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        directory_AppStorageSource = new File(
                downloadsFolder_path
        );
    }

    void initListView(String LIST_TYPE) throws IOException {
        if (LIST_TYPE.equals(FILES_LIST)) {
            Uri uri = getUri_ofLoadFile_fromOutSource();
            if (uri != null) {
                copyFile(uri, directory_AppStorageSource);
            }
            array_files =
                    arrListFile_to_ArrStrNameFiles(
                            fillArrayFiles(directory_AppStorageSource)
                    );
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_multiple_choice,
                    array_files
            );
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listView.setAdapter(adapter);
        }
        if (LIST_TYPE.equals(REPORT)) {
            ArrayList<myItemForListView> itemsForLists = null;
            MyAdapter myAdapter = null;
            if (report != null)
                itemsForLists = ListView_utilities.initItemsList(REPORT, report.toArray(), this);
            if (itemsForLists != null)
                myAdapter = new MyAdapter(itemsForLists, this);
            if (myAdapter != null) {
                listView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
                listView.setAdapter(myAdapter);
            }
        }
        listView.setVisibility(View.VISIBLE);
    }

    private Uri getUri_ofLoadFile_fromOutSource() {
        Uri receivedUri = null;
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null) {
            receivedUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        }
        return receivedUri;
    }

    ArrayList<File> fillArrayFiles(File directory) {

        ArrayList<File> files = new ArrayList<>();
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (!file.isDirectory()) {
                if (isJsonFile(file.getName())) {
                    files.add(file);
                }
            }
        }
        return files;
    }

    private boolean isJsonFile(String nameFile) {
        String gson = getResources().getString(R.string._json);//=".json";
        int index = nameFile.indexOf(gson);
        return (index == (nameFile.length() - gson.length()));
    }

    String[] arrListFile_to_ArrStrNameFiles(ArrayList<File> arrayList) {
        String[] strArray = new String[arrayList.size()];
        for (int i = 0; i < strArray.length; i++) {
            strArray[i] = (arrayList.get(i)).getName();
        }
        return strArray;
    }

    private void onClick_btnRead() {
        btnRead.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listView.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);
                        launchHandler_parsingProcessing();
                    }
                }
        );
    }

    /**
     * @param isRun показать (true) или скрыть (false) progressBar (информирующий, что происходят вычисления)
     */
    private void progressBar_run(boolean isRun){
        if (isRun) {
            readProcessing_textView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }else {
            readProcessing_textView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void onClick_btnDel() {
        btnDel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listView.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(deleteDB());
                    }
                }
        );
    }

    private void onClick_btnHelp() {
        btnHelp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager manager = getSupportFragmentManager();
                        myDialogFragment_Parser = null;
                        myDialogFragment_Parser = new MyDialogFragment_Parser();
                        Bundle typeDialog = new Bundle();
                        typeDialog.putString(getResources().getString(R.string._type), getResources().getString(R.string._helpDialog));
                        myDialogFragment_Parser.setArguments(typeDialog);
                        myDialogFragment_Parser.show(manager, "myDialog_helpParser");
                    }
                }
        );
    }

    private void onClick_btnReFind(){
        btn_reFind.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            initListView(FILES_LIST);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        listView.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void onClick_btnDelFile(){
        btn_delFile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] checkedFiles = findCheckedFiles(listView);
                        for (String file :checkedFiles) {
                            deletingFile(file);
                        }
                        btn_reFind.performClick();
                    }
                }
        );
    }

    private String deleteDB() {
        return DB_0_EntryToDatabaseUtilities.clearingDB(this)
                + " "
                + getResources().getString(R.string.rows_deleted)
                ;
    }


    private void copyFile(Uri uri, File dest_folder) throws IOException {
        InputStream inputStream_source;
        inputStream_source = this.getContentResolver().openInputStream(uri);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream_source)
        );
        StringBuilder stringBuilder = Parser00.readBuffer(bufferedReader);
        File file_dest = new File(dest_folder, "FromOutSource.json");

        FileWriter writer = new FileWriter(file_dest);
        writer.append(stringBuilder.toString());
        writer.flush();
        writer.close();
        inputStream_source.close();
    }

    /**
     * @param formalizedString_forDB массив строк, где каждый элемент - это пара: {tag/column,value}
     * @param nameTable наименование таблицы в которую надо вставить строки
     * @return true - если вставка завершена корректно, false - если что-то пошло не так
     */
    private boolean updateDB(String[][] formalizedString_forDB, String nameTable) {
        return DB_0_EntryToDatabaseUtilities.insertRows(formalizedString_forDB, nameTable, this);
    }

    /**
     * @param formalizedLists_forDB два списка с ключами, где для ключей:
     *                               [strDB_NAME_table_cheques]->строки по всем продавцам/чекам (ArrayList<String[]>);
     *                              [strDB_NAME_table_purchase_product]->строки по всем товарам из всех чеков (ArrayList<String[][]>).
     *                              []
     * @return true - если вставка завершена корректно, false - если что-то пошло не так
     */
    private boolean updateDB(Map<String, ArrayList<String[][]>> formalizedLists_forDB) {
        return DB_0_EntryToDatabaseUtilities.insertRows(formalizedLists_forDB, report, this);
    }

    private boolean deletingFile(String files) {
        File file = new File(
                downloadsFolder_path
                        + "/" + files
        );
        return file.delete();
    }

    String[] findCheckedFiles(ListView listView) {
        ArrayList<String> arrCheckedFiles = new ArrayList<>();

        SparseBooleanArray chosen = (listView.getCheckedItemPositions());
        for (int i = 0; i < chosen.size(); i++) {
            if (chosen.valueAt(i)) {
                int id = chosen.keyAt(i);
                String s = listView.getItemAtPosition(id).toString();
                arrCheckedFiles.add(
                        s
                );
            }
        }

        String[] arrStrCheckedFiles = new String[arrCheckedFiles.size()];
        arrStrCheckedFiles = arrCheckedFiles.toArray(arrStrCheckedFiles);
        return arrStrCheckedFiles;
    }

    private Map<String, ArrayList<String[][]>> readFile(String filename) {
        File file = new File(
                downloadsFolder_path
                        + "/" + filename
        );
        try {
            // открываем поток для чтения
            FileInputStream inputStream = new FileInputStream(file);
            // буферизируем данные из потока
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream)
            );
            Parser00 parser = new Parser00();
//            String[][] Lists_ForDB = parser.parsTo_FormalizedString(bufferedReader, this);
            Map<String, ArrayList<String[][]>> Lists_ForDB = parser.parsTo_FormalizedString(bufferedReader, this);
//            StringBuilder stringForTexView = parse_arrStr_ToStr(Lists_ForDB);
//            String text = textView.getText() + stringForTexView.toString();
//            textView.setText(text);
            inputStream.close();
            return Lists_ForDB;
        } catch (IOException e) {
            e.printStackTrace();
            textView.setText(e.toString());
            return null;
        }
    }

//    private StringBuilder parse_arrStr_ToStr(String[][] formalizedString_inputArr) {
//        String[] formalizedString_Tags = getResources().getStringArray(R.array.formalizedString_tags);
//        StringBuilder outputStr = new StringBuilder();
//        for (String[] strings : formalizedString_inputArr) {
//            for (int j = 0; j < formalizedString_Tags.length; j++) {
//                outputStr
//                        .append(formalizedString_Tags[j])
//                        .append(" : ")
//                        .append(strings[j])
//                        .append("\n");
//            }
//            outputStr.append("\n----------\n\n");
//        }
//        return outputStr;
//    }

    private void launchHandler_parsingProcessing() {
        progressBar_run(true);
        parsingProcessing.removeCallbacksAndMessages(processing);
        parsingProcessing.postDelayed(processing, 0);
    }

    private void closeHandler_parsingProcessing() {
        parsingProcessing.removeCallbacksAndMessages(null);
        progressBar_run(false);
    }

    /**
     * запуск процесса перевода и внесения записей в БД в фоновом потоке
     */
    private final Runnable processing = new Runnable() {
        @Override
        public void run() {
            report = null;
            report = new Report(getApplicationContext());
            String[] checkedFiles = findCheckedFiles(listView);
            for (String file : checkedFiles) {
                Map<String, ArrayList<String[][]>> formalizedLists_forDB = readFile(file);
                if (formalizedLists_forDB != null) {
                    if (updateDB(formalizedLists_forDB)) deletingFile(file);
                }
            }
            try {
                initListView(REPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            closeHandler_parsingProcessing();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textView = null;
        btnRead = null;
        btnDel = null;
        btnHelp = null;
        listView = null;
        array_files = null;
    }
}