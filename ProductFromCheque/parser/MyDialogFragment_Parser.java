package com.zaar2.ProductFromCheque.parser;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.zaar2.ProductFromCheque.R;

import java.util.Objects;

public class MyDialogFragment_Parser extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        assert getArguments() != null;
//        String type = getArguments().getString(getResources().getString(R.string._type));

        return new AlertDialog.Builder(getActivity())
                .setMessage(getResources().getString(R.string.helpParser))
                .setPositiveButton(
                        "ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Закрываем окно
                                dialog.cancel();
                            }
                        })
                .create();
    }
}
